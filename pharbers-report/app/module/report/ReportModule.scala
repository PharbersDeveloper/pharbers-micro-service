package module.report

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import module.report.ReportMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by jeorch on 18-8-6.
  */
object ReportModule extends ModuleTrait {


    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_queryReport(data: JsValue) =>
            val r: report = impl[report]
            import r._
            processor(value => returnValue(query(value)(names), name))(data)

        case msg_queryAssessReport(data: JsValue) =>
            val ar: assess_report = impl[assess_report]
            import ar._
            processor(value => returnValue(query(value)(names), name))(data)

        case _ => ???
    }
}
