package module.roles

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by jeorch on 18-7-30.
  */
abstract class msg_BindUserRoleCommand extends CommonMessage("bind_user_role", UserRoleModule)

object RoleMessage {

    case class msg_verifyBindUserRoleRegister(data: JsValue) extends msg_BindUserRoleCommand
    case class msg_pushBindUserRole(data: JsValue) extends msg_BindUserRoleCommand
    case class msg_popBindUserRole(data : JsValue) extends msg_BindUserRoleCommand
    case class msg_updateBindUserRole(data : JsValue) extends msg_BindUserRoleCommand
    case class msg_queryBindUserRole(data : JsValue) extends msg_BindUserRoleCommand
    case class msg_queryBindUserRoleMulti(data : JsValue) extends msg_BindUserRoleCommand

}