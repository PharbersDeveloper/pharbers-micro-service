package module.user

import org.bson.types.ObjectId
import module.common.transform._
import com.mongodb.casbah.Imports
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.common.datamodel.basemodel
import module.common.checkExist.checkAttrExist

/**
  * Created by spark on 18-4-19.
  */
class user extends basemodel
        with drTrait with d2mTrait with up2mTrait
        with checkAttrExist with authTrait {

    override val name = "user"
    override def runtimeClass: Class[_] = classOf[user]

    // id 查询
    override val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "user_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    // 不要了
    override val anqc: JsValue => DBObject = _ => DBObject()

    // 查询多个
    override val qcm : JsValue => DBObject = { js =>
        (js \ "data" \ "condition" \ "users").asOpt[List[String]] match {
            case None => DBObject("query" -> "none")
            case Some(ll) => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    // 极简返回结果
    override val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    // 不要了
    override val sr : DBObject => Map[String, JsValue] = _ => Map()

    // 详细返回结果
    override val dr: Imports.DBObject => Map[String, JsValue] = cdr

    // 删除数据规则
    override val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop user" -> toJson("success")
        )
    }

    // 新增数据规则
    override val d2m : JsValue => DBObject = { js =>
        val data = (js \ "data" \ "user").asOpt[JsValue].map (x => x).getOrElse(toJson(""))
        cd2m(data)
    }

    // 修改数据规则
    override val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "data" \ "user").asOpt[JsValue].get
        cup2m(obj, data)
    }

    // 验证邮箱规则
    override val ckAttrExist: JsValue => DBObject = jv => DBObject("email" -> (jv \ "data" \ "user" \ "email").asOpt[String].get)

}