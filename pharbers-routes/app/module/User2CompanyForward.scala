package module

import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.User2CompanyMessage._
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-7-31.
  * des: 用户公司连接功能转发
  */
abstract class msg_User2CompanyCommand extends CommonMessage("user2company", User2CompanyForward)

object User2CompanyMessage {

    case class msg_bindUserCompany(data: JsValue) extends msg_User2CompanyCommand

    case class msg_unbindUserCompany(data: JsValue) extends msg_User2CompanyCommand

    case class msg_queryUserCompanyBind(data: JsValue) extends msg_User2CompanyCommand

}

object User2CompanyForward extends ModuleTrait {

    val u2c: phForward = new phForward {
        override implicit lazy val module_name: String = "user-company"
    }

    import u2c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        // TODO to be tested
        case msg_bindUserCompany(data: JsValue) =>
            repeater((d, _) => forward("/api/user-company/bind/push").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_unbindUserCompany(data: JsValue) =>
            repeater((d, _) => forward("/api/user-company/bind/pop").post(d))(mergeResult)(data, pr)

        case msg_queryUserCompanyBind(data: JsValue) =>
            repeater((_, p) => forward("/api/user-company/bind/query").post(toJson(p.get)))(mergeResult)(data, pr)

        case _ => ???
    }

}

