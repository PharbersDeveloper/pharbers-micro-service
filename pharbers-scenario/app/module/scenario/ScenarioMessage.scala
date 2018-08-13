package module.scenario

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-8-7.
  */
abstract class msg_ScenarioCommand extends CommonMessage("scenario", ScenarioModule)
abstract class msg_BudgetProgressCommand extends CommonMessage("BudgetProgress", BudgetProgressModule)
abstract class msg_HospitalListCommand extends CommonMessage("HospitalList", HospitalListModule)
abstract class msg_HospitalDetailCommand extends CommonMessage("HospitalDetail", HospitalDetailModule)
abstract class msg_AllotTaskCommand extends CommonMessage("AllotTask", AllotTaskModule)
abstract class msg_CreatePhaseCommand extends CommonMessage("CreatePhase", CreatePhaseModule)

object ScenarioMessage {

    case class msg_pushScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_popScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryScenarioByUUID(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryMultiScenario(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryMultiScenarioByUserAndProposal(data: JsValue) extends msg_ScenarioCommand

    case class msg_queryBudgetProgress(data: JsValue) extends msg_BudgetProgressCommand

    case class msg_queryHospitalList(data: JsValue) extends msg_HospitalListCommand

    case class msg_queryHospitalDetail(data: JsValue) extends msg_HospitalDetailCommand

    case class msg_allotRepTask(data: JsValue) extends msg_AllotTaskCommand

    case class msg_allotManagerTask(data: JsValue) extends msg_AllotTaskCommand

    case class msg_report2current(data: JsValue) extends msg_CreatePhaseCommand

    case class msg_current2past(data: JsValue) extends msg_CreatePhaseCommand

    case class msg_proposal2current(data: JsValue) extends msg_CreatePhaseCommand

}