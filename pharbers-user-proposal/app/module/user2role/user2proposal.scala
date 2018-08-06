package module.user2role

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.transform.drTrait
import module.common.stragety.bindTrait
import com.mongodb.casbah.Imports.DBObject

/**
  * Created by jeorch on 18-8-3.
  */
class user2proposal extends bindTrait with drTrait {
    val db_name = "bind_user_proposal"

    val bind: JsValue => DBObject = { jv =>
        val builder = MongoDBObject.newBuilder
        builder += "user_id" -> (jv \ "user" \ "id").asOpt[String].get
        builder += "proposal_id" -> (jv \ "proposal" \ "id").asOpt[String].get

        builder.result
    }

    val qcm: JsValue => DBObject = { jv =>
        $and(
            (jv \ "data" \ "condition" \ "user_id").asOpt[String] match {
                case Some(u) => DBObject("user_id" -> u)
                case None => DBObject()
            },
            (jv \ "data" \ "condition" \ "proposal_id").asOpt[String] match {
                case Some(c) => DBObject("proposal_id" -> c)
                case None => DBObject()
            }
        )
    }

}