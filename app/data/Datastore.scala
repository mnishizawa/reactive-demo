package data

import scala.collection.mutable._

/**
 * For our purposes this will be a fine implementation of a datastore
 *
 * @tparam A
 */
class Datastore[A] {

  val data = new HashMap[String, Set[A]] with MultiMap[String,A]

  def find(key:String):Option[Set[A]] = {
    data.get(key)
  }

  def insert(key: String, value: A ) = {
    data.addBinding(key, value)
  }

}
