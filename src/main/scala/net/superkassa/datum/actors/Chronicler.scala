package net.superkassa.datum.actors

import akka.persistence.PersistentActor

case class Chronicler() extends PersistentActor {
  override def receiveRecover: Receive = ???
  override def receiveCommand: Receive = ???
  override def persistenceId: String = getClass.getSimpleName
}
