package module.hosp

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-8-2.
  */
abstract class msg_HospCommand extends CommonMessage("hosp", HospModule)

object HospMessage {

    case class msg_pushHosp(data: JsValue) extends msg_HospCommand

    case class msg_popHosp(data: JsValue) extends msg_HospCommand

    case class msg_updateHosp(data: JsValue) extends msg_HospCommand

    case class msg_queryHosp(data: JsValue) extends msg_HospCommand

    case class msg_queryHospMulti(data: JsValue) extends msg_HospCommand

}