package module

import module.common.repeater
import module.common.repeater._
import module.StyleColumnMessage._
import play.api.libs.json.JsValue
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-10.
  * des: 报告中表格的表头样式功能转发
  */
abstract class msg_StyleColumnCommand extends CommonMessage("StyleColumn", StyleColumnModule)

object StyleColumnMessage {
    case class msg_queryStyleColumn(data: JsValue) extends msg_StyleColumnCommand
}

object StyleColumnModule extends ModuleTrait {
    val sw = styleColumn()
    import sw._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryStyleColumn(data: JsValue) =>
            repeater((d, _) => forward("/api/column/query").post(d))(mergeResult)(data, pr)
        case _ => ???
    }

}

case class styleColumn() extends phForward {
    override lazy val module_name: String = "style-column"
}
