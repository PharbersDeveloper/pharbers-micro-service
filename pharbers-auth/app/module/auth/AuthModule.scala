package module.auth

import module.common.processor
import module.common.processor._
import module.auth.AuthMessage._
import play.api.libs.json.JsValue
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object AuthModule extends ModuleTrait {
    val auth: authTrait = new authTrait {}
    import auth._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_authEncryptFilter(data: JsValue) =>
            processor(value => returnValue(encryptFilter(value)))(data)
        case msg_authSetExpire(data: JsValue) =>
            processor(_ => returnValue(setExpire(pr)))(data)
        case msg_authParseToken(data: JsValue) =>
            processor(value => returnValue(parseExpire(value)))(data)

        case _ => ???
    }
}
