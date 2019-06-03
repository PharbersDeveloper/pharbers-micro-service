package module

import module.RoleMessage._
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-1.
  * des: 角色功能转发
  */
abstract class msg_RoleCommand extends CommonMessage("role", RoleModule)

object RoleMessage {

    case class msg_pushRole(data: JsValue) extends msg_RoleCommand

    case class msg_popRole(data: JsValue) extends msg_RoleCommand

    case class msg_updateRole(data: JsValue) extends msg_RoleCommand

    case class msg_queryRole(data: JsValue) extends msg_RoleCommand

    case class msg_queryRoleMultiByBind(data: JsValue) extends msg_RoleCommand

    case class msg_queryRoleMulti(data: JsValue) extends msg_RoleCommand

}

object RoleModule extends ModuleTrait {
    val r = role()
    import r._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        // TODO to be tested
        case msg_pushRole(data: JsValue) =>
            repeater((d, _) => forward("/api/role/push").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_popRole(data: JsValue) =>
            repeater((d, _) => forward("/api/role/pop").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_updateRole(data: JsValue) =>
            repeater((d, _) => forward("/api/role/update").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_queryRole(data: JsValue) =>
            repeater((d, p) => forward("/api/role/query").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_queryRoleMulti(data: JsValue) =>
            repeater((d, _) => forward("/api/role/query/multi").post(d))(mergeResult)(data, pr)

        case msg_queryRoleMultiByBind(data: JsValue) =>
            repeater((_, p) => forward("/api/role/query/multi").post(mergeCond(p)))(mergeResult)(data, pr)

        case _ => ???
    }

}

case class role() extends phForward {
    override lazy val module_name: String = "role"

    def mergeCond: Option[Map[String, JsValue]] => JsValue = { p =>
        val ids = p.get("binds").as[List[Map[String, JsValue]]].map(_("role_id"))

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("roles"),
                "condition" -> toJson(Map(
                    "roles" -> toJson(ids)
                ))
            ))
        ))
    }
}