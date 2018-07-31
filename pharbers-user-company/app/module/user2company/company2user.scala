package module.user2company

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.stragety.one2manyTrait
import play.api.libs.json.Json.toJson

/**
  * Created by clock on 18-7-30.
  */
class company2user extends one2manyTrait {
    override val ts_name: String = "company"
    override val ta_name: String = "user"

    override val bind_qc: Map[String, JsValue] => DBObject = map =>
        DBObject("company_id" -> map("id").asOpt[String].get)


    override val bind_ssr: DBObject => Map[String, JsValue] = { obj =>
        Map("id" -> toJson(obj.getAs[String]("user_id").get))
    }

    override val queryCondition: List[Map[String, JsValue]] => JsValue = { lst =>
        toJson(Map("data" -> toJson(
            Map("condition" -> toJson(
                Map("users" -> toJson(lst.map(_ ("id").as[String])))
            ))
        )))
    }
}