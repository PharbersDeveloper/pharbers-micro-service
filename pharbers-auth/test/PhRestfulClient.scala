import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import scala.concurrent.{ExecutionContext, Future}

class PhRestfulClient(ws: WSClient, baseUrl: String = "http://127.0.0.1:9000")(implicit ec: ExecutionContext) {
    @Inject def this(ws: WSClient, ec: ExecutionContext) = this(ws, "http://127.0.0.1:9000")(ec)

    def tokenEncrypt(user: JsValue, company: JsValue, roles: JsValue): Future[JsValue] = {
        ws.url(baseUrl + "/api/auth/encrypt")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "timestamp" -> toJson(1530689119000L),
                    "user" -> user,
                    "company" -> company,
                    "roles" -> roles
                ))
            )
            .map { response =>
                response.json
            }
    }

    def tokenParse(token: String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/auth/parse")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "token" -> toJson(token)
                ))
            )
            .map { response =>
                response.json
            }
    }

}
