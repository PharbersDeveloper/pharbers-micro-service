package module.representative

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.common.transform.drTrait
import module.common.datamodel.basemodel

/**
  * Created by clock on 18-8-2.
  */
class representatives extends basemodel with drTrait {

    val db_name = "representatives"
    override val name = "rep"
    override def runtimeClass: Class[_] = classOf[representatives]

    override val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "rep_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    override val anqc: JsValue => DBObject = _ => DBObject()

    override val qcm : JsValue => DBObject = { js =>
        (js \ "data" \ "condition" \ "reps").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    override val ssr : DBObject => Map[String, JsValue] = _ => Map()

    override val sr : DBObject => Map[String, JsValue] = _ => Map()

    override val dr : DBObject => Map[String, JsValue] = cdr

    override val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop representative" -> toJson("success")
        )
    }

    override val d2m : JsValue => DBObject = { js =>
        val data = (js \ "data" \ "rep").asOpt[JsValue].get

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // representative 唯一标示

        builder += "rep_name" -> (data \ "rep_name").asOpt[String].map (x => x).getOrElse("")
        builder += "rep_image" -> (data \ "rep_image").asOpt[String].map (x => x).getOrElse("")
        builder += "rep_level" -> (data \ "rep_level").asOpt[String].map (x => x).getOrElse("")
        builder += "age" -> (data \ "age").asOpt[Int].map (x => x).getOrElse(0)
        builder += "education" -> (data \ "education").asOpt[String].map (x => x).getOrElse("")

        builder += "profe_bg" -> (data \ "profe_bg").asOpt[String].map (x => x).getOrElse("")
        builder += "service_year" -> (data \ "service_year").asOpt[Int].map (x => x).getOrElse(0)
        builder += "entry_time" -> (data \ "entry_time").asOpt[Int].map (x => x).getOrElse(0)
        builder += "business_exp" -> (data \ "business_exp").asOpt[String].map (x => x).getOrElse("")
        builder += "advantage" -> (data \ "advantage").asOpt[String].map (x => x).getOrElse("")

        builder += "weakness" -> (data \ "weakness").asOpt[String].map (x => x).getOrElse("")
        builder += "sales_skills_val" -> (data \ "sales_skills_val").asOpt[Int].map (x => x).getOrElse(0)
        builder += "prod_knowledge_val" -> (data \ "prod_knowledge_val").asOpt[Int].map (x => x).getOrElse(0)
        builder += "motivation_val" -> (data \ "motivation_val").asOpt[Int].map (x => x).getOrElse(0)
        builder += "overall_val" -> (data \ "overall_val").asOpt[Int].map (x => x).getOrElse(0)

        builder.result
    }

    override val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "data" \ "rep").asOpt[JsValue].get

        (data \ "rep_name").asOpt[String].map (x => obj += "rep_name" -> x).getOrElse(Unit)
        (data \ "rep_image").asOpt[String].map (x => obj += "rep_image" -> x).getOrElse(Unit)
        (data \ "rep_level").asOpt[String].map (x => obj += "rep_level" -> x).getOrElse(Unit)
        (data \ "age").asOpt[Int].map (x => obj += "age" -> int2Integer(x)).getOrElse(Unit)
        (data \ "education").asOpt[String].map (x => obj += "education" -> x).getOrElse(Unit)

        (data \ "profe_bg").asOpt[String].map (x => obj += "profe_bg" -> x).getOrElse(Unit)
        (data \ "service_year").asOpt[Int].map (x => obj += "service_year" -> int2Integer(x)).getOrElse(Unit)
        (data \ "entry_time").asOpt[Int].map (x => obj += "entry_time" -> int2Integer(x)).getOrElse(Unit)
        (data \ "business_exp").asOpt[String].map (x => obj += "business_exp" -> x).getOrElse(Unit)
        (data \ "advantage").asOpt[String].map (x => obj += "advantage" -> x).getOrElse(Unit)

        (data \ "weakness").asOpt[String].map (x => obj += "weakness" -> x).getOrElse(Unit)
        (data \ "sales_skills_val").asOpt[Int].map (x => obj += "sales_skills_val" -> int2Integer(x)).getOrElse(Unit)
        (data \ "prod_knowledge_val").asOpt[Int].map (x => obj += "prod_knowledge_val" -> int2Integer(x)).getOrElse(Unit)
        (data \ "motivation_val").asOpt[Int].map (x => obj += "motivation_val" -> int2Integer(x)).getOrElse(Unit)
        (data \ "overall_val").asOpt[Int].map (x => obj += "overall_val" -> int2Integer(x)).getOrElse(Unit)

        obj
    }
}
