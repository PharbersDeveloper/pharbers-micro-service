package module.resources

import module.common.processor
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import module.resources.CompanyMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

/**
  * Created by jeorch on 18-8-2.
  */
object GoodModule extends ModuleTrait {
    val c: good = impl[good]
    import c._

    override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_verifyGoodRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "good has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushGood(data) => pushMacro(d2m, dr, data, names, name)
        case msg_popGood(data: JsValue) => popMacro(qc, popr, data, names)
        case msg_updateGood(data: JsValue) => updateMacro(qc, up2m, dr, data, names, name)
        case msg_queryGood(data: JsValue) => queryMacro(qc, dr, data, names, name)
        case msg_queryGoodsMulti(data: JsValue) => queryMultiMacro(qcm, dr, data, names, names)

        case _ => ???
    }
}
