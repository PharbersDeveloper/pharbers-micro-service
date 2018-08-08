package controllers

import play.api.mvc._
import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.libs.json.Json.toJson
import module.scenario.ScenarioMessage._
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

/**
  * Created by clock on 18-8-7.
  */
class ScenarioController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager)
        extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def pushScenario() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("push new scenario"))), jv)
                    :: msg_pushScenario(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def popScenario() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("pop scenario"))), jv)
                    :: msg_popScenario(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryScenario() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query scenario"))), jv)
                    :: msg_queryScenario(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryScenarioByUUID() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query scenario by UUID"))), jv)
                    :: msg_queryScenarioByUUID(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryMultiScenario() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query multi scenario"))), jv)
                    :: msg_queryMultiScenario(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryMultiScenarioByUserAndProposal() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query multi scenario by user and proposal"))), jv)
                    :: msg_queryMultiScenarioByUserAndProposal(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryBudgetProgress = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query budget info in scenario"))), jv)
                    :: msg_queryBudgetProgress(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryHospitalLst = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query hospital list in scenario"))), jv)
                    :: msg_queryHospitalList(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryHospitalDetail = Action{request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query hospital detail in scenario"))), jv)
                    :: msg_queryHospitalDetail(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def allotTask = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("allot task in scenario"))), jv) ::
                msg_updateRepTask(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })

    def createPhase = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("create new phase in scenario"))), jv) ::
                msg_report2current(jv) ::
                msg_current2past(jv) ::
                msg_proposal2current(jv) ::
                msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
    })
}