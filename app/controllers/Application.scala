package controllers


import play.api.Play.current
import play.api.mvc._
import data._
import actors.{WatchProject, AddTime, ProjectActor}
import play.api.libs.json.JsValue
import play.api.libs.concurrent._

object Application extends Controller {

  val data = new Datastore[TimeEntry]

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
          Akka.system.actorSelection("/user/*") ! WatchProject(t.projectName)
          Ok("Time entry saved")
        }
        case None => BadRequest
      }
  }

  /**
   * Create an actor to manage observation of time related to the project
   * @return
   */
  def observeProject(project:String) = WebSocket.acceptWithActor[String,JsValue] { request => out =>
    ProjectActor.props(out, project, data)
  }
}
