package scenario

import module.common.processor
import module.common.processor._
import scenario.ScenarioMessage.{msg_queryScenario, _}
import play.api.libs.json.{JsObject, JsValue}
import module.common.transform.drTrait
import com.pharbers.bmpattern.ModuleTrait
import com.mongodb.casbah.Imports.DBObject
import com.pharbers.pharbersmacro.CURDMacro.queryMacro
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}
import module.GoodsMessage.msg_queryMultiGoodsByScenario
import module.HospMessage.msg_queryMultiHospByScenario
import module.RepMessage.msg_queryMultiRepByScenario
import module.ResourceMessage.msg_queryMultiResourceByScenario

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
                msg_queryMultiGoodsByScenario (jv) ::
                Nil

    case class msg_queryScenario(data: JsValue) extends msg_ScenarioCommand
    case class msg_formatQueryHospLst(data: JsValue) extends msg_ScenarioCommand
    case class msg_formatQueryBudget(data: JsValue) extends msg_ScenarioCommand
    case class msg_formatQueryHumans(data: JsValue) extends msg_ScenarioCommand
    case class msg_formatQueryHospitalDetails(data: JsValue) extends msg_ScenarioCommand

}

object ScenarioModule extends ModuleTrait with FormatScenarioTrait {

    val scenario = scenarios()

    import scenario._


    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryScenario(data: JsValue) =>
            queryMacro(qc, cdr, data, db_name, name)
        case msg_formatQueryHospLst(data: JsValue) =>
            format(pr)(formatHospitals)
        case msg_formatQueryBudget(_) => format(pr)(formatBudget)
        case msg_formatQueryHumans(_) => format(pr)(formatHumans)
        case msg_formatQueryHospitalDetails(data) =>
            format(pr.map( _ ++: data.as[JsObject].value.toMap).orElse(pr))(formatHospitalDetails)

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
}