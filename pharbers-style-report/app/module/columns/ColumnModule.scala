package module.columns

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import module.columns.ColumnMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object ColumnModule extends ModuleTrait {
    val c: column = impl[column]
    import c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_pushColumn(data: JsValue) => ??? //pushMacro(d2m, dr, data, db_name, name)
        case msg_popColumn(data: JsValue) => ??? //popMacro(qc, popr, data, db_name)
        case msg_updateColumn(data: JsValue) => ??? //updateMacro(qc, up2m, dr, data, db_name, name)
        case msg_queryColumn(data: JsValue) =>
            processor (value => returnValue(query(value)(db_name)(qc, dr, cm), name))(data)
        case msg_queryColumnMulti(data: JsValue) =>
            processor (value => returnValue(queryMulti(value)(db_name)(qcm, dr, cm), names))(data)

        case _ => ???
    }
}
