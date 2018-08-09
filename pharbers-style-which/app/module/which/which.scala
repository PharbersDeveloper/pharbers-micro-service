package module.which

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.common.transform.drTrait
import module.common.datamodel.basemodel
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager

/**
  * Created by clock on 18-7-30.
  */
class which extends basemodel with drTrait {

    override val name = "which"
    lazy val db_name = "tm_whichs"
    override def runtimeClass: Class[_] = classOf[which]

    // id 查询
    override val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "which_id").asOpt[String].get
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

    // 不要了
    override val ssr : DBObject => Map[String, JsValue] = _ => Map()

    // 不要了
    override val sr : DBObject => Map[String, JsValue] = _ => Map()

    // 详细返回结果
    override val dr: DBObject => Map[String, JsValue] = cdr

    // 删除数据规则
    override val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop which" -> toJson("success")
        )
    }

    // 不要了
    override val d2m : JsValue => DBObject = _ => DBObject()

    // 不要了
    override val up2m : (DBObject, JsValue) => DBObject = (_, _) => DBObject()

    def query(data : JsValue)(db_name : String)
             (implicit func : JsValue => DBObject,
              func_out : DBObject => Map[String, JsValue],
              cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("style").get
        db.queryObject(func(data), db_name)(func_out)match {
            case Some(m) => m
            case None => Map().empty
        }
    }

    def queryMulti(data : JsValue)(db_name : String)
                  (implicit func : JsValue => DBObject,
                   func_out : DBObject => Map[String, JsValue],
                   cm: CommonModules) : List[Map[String, JsValue]] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("style").get
        val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
        val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
        val sort = (data \ "sort").asOpt[String].map (x => x).getOrElse("date")
        db.queryMultipleObject(func(data), db_name, sort, skip, take)(func_out)
    }
}
