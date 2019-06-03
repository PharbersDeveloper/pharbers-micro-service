package module.column

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-7-29.
  */
abstract class msg_ColumnCommand extends CommonMessage("column", ColumnModule)

// 铁马周期报告,表头样式
object ColumnMessage {
    case class msg_pushColumn(data: JsValue) extends msg_ColumnCommand
    case class msg_popColumn(data: JsValue) extends msg_ColumnCommand
    case class msg_updateColumn(data: JsValue) extends msg_ColumnCommand
    case class msg_queryColumn(data: JsValue) extends msg_ColumnCommand
    case class msg_queryColumnMulti(data: JsValue) extends msg_ColumnCommand
}