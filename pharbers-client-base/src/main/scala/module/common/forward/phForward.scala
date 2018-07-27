package module.common.forward

import com.pharbers.http.{HTTP, httpOpt}
import play.api.libs.json.{JsObject, JsValue}
import com.pharbers.baseModules.PharbersInjectModule

trait phForward extends PharbersInjectModule {

    val module_name: String

    override val id: String = module_name
    override val configPath: String = "pharbers_config/routes_config.xml"
    override val md: List[String] = "host" :: "port" :: Nil

    def host: String = config.mc.find(p => p._1 == "host").get._2.toString
    def port: String = config.mc.find(p => p._1 == "port").get._2.toString

    implicit val j2pr: JsValue => (Option[Map[String, JsValue]], Option[JsValue]) =
        jv => (Some(jv.asInstanceOf[JsObject].value.toMap), None)

    def forward(api: String): httpOpt =
        HTTP(s"http://$host:$port$api").header("Accept" -> "application/json", "Content-Type" -> "application/json")

}