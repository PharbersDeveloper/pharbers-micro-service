package module.scenario

import module.common.processor
import module.common.processor._
import com.mongodb.casbah.Imports._
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import play.api.libs.json.{JsArray, JsNumber, JsString, JsValue}
import module.scenario.ScenarioMessage.{msg_allotManagerTask, msg_allotRepTask}

/**
  * Created by clock on 18-8-2.
  */
object AllotTaskModule extends ModuleTrait {
    val am: AllotModule = impl[AllotModule]

    import am._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_allotRepTask(data) =>
            processor(value => returnValue(update(value)(names)(u_qc, upDGR, success_result, cm)))(data)

        case msg_allotManagerTask(data) =>
            processor(value => returnValue(update(value)(names)(u_qc, upMR, success_result, cm)))(data)

        case _ => ???
    }
}

case class AllotModule() extends scenario {

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

    val upMR: (DBObject, JsValue) => DBObject = { (obj, js) =>
        val in_rep_manage = (js \ "rep_manage").as[JsArray].value
        val current = obj.get("current").asInstanceOf[DBObject]

        val (targetManager, keepReso) = {
            val resoLst = current.get("connect_reso").asInstanceOf[BasicDBList].toList.map(_.asInstanceOf[DBObject])
            val manager = resoLst.filter(x => x.get("type").asInstanceOf[String] == "manager")

            val keepReso = resoLst diff manager
            (manager.head, keepReso)
        }
        val newTargetManager = {
            targetManager += "relationship" -> DBObject(
                "kpi_analysis" -> int2Integer((js \ "manager" \ "kpi_analysis").as[Int]),
                "admin_work" -> int2Integer((js \ "manager" \ "admin_work").as[Int]),
                "team_meet" -> int2Integer((js \ "manager" \ "team_meet").as[Int]),
                "field_work" -> int2Integer((js \ "manager" \ "field_work").as[Int]),
                "sales_train" -> int2Integer((js \ "manager" \ "sales_train").as[Int])
            )
        }
        val resoLst = MongoDBList(newTargetManager :: keepReso: _*).underlying
        current += "connect_reso" -> resoLst

        val manager_id = targetManager.get("id").asInstanceOf[String]
        in_rep_manage.foreach { data =>
            val rep_id = (data \ "rep_id").as[JsString].value
            val (targetMR, keepMR) = {
                val dgrLst = current.get("reso_rep").asInstanceOf[BasicDBList].toList.map(_.asInstanceOf[DBObject])
                val targetDGR = dgrLst.filter(o => o.get("reso_id").asInstanceOf[String] == manager_id)
                        .filter(o => o.get("rep_id").asInstanceOf[String] == rep_id)

                val keepDGR = dgrLst diff targetDGR
                (targetDGR.headOption, keepDGR)
            }
            val newTargetMR = targetMR match {
                case Some(old) =>
                    val relationship = old.get("relationship").asInstanceOf[DBObject]
                    relationship += "field_work" -> int2Integer((data \ "field_work").as[Int])
                    relationship += "sales_train" -> int2Integer((data \ "sales_train").as[Int])
                    relationship += "product_train" -> int2Integer((data \ "product_train").as[Int])
                    relationship += "team_meet" -> int2Integer((data \ "team_meet").as[Int])
                    old += "relationship" -> relationship
                    old
                case None =>
                    val builder = MongoDBObject.newBuilder
                    builder += "field_work" -> int2Integer((data \ "field_work").as[Int])
                    builder += "sales_train" -> int2Integer((data \ "sales_train").as[Int])
                    builder += "product_train" -> int2Integer((data \ "product_train").as[Int])
                    builder += "team_meet" -> int2Integer((data \ "team_meet").as[Int])

                    val relationship = builder.result
                    DBObject(
                        "reso_id" -> manager_id,
                        "rep_id" -> rep_id,
                        "relationship" -> relationship
                    )
            }
            val resoLst = MongoDBList(newTargetMR :: keepMR: _*).underlying
            current += "reso_rep" -> resoLst
        }

        obj += "current" -> current
        obj
    }

}
