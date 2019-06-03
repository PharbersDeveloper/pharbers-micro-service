package module.user

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import scala.collection.immutable.Map
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager

/**
  * Created by clock on 18-7-30.
  */
trait authTrait {

    val authPwd: JsValue => DBObject = { js =>
        $and(
            DBObject("email" -> (js \ "data" \ "condition" \ "email").asOpt[String].map(x => x).getOrElse("")),
            DBObject("password" -> (js \ "data" \ "condition" \ "password").asOpt[String].map(x => x).getOrElse(""))
        )
    }

    def authWithPassword(func: JsValue => DBObject,
                         func_out: DBObject => Map[String, JsValue])
                        (data: JsValue)(db_name: String)
                        (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        db.queryObject(func(data), db_name)(func_out) match {
            case None => throw new Exception("email or password error")
            case Some(one) => one
        }
    }

}
