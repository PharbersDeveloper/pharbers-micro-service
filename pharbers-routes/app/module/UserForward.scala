package module

import module.UserMessage._
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-7-27.
  * des: 用户功能转发
  */
abstract class msg_UserCommand extends CommonMessage("user", UserModule)

object UserMessage {

    case class msg_userWithPassword(data: JsValue) extends msg_UserCommand

}

object UserModule extends ModuleTrait {

    val user: phForward = new phForward {
        override implicit lazy val module_name: String = "user"
    }

    import user._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_userWithPassword(data: JsValue) =>
            repeater((d, _) => forward("/api/user/pwd").post(d))(mergeResult)(data, pr)

        case _ => ???
    }

}

