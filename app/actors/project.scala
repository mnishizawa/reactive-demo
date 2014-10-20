package actors

import akka.actor.{Actor, Props, ActorRef}
import data._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.Future

sealed trait EntryEvent
case class UpdateProject(name: String) extends EntryEvent

object ProjectActor {
  def props(out: ActorRef, datastoreRef: Datastore[TimeEntry]) = Props(new ProjectActor(out, datastoreRef))
}

class ProjectActor(out: ActorRef, datastoreRef: Datastore[TimeEntry]) extends Actor {

  var project: Option[String] = None

  def receive = {
    case UpdateProject(pName) => project match {
      case Some(p) => if (p.equalsIgnoreCase(pName)) {
        getDataAsJson(pName) map { data => out ! data }
      }

      case None => Logger.debug("Not watching %s, ignoring message update".format(pName))
    }

    case s: String => datastoreRef.find(s) map { pf =>
        if (!pf.isEmpty) {
          project = Some(s)
          Logger.debug("value of project is %s watched by ref %s".format(project.get, self.path))
          getDataAsJson(s) map {
            data =>
              out ! data
          }
        } else {
          out ! "I don't know what %s is".format(s)
        }
    }
  }

  def getDataAsJson(projectName: String): Future[JsValue] =
    datastoreRef.find(projectName).map { px => Json.toJson(px) }


  override def postStop() {
    Logger.warn("Socket no longer accepting events, shutting down actor " + self.path)
  }
}
