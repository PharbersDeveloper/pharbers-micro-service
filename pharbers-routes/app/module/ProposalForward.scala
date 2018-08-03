package module

import module.ProposalMessage._
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.{JsArray, JsValue}
import play.api.libs.json.Json.toJson
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}
import scenario.FormatScenarioTrait
import scenario.ScenarioMessage.msg_queryMultiScenario

/**
  * Created by jeorch on 18-8-3.
  * des: 关卡功能转发
  */
abstract class msg_ProposalCommand extends CommonMessage("proposal", ProposalModule)

object ProposalMessage {

    case class msg_pushProposal(data: JsValue) extends msg_ProposalCommand

    case class msg_popProposal(data: JsValue) extends msg_ProposalCommand

    case class msg_updateProposal(data: JsValue) extends msg_ProposalCommand

    case class msg_queryProposal(data: JsValue) extends msg_ProposalCommand

    case class msg_queryProposalByBind(data: JsValue) extends msg_ProposalCommand

    case class msg_queryProposalMulti(data: JsValue) extends msg_ProposalCommand

    case class msg_queryScenarioByProposal(data: JsValue) extends msg_ProposalCommand

}

object ProposalModule extends ModuleTrait with FormatScenarioTrait {

    val c = proposal()

    import c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        // TODO to be tested
        case msg_pushProposal(data: JsValue) =>
            repeater((d, _) => forward("/api/proposal/push").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_popProposal(data: JsValue) =>
            repeater((d, _) => forward("/api/proposal/pop").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_updateProposal(data: JsValue) =>
            repeater((d, _) => forward("/api/proposal/update").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_queryProposal(data: JsValue) =>
            repeater((d, p) => forward("/api/proposal/query").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_queryProposalMulti(data: JsValue) =>
            repeater((d, _) => forward("/api/proposal/query/multi").post(d))(mergeResult)(data, pr)

        case msg_queryProposalByBind(data: JsValue) =>
            repeater((d, p) => forward("/api/proposal/query/multi").post(mergePBC(p)))(mergeResult)(data, pr)

        case msg_queryScenarioByProposal(data: JsValue) => format(pr)(formatProposalsCond)

        case _ => ???
    }

}

case class proposal() extends phForward {
    override lazy val module_name: String = "proposal"

    // proposal binding condition
    def mergePBC: Option[Map[String, JsValue]] => JsValue = { p =>
        val ids = p.get("binds").as[JsArray].value.map(x => (x \ "proposal_id").get.as[String]).toList

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("proposal"),
                "condition" -> toJson(Map(
                    "proposals" -> toJson(ids)
                ))
            ))
        ))
    }

    // scenario proposal condition
    def mergeSPC: Option[Map[String, JsValue]] => JsValue = { p =>
        val ids = p.get("binds").as[JsArray].value.map(x => (x \ "proposal_id").get.as[String]).toList

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("proposal"),
                "condition" -> toJson(Map(
                    "proposals" -> toJson(ids)
                ))
            ))
        ))
    }
}