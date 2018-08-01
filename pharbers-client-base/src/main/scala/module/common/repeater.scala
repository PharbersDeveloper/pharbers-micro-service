package module.common

import com.pharbers.ErrorCode._
import play.api.libs.json.JsValue

object repeater {

    def apply(in_func: (JsValue, Option[Map[String, JsValue]]) => JsValue)
             (out_func: (JsValue, Option[Map[String, JsValue]]) => (Option[Map[String, JsValue]], Option[JsValue]))
             (data: JsValue, pr: Option[Map[String, JsValue]])
             (implicit name: String = "function"): (Option[Map[String, JsValue]], Option[JsValue]) =
        try {
            val result = checkResult(in_func(data, pr))
            out_func(result, pr)
        } catch {
            case ex: Exception =>
                println(s"$name error=${ex.getMessage}")
                (None, Some(errorToJson(ex.getMessage)))
        }

    def checkResult(jv: JsValue): JsValue =
        if ((jv \ "status").as[String] == "ok") jv else throw new Exception(getErrorNameByCode((jv \ "error" \ "code").as[Int]))

    val onlyResult: (JsValue, Option[Map[String, JsValue]]) => (Option[Map[String, JsValue]], Option[JsValue]) =
        (r, _) => (Some((r \ "result").as[Map[String, JsValue]]), None)

    val onlyPrResult: (JsValue, Option[Map[String, JsValue]]) => (Option[Map[String, JsValue]], Option[JsValue]) = {
        (_, p) =>
            val tmp = p.get.get("result") match {
                case None => Map[String, JsValue]()
                case Some(x) => x.as[Map[String, JsValue]]
            }
            (Some(tmp), None)
    }

    val mergeResult: (JsValue, Option[Map[String, JsValue]]) => (Option[Map[String, JsValue]], Option[JsValue]) =
        (r, p) => (Some(p.get ++ (r \ "result").as[Map[String, JsValue]] - "status"), None)

}