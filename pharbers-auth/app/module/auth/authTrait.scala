package module.auth

import java.util.Date
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import scala.collection.immutable.Map
import com.pharbers.sercuity.Sercurity
import com.pharbers.bmmessages.CommonModules
import com.pharbers.driver.PhRedisDriverImpl

/**
  * Created by clock on 18-6-7.
  */
trait authTrait {

    // 对存入 Redis 的有用数据过滤
    def encryptFilter(data: JsValue)
                     (implicit cm: CommonModules): Map[String, JsValue] = {

        val token_expire = (data \ "data" \ "condition" \ "token_expire").asOpt[Int].map(x => x).getOrElse(24 * 60 * 60) //default expire in 24h

        val user_id = (data \ "user" \ "id").as[String]
        val email = (data \ "user" \ "email").asOpt[String].getOrElse("")
        val user_name = (data \ "user" \ "user_name").asOpt[String].getOrElse("")

        val company_id = (data \ "company" \ "id").asOpt[String].getOrElse("")

        val roles = (data \ "roles").asOpt[JsArray] match {
            case Some(x) => x.value.map(x => (x \ "role_name").asOpt[String].getOrElse("")).mkString("#")
            case None => ""
        }

        Map(
            "token_expire" -> toJson(token_expire),
            "data" -> toJson(
                Map(
                    "user_id" -> toJson(user_id),
                    "email" -> toJson(email),
                    "user_name" -> toJson(user_name),
                    "company_id" -> toJson(company_id),
                    "roles" -> toJson(roles)
                )
            )
        )
    }

    // 将 pr 全部存入 Reids
    def setExpire(pr: Option[Map[String, JsValue]])
                 (implicit cm: CommonModules): Map[String, JsValue] = {
        val rd = cm.modules.get.get("rd").map(x => x.asInstanceOf[PhRedisDriverImpl]).getOrElse(throw new Exception("no redis connection"))
        val token_expire = pr.get("token_expire").asInstanceOf[JsNumber].value
        val data = pr.get("data").asInstanceOf[JsObject].value

        val uid = data("user_id").asInstanceOf[JsString].value
        val accessToken = "bearer" + Sercurity.md5Hash(uid + new Date().getTime)

        data.foreach { x =>
            x._2 match {
                case number: JsNumber => rd.addMap(accessToken, x._1, number.value.toLong)
                case str: JsString => rd.addMap(accessToken, x._1, str.value)
                case _ => rd.addMap(accessToken, x._1, x._2.toString())
            }
        }
        rd.expire(accessToken, token_expire.toInt)

        Map("user_token" -> toJson(accessToken))
    }

    // 根据 data 中的 token 去 Redis 找到之前存入的全部信息
    def parseExpire(data: JsValue)
                   (implicit cm: CommonModules): Map[String, JsValue] = {

        val rd = cm.modules.get.get("rd").map(x => x.asInstanceOf[PhRedisDriverImpl]).getOrElse(throw new Exception("no redis connection"))
        val token = (data \ "token").asOpt[String].get

        if (!rd.exsits(token)) throw new Exception("token expired")

        Map("user" -> toJson(rd.getMapAllValue(token).map(x => x._1 -> toJson(x._2))))
    }
}
