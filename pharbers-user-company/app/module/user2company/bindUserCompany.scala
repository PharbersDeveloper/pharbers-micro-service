package module.user2company

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports.{DBObject, MongoDBObject}
import module.common.stragety.bindTrait

/**
  * Created by clock on 18-7-30.
  */
class bindUserCompany extends bindTrait {

    override val bind: JsValue => DBObject = { jv =>
        val builder = MongoDBObject.newBuilder
        builder += "user_id" -> (jv \ "user" \ "id").asOpt[String].get
        builder += "company_id" -> (jv \ "company" \ "id").asOpt[String].get

        builder.result
    }

    override val unbind: JsValue => DBObject = { jv =>
        val builder = MongoDBObject.newBuilder
        val _id = (jv \ "data" \ "condition" \ "bind_id").asOpt[String].get
        builder += "_id" -> new ObjectId(_id)

        builder.result
    }

}