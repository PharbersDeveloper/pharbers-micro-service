package module.user2role

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by jeorch on 18-8-3.
  */
abstract class msg_User2ProposalCommand extends CommonMessage("user2proposal", User2ProposalModule)

object User2ProposalMessage {

    case class msg_bindUserProposal(data: JsValue) extends msg_User2ProposalCommand

    case class msg_unbindUserProposal(data: JsValue) extends msg_User2ProposalCommand

    case class msg_queryUserProposalBind(data: JsValue) extends msg_User2ProposalCommand

}