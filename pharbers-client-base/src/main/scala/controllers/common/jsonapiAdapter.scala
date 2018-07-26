package controllers.common

import java.util.Date
import com.pharbers.ErrorCode
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsValue, Json}
import com.pharbers.bmpattern.ModuleTrait
import controllers.common.JsonapiAdapter.msg_JsonapiAdapter
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

object JsonapiAdapter {
    implicit val jsonapi_adapter : (JsValue, Option[Map[String, JsValue]]) => (Option[Map[String, JsValue]], Option[JsValue]) = (jv, pr) => {

        val major = (jv \ "version" \ "major").asOpt[Int].get
        val minor = (jv \ "version" \ "minor").asOpt[Int].get

        val `type` = (jv \ "data" \ "type").asOpt[String].get
        val `data` = pr.get.values.head
        (Some(Map(
            "timestamp" -> toJson(new Date().getTime),
            "version" -> toJson(Map(
                "major" -> toJson(major), "minor" -> toJson(minor)
            )),
            "data" -> toJson(Map(
                "type" -> toJson(`type`),
                "attribute" -> toJson(`data`)
            ))
        )), None)
    }

    abstract class msg_JsonapiCommand extends CommonMessage("jsonapiAdapt", JsonapiAdapterModule)
    case class msg_JsonapiAdapter(jv: JsValue)(implicit val fun : (JsValue, Option[Map[String, JsValue]]) => (Option[Map[String, JsValue]], Option[JsValue])) extends msg_JsonapiCommand
}

object JsonapiAdapterModule extends ModuleTrait {
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case cmd : msg_JsonapiAdapter => cmd.fun(cmd.jv, pr)
        case _ => (None, Some(ErrorCode.errorToJson("can not parse result")))
    }
}