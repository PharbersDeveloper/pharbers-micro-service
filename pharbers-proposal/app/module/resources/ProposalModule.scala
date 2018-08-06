package module.resources

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import module.resources.ProposalMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by jeorch on 18-8-3.
  */
object ProposalModule extends ModuleTrait {
    val c: proposal = impl[proposal]
    import c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_verifyProposalRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "proposal has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushProposal(data) => pushMacro(d2m, dr, data, names, name)
        case msg_popProposal(data: JsValue) => popMacro(qc, popr, data, names)
        case msg_updateProposal(data: JsValue) => updateMacro(qc, up2m, dr, data, names, name)
        case msg_queryProposal(data: JsValue) => queryMacro(qc, dr, data, names, name)
        case msg_queryProposalsMulti(data: JsValue) => queryMultiMacro(qcm, dr, data, names, names)

        case _ => ???
    }
}
