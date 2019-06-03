package module

import module.RepMessage._
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.forward.phForward
import module.utilTrait.extractIDTrait
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-2.
  * des: 代表功能转发
  */
abstract class msg_RepCommand extends CommonMessage("representative", RepModule)

object RepMessage {

    case class msg_queryMultiRepByScenario(data: JsValue) extends msg_RepCommand

    case class msg_formatRepInfo(data: JsValue) extends msg_RepCommand

}

object RepModule extends ModuleTrait {
    val rep = representative()

    import rep._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryMultiRepByScenario(data: JsValue) =>
            repeater((_, p) => forward("/api/rep/query/multi").post(mergeCond(p)))(mergeResult)(data, pr)

        case msg_formatRepInfo(data: JsValue) =>
            formatRepInfo(data, pr.get)

        case _ => ???
    }

}

case class representative() extends phForward with extractIDTrait {
    override lazy val module_name: String = "representative"

    def mergeCond: Option[Map[String, JsValue]] => JsValue = { p =>
        val idLst = extractID("connect_rep")(p)

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("reps"),
                "condition" -> toJson(Map(
                    "reps" -> toJson(idLst)
                ))
            ))
        ))
    }

    def formatRepInfo(data: JsValue, pr: Map[String, JsValue]): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val uuid = (data \ "data" \ "condition" \ "uuid").as[String]
        val reps = pr("reps").as[List[Map[String, JsValue]]]

        val repScoreLst = reps.map{ rep =>
            Map(
                "id" -> rep("id"),
                "name" -> rep("rep_name"),
                "rep_image" -> rep("rep_image"),
                "ability" -> toJson(List(
                    Map("title" -> toJson("总能力值"), "value" -> rep("overall_val")),
                    Map("title" -> toJson("产品知识"), "value" -> rep("prod_knowledge_val")),
                    Map("title" -> toJson("销售技巧"), "value" -> rep("sales_skills_val")),
                    Map("title" -> toJson("工作积极性"), "value" -> rep("motivation_val"))
                ))
            )
        }
        val rep_score = Map(
            "key" -> toJson("target-representative-card"),
            "values" -> toJson(repScoreLst)
        )

        val repDetailLst = reps.map{ rep =>
            Map(
                "id" -> rep("id"),
                "lable" -> toJson(Map(
                    "name" -> rep("rep_name"),
                    "rep_image" -> rep("rep_image"),
                    "age" -> rep("age"),
                    "education" -> rep("education"),
                    "profe_bg" -> rep("profe_bg"),
                    "service_year" -> rep("service_year"),
                    "entry_time" -> rep("entry_time")
                )),
                "self_business" -> toJson(List(
                    Map("title" -> toJson("业务经验"), "value" -> toJson(rep("business_exp").as[String].split(";"))),
                    Map("title" -> toJson("优势"), "value" -> toJson(rep("advantage").as[String].split(";"))),
                    Map("title" -> toJson("待提高"), "value" -> toJson(rep("weakness").as[String].split(";")))
                )),
                "score" -> toJson(List(
                    Map("title" -> toJson("产品知识"), "value" -> rep("prod_knowledge_val")),
                    Map("title" -> toJson("销售技巧"), "value" -> rep("sales_skills_val")),
                    Map("title" -> toJson("工作积极性"), "value" -> rep("motivation_val"))
                ))
            )
        }
        val rep_detail = Map(
            "key" -> toJson("target-rep-detail"),
            "values" -> toJson(repDetailLst)
        )

        val component_data = rep_score :: rep_detail ::Nil

        val result = Map(
            "id" -> toJson(uuid),
            "component_data" -> toJson(component_data)
        )

        (Some(Map("result" -> toJson(result))), None)
    }
}

