package module.common.stragety

import com.mongodb.casbah.Imports.{DBObject, _}
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import org.bson.types.ObjectId
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsString, JsValue}

trait bindTrait {
    val bind: JsValue => DBObject
    val unbind: JsValue => DBObject

    def bindConnection(data: JsValue)
                      (connect: String)
                      (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        val ssr: DBObject => Map[String, JsValue] = obj =>
            Map("id" -> toJson(obj.getAs[ObjectId]("_id").get.toString))

        val o = bind(data)
        val id = db.queryObject(o, connect)(ssr) match {
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

    def unbindConnection(data: JsValue)
                        (connect: String)
                        (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        val ssr: DBObject => Map[String, JsValue] = obj =>
            Map("id" -> toJson(obj.getAs[ObjectId]("_id").get.toString))

        db.queryCount(unbind(data), connect)(ssr) match {
            case Some(1) => db.deleteObject(unbind(data), connect, "_id")
            case _ => Unit
        }

        Map("unbind result" -> toJson("success"))
    }
}
