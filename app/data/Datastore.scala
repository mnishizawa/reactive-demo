package data

import scala.collection.mutable._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * For our purposes this will be a fine implementation of a datastore
 *
 * @tparam A
 */
class Datastore[A] {

  val data = new HashMap[String, Set[A]] with MultiMap[String,A]

  def find(key:String):Future[Set[A]] = future {
      data.get(key).getOrElse(Set.empty)
  }


  def insert(key: String, value: A ) = {
    data.addBinding(key, value)
  }

}
