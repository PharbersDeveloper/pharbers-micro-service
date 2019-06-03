package module

import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.User2RoleMessage._
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-1.
  * des: 用户角色连接功能转发
  */
abstract class msg_User2RoleCommand extends CommonMessage("user2role", User2RoleForward)

object User2RoleMessage {

    case class msg_bindUserRole(data: JsValue) extends msg_User2RoleCommand

    case class msg_unbindUserRole(data: JsValue) extends msg_User2RoleCommand

    case class msg_queryUserRolesBind(data: JsValue) extends msg_User2RoleCommand

}

object User2RoleForward extends ModuleTrait {

    val u2r: phForward = new phForward {
        override implicit lazy val module_name: String = "user-role"
    }

    import u2r._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        // TODO to be tested
        case msg_bindUserRole(data: JsValue) =>
            repeater((d, _) => forward("/api/user-role/bind/push").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_unbindUserRole(data: JsValue) =>
            repeater((d, _) => forward("/api/user-role/bind/pop").post(d))(mergeResult)(data, pr)

        case msg_queryUserRolesBind(data: JsValue) =>
            repeater((_, p) => forward("/api/user-role/bind/query").post(toJson(p.get)))(mergeResult)(data, pr)

        case _ => ???
    }

}

