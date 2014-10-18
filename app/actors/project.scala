package actors

import akka.actor.{Actor, Props, ActorRef}
import data._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

object ProjectActor {
  def props(out: ActorRef, datastoreRef: Datastore[TimeEntry]) = Props(new ProjectActor(out, datastoreRef))
}

class ProjectActor(out: ActorRef,  datastoreRef: Datastore[TimeEntry]) extends Actor {

  var project:String = ""

  def receive = {
    case WatchProject(p) => {
      Logger.debug("rec message to watch project %s".format(p))

      if (project.equalsIgnoreCase(p)) {
        out ! datastoreRef.find(project).map { px =>
          Logger.warn("found %d time entries for %s".format(px.size, project))
          px
        }
      }
    }

    case s:String => {
      datastoreRef.find(s) map { pf =>
        if(!pf.isEmpty) {
          project = s
          Logger.debug("Now watching " + s)
        } else {
          Logger.error("I don't know what %s is".format(s))
          out ! "I don't know what %s is".format(s)
        }
      }
    }
  }

  override def postStop() {
    Logger.warn("Socket no longer accepting events, shutting down actor")
  }
}
