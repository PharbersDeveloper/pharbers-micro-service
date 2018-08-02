package module

import module.CompanyMessage._
import module.common.repeater
import module.common.repeater._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.forward.phForward
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-7-31.
  * des: 公司功能转发
  */
abstract class msg_CompanyCommand extends CommonMessage("company", CompanyModule)

object CompanyMessage {

    case class msg_pushCompany(data: JsValue) extends msg_CompanyCommand

    case class msg_popCompany(data: JsValue) extends msg_CompanyCommand

    case class msg_updateCompany(data: JsValue) extends msg_CompanyCommand

    case class msg_queryCompany(data: JsValue) extends msg_CompanyCommand

    case class msg_queryCompanyByBind(data: JsValue) extends msg_CompanyCommand

    case class msg_queryCompanyMulti(data: JsValue) extends msg_CompanyCommand

}

object CompanyModule extends ModuleTrait {

    val c = company()

    import c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        // TODO to be tested
        case msg_pushCompany(data: JsValue) =>
            repeater((d, _) => forward("/api/company/push").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_popCompany(data: JsValue) =>
            repeater((d, _) => forward("/api/company/pop").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_updateCompany(data: JsValue) =>
            repeater((d, _) => forward("/api/company/update").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_queryCompany(data: JsValue) =>
            repeater((d, p) => forward("/api/company/query").post(d))(mergeResult)(data, pr)

        // TODO to be tested
        case msg_queryCompanyMulti(data: JsValue) =>
            repeater((d, _) => forward("/api/company/query/multi").post(d))(mergeResult)(data, pr)

        case msg_queryCompanyByBind(data: JsValue) =>
            repeater((_, p) => forward("/api/company/query").post(mergeCond(p)))(mergeResult)(data, pr)

        case _ => ???
    }

}

case class company() extends phForward {
    override lazy val module_name: String = "company"

    def mergeCond: Option[Map[String, JsValue]] => JsValue = { p =>
        val id = p.get("binds").as[List[Map[String, JsValue]]].map(_ ("company_id")).head

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("company"),
                "condition" -> toJson(Map(
                    "company_id" -> toJson(id)
                ))
            ))
        ))
    }
}