package module

import module.ResourceMessage._
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.forward.phForward
import module.utilTrait.extractIDTrait
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-2.
  * des: 资源功能转发
  */
abstract class msg_ResourceCommand extends CommonMessage("resource", ResourceModule)

object ResourceMessage {

    case class msg_queryMultiResourceByScenario(data: JsValue) extends msg_ResourceCommand

}

object ResourceModule extends ModuleTrait {
    val reso = resource()
    import reso._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryMultiResourceByScenario(data: JsValue) =>
            repeater((_, p) => forward("/api/resource/query/multi").post(mergeCond(p)))(mergeResult)(data, pr)

        case _ => ???
    }

}

case class resource() extends phForward with extractIDTrait {
    override lazy val module_name: String = "resource"

    def mergeCond: Option[Map[String, JsValue]] => JsValue = { p =>
        val idLst = extractID("connect_reso")(p)

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("resources"),
                "condition" -> toJson(Map(
                    "resources" -> toJson(idLst)
                ))
            ))
        ))
    }
}
