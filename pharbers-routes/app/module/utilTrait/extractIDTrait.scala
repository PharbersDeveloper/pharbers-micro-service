package module.utilTrait

import play.api.libs.json.JsValue

trait extractIDTrait {
    def extractID(connect_name: String, connect_type: String = "")(pr: Option[Map[String, JsValue]]): List[String] = {

        def getSubID(phase_data: Map[String, JsValue]): List[String] =
            phase_data(connect_name).as[List[Map[String, JsValue]]]
                .filter(x =>
                    if(connect_type != "") x("type").as[String] == connect_type
                    else true
                )
                .map(x => x("id").as[String])

        val current = (pr.get("scenario") \ "current").as[Map[String, JsValue]]
        val past = (pr.get("scenario") \ "past").as[List[Map[String, JsValue]]]

        (getSubID(current) ::: past.flatMap(getSubID)).distinct
    }
}
