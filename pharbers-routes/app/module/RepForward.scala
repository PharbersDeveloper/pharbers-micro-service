package module

import module.RepMessage._
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
  * des: 代表功能转发
  */
abstract class msg_RepCommand extends CommonMessage("representative", RepModule)

object RepMessage {

    case class msg_queryMultiRepByScenario(data: JsValue) extends msg_RepCommand

}

object RepModule extends ModuleTrait {
    val rep = representative()

    import rep._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryMultiRepByScenario(data: JsValue) =>
            repeater((_, p) => forward("/api/rep/query/multi").post(mergeCond(p)))(mergeResult)(data, pr)

        case _ => ???
    }

}

case class representative() extends phForward with extractIDTrait {
    override lazy val module_name: String = "representative"

    def mergeCond: Option[Map[String, JsValue]] => JsValue = { p =>
        val idLst = extractID("connect_rep")(p)

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("reps"),
                "condition" -> toJson(Map(
                    "reps" -> toJson(idLst)
                ))
            ))
        ))
    }
}

