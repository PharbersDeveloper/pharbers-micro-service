package module.resources

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by jeorch on 18-8-6.
  */
abstract class msg_ReportCommand extends CommonMessage("report", ReportModule)

object ReportMessage {
    case class msg_queryReport(data: JsValue) extends msg_ReportCommand
}