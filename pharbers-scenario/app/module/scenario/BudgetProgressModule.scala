package module.scenario

import play.api.libs.json.JsValue
import module.common.stragety.impl
import play.api.libs.json.Json.toJson
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import module.scenario.ScenarioMessage.msg_queryBudgetProgress

/**
  * Created by clock on 18-8-2.
  */
object BudgetProgressModule extends ModuleTrait {
    val s: scenario = impl[scenario]

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryBudgetProgress(data) =>
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

        case _ => ???
    }

}
