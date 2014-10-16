package controllers

import play.api.mvc._
import data._

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
          Ok("Time entry saved")
        }
        case None => BadRequest
      }
  }

  /**
   * Create an actor to manage observation of time related to the project
   * @return
   */
  def observeProject = TODO
}
