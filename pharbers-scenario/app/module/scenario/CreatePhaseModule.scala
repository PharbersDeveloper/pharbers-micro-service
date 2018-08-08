package module.scenario

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.stragety.impl
import module.scenario.ScenarioMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-7.
  */
object CreatePhaseModule extends ModuleTrait with jv2dTrait {
    val s: scenario = impl[scenario]
    import s._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_report2current(data) => updateMacro(u_qc, r2c, success_result, data, names, name)
        case msg_current2past(data) => updateMacro(u_qc, c2p, success_result, data, names, name)
        case msg_proposal2current(data) => updateMacro(u_qc, p2c, success_result, data, names, name)

        case _ => ???
    }

    val r2c: (DBObject, JsValue) => DBObject = { (obj, js) =>
        val current = obj.get("current").asInstanceOf[DBObject]
        val dest_goods = current.get("dest_goods").asInstanceOf[BasicDBList]
                .toList.map(_.asInstanceOf[DBObject])

        val report_id = (js \ "report" \ "id").as[String]
        val dests_goods_report = (js \ "report" \ "dests_goods_report" \ "value").as[List[Map[String, JsValue]]]
        val compete_report = (js \ "report" \ "compete_report").as[List[Map[String, JsValue]]]

        val tmp = dest_goods.map { dg =>
            val dest_id = dg.get("dest_id").asInstanceOf[String]
            val goods_id = dg.get("goods_id").asInstanceOf[String]
            val relationship = dg.get("relationship").asInstanceOf[DBObject]

            val target_goods = dests_goods_report.find(x => x("dest_id").as[String] == dest_id && x("goods_id").as[String] == goods_id)
            val compete_goods = compete_report.find(x => x("dest_id").as[String] == dest_id && x("goods_id").as[String] == goods_id)
                    .map(_ ("compete_info").as[List[Map[String, JsValue]]])
                    .map(l => l.map { m =>
                        val builder = MongoDBObject.newBuilder
                        builder += "sales" -> long2Long(m("sales").as[Long])
                        builder += "sales_growth" -> double2Double(m("sales_growth").as[Double])
                        builder += "share" -> double2Double(m("share").as[Double])
                        builder += "share_change" -> double2Double(m("share_change").as[Double])
                        builder.result
                    })


            (target_goods, compete_goods) match {
                case (Some(tg), Some(cg)) =>
                    relationship += "share" -> double2Double(tg("share").as[Double])
                    relationship += "share_change" -> double2Double(tg("share_change").as[Double])
                    relationship += "sales" -> long2Long(tg("sales").as[Long])
                    relationship += "sales_growth" -> double2Double(tg("sales_growth").as[Double])
                    relationship += "contri_rate" -> double2Double(tg("contri_rate").as[Double])
                    relationship += "compete_goods" -> MongoDBList(cg: _*).underlying
                    dg += "relationship" -> relationship
                    dg

                case _ => dg
            }
        }

        current += "dest_goods" -> tmp
        current += "report_id" -> report_id
        obj += "current" -> current
        obj
    }

    val c2p: (DBObject, JsValue) => DBObject = { (obj, js) =>
        val past = obj.get("past").asInstanceOf[BasicDBList].toList.map(_.asInstanceOf[DBObject])
        val current = obj.get("current").asInstanceOf[DBObject]
        obj += "current" -> DBObject()
        obj += "past" -> MongoDBList(current :: past :_*).underlying
        obj
    }

    val p2c: (DBObject, JsValue) => DBObject = { (obj, js) =>
        val current_phase = obj.get("current_phase").asInstanceOf[Int]
        val next_phase_data = (js \ "proposal" \ "scenarios").as[List[Map[String, JsValue]]]
                        .find(x => x("phase").as[Int] == current_phase + 1)
                        .getOrElse(throw new Exception("not next phase of data"))
        obj += "current" -> jv2d(next_phase_data)
        obj += "current_phase" -> int2Integer(current_phase + 1)
        obj
    }
}
