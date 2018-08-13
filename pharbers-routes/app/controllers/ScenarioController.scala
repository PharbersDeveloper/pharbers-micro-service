package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import module.ScenarioMessage._
import play.api.libs.json.Json.toJson
import module.AuthMessage.msg_tokenParse
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.ReportMessage.msg_queryReportByCurrent
import controllers.common.JsonapiAdapter.msg_JsonapiAdapter
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import module.ProposalMessage.{msg_queryProposal, msg_queryProposalByScenario}
import module.RepMessage.{msg_formatRepInfo, msg_queryMultiRepByScenario}

class ScenarioController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager) extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result
    import controllers.common.JsonapiAdapter.jsonapi_adapter

    def createSecnario = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("create new scenario"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryProposal(jv) ::
                msg_pushScenario(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryBudgetProgress = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query budget info"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryScenarioDetails(jv) :::
                msg_queryBudgetProgress(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryRepInfo = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query rep info"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryScenario(jv) ::
                msg_queryMultiRepByScenario(jv) ::
                msg_formatRepInfo(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryHospitalLst = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query hospital list"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryScenarioDetails(jv) :::
                msg_queryQueryHospLst(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryHospitalDetail = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query hospital detail"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryScenarioDetails(jv) :::
                msg_queryHospitalDetail(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def allotTask = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("allot task"))), jv) ::
                msg_tokenParse(jv) ::
                msg_allotTask(jv) ::
                msg_managerTask(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def createPhase = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("create new phase"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryScenario(jv) ::
                msg_queryReportByCurrent(jv) ::
                msg_queryProposalByScenario(jv) ::
                msg_createPhase(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })
}
