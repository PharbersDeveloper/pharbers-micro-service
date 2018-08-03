package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import scenario.ScenarioMessage._
import play.api.libs.json.Json.toJson
import module.AuthMessage.msg_tokenParse
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import controllers.common.JsonapiAdapter.msg_JsonapiAdapter
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

class ScenarioController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager) extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result
    import controllers.common.JsonapiAdapter.jsonapi_adapter

    def queryHospitalLst = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query hospital list"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryScenarioDetails(jv) :::
                msg_formatQueryHospLst(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryBudgetInfo = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query budget info"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryScenarioDetails(jv) :::
                msg_formatQueryBudget(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryhumansInfo = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query humans info"))), jv) ::
                msg_tokenParse(jv) ::
                msg_queryScenarioDetails(jv) :::
                msg_formatQueryHumans(jv) ::
                msg_JsonapiAdapter(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })
}
