package module

import module.GoodsMessage._
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
abstract class msg_GoodsCommand extends CommonMessage("goods", GoodsModule)

object GoodsMessage {

    case class msg_queryMultiGoodsByScenario(data: JsValue) extends msg_GoodsCommand

}

object GoodsModule extends ModuleTrait {
    val g = goods()
    import g._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryMultiGoodsByScenario(data: JsValue) =>
            repeater((_, p) => forward("/api/good/query/multi").post(mergeCond(p)))(mergeResult)(data, pr)

        case _ => ???
    }

}

case class goods() extends phForward with extractIDTrait {
    override lazy val module_name: String = "good"

    def mergeCond: Option[Map[String, JsValue]] => JsValue = { p =>
        val idLst = extractID("connect_goods")(p)

        toJson(Map(
            "data" -> toJson(Map(
                "type" -> toJson("goods"),
                "condition" -> toJson(Map(
                    "goods" -> toJson(idLst)
                ))
            ))
        ))
    }
}

