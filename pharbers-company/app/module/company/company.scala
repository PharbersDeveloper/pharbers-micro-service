package module.company

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.common.datamodel.basemodel
import module.common.checkExist.checkAttrExist
import module.common.transform.{d2mTrait, drTrait, up2mTrait}

/**
  * Created by clock on 18-7-30.
  */
class company extends basemodel
        with drTrait with d2mTrait with up2mTrait
        with checkAttrExist {

    override val name = "company"
    override lazy val names = "companies"
    override def runtimeClass: Class[_] = classOf[company]

    // id 查询
    override val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "company_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    // 不要了
    override val anqc: JsValue => DBObject = _ => DBObject()

    // 查询多个
    override val qcm : JsValue => DBObject = { js =>
        (js \ "data" \ "condition" \ names).asOpt[List[String]] match {
            case None => DBObject("query" -> "none")
            case Some(ll) => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    // 极简返回结果
    override val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "company_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    // 不要了
    override val sr : DBObject => Map[String, JsValue] = _ => Map()

    // 详细返回结果
    override val dr: DBObject => Map[String, JsValue] = cdr

    // 删除数据规则
    override val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop company" -> toJson("success")
        )
    }

    // 新增数据规则
    override val d2m : JsValue => DBObject = { js =>
        val data = (js \ "data" \ "company").asOpt[JsValue].map (x => x).getOrElse(toJson(""))
        cd2m(data)
    }

    // 修改数据规则
    override val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "data" \ "company").asOpt[JsValue].get
        cup2m(obj, data)
    }

    // 验证公司名规则
    override val ckAttrExist: JsValue => DBObject = jv => DBObject("company_name" -> (jv \ "data" \ "company" \ "company_name").asOpt[String].get)

}
