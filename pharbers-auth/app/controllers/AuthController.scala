package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import module.auth.AuthMessage._
import play.api.libs.json.Json.toJson
import controllers.common.requestArgsQuery
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

/**
  * Created by clock on 18-7-27.
  */
class AuthController @Inject()(implicit cc: ControllerComponents, as_inject: ActorSystem,
                               dbt: dbInstanceManager, rd: PhRedisDriverImpl)
        extends AbstractController(cc) {

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def encryptToken = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("encrypt token"))), jv)
                :: msg_authEncryptFilter(jv)
                :: msg_authSetExpire(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "rd" -> rd))))
    })

    def parseToken = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("parse token"))), jv)
                :: msg_authParseToken(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "rd" -> rd))))
    })
}
