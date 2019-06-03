package module.report

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.common.transform.drTrait

import scala.reflect.ClassTag

/**
  * Created by jeorch on 18-8-6.
  */
class report extends ClassTag[report] with drTrait {

    val name = "report"
    lazy val names = "reports"

    override def runtimeClass: Class[_] = classOf[report]

    // id 查询
    implicit val qc: JsValue => DBObject = { js =>
        val report_id = (js \ "data" \ "condition" \ "report_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(report_id))
    }

    implicit val idr: DBObject => Map[String, JsValue] = cdr

    def query(data : JsValue)(db_name : String)
             (implicit func : JsValue => DBObject,
              func_out : DBObject => Map[String, JsValue],
              cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("report").get
        db.queryObject(func(data), db_name)(func_out) match {
            case Some(m) => m
            case None => Map().empty
        }
    }

}
