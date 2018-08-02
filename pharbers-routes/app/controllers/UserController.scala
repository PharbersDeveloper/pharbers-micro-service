package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.libs.json.Json.toJson
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import controllers.common.JsonapiAdapter.msg_JsonapiAdapter
import module.AuthMessage.msg_TokenEncrypt
import module.CompanyMessage.msg_queryCompanyByBind
import module.RoleMessage.msg_queryRoleByBind
import module.User2CompanyMessage.msg_queryUserCompanyBind
import module.User2RoleMessage.msg_queryUserRolesBind
import module.UserMessage.msg_userWithPassword

class UserController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem) extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result
    import controllers.common.JsonapiAdapter.jsonapi_adapter

    def userLogin = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("user login"))), jv)
                :: msg_userWithPassword(jv)
                :: msg_queryUserCompanyBind(jv)
                :: msg_queryCompanyByBind(jv)
                :: msg_queryUserRolesBind(jv)
                :: msg_queryRoleByBind(jv)
                :: msg_TokenEncrypt(jv)
                :: msg_JsonapiAdapter(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map())))
    })
}
