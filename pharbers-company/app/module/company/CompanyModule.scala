package module.company

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import module.company.CompanyMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}


object CompanyModule extends ModuleTrait {
    val c: company = impl[company]
    import c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_verifyCompanyRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "company name has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushCompany(data) => pushMacro(d2m, dr, data, names, name)
        case msg_popCompany(data: JsValue) => popMacro(qc, popr, data, names)
        case msg_updateCompany(data: JsValue) => updateMacro(qc, up2m, dr, data, names, name)
        case msg_queryCompany(data: JsValue) => queryMacro(qc, dr, data, names, name)
        case msg_queryCompanyMulti(data: JsValue) => queryMultiMacro(qcm, dr, data, names, names)

        case _ => ???
    }
}
