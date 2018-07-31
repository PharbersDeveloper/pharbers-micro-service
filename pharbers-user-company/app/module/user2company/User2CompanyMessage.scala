package module.user2company

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-7-30.
  */
abstract class msg_User2CompanyCommand extends CommonMessage("user2company", User2CompanyModule)

// 用户公司关系
object User2CompanyMessage {
    case class msg_bindUserCompany(data: JsValue) extends msg_User2CompanyCommand
    case class msg_unbindUserCompany(data: JsValue) extends msg_User2CompanyCommand
    case class msg_queryCompanyByUser(data: JsValue) extends msg_User2CompanyCommand
    case class msg_queryUsersByCompany(data: JsValue) extends msg_User2CompanyCommand
}