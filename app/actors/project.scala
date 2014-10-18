package actors

import akka.actor.{Actor, Props, ActorRef}
import data._

object ProjectActor {
  def props(out: ActorRef, project: String, datastoreRef: Datastore[TimeEntry]) = Props(new ProjectActor(out, project, datastoreRef))
}

class ProjectActor(out: ActorRef, project: String, datastoreRef: Datastore[TimeEntry]) extends Actor {

  def receive = {
    case WatchProject(p) => {
      if (project.equalsIgnoreCase(p)) {
        out ! datastoreRef.find(project)
      }
    }
  }

  override def postStop() {
    println("Socket no longer accepting events, shutting down actor")
  }
}
