package scenario

import module.common.processor
import module.common.processor._
import scenario.ScenarioMessage._
import com.mongodb.casbah.Imports._
import module.common.transform.drTrait
import com.pharbers.bmpattern.ModuleTrait
import play.api.libs.json.{JsString, JsValue}
import module.RepMessage.msg_queryMultiRepByScenario
import com.pharbers.pharbersmacro.CURDMacro.{queryMacro, queryMultiMacro}
import module.HospMessage.msg_queryMultiHospByScenario
import module.GoodsMessage.msg_queryMultiGoodsByScenario
import module.ResourceMessage.msg_queryMultiResourceByScenario
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

/**
  * Created by clock on 18-8-2.
  * des: 场景功能转发
  */
abstract class msg_ScenarioCommand extends CommonMessage("scenario", ScenarioModule)

object ScenarioMessage {
    def msg_queryScenarioDetails(jv: JsValue): List[CommonMessage] = msg_queryScenario(jv) ::
            msg_queryMultiHospByScenario(jv) ::
            msg_queryMultiRepByScenario(jv) ::
            msg_queryMultiResourceByScenario(jv) ::
            msg_queryMultiGoodsByScenario(jv) ::
            Nil

    case class msg_queryScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryMultiScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryQueryHospLst(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryBudgetProgress(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryHospitalDetail(data: JsValue) extends msg_ScenarioCommand

}

object ScenarioModule extends ModuleTrait with FormatScenarioTrait {

    val scenario = scenarios()

    import scenario._


    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryScenario(data: JsValue) =>
            queryMacro(qc, cdr, data, db_name, name)

        case msg_queryMultiScenario(data: JsValue) => {
            queryMultiMacro(qcm, cdr, data, db_name, name)
        }

        case msg_queryQueryHospLst(_: JsValue) =>
            format(pr)(formatHospitals)

        case msg_queryBudgetProgress(_) =>
            format(pr)(formatBudget)

        case msg_queryHospitalDetail(data) =>
            val hosp_id = (data \ "data" \ "condition" \ "hospital_id").as[JsString]
            format(Some(pr.get ++ Map("hosp_id" -> hosp_id)))(formatHospitalDetails)

        case _ => ???
    }

}

case class scenarios() extends drTrait {
    val db_name = "scenarios"
    val name = "scenario"

    val qc: JsValue => DBObject = { js =>
        val tmp = (js \ "data" \ "condition" \ "uuid").asOpt[String].get
        DBObject("uuid" -> tmp)
    }

    val qcm: JsValue => DBObject = { js =>
        val user_id = (js \ "data" \ "condition" \ "user_id").asOpt[String].get

        (js \ "data" \ "condition" \ "proposals").asOpt[List[String]] match {
            case None => DBObject("query" -> "none")
            case Some(ll) => $or(ll map (x => DBObject("user_id" -> user_id, "proposal_id" -> x)))
        }
    }
}