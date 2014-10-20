package controllers


import play.api.Play.current
import play.api.mvc._
import data._
import actors._
import play.api.libs.json.{Json, JsValue}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{PoisonPill, ActorSystem}
import play.api.libs.concurrent.Akka

object Application extends Controller {

  val data = new Datastore[TimeEntry]

  val actorSystem = ActorSystem()

  def index = Action {
    Ok(views.html.Index())
  }

  /**
   * Add time to a project
   * @return
   */
  def addTime = Action(BodyParsers.parse.json) {
    request =>
      request.body.validate[TimeEntry].asOpt match {
        case Some(t) => {
          data.insert(t.projectName, t)
          Akka.system.actorSelection("/system/websockets/*/handler") ! UpdateProject(t.projectName)

          Ok("Time entry saved")
        }
        case None => BadRequest
      }
  }

  def listProjects() = Action.async {
    data.listKeys() map { o => Ok(Json.toJson(o)) }
  }


  /**
   * Static REST access to the time for the named project.
   *
   * @param projectName
   * @return
   */
  def getProjectTime(projectName: String) = Action.async {
    data.find(projectName) map { ts =>
      Ok(Json.toJson(ts))
    }
  }

  /**
   * Create an actor to manage observation of time related to the project
   * @return
   */
  def observeProject() = WebSocket.acceptWithActor[String,JsValue] { request => out =>
    ProjectActor.props(out, data)
  }
}
