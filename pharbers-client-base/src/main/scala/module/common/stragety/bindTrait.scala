package module.common.stragety

import com.mongodb.casbah.Imports.{DBObject, _}
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import org.bson.types.ObjectId
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsString, JsValue}

trait bindTrait {
    val bind_ssr: DBObject => Map[String, JsValue] = obj =>
        Map("id" -> toJson(obj.getAs[ObjectId]("_id").get.toString))

    val del_cond: Map[String, JsValue] => DBObject = map =>
        DBObject("_id" -> new ObjectId(map("id").as[String]))

    def bindConnection(bindFunc: JsValue => DBObject)(data: JsValue)(connect: String)
                      (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        val o = bindFunc(data)
        val id = db.queryObject(o, connect)(bind_ssr) match {
            case None =>
                val id = ObjectId.get()
                o.put("_id", id)
                db.insertObject(o, connect, "_id")
                id.toString
            case Some(r) =>
                r("id").asInstanceOf[JsString].value
        }

        Map("bind_id" -> toJson(id))
    }

    def unbindConnection(unbindFunc: JsValue => DBObject)(data: JsValue)(connect: String)
                        (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        db.queryMultipleObject(unbindFunc(data), connect)(bind_ssr) match {
            case Nil => Unit
            case lst: List[Map[String, JsValue]] => lst.foreach(x =>
                db.deleteObject(del_cond(x), connect, "_id")
            )
        }

        Map("unbind result" -> toJson("success"))
    }
}
