package module.scenario

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import module.scenario.ScenarioMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-2.
  */
object ScenarioModule extends ModuleTrait {
    val s: scenario = impl[scenario]
    import s._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_pushScenario(data) => pushMacro(d2m, ssr, data, names, name)
        case msg_popScenario(data) => popMacro(qc, popr, data, names)
        case msg_queryScenario(data) => queryMacro(qc, dr, data, names, name)
        case msg_queryScenarioByUUID(data) => queryMacro(u_qc, dr, data, names, name)
        case msg_queryMultiScenario(data) => queryMultiMacro(qcm, dr, data, names, names)
        case msg_queryMultiScenarioByUserAndProposal(data) => queryMultiMacro(u_p_qcm, dr, data, names, names)
        case _ => ???
    }

}
