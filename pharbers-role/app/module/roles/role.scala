package module.roles

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.checkExist.checkAttrExist
import play.api.libs.json.Json.toJson
import module.common.datamodel.basemodel

/**
  * Created by clock on 18-7-6.
  */
class role extends basemodel
        with checkAttrExist {

    override val name = "role"
    override def runtimeClass: Class[_] = classOf[role]

    override val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "role_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    override val anqc: JsValue => DBObject = { js =>
        val tmp = (js \ "role" \ "role_id").asOpt[String].get
        DBObject("role_id" -> tmp)
    }

    override val qcm : JsValue => DBObject = { js =>
        (js \ "role" \ "roles").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("role_name" -> x)))
        }
    }

    override val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "role_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    override val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "role_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "role_name" -> toJson(obj.getAs[String]("role_name").get),
            "role_level" -> toJson(obj.getAs[Int]("role_level").get)
        )
    }

    override val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "role_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "role_name" -> toJson(obj.getAs[String]("role_name").get),
            "role_des" -> toJson(obj.getAs[String]("role_des").get),
            "role_level" -> toJson(obj.getAs[Int]("role_level").get)
        )
    }

    override val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop role" -> toJson("success")
        )
    }

    override val d2m : JsValue => DBObject = { js =>
        val data = (js \ "data" \ "role").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // role_id 唯一标示
        builder += "role_name" -> (data \ "role_name").asOpt[String].map (x => x).getOrElse("")
        builder += "role_des" -> (data \ "role_des").asOpt[String].map (x => x).getOrElse("")
        builder += "role_level" -> (data \ "role_level").asOpt[Int].map (x => x).getOrElse(9)

        builder.result
    }

    override val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "data" \ "role").asOpt[JsValue].get

        (data \ "role_name").asOpt[String].map (x => obj += "role_name" -> x).getOrElse(Unit)
        (data \ "role_des").asOpt[String].map (x => obj += "role_des" -> x).getOrElse(Unit)
        (data \ "role_level").asOpt[Int].map (x => obj += "role_level" -> int2Integer(x)).getOrElse(Unit)

        obj
    }

    override val ckAttrExist: JsValue => DBObject = { jv =>
        $or(
            DBObject("role_name" -> (jv \ "data" \ "role" \ "role_name").asOpt[String].get),
            DBObject("role_level" -> (jv \ "data" \ "role" \ "role_level").asOpt[Int].get)
        )
    }
}
