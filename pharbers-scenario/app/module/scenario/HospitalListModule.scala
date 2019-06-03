package module.scenario

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.scenario.ScenarioMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-7.
  */
object HospitalListModule extends ModuleTrait {

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryHospitalList(data) =>
            val hosps = data("hosps").as[List[Map[String, JsValue]]]
            val goods = data("goods").as[List[Map[String, JsValue]]]
            val reps = data("reps").as[List[Map[String, JsValue]]]

            val current = (toJson(data) \ "scenario" \ "current").as[Map[String, JsValue]]
            val past = (toJson(data) \ "scenario" \ "past").as[List[Map[String, JsValue]]]
            val current_phase = current("phase").as[Int]

            val current_dests = current("connect_dest").as[List[String Map JsValue]]
            val current_dest_goods = current("dest_goods").as[List[String Map JsValue]]
            val current_dest_goods_rep = current("dest_goods_rep").as[List[String Map JsValue]]
            val pre_dest_goods = past.find(p => p("phase").as[Int] == current_phase - 1)
                    .get("dest_goods").as[List[Map[String, JsValue]]]

            val result = current_dests.map { c_dest =>
                val repLst = current_dest_goods_rep.filter(x => x("dest_id") == c_dest("id"))
                        .map(_ ("rep_id"))
                        .map(id => reps.find(rep => rep("id") == id).get)

                val hosp_details = Map(
                    "key" -> toJson("target-hospital-card"),
                    "values" -> toJson(
                        hosps.find(x => x("id") == c_dest("id")).getOrElse(Map()) ++ Map(
                            "representives" -> toJson(repLst)
                        )
                    )
                )

                val p_self_goods = pre_dest_goods.filter(x => x("dest_id") == c_dest("id"))

                val medLst = current_dest_goods.filter(x => x("dest_id") == c_dest("id"))
                        .map(x => x("goods_id") -> x("relationship"))
                        .map { case (id, relationship) => goods.find(good => good("id") == id).get -> relationship }
                        .map { case (med, relationship) =>
                            p_self_goods.find(_ ("goods_id") == med("id")) match {
                                case Some(x) => med ++ x("relationship").as[Map[String, JsValue]]
                                case None => med ++ relationship.as[Map[String, JsValue]]
                            }
                        }

                val goods_details: Map[String, JsValue] = Map(
                    "key" -> toJson("target-goods-card"),
                    "values" -> toJson(Map(
                        "medicines" -> toJson(medLst)
                    ))
                )

                toJson(Map(
                    "id" -> c_dest("id"),
                    "component_data" -> toJson(hosp_details :: goods_details :: Nil)
                ))
            }

            (Some(Map("result" -> toJson(result))), None)
        case _ => ???
    }

}
