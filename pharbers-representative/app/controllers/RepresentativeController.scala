package controllers

import play.api.mvc._
import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.libs.json.Json.toJson
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import module.representative.RepresentativeMessage._
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

/**
  * Created by clock on 18-8-2.
  */
class RepresentativeController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem, dbt: dbInstanceManager)
        extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def pushRepresentative() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("push new representative"))), jv)
                    :: msg_pushRepresentative(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def popRepresentative() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("pop representative"))), jv)
                    :: msg_popRepresentative(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def updateRepresentative() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("update representative"))), jv)
                    :: msg_updateRepresentative(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryRepresentative() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query representative"))), jv)
                    :: msg_queryRepresentative(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

    def queryMultiRepresentative() = Action { request =>
        requestArgsQuery().requestArgs(request) { jv =>
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("query multi representative"))), jv)
                    :: msg_queryMultiRepresentative(jv)
                    :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt))))
        }
    }

}