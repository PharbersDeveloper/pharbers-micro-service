package module.representative

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import module.representative.RepresentativeMessage._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-2.
  */
object RepresentativeModule extends ModuleTrait {
    val r: representatives = impl[representatives]
    import r._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_pushRepresentative(data) => pushMacro(d2m, dr, data, db_name, name)
        case msg_popRepresentative(data) => popMacro(qc, popr, data, db_name)
        case msg_updateRepresentative(data) => updateMacro(qc, up2m, dr, data, db_name, name)
        case msg_queryRepresentative(data) => queryMacro(qc, dr, data, db_name, name)
        case msg_queryMultiRepresentative(data) => queryMultiMacro(qcm, dr, data, db_name, names)
        case _ => ???
    }

}
