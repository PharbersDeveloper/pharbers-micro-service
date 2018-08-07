package module.resources

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import module.resources.ReportMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by jeorch on 18-8-6.
  */
object ReportModule extends ModuleTrait {
    val r: report = impl[report]
    import r._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_queryReport(data: JsValue) => processor(value => returnValue(query(value)(names)))(data)

        case _ => ???
    }
}
