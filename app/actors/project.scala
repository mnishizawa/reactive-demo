package actors

import akka.actor.{Actor, Props, ActorRef}
import data._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.libs.json.Json

object ProjectActor {
  def props(out: ActorRef, datastoreRef: Datastore[TimeEntry]) = Props(new ProjectActor(out, datastoreRef))
}

object Refs {
  var socketRef:Option[ActorRef] = None
}

class ProjectActor(out: ActorRef,  datastoreRef: Datastore[TimeEntry]) extends Actor {

  var project:Option[String] = None

  def receive = {
    case NotifyAll(pName) => context.system.actorSelection("/system/websockets/*/handler") ! UpdateProject(pName)
    case UpdateProject(pName) => {
      project match {
        case Some(p) => {
          if(p.equalsIgnoreCase(pName)) {
            datastoreRef.find(pName).map { px =>
              val jsonResponse = Json.toJson(px)
              out ! jsonResponse
            }
          }
        }
        case None => Logger.debug("Not watching %s, ignoring message update".format(pName))
      }

    }

    case s:String =>  datastoreRef.find(s) map { pf =>
        if(!pf.isEmpty) {
          project = Some(s)
          out ! "Now watching " + s
          Logger.debug("value of project is %s watched by ref %s".format(project.get, self.path ))
        } else {
          Logger.error("I don't know what %s is".format(s))
          out ! "I don't know what %s is".format(s)
        }
      }

  }

  override def postStop() {
    Logger.warn("Socket no longer accepting events, shutting down actor " + self.path)
  }
}
