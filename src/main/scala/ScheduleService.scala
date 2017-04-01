
import java.util.UUID

import com.twitter.finagle.http.Status
import io.circe.generic.auto._
import io.finch.circe._
import io.finch._

case class Resource(id: UUID, name: String)
case class Session(id: UUID, title: Title, owner: UserId, resources: Seq[Resource], timeSlots: Int)
case class Topic(id: UUID, title: Title, description: String)
case class Vote(user: UserId, list: Seq[Topic])
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

case class FinchScheduleService() {
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


  def createSession = (payload: CreateSessionPayload) => Ok(Session(
    UUID.randomUUID(),
    Title(payload.title),
    owner = UserId(payload.userId),
    payload.resources.map(Resource(UUID.randomUUID(), _)),
    payload.timeSlots)
  )
  def getSessionStatus = (sessionId: SessionId) => Output.unit(Status.Locked)
  def setSessionStatus = (sessionId: SessionId, status: Status) => Ok()
  def getSchema = (sessionId: SessionId) =>  Output.unit(Status.Locked)
  def createTopic = (sessionId: SessionId, topic: Topic) => Ok()
  def listTopics = (sessionId: SessionId) => Ok(Seq.empty[Topic])
  def placeVote = (sessionId: SessionId, userId: UserId, vote: Vote) => Ok()


}
