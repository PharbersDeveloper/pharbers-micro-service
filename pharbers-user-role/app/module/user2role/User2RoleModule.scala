package module.user2role

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import module.user2role.User2RoleMessage._
import com.pharbers.pharbersmacro.CURDMacro.queryMultiMacro
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by clock on 18-7-31.
  */
object User2RoleModule extends ModuleTrait {
    val uc: user2role = impl[user2role]
    import uc._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_bindUserRole(data: JsValue) =>
            processor(value => returnValue(bindConnection(bind)(value)(db_name)))(data)
        case msg_unbindUserRole(data: JsValue) =>
            processor(value => returnValue(unbindConnection(qcm)(value)(db_name)))(data)
        case msg_queryUserRoleBind(data: JsValue) =>
            queryMultiMacro(qcm, cdr, data, db_name, "binds")

        case _ => ???
    }
}
