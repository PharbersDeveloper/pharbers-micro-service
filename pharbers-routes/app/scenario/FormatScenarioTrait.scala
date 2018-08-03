package scenario

import play.api.libs.json._
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.common.transform.SearchData

trait FormatScenarioTrait extends SearchData {

    def format(input: Option[String Map JsValue])
              (out: String Map JsValue => (Option[String Map JsValue], Option[JsValue])): (Option[String Map JsValue], Option[JsValue]) = {
        input match {
            case None => (None, None)
            case Some(o) => out(o)
        }
    }

    val formatHospitals: String Map JsValue => (Option[String Map JsValue], Option[JsValue]) = { data =>
        val hosps = data("hosps").as[List[Map[String, JsValue]]]
        val goods = data("goods").as[List[Map[String, JsValue]]]
        val reps = data("reps").as[List[Map[String, JsValue]]]

        val current = (toJson(data) \ "scenario" \ "current").as[Map[String, JsValue]]
        val past = (toJson(data) \ "scenario" \ "past").as[List[Map[String, JsValue]]]
        val current_phase = current("phase").as[Int]

        val current_dests = current("connect_dest").as[List[String Map JsValue]]
        val current_dest_rep = current("dest_rep").as[List[String Map JsValue]]
        val current_dest_goods = current("dest_goods").as[List[String Map JsValue]]
        val pre_dest_goods = past.find(p => p("phase").as[Int] == current_phase - 1)
                .get("dest_goods").as[List[Map[String, JsValue]]]

        val result = current_dests.map { c_dest =>
            val repLst = current_dest_rep.filter(x => x("dest_id") == c_dest("id"))
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
    }

    val formatBudget: String Map JsValue => (Option[String Map JsValue], Option[JsValue]) = { data =>
        val current = (toJson(data) \ "scenario" \ "current").asOpt[Map[String, JsValue]]
        val connect_reso = current.get("connect_reso").as[List[String Map JsValue]]

        val total_day = connect_reso.find(_ ("type").as[String] == "day")
                .get("relationship").asOpt[Map[String, JsValue]]
                .get("value").as[Int]

        val total_money = connect_reso.find(_ ("type").as[String] == "money")
                .get("relationship").asOpt[Map[String, JsValue]]
                .get("value").as[Long]

        val current_dest_goods_rep = current.get("dest_goods_rep").as[List[String Map JsValue]]
        val all_use_money = current_dest_goods_rep.map(_ ("relationship").as[Map[String, JsValue]])
                .map(x => x("user_input_money").as[Long])
                .sum

        val reps = data("reps").as[List[Map[String, JsValue]]]
        val connect_rep = current.get("connect_rep").as[List[String Map JsValue]]
                .map(x => x ++ reps.find(_ ("id") == x("id")).get)
        val everyone_use_day = connect_rep.map { rep =>
            val use_day = current_dest_goods_rep.filter(_ ("rep_id") == rep("id"))
                    .map(_ ("relationship").as[Map[String, JsValue]])
                    .map(x => x("user_input_day").as[Int])
                    .sum
            Map(
                "name" -> rep("rep_name"),
                "total" -> toJson(total_day),
                "used" -> toJson(use_day)
            )
        }

        val result = Map(
            "budget" -> toJson(Map(
                "used" -> toJson(all_use_money),
                "total" -> toJson(total_money)
            )),
            "manpower" -> toJson(everyone_use_day)
        )

        (Some(Map("result" -> toJson(result))), None)
    }

    val formatHospitalDetails: String Map JsValue => (Option[String Map JsValue], Option[JsValue]) = { data =>
        val target_hosp_id = data("hosp_id")

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
                    .get("relationship").as[Map[String, JsValue]]
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
                    "value" -> pre_target_dest_goods_relationship("sales")
                ),
                Map(
                    "key" -> toJson("上期增长"),
                    "value" -> pre_target_dest_goods_relationship("sales_growth")
                ),
                Map(
                    "key" -> toJson("份额"),
                    "value" -> pre_target_dest_goods_relationship("share")
                ),
                Map(
                    "key" -> toJson("上期贡献率"),
                    "value" -> pre_target_dest_goods_relationship("contri_rate")
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
    }

    val formatProposalsCond: String Map JsValue => (Option[String Map JsValue], Option[JsValue]) = { data =>

        val proposals = data("proposals").as[List[Map[String, JsValue]]].map(_("id").as[String])
        val user_id = data("user").as[Map[String, JsValue]].get("user_id").get
        val condition = proposals.map(x => {
            toJson(Map(
                "user_id" -> toJson(user_id),
                "proposals" -> toJson(proposals)
            ))
        })

        val result = Map(
            "condition" -> toJson(condition)
        )

        (Some(Map("data" -> toJson(result))), None)
    }
}