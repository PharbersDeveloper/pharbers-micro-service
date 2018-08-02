package module.user2role

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.transform.drTrait
import module.common.stragety.bindTrait
import com.mongodb.casbah.Imports.DBObject

/**
  * Created by clock on 18-7-31.
  */
class user2role extends bindTrait with drTrait {
    val db_name = "bind_user_role"

    val bind: JsValue => DBObject = { jv =>
        val builder = MongoDBObject.newBuilder
        builder += "user_id" -> (jv \ "user" \ "id").asOpt[String].get
        builder += "role_id" -> (jv \ "role" \ "id").asOpt[String].get

        builder.result
    }

    val qcm: JsValue => DBObject = { jv =>
        $and(
            (jv \ "data" \ "condition" \ "user_id").asOpt[String] match {
                case Some(u) => DBObject("user_id" -> u)
                case None => DBObject()
            },
            (jv \ "data" \ "condition" \ "role_id").asOpt[String] match {
                case Some(c) => DBObject("role_id" -> c)
                case None => DBObject()
            }
        )
    }

}