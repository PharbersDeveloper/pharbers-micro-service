package module.which

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.which.WhichMessage._
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object WhichModule extends ModuleTrait {
    val c: which = impl[which]
    import c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_pushWhich(data: JsValue) => ??? //pushMacro(d2m, dr, data, db_name, name)
        case msg_popWhich(data: JsValue) => ??? //popMacro(qc, popr, data, db_name)
        case msg_updateWhich(data: JsValue) => ??? //updateMacro(qc, up2m, dr, data, db_name, name)
        case msg_queryWhich(data: JsValue) =>
            processor (value => returnValue(query(value)(db_name)(qc, dr, cm), name))(data)
        case msg_queryWhichMulti(data: JsValue) =>
            processor (value => returnValue(queryMulti(value)(db_name)(qcm, dr, cm), names))(data)

        case _ => ???
    }
}
