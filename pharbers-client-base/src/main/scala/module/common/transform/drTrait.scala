package module.common.transform

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports.{DBObject, ObjectId, _}

trait drTrait {

    val cdr: DBObject => Map[String, JsValue] = { obj =>
        obj.map { cell =>
            cell._2 match {
                case id: ObjectId => "id" -> toJson(id.toString)
                case str: String => cell._1 -> toJson(str)
                case i: java.lang.Integer => cell._1 -> toJson(i.toInt)
                case l: java.lang.Long => cell._1 -> toJson(l.toLong)
                case d: java.lang.Double => cell._1 -> toJson(d.toDouble)
                case d: java.lang.Boolean => cell._1 -> toJson(d.booleanValue)
                case lst: BasicDBList => cell._1 -> toJson(lst.toList.map(x => cdr(x.asInstanceOf[DBObject])))
                case obj: DBObject => cell._1 -> toJson(cdr(obj))
            }
        }.toMap
    }

}
