package module.scenario

import module.common.processor
import module.common.processor._
import com.mongodb.casbah.Imports._
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import module.scenario.ScenarioMessage.msg_updateRepTask
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import play.api.libs.json.{JsArray, JsNumber, JsString, JsValue}

/**
  * Created by clock on 18-8-2.
  */
object UpdateRepTaskModule extends ModuleTrait {
    val s: scenario = impl[scenario]
    import s.{names, u_qc, success_result}

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_updateRepTask(data) =>
            val upDGR: (DBObject, JsValue) => DBObject = { (obj, js) =>
                val in_data = (js \ "task").as[JsArray].value
                val current = obj.get("current").asInstanceOf[DBObject]
                in_data.foreach { data =>
                    val dest_id = (data \ "dest_id").as[JsString].value
                    val goods_id = (data \ "goods_id").as[JsString].value
                    val rep_id = (data \ "rep_id").as[JsString].value

                    val (targetDGR, keepDGR) = {
                        val dgrLst = current.get("dest_goods_rep").asInstanceOf[BasicDBList].toList.map(_.asInstanceOf[DBObject])
                        val targetDGR = dgrLst.filter(o => o.get("dest_id").asInstanceOf[String] == dest_id)
                                .filter(o => o.get("goods_id").asInstanceOf[String] == goods_id)
                                .filter(o => o.get("rep_id").asInstanceOf[String] == rep_id)

                        val keepDGR = dgrLst diff targetDGR
                        (targetDGR.headOption, keepDGR)
                    }

                    val newTargetDGR = targetDGR match {
                        case Some(old) =>
                            val relationship = old.get("relationship").asInstanceOf[DBObject]
                            relationship += "user_input_day" -> int2Integer(data("user_input_day").asInstanceOf[JsNumber].value.toInt)
                            relationship += "budget_proportion" -> double2Double(data("budget_proportion").asInstanceOf[JsNumber].value.toDouble)
                            relationship += "user_input_target" -> long2Long(data("user_input_target").asInstanceOf[JsNumber].value.toLong)
                            relationship += "target_growth" -> double2Double(data("target_growth").asInstanceOf[JsNumber].value.toDouble)
                            relationship += "user_input_money" -> long2Long(data("user_input_money").asInstanceOf[JsNumber].value.toLong)
                            old += "relationship" -> relationship
                            old
                        case None =>
                            val builder = MongoDBObject.newBuilder
                            builder += "user_input_day" -> int2Integer(data("user_input_day").asInstanceOf[JsNumber].value.toInt)
                            builder += "budget_proportion" -> double2Double(data("budget_proportion").asInstanceOf[JsNumber].value.toDouble)
                            builder += "user_input_target" -> long2Long(data("user_input_target").asInstanceOf[JsNumber].value.toLong)
                            builder += "target_growth" -> double2Double(data("target_growth").asInstanceOf[JsNumber].value.toDouble)
                            builder += "user_input_money" -> long2Long(data("user_input_money").asInstanceOf[JsNumber].value.toLong)

                            val relationship = builder.result
                            DBObject(
                                "dest_id" -> dest_id,
                                "goods_id" -> goods_id,
                                "rep_id" -> rep_id,
                                "relationship" -> relationship
                            )
                    }

                    val dgrLst = MongoDBList(newTargetDGR :: keepDGR: _*).underlying
                    current += "dest_goods_rep" -> dgrLst
                }

                obj += "current" -> current

                obj
            }
            processor(value => returnValue(update(value)(names)(u_qc, upDGR, success_result, cm)))(data)

        case _ => ???
    }

}
