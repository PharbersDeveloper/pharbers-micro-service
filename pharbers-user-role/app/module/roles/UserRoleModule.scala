package module.roles

import module.roles.RoleMessage._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import module.common.processor
import module.common.processor._

/**
  * Created by jeorch on 18-7-30.
  */
object UserRoleModule extends ModuleTrait {
    val ur: UserRole = impl[UserRole]
    import ur._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_verifyBindUserRoleRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "bind_user_role info has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushBindUserRole(data) => pushMacro(d2m, dr, data, name, name)
        case msg_popBindUserRole(data) => popMacro(qc, popr, data, name)
        case msg_updateBindUserRole(data) => updateMacro(qc, up2m, dr, data, name, name)
        case msg_queryBindUserRole(data) => queryMacro(qc, dr, data, name, name)
        case msg_queryBindUserRoleMulti(data) => queryMultiMacro(qcm, dr, data, name, names)

        case _ => ???
    }

}
