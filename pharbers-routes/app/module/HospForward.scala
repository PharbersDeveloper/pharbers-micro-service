package module

import module.HospMessage._
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
  * des: 医院功能转发
  */
abstract class msg_HospCommand extends CommonMessage("hosp", HospModule)

object HospMessage {

    case class msg_queryMultiHospByScenario(data: JsValue) extends msg_HospCommand

}

object HospModule extends ModuleTrait {
    val hosp = hospital()

    import hosp._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryMultiHospByScenario(data: JsValue) =>
            repeater((_, p) => forward("/api/hosp/query/multi").post(mergeCond(p)))(mergeResult)(data, pr)

        case _ => ???
    }

}

case class hospital() extends phForward with extractIDTrait {
    override lazy val module_name: String = "hosp"

    def mergeCond: Option[Map[String, JsValue]] => JsValue = { p =>
        val idLst = extractID("connect_dest", "hosp")(p)

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("hosps"),
                "condition" -> toJson(Map(
                    "hosps" -> toJson(idLst)
                ))
            ))
        ))
    }
}

