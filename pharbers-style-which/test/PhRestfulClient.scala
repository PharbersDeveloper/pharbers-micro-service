import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import scala.concurrent.{ExecutionContext, Future}

class PhRestfulClient(ws: WSClient, baseUrl: String = "http://127.0.0.1:9000")(implicit ec: ExecutionContext) {
    @Inject def this(ws: WSClient, ec: ExecutionContext) = this(ws, "http://127.0.0.1:9000")(ec)

    def pushCompany(company: JsValue): Future[JsValue] = {
        ws.url(baseUrl + "/api/company/push")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "token" -> toJson(""),
                    "timestamp" -> toJson(1530689119000L),
                    "version" -> toJson(Map("major" -> toJson(1), "minor" -> toJson(0))),
                    "data" -> toJson(Map(
                        "type" -> toJson("company"),
                        "company" -> company
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

    def updateCompany(company_id: String, company: JsValue): Future[JsValue] = {
        ws.url(baseUrl + "/api/company/update")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "token" -> toJson(""),
                    "timestamp" -> toJson(1530689119000L),
                    "version" -> toJson(Map("major" -> toJson(1), "minor" -> toJson(0))),
                    "data" -> toJson(Map(
                        "type" -> toJson("company"),
                        "condition" -> toJson(Map("company_id" -> toJson(company_id))),
                        "company" -> company
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

    def queryCompany(company_id: String): Future[JsValue] = {
        ws.url(baseUrl + "/api/company/query")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "token" -> toJson(""),
                    "timestamp" -> toJson(1530689119000L),
                    "version" -> toJson(Map("major" -> toJson(1), "minor" -> toJson(0))),
                    "data" -> toJson(Map(
                        "type" -> toJson("company"),
                        "condition" -> toJson(Map("company_id" -> toJson(company_id)))
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

    def queryCompanyMulti(company_id_lst: List[String]): Future[JsValue] = {
        ws.url(baseUrl + "/api/company/query/multi")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "token" -> toJson(""),
                    "timestamp" -> toJson(1530689119000L),
                    "version" -> toJson(Map("major" -> toJson(1), "minor" -> toJson(0))),
                    "data" -> toJson(Map(
                        "type" -> toJson("company"),
                        "condition" -> toJson(Map("companies" -> toJson(company_id_lst)))
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

    def popCompany(company_id: String): Future[JsValue] = {
        ws.url(baseUrl + "/api/company/pop")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "token" -> toJson(""),
                    "timestamp" -> toJson(1530689119000L),
                    "version" -> toJson(Map("major" -> toJson(1), "minor" -> toJson(0))),
                    "data" -> toJson(Map(
                        "type" -> toJson("company"),
                        "condition" -> toJson(Map("company_id" -> toJson(company_id)))
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

}
