package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.libs.json.Json.toJson
import controllers.common.requestArgsQuery
import module.user2role.User2ProposalMessage._
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

/**
  * Created by jeorch on 18-8-3.
  */
class User2ProposalController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager) extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def bind = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("bind user proposal"))), jv)
                :: msg_bindUserProposal(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def unbind = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("unbind user proposal"))), jv)
                :: msg_unbindUserProposal(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryBind = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query user proposal connection"))), jv)
                :: msg_queryUserProposalBind(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

}
