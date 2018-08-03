package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.libs.json.Json.toJson
import module.resources.ProposalMessage._
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

/**
  * Created by jeorch on 18-8-3.
  */
class ProposalController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager) extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def pushProposal= Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("push proposal"))), jv)
                :: msg_verifyProposalRegister(jv)
                :: msg_pushProposal(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def popProposal= Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("pop proposal"))), jv)
                :: msg_popProposal(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def updateProposal= Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("update proposal"))), jv)
                :: msg_updateProposal(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryProposal= Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query proposal"))), jv)
                :: msg_queryProposal(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryProposalsMulti = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query multi proposals"))), jv)
                :: msg_queryProposalsMulti(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

}
