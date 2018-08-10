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
import module.ReportMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{AbstractController, ControllerComponents}

/**
  * @ ProjectName pharbers-routes.controllers.ReportController
  * @ author jeorch
  * @ date 18-8-6
  * @ Description: TODO
  */
class ReportController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager) extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result
    import controllers.common.JsonapiAdapter.jsonapi_adapter

    def queryTotalReport = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query report total"))), jv)
            :: msg_tokenParse(jv)
            :: msg_queryReport(jv)
            :: msg_formatTotalReport(jv)
            :: msg_JsonapiAdapter(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryDestsGoodsReport = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query report DestsGoods"))), jv)
            :: msg_tokenParse(jv)
            :: msg_queryReport(jv)
            :: msg_formatDestsGoodsReport(jv)
            :: msg_JsonapiAdapter(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryRepGoodsReport = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query report RepGoods"))), jv)
            :: msg_tokenParse(jv)
            :: msg_queryReport(jv)
            :: msg_formatRepGoodsReport(jv)
            :: msg_JsonapiAdapter(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryResoAllocation = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query ResoAllocation"))), jv)
            :: msg_tokenParse(jv)
            :: msg_queryReport(jv)
            :: msg_formatResoAllocation(jv)
            :: msg_JsonapiAdapter(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryRepIndResos = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query RepIndResos"))), jv)
            :: msg_tokenParse(jv)
            :: msg_queryReport(jv)
            :: msg_formatRepIndResos(jv)
            :: msg_JsonapiAdapter(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def queryAssessReport = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query assess report"))), jv)
            :: msg_tokenParse(jv)
            :: msg_queryAssessReport(jv)
            :: msg_formatAssessReport(jv)
            :: msg_JsonapiAdapter(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })
}
