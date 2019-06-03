package module.which

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-7-29.
  */
abstract class msg_WhichCommand extends CommonMessage("which", WhichModule)

// 铁马周期报告,表头样式
object WhichMessage {
    case class msg_pushWhich(data: JsValue) extends msg_WhichCommand
    case class msg_popWhich(data: JsValue) extends msg_WhichCommand
    case class msg_updateWhich(data: JsValue) extends msg_WhichCommand
    case class msg_queryWhich(data: JsValue) extends msg_WhichCommand
    case class msg_queryWhichMulti(data: JsValue) extends msg_WhichCommand
}