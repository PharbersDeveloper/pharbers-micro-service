package module.user

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-7-29.
  */
abstract class msg_UserCommand extends CommonMessage("user", UserModule)

// 用户
object UserMessage {
    case class msg_verifyUserRegister(data: JsValue) extends msg_UserCommand
    case class msg_pushUser(data: JsValue) extends msg_UserCommand
    case class msg_popUser(data: JsValue) extends msg_UserCommand
    case class msg_updateUser(data: JsValue) extends msg_UserCommand
    case class msg_queryUser(data: JsValue) extends msg_UserCommand
    case class msg_queryUserMulti(data: JsValue) extends msg_UserCommand
    case class msg_userAuthPwd(data: JsValue) extends msg_UserCommand
}