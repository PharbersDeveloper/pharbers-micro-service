
import scala.concurrent.Await
import play.api.libs.json.JsValue
import play.api.test.WsTestClient
import scala.concurrent.duration._
import org.specs2.matcher.MatchResult
import play.api.libs.json.Json.toJson
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}

import scala.concurrent.ExecutionContext.Implicits.global

class PhCompanySpec extends Specification with BeforeAll with AfterAll {
    val time_out: FiniteDuration = 2 second
    var company_id: String = ""

    override def beforeAll(): Unit = pushCompanyTest
    override def afterAll(): Unit = popCompanyTest

    override def is = s2"""
        This is a max to check the restful logic string

            The 'max' adding user functions should
                update company                $updateCompanyTest
                query company                 $queryCompanyTest
                query company multi           $queryCompanyMultiTest
                                                                              """

    lazy val company_map: Map[String, String] = Map(
        "company_name" -> "test_name",
        "company_des" -> "test_des"
    )

    def pushCompanyTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new PhRestfulClient(client, "http://127.0.0.1:9000").pushCompany(
                toJson(company_map)
            ), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result" \ "company").asOpt[JsValue].get
            company_id = (result \ "id").asOpt[String].get
            company_id.length must_!= 0
        }
    }

    def updateCompanyTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new PhRestfulClient(client, "http://127.0.0.1:9000").updateCompany(
                company_id,
                toJson(company_map)
            ), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result" \ "company").asOpt[JsValue].get
            val result_id = (result \ "id").asOpt[String].get
            result_id must_== company_id
        }
    }

    def queryCompanyTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new PhRestfulClient(client, "http://127.0.0.1:9000").queryCompany(
                company_id
            ), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result" \ "company").asOpt[JsValue].get
            val result_id = (result \ "id").asOpt[String].get
            result_id must_== company_id
            (result \ "company_name").asOpt[String].get must_== company_map("company_name")
            (result \ "company_des").asOpt[String].get must_== company_map("company_des")
        }
    }

    def queryCompanyMultiTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new PhRestfulClient(client, "http://127.0.0.1:9000").queryCompanyMulti(
                company_id :: Nil
            ), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result" \ "companies").asOpt[List[JsValue]].get.head
            val result_id = (result \ "id").asOpt[String].get
            result_id must_== company_id
            (result \ "company_name").asOpt[String].get must_== company_map("company_name")
            (result \ "company_des").asOpt[String].get must_== company_map("company_des")
        }
    }

    def popCompanyTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new PhRestfulClient(client, "http://127.0.0.1:9000").popCompany(
                company_id
            ), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"
        }
    }

}