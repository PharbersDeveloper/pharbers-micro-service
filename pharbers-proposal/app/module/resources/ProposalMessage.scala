package module.resources

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by jeorch on 18-8-3.
  */
abstract class msg_ProposalCommand extends CommonMessage("proposal", ProposalModule)

object ProposalMessage {
    case class msg_verifyProposalRegister(data: JsValue) extends msg_ProposalCommand
    case class msg_pushProposal(data: JsValue) extends msg_ProposalCommand
    case class msg_popProposal(data: JsValue) extends msg_ProposalCommand
    case class msg_updateProposal(data: JsValue) extends msg_ProposalCommand
    case class msg_queryProposal(data: JsValue) extends msg_ProposalCommand
    case class msg_queryProposalsMulti(data: JsValue) extends msg_ProposalCommand
}