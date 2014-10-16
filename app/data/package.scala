import org.joda.time.DateTime

import play.api.libs.json.{Reads, Writes, JsPath}
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._

package object data {

  case class TimeEntry(name:String, projectName:String, hours: Double, entryDate: DateTime)

  implicit val timeEntryReads: Reads[TimeEntry] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "project_name").read[String] and
      (JsPath \ "hours").read[Double] and
      (JsPath \ "entryDate").read[DateTime]
    )(TimeEntry.apply _)

  implicit val timeEntryWrites: Writes[TimeEntry] = (
      (JsPath \ "name").write[String] and
      (JsPath \ "project_name").write[String] and
      (JsPath \ "hours").write[Double] and
      (JsPath \ "entryDate").wrie[DateTime]
    )(unlift(TimeEntry.unapply))
}
