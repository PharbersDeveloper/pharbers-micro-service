package module

import module.ScenarioMessage._
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import module.RepMessage.msg_queryMultiRepByScenario
import module.HospMessage.msg_queryMultiHospByScenario
import module.GoodsMessage.msg_queryMultiGoodsByScenario
import module.ResourceMessage.msg_queryMultiResourceByScenario
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-7.
  * des: 场景功能转发
  */
abstract class msg_ScenarioCommand extends CommonMessage("scenario", ScenarioModule)

object ScenarioMessage {
    def msg_queryScenarioDetails(jv: JsValue): List[CommonMessage] = msg_queryScenario(jv) ::
            msg_queryMultiHospByScenario(jv) ::
            msg_queryMultiRepByScenario(jv) ::
            msg_queryMultiResourceByScenario(jv) ::
            msg_queryMultiGoodsByScenario(jv) ::
            Nil

    case class msg_queryScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryBudgetProgress(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryQueryHospLst(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryHospitalDetail(data: JsValue) extends msg_ScenarioCommand

    case class msg_allotTask(data: JsValue) extends msg_ScenarioCommand

    case class msg_createPhase(data: JsValue) extends msg_ScenarioCommand
}

object ScenarioModule extends ModuleTrait {
    val scenario: phForward = new phForward {
        override lazy val module_name: String = "scenario"
    }

    import scenario._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryScenario(data: JsValue) =>
            repeater((d, _) => forward("/api/scenario/query/uuid").post(d))(mergeResult)(data, pr)

        case msg_queryBudgetProgress(data: JsValue) =>
            repeater((_, p) => forward("/api/scenario/budget/info").post(toJson(p)))(onlyResult)(data, pr)

        case msg_queryQueryHospLst(data: JsValue) =>
            repeater((_, p) => forward("/api/scenario/hospital/lst").post(toJson(p)))(onlyResult)(data, pr)

        case msg_queryHospitalDetail(data: JsValue) =>
            repeater((d, p) => forward("/api/scenario/hospital/detail").post(toJson(d.as[Map[String, JsValue]] ++ p.get)))(onlyResult)(data, pr)

        case msg_allotTask(data: JsValue) =>
            repeater((d, _) => forward("/api/scenario/task/allot").post(d))(onlyResult)(data, pr)

        case msg_createPhase(data: JsValue) =>
            repeater((d, p) => forward("/api/scenario/phase/next").post(toJson(d.as[Map[String, JsValue]] ++ p.get)))(onlyResult)(data, pr)

        case _ => ???
    }
}

