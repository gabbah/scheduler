import java.util.UUID

import com.outworkers.phantom.dsl._
import com.outworkers.phantom.dsl.ResultSet
import com.outworkers.phantom.finagle._
import com.twitter.util
import com.twitter.util.Future

object Defaults {
  val hosts = Seq("127.0.0.1")
  val connector = ContactPoint.embedded.keySpace("scheduler")
}

class Sessions extends CassandraTable[Sessions, Session] {
  object id extends UUIDColumn(this) with PartitionKey
  object owner extends UUIDColumn(this)
  object title extends StringColumn(this)
  object resources extends MapColumn[UUID, String](this)
  object timeSlots extends IntColumn(this)
  object status extends StringColumn(this)

  override def fromRow(r: Row): Session =
    Session(
      id(r),
      Title(title(r)),
      UserId(owner(r)),
      resources(r).map{ case (uuid: UUID, s: String) =>  Resource(uuid,s)}.toSeq,
      timeSlots(r),
      status(r) match { case "Open" => Open; case "Locked" => Locked}
    )
}

class Topics extends CassandraTable[Topics, Topic] {
  object session_id extends UUIDColumn(this) with PartitionKey
  object id extends UUIDColumn(this) with PrimaryKey
  object title extends StringColumn(this)
  object description extends StringColumn(this)

  override def fromRow(r: Row): Topic =
  Topic(
    id(r),
    session_id(r),
    Title(title(r)),
    description(r)
  )
}

abstract class MySessionsTable extends Sessions with RootConnector
abstract class MyTopicsTable extends Topics with RootConnector

class Repository(override val connector: KeySpaceDef) extends Database[Repository](connector) {
  implicit val s = connector.session

  object sessions extends MySessionsTable with connector.Connector
  object topics extends MyTopicsTable with connector.Connector

  def store(session: Session): Future[Session] = {
    sessions.insert
      .value(_.id, session.id)
      .value(_.resources, session.resources.map(r => (r.id, r.name)).toMap)
      .value(_.title, session.title.title)
      .value(_.owner, session.owner.id)
      .value(_.status, session.status.toString)
      .value(_.timeSlots, session.timeSlots)
      .execute()
      .map { case r: ResultSet => session}
  }

  def store(topic: Topic): Future[Topic] = {
    topics.insert
    .value(_.session_id, topic.sessionId)
    .value(_.id, topic.id)
    .value(_.title, topic.title.title)
    .value(_.description, topic.description)
    .execute()
    .map { case r: ResultSet => topic}

  }

  def getSession(sessionId: UUID): util.Future[Option[Session]] =
    sessions.select.where(_.id eqs sessionId).get()

  def getTopics(sessionId: SessionId): util.Future[List[Topic]] =
    topics.select.where(_.session_id eqs sessionId.id).collect()
}

object Repository extends Repository(Defaults.connector)