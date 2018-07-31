package module.roles

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.checkExist.checkAttrExist
import play.api.libs.json.Json.toJson
import module.common.datamodel.basemodel

/**
  * Created by jeorch on 18-7-30.
  */
class UserRole extends basemodel
        with checkAttrExist {

    override val name = "bind_user_role"
    override def runtimeClass: Class[_] = classOf[UserRole]

    override val qc : JsValue => DBObject = { js =>
        val user_id = (js \ "data" \ "condition" \ "user_id").asOpt[String].getOrElse("")
        val role_id = (js \ "data" \ "condition" \ "role_id").asOpt[String].getOrElse("")
        $or(DBObject("user_id" -> user_id) :: DBObject("role_id" -> role_id) :: Nil)
    }

    override val anqc: JsValue => DBObject = { js =>
        val user_id = (js \ "data" \ "condition" \ "user_id").asOpt[String].getOrElse("")
        val role_id = (js \ "data" \ "condition" \ "role_id").asOpt[String].getOrElse("")
        $or(DBObject("user_id" -> user_id) :: DBObject("role_id" -> role_id) :: Nil)
    }

    override val qcm : JsValue => DBObject = { js =>
        val umap = Map("users" -> (js \ "data" \ "condition" \ "users").asOpt[List[String]].getOrElse(List.empty))
        val rmap = Map("roles" -> (js \ "data" \ "condition" \ "roles").asOpt[List[String]].getOrElse(List.empty))
        umap("users") ::: rmap("roles") match {
            case Nil => DBObject("query" -> "none")
            case _ => $and($or(umap("users") map (x => DBObject("user_id" -> x))) :: $or(rmap("roles") map (x => DBObject("role_id" -> x))) :: Nil)
        }
    }

    override val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[String]("user_id").get),
            "role_id" -> toJson(obj.getAs[String]("role_id").get)
        )
    }

    override val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[String]("user_id").get),
            "role_id" -> toJson(obj.getAs[String]("role_id").get)
        )
    }

    override val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[String]("user_id").get),
            "role_id" -> toJson(obj.getAs[String]("role_id").get)
        )
    }

    override val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop bind_user_role" -> toJson("success")
        )
    }

    override val d2m : JsValue => DBObject = { js =>
        val data = (js \ "data" \ "bind_user_role").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // bind_user_role 唯一标示
        builder += "user_id" -> (data \ "user_id").asOpt[String].getOrElse(throw new Exception("user_id was not found"))
        builder += "role_id" -> (data \ "role_id").asOpt[String].getOrElse(throw new Exception("role_id was not found"))

        builder.result
    }

    override val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "data" \ "bind_user_role").asOpt[JsValue].get

        (data \ "user_id").asOpt[String].map (x => obj += "user_id" -> x).getOrElse(Unit)
        (data \ "role_id").asOpt[String].map (x => obj += "role_id" -> x).getOrElse(Unit)

        obj
    }

    override val ckAttrExist: JsValue => DBObject = { jv =>
        $and(
            DBObject("user_id" -> (jv \ "data" \ "bind_user_role" \ "user_id").asOpt[String].get),
            DBObject("role_id" -> (jv \ "data" \ "bind_user_role" \ "role_id").asOpt[String].get)
        )
    }
}
