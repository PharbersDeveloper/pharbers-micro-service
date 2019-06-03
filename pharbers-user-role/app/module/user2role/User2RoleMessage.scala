package module.user2role

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-7-31.
  */
abstract class msg_User2RoleCommand extends CommonMessage("user2role", User2RoleModule)

// 用户角色关系
object User2RoleMessage {

    case class msg_bindUserRole(data: JsValue) extends msg_User2RoleCommand

    case class msg_unbindUserRole(data: JsValue) extends msg_User2RoleCommand

    case class msg_queryUserRoleBind(data: JsValue) extends msg_User2RoleCommand

}