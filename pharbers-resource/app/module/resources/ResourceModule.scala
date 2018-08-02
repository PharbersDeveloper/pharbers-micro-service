package module.resources

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import module.resources.CompanyMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by jeorch on 18-8-2.
  */
object ResourceModule extends ModuleTrait {
    val c: resource = impl[resource]
    import c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_verifyResourceRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "resource has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushResource(data) => pushMacro(d2m, dr, data, names, name)
        case msg_popResource(data: JsValue) => popMacro(qc, popr, data, names)
        case msg_updateResource(data: JsValue) => updateMacro(qc, up2m, dr, data, names, name)
        case msg_queryResource(data: JsValue) => queryMacro(qc, dr, data, names, name)
        case msg_queryResourceMulti(data: JsValue) => queryMultiMacro(qcm, dr, data, names, names)

        case _ => ???
    }
}
