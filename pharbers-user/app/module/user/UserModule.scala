package module.user

import module.common.processor
import module.common.processor._
import module.user.UserMessage._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object UserModule extends ModuleTrait {
    val u: user = impl[user]
    import u._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_verifyUserRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "user email has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushUser(data) => pushMacro(d2m, dr, data, names, name)
        case msg_popUser(data: JsValue) => popMacro(qc, popr, data, names)
        case msg_updateUser(data: JsValue) => updateMacro(qc, up2m, dr, data, names, name)
        case msg_queryUser(data: JsValue) => queryMacro(qc, dr, data, names, name)
        case msg_queryUserMulti(data: JsValue) => queryMultiMacro(qcm, dr, data, names, names)
        case msg_userAuthPwd(data: JsValue) => processor(value => returnValue(authWithPassword(authPwd, dr)(value)(names), name))(data)

        case _ => ???
    }
}
