package controllers

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import controllers.common.JsonapiAdapter.msg_JsonapiAdapter
import controllers.common.requestArgsQuery
import javax.inject.Inject
import module.AuthMessage.msg_tokenParse
import module.ProposalMessage.{msg_queryProposalByBind, msg_queryScenarioByProposal}
import module.User2PrpodsalMessage.msg_queryUserProposalBind
import play.api.libs.json.Json.toJson
import play.api.mvc.{AbstractController, ControllerComponents}
import scenario.ScenarioMessage.msg_queryMultiScenario

class ProposalController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager) extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result
    import controllers.common.JsonapiAdapter.jsonapi_adapter

    def queryProposalLst = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query proposal list"))), jv)
                :: msg_tokenParse(jv)
                :: msg_queryUserProposalBind(jv)
                :: msg_queryProposalByBind(jv)
                :: msg_queryScenarioByProposal(jv)
                :: msg_queryMultiScenario(jv)
//                :: msg_JsonapiAdapter(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map())))
    })
}
