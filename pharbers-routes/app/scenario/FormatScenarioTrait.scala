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

    val formatHospitals: String Map JsValue => (Option[String Map JsValue], Option[JsValue]) = { m =>
        val hosps = m("hosps").as[List[Map[String, JsValue]]]
        val goods = m("goods").as[List[Map[String, JsValue]]]
        val reps = m("reps").as[List[Map[String, JsValue]]]

        val current = (toJson(m) \ "scenario" \ "current").as[Map[String, JsValue]]
        val past = (toJson(m) \ "scenario" \ "past").as[List[Map[String, JsValue]]]
        val current_phase = current("phase").as[Int]

        val current_dests = current("connect_dest").as[List[String Map JsValue]]
        val current_dest_rep = current("dest_rep").as[List[String Map JsValue]]
        val current_dest_goods = current("dest_goods").as[List[String Map JsValue]]
        val previous_dest_goods = past.find(p => p("phase").as[Int] == current_phase - 1)
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

            val p_self_goods = previous_dest_goods.filter(x => x("dest_id") == c_dest("id"))

            val medLst = current_dest_goods.filter(x => x("dest_id") == c_dest("id"))
                    .map(_ ("goods_id"))
                    .map(id => goods.find(good => good("id") == id).get)
                    .map(med =>
                        p_self_goods.find(_ ("goods_id") == med("id")) match {
                            case Some(x) => med - "relationship" ++ x("relationship").as[Map[String, JsValue]]
                            case None => med - "relationship" ++ c_dest("relationship").as[Map[String, JsValue]]
                        }
                    )

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

    val formatBudget: String Map JsValue => (Option[String Map JsValue], Option[JsValue]) = { m =>
        val current = searchJSValue(m("scenario"))("current")("current")
        val total = searchJSValue(current)("connect_reso")("connect_reso").as[List[String Map JsValue]].find(f => f("type").as[String] == "money").
                map(x => searchJSValue(toJson(x))("value")("value")).getOrElse(toJson(0))
        val used = searchJSValue(current)("dest_goods_rep")("dest_goods_rep").as[List[String Map JsValue]].
                map(x => searchJSValue(toJson(x))("user_input_money")("user_input_money").as[Double]).sum
        (Some(Map("result" -> toJson(Map("total" -> toJson(total)) ++ Map("used" -> toJson(used))))), None)
    }

    val formatHumans: String Map JsValue => (Option[String Map JsValue], Option[JsValue]) = { m =>
        val current = (toJson(m) \ "scenario" \ "current" \ "connect_reso").as[List[Map[String, JsValue]]]
        println(current)
//		val total = searchJSValue(current)("connect_reso")().as[List[String Map JsValue]].find(f => f("type").as[String] == "day").
//			map(x =>searchJSValue(toJson(x))("value")("value")).getOrElse(toJson(0))
//
//		val reVal = searchJSValue(current)("dest_goods_rep")("dest_goods_rep").as[List[String Map JsValue]].
//			groupBy(g => g("rep_id").as[String]).map { x =>
//			searchJSValue(current)("connect_rep")("connect_rep").as[List[String Map JsValue]].find(f => f("id").as[String] == x._1).map { rep =>
//				val used = x._2.map(z => searchJSValue(toJson(z))("user_input_day")("user_input_day").as[Double]).sum
//				Map("name" -> rep("rep_name"), "total" -> toJson(total), "used" -> toJson(used))
//			}.getOrElse(Map.empty)
//		}.toList
        (Some(Map("result" -> toJson("reVal"))), None)
    }

    val formatHospitalDetails: String Map JsValue => (Option[String Map JsValue], Option[JsValue]) = { m =>

        val hospital_id = searchJSValue(m("data"))("hospital_id")("hospital_id").as[String]
        val current = searchJSValue(m("scenario"))("current")("current")
        val past = searchJSValue(m("scenario"))("past")("past").as[List[String Map JsValue]]
        val connect_goods = searchJSValue(current)("connect_goods")("connect_goods").as[List[String Map JsValue]]
        val c_phase = searchJSValue(current)("phase")("phase").as[Int]
        val p_phase_obj = past.find(f => f("phase").as[Int] == c_phase - 1).map(x => toJson(x)).getOrElse(throw new Exception("is null"))

        val hospital = searchJSValue(current)("connect_dest")("connect_dest").as[List[String Map JsValue]].
                find(f => f("id").as[String] == hospital_id).map { x =>
            val basicinfo = Map("key" -> toJson("医院类型"), "value" -> x("hosp_category")) ::
                    Map("key" -> toJson("医院等级"), "value" -> x("hosp_level")) ::
                    Map("key" -> toJson("病床数量"), "value" -> x("focus_department")) ::
                    Map("key" -> toJson("特色科室"), "value" -> x("beds")) ::
                    Map("key" -> toJson("门诊人次/年"), "value" -> x("outpatient_yearly")) ::
                    Map("key" -> toJson("手术台数"), "value" -> x("surgery_yearly")) ::
                    Map("key" -> toJson("住院人数/年"), "value" -> x("inpatient_yearly")) ::
                    Nil
            Map("id" -> toJson(hospital_id),
                "name" -> x("hosp_name"),
                "basicinfo" -> toJson(basicinfo),
                "news" -> Json.parse("{}"),
                "policy" -> Json.parse("{}")
            )
        }.getOrElse(throw new Exception("is null"))

        val goods = searchJSValue(current)("dest_goods")("dest_goods").
                as[List[String Map JsValue]].filter(f => f("dest_id").as[String] == hospital_id).
                map(x => connect_goods.find(f => f("id").as[String] == x("goods_id").as[String]).map(_ ++ x).
                        getOrElse(throw new Exception("is null"))).map { details =>

            val p_target = searchJSValue(p_phase_obj)("dest_goods_rep")("dest_goods_rep").as[List[String Map JsValue]].
                    filter(f => f("dest_id").as[String] == hospital_id && f("goods_id").as[String] == details("goods_id").as[String]).map(d =>
                (d("relationship") \ "user_input_target").as[Long]
            ).sum

            val basicInfo = ((details("relationship") \ "compete_goods").as[List[String Map JsValue]].
                    map(x => x("goods_id").as[String]) :+ details("goods_id").as[String]).map { x =>
                connect_goods.find(f => f("id").as[String] == x).map { d =>
                    Map("product_name" -> d("prod_name"),
                        "type" -> details("prod_category"),
                        "treatmentarea" -> d("therapeutic_field"),
                        "selltime" -> d("launch_time"),
                        "medicalinsurance" -> d("insure_type"),
                        "development" -> d("research_type"),
                        "companyprice" -> d("ref_price")
                    )
                }.getOrElse(throw new Exception(""))
            }

            val profile = searchJSValue(p_phase_obj)("dest_goods")("dest_goods").
                    as[List[String Map JsValue]].find(f => f("dest_id").as[String] == hospital_id && f("goods_id").as[String] == details("goods_id").as[String]).get

            val history = past.flatMap { pi =>
                pi("dest_goods_rep").as[List[String Map JsValue]].
                        filter(f => f("dest_id").as[String] == hospital_id && f("goods_id").as[String] == details("goods_id").as[String]).map { x =>
                    pi("connect_rep").as[List[String Map JsValue]].find(f => f("id").as[String] == x("rep_id").as[String]).map { d =>
                        Map("time" -> toJson(s"周期${pi("phase").as[Int]}"),
                            "representative" -> d("rep_name"),
                            "timemanagement" -> (x("relationship") \ "user_input_day").as[JsValue],
                            "budgetallocation" -> (x("relationship") \ "user_input_money").as[JsValue],
                            "budgetratio" -> (x("relationship") \ "budget_proportion").as[JsValue],
                            "target" -> (x("relationship") \ "user_input_target").as[JsValue],
                            "growth" -> (x("relationship") \ "target_growth").as[JsValue],
                            "achievementrate" -> (x("relationship") \ "achieve_rate").as[JsValue]
                        )
                    }
                }
            }

            val overview = Map("key" -> toJson("药品市场潜力"), "value" -> (details("relationship") \ "potential").as[JsValue]) ::
                    Map("key" -> toJson("增长潜力"), "value" -> (details("relationship") \ "potential_growth").as[JsValue]) ::
                    Map("key" -> toJson("上期销售额"), "value" -> (profile("relationship") \ "sales").as[JsValue]) ::
                    Map("key" -> toJson("上期增长"), "value" -> (profile("relationship") \ "sales_growth").as[JsValue]) ::
                    Map("key" -> toJson("份额"), "value" -> (profile("relationship") \ "share").as[JsValue]) ::
                    Map("key" -> toJson("上期贡献率"), "value" -> (details("relationship") \ "contri_rate").as[JsValue]) :: Nil

            Map("id" -> details("id"),
                "name" -> toJson(details("prod_category")),
                "p_target" -> toJson(p_target),
                "overview" -> toJson(overview),
                "detail" -> toJson(Map(
                    "id" -> toJson(s"${details("id").as[String]}_detail"),
                    "value" -> toJson(basicInfo))),
                "history" -> toJson(Map(
                    "id" -> toJson(s"${details("id").as[String]}_history"),
                    "value" -> toJson(history))),
                "competitionproducts" -> toJson(Map(
                    "id" -> toJson(s"${details("id").as[String]}_competitionproducts"),
                    "value" -> Json.parse("[]")
                ))
            )
        }

        (Some(Map("result" -> toJson(
            Map("hospital" -> toJson(hospital),
                "medicines" -> toJson(goods))
        ))), None)
    }
}