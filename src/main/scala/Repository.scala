import java.util.UUID

import com.outworkers.phantom.dsl._
import com.outworkers.phantom.finagle._
import com.twitter.util

object Defaults {
  val hosts = Seq("127.0.0.1")
  val connector = ContactPoint.embedded.keySpace("scheduler")
}

class Sessions extends CassandraTable[Sessions, Session] {
  object id extends UUIDColumn(this) with PartitionKey
  object owner extends UUIDColumn(this) with PrimaryKey
  object title extends StringColumn(this)
  object resources extends MapColumn[UUID, String](this)
  object timeSlots extends IntColumn(this)
  object status extends StringColumn(this)
}

abstract class MySessionsTable extends Sessions with RootConnector

class Repository(override val connector: KeySpaceDef) extends Database[Repository](connector) {
  implicit val s = connector.session

  object sessions extends MySessionsTable with connector.Connector

  def store(session: Session): util.Future[ResultSet] =
    sessions.insert
      .value(_.id, session.id)
      .value(_.resources, session.resources.map(r => (r.id, r.name)).toMap)
      .value(_.title, session.title.title)
      .value(_.owner, session.owner.id)
      .value(_.status, session.status.toString)
      .value(_.timeSlots, session.timeSlots)
      .execute()
}

object Repository extends Repository(Defaults.connector)