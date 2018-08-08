package module

import module.common.repeater
import module.common.repeater._
import module.ProposalMessage._
import module.common.processor
import module.common.processor._
import play.api.libs.json.Json.toJson
import module.common.transform.drTrait
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.mongodb.casbah.Imports.{$or, DBObject}
import play.api.libs.json.{JsArray, JsNumber, JsValue}
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

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

    case class msg_queryProposalByScenario(data: JsValue) extends msg_ProposalCommand

    case class msg_formatProposalWithScenario(data: JsValue) extends msg_ProposalCommand

}

object ProposalModule extends ModuleTrait {

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
            repeater((d, _) => forward("/api/proposal/query").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_queryProposalMulti(data: JsValue) =>
            repeater((d, _) => forward("/api/proposal/query/multi").post(d))(mergeResult)(data, pr)

        case msg_queryProposalByBind(data: JsValue) =>
            repeater((_, p) => forward("/api/proposal/query/multi").post(mergePBC(p)))(mergeResult)(data, pr)

        case msg_queryProposalByScenario(data: JsValue) =>
            repeater((_, p) => forward("/api/proposal/query").post(qpByS(p)))(mergeResult)(data, pr)

        case msg_formatProposalWithScenario(_) =>
            formatProposalWithScenario(pr)

        case _ => ???
    }

}

case class proposal() extends phForward with drTrait {
    override lazy val module_name: String = "proposal"

    // proposal binding condition
    def mergePBC: Option[Map[String, JsValue]] => JsValue = { p =>
        val ids = p.get("binds").as[JsArray].value.map(x => (x \ "proposal_id").get.as[String]).toList

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("proposals"),
                "condition" -> toJson(Map(
                    "proposals" -> toJson(ids)
                ))
            ))
        ))
    }

    val qpByS: Option[Map[String, JsValue]] => JsValue = { pr =>
        val proposal_id = pr.get("scenario").asOpt[Map[String, JsValue]].get("proposal_id")

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("proposal"),
                "condition" -> toJson(Map(
                    "proposal_id" -> toJson(proposal_id)
                ))
            ))
        ))
    }

    // 关卡列表, 多余信息过滤
    def formatProposalWithScenario(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val proposals = pr.get("proposals").as[List[Map[String, JsValue]]].map{ m =>
            m - "scenarios"
        }

        val scenarios = pr.get("scenarios").as[List[Map[String, JsValue]]].map{ m =>
            Map(
                "uuid" -> m("uuid"),
                "proposal_id" -> m("proposal_id")
            )
        }

        val result = proposals.map{p =>
            val uuid = scenarios.find(_("proposal_id") == p("id")) match {
                case None => toJson("none")
                case Some(x) => x("uuid")
            }
            p ++ Map("uuid" -> uuid)
        }

        (Some(Map("result" -> toJson(result))), None)
    }
}