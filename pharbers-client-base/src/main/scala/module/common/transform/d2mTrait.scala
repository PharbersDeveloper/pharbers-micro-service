package module.common.transform

import play.api.libs.json._
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._

trait d2mTrait {

    val cd2m: JsValue => DBObject = { jv =>
        val data = jv.asInstanceOf[JsObject].value

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()

        data.foreach {
            case (k, v: JsString) => builder += k -> v.value
            case (k, v: JsNumber) => builder += k -> double2Double(v.value.doubleValue())
            case (k, v: JsArray) => builder += k -> MongoDBList(v.value.map(cd2m).flatten).underlying
            case (k, v: JsValue) => builder += k -> v.toString()
        }

        builder.result
    }

}
