package controllers.common

import play.api.mvc._
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.Inject
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.json.JsValue
import akka.actor.{ActorSystem, Props}
import play.api.libs.Files.TemporaryFile
import com.pharbers.bmpattern.RoutesActor
import com.pharbers.bmmessages.{MessageRoutes, excute}

object requestArgsQuery {
	def apply()(implicit akkasys : ActorSystem, cc: ControllerComponents) = new requestArgsQuery()
}

class requestArgsQuery @Inject() (implicit akkasys : ActorSystem, cc: ControllerComponents) extends AbstractController(cc) {
	implicit val t: Timeout = Timeout(5 second)

	def requestArgs(request : Request[AnyContent])(func : JsValue => MessageRoutes) : Result = {
		try {
			request.body.asJson.map { x =>
				Ok(commonExcution(func(x)))
			}.getOrElse (BadRequest("Bad Request for input"))
		} catch {
			case _ : Exception => BadRequest("Bad Request for input")
		}
	}

	def commonExcution(msr : MessageRoutes) : JsValue = {
		val act = akkasys.actorOf(Props[RoutesActor])
		val r = act ? excute(msr)
		Await.result(r.mapTo[JsValue], t.duration)
	}

	def uploadRequestArgs(request : Request[AnyContent])(func : MultipartFormData[TemporaryFile] => JsValue) : Result = {
		try {
			request.body.asMultipartFormData.map { x =>
				Ok(func(x))
			}.getOrElse (BadRequest("Bad Request for input"))
		} catch {
			case _ : Exception => BadRequest("Bad Request for input")
		}
	}
}