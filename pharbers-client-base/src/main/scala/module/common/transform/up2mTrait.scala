package module.common.transform

import play.api.libs.json._
import com.mongodb.casbah.Imports._

trait up2mTrait {

    val cup2m : (DBObject, JsValue) => DBObject = { (obj, jv) =>
        val data = jv.asInstanceOf[JsObject].value

        data.foreach {
            case (k, v: JsString) => obj += k -> v.value
            case (k, v: JsNumber) => obj += k -> double2Double(v.value.doubleValue())
            case (k, v: JsValue) => obj += k -> v.toString()
        }

        obj
    }

}
