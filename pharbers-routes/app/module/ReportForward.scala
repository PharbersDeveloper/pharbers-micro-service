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

    case class msg_queryReportByCurrent(data: JsValue) extends msg_ReportCommand

    case class msg_queryReportByPast(data: JsValue) extends msg_ReportCommand

    case class msg_formatTotalReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatDestsGoodsReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatRepGoodsReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatResoAllocation(data: JsValue) extends msg_ReportCommand

    case class msg_formatRepIndResos(data: JsValue) extends msg_ReportCommand

    case class msg_queryAssessReport(data: JsValue) extends msg_ReportCommand

    case class msg_formatAssessReport(data: JsValue) extends msg_ReportCommand

}

object ReportModule extends ModuleTrait {
    val r = report()
    import r._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryReportByCurrent(data: JsValue) =>
            repeater((_, p) => forward("/api/report/query").post(byCurrent(p)))(mergeResult)(data, pr)

        case msg_queryReportByPast(data: JsValue) =>
            repeater((_, p) => forward("/api/report/query").post(byPast(p)))(mergeResult)(data, pr)

        case msg_formatTotalReport(_) =>
            mergeReportColumn("summary_report", pr)

        case msg_formatDestsGoodsReport(_) =>
            mergeReportColumn("dests_goods_report", pr)

        case msg_formatRepGoodsReport(_) =>
            mergeReportColumn("rep_goods_report", pr)

        case msg_formatResoAllocation(_) =>
            mergeReportColumn("reso_allocation_report", pr)

        case msg_formatRepIndResos(_) =>
            mergeReportColumn("rep_ind_resos", pr)

        case msg_queryAssessReport(data: JsValue) =>
            repeater((d, _) => forward("/api/assess-report/query").post(d))(mergeResult)(data, pr)

        case msg_formatAssessReport(_) =>
            (Some(Map("result" -> toJson(pr.get("assess_report").asOpt[Map[String, JsValue]].get))), None)

        case _ => ???
    }

}


case class report() extends phForward {

    override implicit lazy val module_name: String = "report"

    def byCurrent(pr: Option[Map[String, JsValue]]): JsValue = {
        val report_id = (pr.get("scenario") \ "current" \ "report_id").as[String]

        toJson(Map(
            "data" -> toJson(Map(
                "condition" -> toJson(Map(
                    "report_id" -> toJson(report_id)
                ))
            ))
        ))
    }

    def byPast(pr: Option[Map[String, JsValue]]): JsValue = {
        val report_id = (pr.get("scenario") \ "past").as[List[Map[String, JsValue]]]
                .map(m => m("phase").as[Int] -> m("report_id").as[String])
                .maxBy(_._1)._2

        toJson(Map(
            "data" -> toJson(Map(
                "condition" -> toJson(Map(
                    "report_id" -> toJson(report_id)
                ))
            ))
        ))
    }

    def mergeReportColumn(key: String, pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val target = pr.get("report").asOpt[Map[String, JsValue]].get(key).as[Map[String, JsValue]]
        val columns = pr.get("column").asOpt[Map[String, JsValue]].get("columns_value")

        val card = Map(
            "key" -> toJson("data-show-card"),
            "values" -> target("overview")
        )

        val table = Map(
            "key" -> toJson("result-table"),
            "values" -> toJson(
                Map(
                    "columns" -> columns,
                    "columnsValue" -> target("value")
                )
            )
        )

        val componentLst = card :: table :: Nil
        (Some(Map("rusult" -> toJson(Map("component_data" -> toJson(componentLst))))), None)
    }

}