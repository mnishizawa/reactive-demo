package actors

import data.TimeEntry

sealed trait EntryEvent
case class AddTime(time: TimeEntry)
case class WatchProject(projectName: String)
