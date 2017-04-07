
import java.util.UUID

import com.twitter.finagle.http.Status
import com.twitter.util.Future
import io.circe.generic.auto._
import io.finch.circe._
import io.finch._

case class Resource(id: UUID, name: String)
case class Session(id: UUID, title: Title, owner: UserId, resources: Seq[Resource], timeSlots: Int, status: SessionStatus)
case class Topic(id: UUID, title: Title, description: String)
case class Vote(userId: UserId, list: Seq[Topic])
case class UserId(id: UUID)
case class SessionId(id: UUID)
case class Title(title: String){
  assert(title.length < 50)
}

case class CreateSessionPayload(userId: UUID, title: String, resources: Seq[String], timeSlots: Int)
sealed trait SessionStatus
case object Locked extends SessionStatus
case object Open extends SessionStatus
case class Schema()

case class FinchScheduleService(repo: Repository) {
  val userId = uuid.as[UserId]
  val session = "sessions" :: uuid.as[SessionId]

  val api =
    post("sessions" :: jsonBody[CreateSessionPayload] )      { createSession     } :+:
    get (session :: "status")                                { getSessionStatus  } :+:
    get (session :: "schema")                                { getSchema         } :+:
    put (session :: "status" :: jsonBody[Status])            { setSessionStatus  } :+:
    get (session :: "topics" )                               { listTopics        } :+:
    post(session :: "topics" :: jsonBody[Topic])             { createTopic       } :+:
    put (session :: "votes"  :: userId :: jsonBody[Vote])    { placeVote         }


  def createSession = (payload: CreateSessionPayload) => repo.store(
    Session(
      UUID.randomUUID(),
      Title(payload.title),
      owner = UserId(payload.userId),
      payload.resources.map(Resource(UUID.randomUUID(), _)),
      payload.timeSlots,
      Open)
  ).map(_ =>  Ok() ) //.rescue(Future(Conflict(_)))
  def getSessionStatus = (sessionId: SessionId) => Future(Output.unit(Status.Locked))
  def setSessionStatus = (sessionId: SessionId, status: Status) => Future(Ok())
  def getSchema = (sessionId: SessionId) =>  Future(Output.unit(Status.Locked))
  def createTopic = (sessionId: SessionId, topic: Topic) => Future(Ok())
  def listTopics = (sessionId: SessionId) => Future(Ok(Seq.empty[Topic]))
  def placeVote = (sessionId: SessionId, userId: UserId, vote: Vote) => Future(Ok())


}
