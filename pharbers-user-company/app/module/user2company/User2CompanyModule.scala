package module.user2company

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import module.user2company.User2CompanyMessage._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.pharbersmacro.CURDMacro.{popMacro, queryMultiMacro}

/**
  * Created by clock on 18-7-31.
  */
object User2CompanyModule extends ModuleTrait {
    val uc: user2company = impl[user2company]
    import uc._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_bindUserCompany(data: JsValue) =>
            processor(value => returnValue(bindConnection(bind)(value)(db_name)))(data)
        case msg_unbindUserCompany(data: JsValue) =>
            processor(value => returnValue(unbindConnection(qcm)(value)(db_name)))(data)
        case msg_queryUserCompanyBind(data: JsValue) =>
            queryMultiMacro(qcm, cdr, data, db_name, "binds")

        case _ => ???
    }
}
