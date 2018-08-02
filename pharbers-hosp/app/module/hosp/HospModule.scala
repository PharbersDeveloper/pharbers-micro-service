package module.hosp

import module.common.processor
import module.common.processor._
import module.hosp.HospMessage._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-2.
  */
object HospModule extends ModuleTrait {
    val h: hosp = impl[hosp]
    import h._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_pushHosp(data) => pushMacro(d2m, dr, data, db_name, name)
        case msg_popHosp(data) => popMacro(qc, popr, data, db_name)
        case msg_updateHosp(data) => updateMacro(qc, up2m, dr, data, db_name, name)
        case msg_queryHosp(data) => queryMacro(qc, dr, data, db_name, name)
        case msg_queryHospMulti(data) => queryMultiMacro(qcm, dr, data, db_name, names)
        case _ => ???
    }

}
