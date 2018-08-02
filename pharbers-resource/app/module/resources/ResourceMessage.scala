package module.resources

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by jeorch on 18-8-2.
  */
abstract class msg_ResourceCommand extends CommonMessage("resource", ResourceModule)

// 公司
object ResourceMessage {
    case class msg_verifyResourceRegister(data: JsValue) extends msg_ResourceCommand
    case class msg_pushResource(data: JsValue) extends msg_ResourceCommand
    case class msg_popResource(data: JsValue) extends msg_ResourceCommand
    case class msg_updateResource(data: JsValue) extends msg_ResourceCommand
    case class msg_queryResource(data: JsValue) extends msg_ResourceCommand
    case class msg_queryResourceMulti(data: JsValue) extends msg_ResourceCommand
}