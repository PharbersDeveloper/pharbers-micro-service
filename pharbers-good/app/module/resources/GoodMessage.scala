package module.resources

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by jeorch on 18-8-2.
  */
abstract class msg_GoodCommand extends CommonMessage("good", GoodModule)

object GoodMessage {
    case class msg_verifyGoodRegister(data: JsValue) extends msg_GoodCommand
    case class msg_pushGood(data: JsValue) extends msg_GoodCommand
    case class msg_popGood(data: JsValue) extends msg_GoodCommand
    case class msg_updateGood(data: JsValue) extends msg_GoodCommand
    case class msg_queryGood(data: JsValue) extends msg_GoodCommand
    case class msg_queryGoodsMulti(data: JsValue) extends msg_GoodCommand
}