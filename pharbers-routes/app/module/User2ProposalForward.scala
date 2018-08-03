package module

import module.common.repeater
import module.common.repeater._
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson
import module.User2PrpodsalMessage._
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by jeorch on 18-8-3.
  * des: 用户关卡连接功能转发
  */
abstract class msg_User2ProposalCommand extends CommonMessage("user2proposal", User2ProposalForward)

object User2PrpodsalMessage {

    case class msg_bindUserProposal(data: JsValue) extends msg_User2ProposalCommand

    case class msg_unbindUserProposal(data: JsValue) extends msg_User2ProposalCommand

    case class msg_queryUserProposalBind(data: JsValue) extends msg_User2ProposalCommand

}

object User2ProposalForward extends ModuleTrait {

    val u2p = user2proposal()

    import u2p._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        // TODO to be tested
        case msg_bindUserProposal(data: JsValue) =>
            repeater((d, p) => forward("/api/user-proposal/bind/push").post(mergeCond(d, p)))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_unbindUserProposal(data: JsValue) =>
            repeater((d, p) => forward("/api/user-proposal/bind/pop").post(mergeCond(d, p)))(mergeResult)(data, pr)

        case msg_queryUserProposalBind(data: JsValue) =>
            repeater((d, p) => forward("/api/user-proposal/bind/query").post(mergeCond(d, p)))(mergeResult)(data, pr)

        case _ => ???
    }

}

case class user2proposal() extends phForward {
    override lazy val module_name: String = "user-proposal"

    def mergeCond: (JsValue, Option[Map[String, JsValue]]) => JsValue = { (d, p) =>
        val user_id = Map("user_id" -> p.get("user").as[JsObject].value("user_id"))
        val origin_condition = (d \ "data" \ "condition").asOpt[Map[String, JsValue]].getOrElse(Map.empty)

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("user-proposal"),
                "condition" -> toJson(user_id ++ origin_condition)
            ))
        ))
    }
}

