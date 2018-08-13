package module.scenario

import java.util.{Date, UUID}
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.common.transform.drTrait
import module.common.datamodel.basemodel
import play.api.libs.json.{JsString, JsValue}

/**
  * Created by clock on 18-8-7.
  */
class scenario extends basemodel with drTrait with jv2dTrait {

    override val name = "scenario"
    override def runtimeClass: Class[_] = classOf[scenario]

    // 根据UUID查询
    val u_qc: JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "uuid").as[String]
        DBObject("uuid" -> tmp)
    }

    // 根据ID查询
    override val qc: JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "scenario_id").as[String]
        DBObject("_id" -> new ObjectId(tmp))
    }

    // 弃用
    override val anqc: JsValue => DBObject = _ => DBObject()

    // 根据ID查询多个
    override val qcm: JsValue => DBObject = { js =>
        (js \ "data" \ "condition" \ "scenarios").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll: List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    // 根据user prorosal查询多个
    val u_p_qcm: JsValue => DBObject = { js =>
        val user_id = (js \ "data" \ "condition" \ "user_id").asOpt[String].get

        (js \ "data" \ "condition" \ "proposals").asOpt[List[String]] match {
            case None => DBObject("query" -> "none")
            case Some(ll) => $or(ll map (x => DBObject("user_id" -> user_id, "proposal_id" -> x)))
        }
    }

    // 极简返回结果
    override val ssr: DBObject => Map[String, JsValue] = { obj =>
        Map(
            "id" -> toJson(obj.get("_id").toString),
            "uuid" -> toJson(obj.get("uuid").toString)
        )
    }

    override val sr: DBObject => Map[String, JsValue] = _ => Map()

    // 详细返回结果
    override val dr: DBObject => Map[String, JsValue] = cdr

    override val popr: DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop scenario" -> toJson("success")
        )
    }

    // 创建新的Scenario
    override val d2m: JsValue => DBObject = { jsValue =>
        val user_id = (jsValue \ "scenario" \ "user_id").asOpt[JsString].getOrElse(throw new Exception("request not find user_id")).value
        val proposal_id = (jsValue \ "scenario" \ "proposal_id").asOpt[JsString].getOrElse(throw new Exception("request not find proposal_id")).value
        val current = (jsValue \ "scenario" \ "current").asOpt[JsValue].getOrElse(throw new Exception("request not find current"))
        val current_phase = (jsValue \ "scenario" \ "current" \ "phase").as[Int]
        val total_phase = (jsValue \ "scenario" \ "total_phase").as[Int]
        val past = (jsValue \ "scenario" \ "past").asOpt[List[JsValue]].getOrElse(throw new Exception("request not find past"))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()                   // object_id 唯一标示
        builder += "uuid" -> UUID.randomUUID.toString        // uuid 唯一标示
        builder += "user_id" -> user_id
        builder += "proposal_id" -> proposal_id
        builder += "timestamp" -> new Date().getTime
        builder += "current_phase" -> int2Integer(current_phase)
        builder += "total_phase" -> int2Integer(total_phase)
        builder += "assess_report" -> ""
        builder += "current" -> jv2d(current)
        builder += "past" -> MongoDBList(past.map(jv2d): _*).underlying

        builder.result
    }

    // 弃用
    override val up2m: (DBObject, JsValue) => DBObject = (_, _) => DBObject()

    val success_result: DBObject => Map[String, JsValue] = { _ => Map("result" -> toJson("update success")) }
}
