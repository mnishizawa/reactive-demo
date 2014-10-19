package data

import scala.collection._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * For our purposes this will be a fine implementation of a datastore
 *
 * @tparam A
 */
class Datastore[A] {

  val data = new mutable.HashMap[String, mutable.Set[A]] with mutable.MultiMap[String,A]

  def find(key:String):Future[Set[A]] = future {
      data.get(key).getOrElse(Set.empty)
  }


  def insert(key: String, value: A ) = {
    data.addBinding(key, value)
  }

  def listKeys():Future[Set[String]] = future ( data.keySet )

}
