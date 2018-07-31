package module.common.stragety

import com.mongodb.DBObject
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.common.forward.phForward
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsObject, JsValue}

trait one2oneTrait {
    val ts_name: String
    val ta_name: String

    val bind_qc: Map[String, JsValue] => DBObject
    val bind_ssr: DBObject => Map[String, JsValue]

    def queryConnection(data: JsValue, primary_key: String = "_id")
                       (connect: String)
                       (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        val pr = (data \ ts_name).asOpt[Map[String, JsValue]]

        val ta = pr match {
            case None => throw new Exception("data not exist")
            case Some(ts) =>
                if (ts.isEmpty) throw new Exception("data not exist")

                db.queryObject(bind_qc(ts), connect)(bind_ssr) match {
                    case None => Map().empty
                    case Some(ta_cond) =>
                        val ta_module: phForward = new phForward { override lazy val module_name: String = ta_name }
                        val ta_result = ta_module.forward(s"/api/$ta_name/query").post(toJson(ta_cond))
                        (ta_result \ "result").asOpt[Map[String, JsValue]] match {
                            case None => Map.empty
                            case Some(x) => x
                        }
                }
        }

        data.as[JsObject].value.toMap ++ ta
    }
}
