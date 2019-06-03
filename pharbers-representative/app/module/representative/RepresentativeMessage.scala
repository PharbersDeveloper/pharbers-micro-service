package module.representative

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-8-2.
  */
abstract class msg_RepresentativeCommand extends CommonMessage("representative", RepresentativeModule)

object RepresentativeMessage {

    case class msg_pushRepresentative(data: JsValue) extends msg_RepresentativeCommand

    case class msg_popRepresentative(data: JsValue) extends msg_RepresentativeCommand

    case class msg_updateRepresentative(data: JsValue) extends msg_RepresentativeCommand

    case class msg_queryRepresentative(data: JsValue) extends msg_RepresentativeCommand

    case class msg_queryMultiRepresentative(data: JsValue) extends msg_RepresentativeCommand

}