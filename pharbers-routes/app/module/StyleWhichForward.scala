package module

import module.common.repeater
import module.common.repeater._
import module.StyleWhichMessage._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-10.
  * des: 报告下拉框功能转发
  */
abstract class msg_StyleWhichCommand extends CommonMessage("StyleWhich", StyleWhichModule)

object StyleWhichMessage {

    case class msg_queryStyleWhichByScenario(data: JsValue) extends msg_StyleWhichCommand

}

object StyleWhichModule extends ModuleTrait {
    val sw = styleWhich()

    import sw._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryStyleWhichByScenario(data: JsValue) =>
            repeater((_, p) => forward("/api/which/query").post(cond(p)))(onlyResult)(data, pr)
        case _ => ???
    }

}

case class styleWhich() extends phForward {
    override lazy val module_name: String = "style-which"

    def cond(pr: Option[Map[String, JsValue]]): JsValue = {
        val which_id = (pr.get("scenario") \ "past").as[List[Map[String, JsValue]]]
                .map(m => m("phase").as[Int] -> m("report_style").as[String])
                .maxBy(_._1)._2

        toJson(Map(
            "data" -> toJson(Map(
                "condition" -> toJson(Map(
                    "which_id" -> toJson(which_id)
                ))
            ))
        ))
    }

}
