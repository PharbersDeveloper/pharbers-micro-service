package module.auth

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by clock on 18-7-26.
  */
abstract class msg_AuthCommand extends CommonMessage("auth", AuthModule)

// 权限
object AuthMessage {

    case class msg_authEncryptFilter(data: JsValue) extends msg_AuthCommand
    case class msg_authSetExpire(data: JsValue) extends msg_AuthCommand
    case class msg_authParseToken(data: JsValue) extends msg_AuthCommand

}