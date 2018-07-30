
import scala.concurrent.Await
import play.api.libs.json.{JsObject, JsValue}
import play.api.test.WsTestClient

import scala.concurrent.duration._
import org.specs2.matcher.MatchResult
import play.api.libs.json.Json.toJson
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}

import scala.concurrent.ExecutionContext.Implicits.global

class PhAuthSpec extends Specification with BeforeAll with AfterAll {
    val time_out: FiniteDuration = 2 second
    var token: String = ""

    override def beforeAll(): Unit = tokenEncryptTest
    override def afterAll(): Unit = Unit

    override def is = s2"""
        This is a max to check the restful logic string

            The 'max' adding user functions should
                parse token                $tokenParseTest
                                                                              """

    lazy val user_map: Map[String, String] = Map(
        "email" -> "test@pharbers.com",
        "user_id" -> "test_id",
        "user_name" -> "test_name"
    )

    lazy val company_map: Map[String, String] = Map(
        "company_id" -> "test_id",
        "company_name" -> "test_name",
        "company_des" -> "test_des"
    )

    lazy val roles_map: List[Map[String, String]] = Map(
        "role_id" -> "test_id",
        "role_name" -> "test_name",
        "role_des" -> "test_des",
        "role_level" -> "test_level"
    ) :: Nil

    def tokenEncryptTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new PhRestfulClient(client, "http://127.0.0.1:9000").tokenEncrypt(
                toJson(user_map),
                toJson(company_map),
                toJson(roles_map)
            ), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            token = (result \ "user_token").asOpt[String].get
            token.length must_!= 0
        }
    }

    def tokenParseTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new PhRestfulClient(client, "http://127.0.0.1:9000").tokenParse(token), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result" \ "user").asOpt[JsObject].get.value
            result.size must_== 5
        }
    }

}