package actors

sealed trait EntryEvent
case class UpdateProject(name: String) extends EntryEvent
case class NotifyAll(name: String) extends EntryEvent
