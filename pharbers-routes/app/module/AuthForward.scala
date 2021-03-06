package module

import module.AuthMessage._
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-7-27.
  * des: 权限功能转发
  */
abstract class msg_AuthCommand extends CommonMessage("auth", AuthModule)

object AuthMessage {
    case class msg_TokenEncrypt(data: JsValue) extends msg_AuthCommand
    case class msg_tokenParse(data: JsValue) extends msg_AuthCommand
}

object AuthModule extends ModuleTrait {
    val auth: phForward = new phForward { override lazy val module_name: String = "auth" }
    import auth._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_TokenEncrypt(data: JsValue) =>
            repeater((_, p) => forward("/api/auth/encrypt").post(toJson(p)))(onlyResult)(data, pr)

        case msg_tokenParse(data: JsValue) =>
            repeater((d, _) => forward("/api/auth/parse").post(d))(onlyResult)(data, pr)

        case _ => ???
    }
}

