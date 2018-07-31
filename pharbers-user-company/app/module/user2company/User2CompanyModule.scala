package module.user2company

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import module.user2company.User2CompanyMessage._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}


object User2CompanyModule extends ModuleTrait {
    val buc: bindUserCompany = impl[bindUserCompany]
    val uc: user2company = impl[user2company]
    val cu: company2user = impl[company2user]

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_bindUserCompany(data: JsValue) =>
            processor(value => returnValue(buc.bindConnection(value)("bind_user_company")))(data)
        case msg_unbindUserCompany(data: JsValue) =>
            processor(value => returnValue(buc.unbindConnection(value)("bind_user_company")))(data)
        case msg_queryCompanyByUser(data: JsValue) =>
            processor(value => returnValue(uc.queryConnection(value)("bind_user_company")))(data)
        case msg_queryUsersByCompany(data: JsValue) =>
            processor(value => returnValue(cu.queryConnection(value)("bind_user_company")))(data)

        case _ => ???
    }
}
