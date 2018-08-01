package module.user2company

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.transform.drTrait
import module.common.stragety.bindTrait
import com.mongodb.casbah.Imports.DBObject

/**
  * Created by clock on 18-7-31.
  */
class user2company extends bindTrait with drTrait {
    val db_name = "bind_user_company"

    val bind: JsValue => DBObject = { jv =>
        val builder = MongoDBObject.newBuilder
        builder += "user_id" -> (jv \ "user" \ "id").asOpt[String].get
        builder += "company_id" -> (jv \ "company" \ "id").asOpt[String].get

        builder.result
    }

    val qcm: JsValue => DBObject = { jv =>
        $and(
            (jv \ "data" \ "condition" \ "user_id").asOpt[String] match {
                case Some(u) => DBObject("user_id" -> u)
                case None => DBObject()
            },
            (jv \ "data" \ "condition" \ "company_id").asOpt[String] match {
                case Some(c) => DBObject("company_id" -> c)
                case None => DBObject()
            }
        )
    }

}