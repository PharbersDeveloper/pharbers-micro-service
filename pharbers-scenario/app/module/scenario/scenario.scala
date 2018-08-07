package module.scenario

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.common.transform.drTrait
import module.common.datamodel.basemodel

/**
  * Created by clock on 18-8-2.
  */
class hosp extends basemodel with drTrait {

    val db_name = "dests"
    override val name = "hosp"
    override def runtimeClass: Class[_] = classOf[hosp]

    override val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "hosp_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    override val anqc: JsValue => DBObject = _ => DBObject()

    override val qcm : JsValue => DBObject = { js =>
        (js \ "data" \ "condition" \ "hosps").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    override val ssr : DBObject => Map[String, JsValue] = _ => Map()

    override val sr : DBObject => Map[String, JsValue] = _ => Map()

    override val dr : DBObject => Map[String, JsValue] = cdr

    override val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop hosp" -> toJson("success")
        )
    }

    override val d2m : JsValue => DBObject = { js =>
        val data = (js \ "data" \ "hosp").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // dest_id 唯一标示
        builder += "type" -> "hosp"      // dest_type 唯一标示

        builder += "hosp_name" -> (data \ "hosp_name").asOpt[String].map (x => x).getOrElse("")
        builder += "hosp_image" -> (data \ "hosp_image").asOpt[String].map (x => x).getOrElse("")
        builder += "hosp_category" -> (data \ "hosp_category").asOpt[String].map (x => x).getOrElse("")
        builder += "hosp_level" -> (data \ "hosp_level").asOpt[String].map (x => x).getOrElse("")
        builder += "init_time" -> (data \ "init_time").asOpt[Int].map (x => x).getOrElse(0)

        builder += "client_grade" -> (data \ "client_grade").asOpt[String].map (x => x).getOrElse("")
        builder += "beds" -> (data \ "beds").asOpt[Long].map (x => x).getOrElse(0L)
        builder += "department" -> (data \ "department").asOpt[String].map (x => x).getOrElse("")
        builder += "focus_department" -> (data \ "focus_department").asOpt[String].map (x => x).getOrElse("")
        builder += "featured_outpatient" -> (data \ "featured_outpatient").asOpt[String].map (x => x).getOrElse("")

        builder += "academic_acceptance_rate" -> (data \ "academic_acceptance_rate").asOpt[String].map (x => x).getOrElse("")
        builder += "academic_influence" -> (data \ "academic_influence").asOpt[String].map (x => x).getOrElse("")
        builder += "patients_distribution_department" -> (data \ "patients_distribution_department").asOpt[String].map (x => x).getOrElse("")
        builder += "outpatient_yearly" -> (data \ "outpatient_yearly").asOpt[Int].map (x => x).getOrElse(0)
        builder += "inpatient_yearly" -> (data \ "inpatient_yearly").asOpt[Int].map (x => x).getOrElse(0)
        builder += "surgery_yearly" -> (data \ "surgery_yearly").asOpt[Int].map (x => x).getOrElse(0)
        builder += "patients_payment_capacity" -> (data \ "patients_payment_capacity").asOpt[String].map (x => x).getOrElse(0)
        builder += "drug_intake" -> (data \ "drug_intake").asOpt[String].map (x => x).getOrElse(0)

        builder.result
    }

    override val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "data" \ "hosp").asOpt[JsValue].get

        (data \ "hosp_name").asOpt[String].map (x => obj += "hosp_name" -> x).getOrElse(Unit)
        (data \ "hosp_image").asOpt[String].map (x => obj += "hosp_image" -> x).getOrElse(Unit)
        (data \ "hosp_category").asOpt[String].map (x => obj += "hosp_category" -> x).getOrElse(Unit)
        (data \ "hosp_level").asOpt[String].map (x => obj += "hosp_level" -> x).getOrElse(Unit)
        (data \ "init_time").asOpt[Int].map (x => obj += "init_time" -> int2Integer(x)).getOrElse(Unit)

        (data \ "client_grade").asOpt[String].map (x => obj += "client_grade" -> x).getOrElse(Unit)
        (data \ "beds").asOpt[Long].map (x => obj += "beds" -> long2Long(x)).getOrElse(Unit)
        (data \ "department").asOpt[String].map (x => obj += "department" -> x).getOrElse(Unit)
        (data \ "focus_department").asOpt[String].map (x => obj += "focus_department" -> x).getOrElse(Unit)
        (data \ "featured_outpatient").asOpt[String].map (x => obj += "featured_outpatient" -> x).getOrElse(Unit)

        (data \ "academic_acceptance_rate").asOpt[String].map (x => obj += "academic_acceptance_rate" -> x).getOrElse(Unit)
        (data \ "academic_influence").asOpt[String].map (x => obj += "academic_influence" -> x).getOrElse(Unit)
        (data \ "patients_distribution_department").asOpt[String].map (x => obj += "patients_distribution_department" -> x).getOrElse(Unit)
        (data \ "outpatient_yearly").asOpt[Int].map (x => obj += "outpatient_yearly" -> int2Integer(x)).getOrElse(Unit)
        (data \ "inpatient_yearly").asOpt[Int].map (x => obj += "inpatient_yearly" -> int2Integer(x)).getOrElse(Unit)

        (data \ "surgery_yearly").asOpt[Int].map (x => obj += "surgery_yearly" -> int2Integer(x)).getOrElse(Unit)
        (data \ "patients_payment_capacity").asOpt[String].map (x => obj += "patients_payment_capacity" -> x).getOrElse(Unit)
        (data \ "drug_intake").asOpt[String].map (x => obj += "drug_intake" -> x).getOrElse(Unit)

        obj
    }
}
