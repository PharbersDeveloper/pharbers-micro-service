package module.scenario

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.scenario.ScenarioMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-2.
  */
object HospitalDetailModule extends ModuleTrait {

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryHospitalDetail(data) =>
            val target_hosp_id = (data \ "data" \ "condition" \ "hospital_id").get

            val target_hosp_detail = data("hosps").as[List[Map[String, JsValue]]]
                    .find(x => x("id") == target_hosp_id).get
            val goods = data("goods").as[List[Map[String, JsValue]]]
            val reps = data("reps").as[List[Map[String, JsValue]]]

            val current = (toJson(data) \ "scenario" \ "current").as[Map[String, JsValue]]
            val past = (toJson(data) \ "scenario" \ "past").as[List[Map[String, JsValue]]]
            val current_phase = current("phase").as[Int]

            val target_dest_goods = current("dest_goods").as[List[String Map JsValue]]
                    .filter(x => x("dest_id") == target_hosp_id)
            val pre_dest_goods = past.find(p => p("phase").as[Int] == current_phase - 1)
                    .get("dest_goods").as[List[Map[String, JsValue]]]
            val pre_dest_goods_rep = past.find(p => p("phase").as[Int] == current_phase - 1)
                    .get("dest_goods_rep").as[List[Map[String, JsValue]]]

            val current_dest_rep = current("dest_rep").as[List[String Map JsValue]]
            val represents = current_dest_rep.filter(_ ("dest_id") == target_hosp_id)
                    .map(_ ("rep_id"))
                    .map(rep_id => reps.find(_ ("id") == rep_id).get)

            val hosp_detail = Map(
                "key" -> toJson("target-detail-hospital-card"),
                "values" -> toJson(target_hosp_detail ++ Map(
                    "represents" -> toJson(represents)
                ))
            )

            val medLst = target_dest_goods.map { target_med =>
                val target_med_detail = goods.find(_ ("id") == target_med("goods_id")).get

                // 上期分配的所有预算
                val pre_target_sum = pre_dest_goods_rep.filter(_ ("dest_id") == target_hosp_id)
                        .filter(_ ("goods_id") == target_med("goods_id"))
                        .map(_ ("relationship").as[Map[String, JsValue]])
                        .map(_ ("user_input_target").as[Long])
                        .sum

                // overview
                val target_med_relationship = target_med("relationship").as[Map[String, JsValue]]
                val pre_target_dest_goods_relationship = pre_dest_goods.filter(_ ("dest_id") == target_hosp_id)
                        .find(_ ("goods_id") == target_med("goods_id"))
                        .map(_("relationship").as[Map[String, JsValue]])

                val overview = List(
                    Map(
                        "key" -> toJson("药品市场潜力"),
                        "value" -> target_med_relationship("potential")
                    ),
                    Map(
                        "key" -> toJson("增长潜力"),
                        "value" -> target_med_relationship("potential_growth")
                    ),
                    Map(
                        "key" -> toJson("上期销售额"),
                        "value" -> pre_target_dest_goods_relationship.map(_("sales")).getOrElse(toJson(0L))

                    ),
                    Map(
                        "key" -> toJson("上期增长"),
                        "value" -> pre_target_dest_goods_relationship.map(_("sales_growth")).getOrElse(toJson(0.0))
                    ),
                    Map(
                        "key" -> toJson("份额"),
                        "value" -> pre_target_dest_goods_relationship.map(_("share")).getOrElse(toJson(0.0))
                    ),
                    Map(
                        "key" -> toJson("上期贡献率"),
                        "value" -> pre_target_dest_goods_relationship.map(_("contri_rate")).getOrElse(toJson(0.0))
                    )
                )

                // detail 表格数据
                val compete_goods_detail = target_med_relationship("compete_goods").as[List[Map[String, JsValue]]]
                        .map(_ ("goods_id"))
                        .map(x => goods.find(_ ("id") == x).get)
                val detail = Map(
                    "id" -> toJson(target_med_detail("id").as[String] + "_detail"),
                    "value" -> toJson(target_med_detail :: compete_goods_detail)
                )

                // history 表格数据
                val history_deploy = past.flatMap { phase_obj =>
                    val target_dest_goods_rep = phase_obj("dest_goods_rep").as[List[Map[String, JsValue]]]
                            .filter(_ ("dest_id") == target_hosp_id)
                            .filter(_ ("goods_id") == target_med("goods_id"))

                    target_dest_goods_rep.map { phase_dest_goods_rep_obj =>
                        val relationship = phase_dest_goods_rep_obj("relationship").as[Map[String, JsValue]]

                        val rep_name = reps.find(_ ("id") == phase_dest_goods_rep_obj("rep_id")).get("rep_name")
                        val use_day = relationship("user_input_day").as[Int]
                        val use_budget = relationship("user_input_money").as[Long]
                        val budget_proportion = relationship("budget_proportion").as[Double]
                        val target = relationship("user_input_target").as[Long]
                        val target_growth = relationship("target_growth").as[Double]
                        val achieve_rate = relationship("achieve_rate").as[Double]

                        Map(
                            "time" -> toJson("周期" + phase_obj("phase").as[Int]),
                            "rep_name" -> toJson(rep_name),
                            "use_day" -> toJson(use_day),
                            "use_budget" -> toJson(use_budget),
                            "budget_proportion" -> toJson(budget_proportion),
                            "target" -> toJson(target),
                            "target_growth" -> toJson(target_growth),
                            "achieve_rate" -> toJson(achieve_rate)
                        )
                    }
                }
                val history = Map(
                    "id" -> toJson(target_med_detail("id").as[String] + "_history"),
                    "value" -> toJson(history_deploy)
                )

                // compete_goods 表格数据
                val compete_goods = Map(
                    "id" -> toJson(target_med_detail("id").as[String] + "_compete_goods"),
                    "value" -> toJson("")
                )

                Map(
                    "id" -> target_med_detail("id"),
                    "prod_category" -> target_med_detail("prod_category"),
                    "pre_target" -> toJson(pre_target_sum),
                    "overview" -> toJson(overview),
                    "detail" -> toJson(detail),
                    "history" -> toJson(history),
                    "compete_goods" -> toJson(compete_goods)
                )
            }

            val med_detail = Map(
                "key" -> toJson("target-detail-medicines-card"),
                "values" -> toJson(medLst)
            )

            val result = Map(
                "id" -> toJson(target_hosp_id),
                "component_data" -> toJson(hosp_detail :: med_detail :: Nil)
            )

            (Some(Map("result" -> toJson(result))), None)
        case _ => ???
    }

}
