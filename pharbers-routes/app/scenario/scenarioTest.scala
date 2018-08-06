package scenario

import module.common.processor
import module.common.processor._
import scenario.ScenarioMessage._
import com.mongodb.casbah.Imports._
import module.common.transform.drTrait
import com.pharbers.bmpattern.ModuleTrait
import play.api.libs.json.{JsArray, JsNumber, JsString, JsValue}
import module.RepMessage.msg_queryMultiRepByScenario
import com.pharbers.pharbersmacro.CURDMacro.{queryMacro, queryMultiMacro}
import module.HospMessage.msg_queryMultiHospByScenario
import module.GoodsMessage.msg_queryMultiGoodsByScenario
import module.ResourceMessage.msg_queryMultiResourceByScenario
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}
import play.api.libs.json.Json.toJson

/**
  * Created by clock on 18-8-2.
  * des: 场景功能转发
  */
abstract class msg_ScenarioCommand extends CommonMessage("scenario", ScenarioModule)

object ScenarioMessage {
    def msg_queryScenarioDetails(jv: JsValue): List[CommonMessage] = msg_queryScenario(jv) ::
            msg_queryMultiHospByScenario(jv) ::
            msg_queryMultiRepByScenario(jv) ::
            msg_queryMultiResourceByScenario(jv) ::
            msg_queryMultiGoodsByScenario(jv) ::
            Nil

    case class msg_queryScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryMultiScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryQueryHospLst(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryBudgetProgress(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryHospitalDetail(data: JsValue) extends msg_ScenarioCommand

    case class msg_updateDGR(data: JsValue) extends msg_ScenarioCommand

    case class msg_current2past(data: JsValue) extends msg_ScenarioCommand

}

object ScenarioModule extends ModuleTrait with FormatScenarioTrait {

    val scenario = scenarios()

    import scenario._


    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryScenario(data: JsValue) =>
            queryMacro(qc, cdr, data, db_name, name)

        case msg_queryMultiScenario(data: JsValue) => {
            queryMultiMacro(qcm, cdr, data, db_name, name)
        }

        case msg_queryQueryHospLst(_: JsValue) =>
            format(pr)(formatHospitals)

        case msg_queryBudgetProgress(_) =>
            format(pr)(formatBudget)

        case msg_queryHospitalDetail(data) =>
            val hosp_id = (data \ "data" \ "condition" \ "hospital_id").as[JsString]
            format(Some(pr.get ++ Map("hosp_id" -> hosp_id)))(formatHospitalDetails)

        case msg_updateDGR(data) =>
            processor(value => returnValue(update(value)(db_name)(qc, upDGR, success_result, cm)))(data)

        case msg_current2past(data) =>
            processor(value => returnValue(update(value)(db_name)(qc, c2p, success_result, cm)))(data)

        case _ => ???
    }

}

case class scenarios() extends drTrait {
    val db_name = "scenarios"
    val name = "scenario"

    val qc: JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "uuid").asOpt[String].get
        DBObject("uuid" -> tmp)
    }

    val qcm: JsValue => DBObject = { js =>
        val user_id = (js \ "data" \ "condition" \ "user_id").asOpt[String].get

        (js \ "data" \ "condition" \ "proposals").asOpt[List[String]] match {
            case None => DBObject("query" -> "none")
            case Some(ll) => $or(ll map (x => DBObject("user_id" -> user_id, "proposal_id" -> x)))
        }
    }

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

    val c2p: (DBObject, JsValue) => DBObject = { (obj, js) =>
        val current = obj.get("current").asInstanceOf[DBObject]
        val dest_goods = current.get("dest_goods").asInstanceOf[BasicDBList]
                .toList.map(_.asInstanceOf[DBObject])

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

        obj += "current" -> (current += "dest_goods" -> tmp)
        obj
    }

    val success_result: DBObject => Map[String, JsValue] = { _ => Map("result" -> toJson("update success")) }
}