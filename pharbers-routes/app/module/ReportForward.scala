package module

import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import module.ReportMessage._
import module.common.forward.phForward
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * @ ProjectName pharbers-routes.module.ReportForward
  * @ author jeorch
  * @ date 18-8-6
  * @ Description: TODO
  */
abstract class msg_ReportCommand extends CommonMessage("report", ReportModule)

object ReportMessage {

    case class msg_queryReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatTotalReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatDestsGoodsReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatRepGoodsReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatResoAllocation(data: JsValue) extends msg_ReportCommand

    case class msg_formatRepIndResos(data: JsValue) extends msg_ReportCommand

    case class msg_queryAssessReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatAssessReport(data: JsValue) extends msg_ReportCommand

}

object ReportModule extends ModuleTrait {

    val report: phForward = new phForward { override implicit lazy val module_name: String = "report" }
    import report._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
    (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryReport(data: JsValue) =>
            repeater((d, _) => forward("/api/report/query").post(d))(mergeResult)(data, pr)

        case msg_formatTotalReport(_) =>
            (Some(Map("result" -> pr.get("report").asOpt[Map[String, JsValue]].get("summary_report"))), None)

        case msg_formatDestsGoodsReport(_) =>
            (Some(Map("result" -> pr.get("report").asOpt[Map[String, JsValue]].get("dests_goods_report"))), None)

        case msg_formatRepGoodsReport(_) =>
            (Some(Map("result" -> pr.get("report").asOpt[Map[String, JsValue]].get("rep_goods_report"))), None)

        case msg_formatResoAllocation(_) =>
            (Some(Map("result" -> pr.get("report").asOpt[Map[String, JsValue]].get("reso_allocation_report"))), None)

        case msg_formatRepIndResos(_) =>
            (Some(Map("result" -> pr.get("report").asOpt[Map[String, JsValue]].get("rep_ind_resos"))), None)

        case msg_queryAssessReport(data: JsValue) =>
            repeater((d, _) => forward("/api/assess-report/query").post(d))(mergeResult)(data, pr)

        case msg_formatAssessReport(_) =>
            (Some(Map("result" -> toJson(pr.get("assess_report").asOpt[Map[String, JsValue]].get))), None)

        case _ => ???
    }

}
