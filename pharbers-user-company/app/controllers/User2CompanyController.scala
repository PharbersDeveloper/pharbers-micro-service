package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.libs.json.Json.toJson
import controllers.common.requestArgsQuery
import module.user2company.User2CompanyMessage._
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

/**
  * Created by clock on 18-7-29.
  */
class User2CompanyController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager) extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def bind = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("bind user company"))), jv)
                :: msg_bindUserCompany(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def unbind = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("unbind user company"))), jv)
                :: msg_unbindUserCompany(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryCompanyByUser = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query company by user"))), jv)
                :: msg_queryCompanyByUser(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryUsersByCompany = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query user by company"))), jv)
                :: msg_queryUsersByCompany(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

}
