package module.user2company

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports.DBObject
import module.common.stragety.one2oneTrait

/**
  * Created by clock on 18-7-30.
  */
class user2company extends one2oneTrait {
    override val ts_name: String = "user"
    override val ta_name: String = "company"

    override val bind_qc: Map[String, JsValue] => DBObject = map =>
        DBObject("user_id" -> map("id").asOpt[String].get)


    override val bind_ssr: DBObject => Map[String, JsValue] = { obj =>
        Map("data" -> toJson(
            Map("condition" -> toJson(
                Map("company_id" -> toJson(obj.getAs[String]("company_id").get))
            ))
        ))
    }
}