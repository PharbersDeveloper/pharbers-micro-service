package module.user2role

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import module.user2role.User2ProposalMessage._
import com.pharbers.pharbersmacro.CURDMacro.queryMultiMacro
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by jeorch on 18-8-3.
  */
object User2ProposalModule extends ModuleTrait {
    val uc: user2proposal = impl[user2proposal]
    import uc._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_bindUserProposal(data: JsValue) =>
            processor(value => returnValue(bindConnection(bind)(value)(db_name)))(data)
        case msg_unbindUserProposal(data: JsValue) =>
            processor(value => returnValue(unbindConnection(qcm)(value)(db_name)))(data)
        case msg_queryUserProposalBind(data: JsValue) =>
            queryMultiMacro(qcm, cdr, data, db_name, "binds")

        case _ => ???
    }
}
