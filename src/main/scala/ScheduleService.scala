
import java.util.UUID

import com.twitter.finagle.http.Status
import io.circe.generic.auto._
import io.finch.circe._
import io.finch._
import shapeless.HNil

case class Test(content: String)
case class Session(id: UUID, title: Title, owner: UserId)
case class Topic(id: UUID, title: Title, description: String)
case class Vote(user: UserId, list: Seq[Topic])
case class UserId(id: UUID)
case class SessionId(id: UUID)
case class Title(title: String){
  assert(title.length < 50)
}

case class CreateSessionPayload(userId: UserId, title: Title)
sealed trait SessionStatus
case object Locked extends SessionStatus
case object Open extends SessionStatus
case class Schema()

case class FinchScheduleService() {
  private val userId = uuid.as[UserId]
  private val session = "sessions" :: uuid.as[SessionId]

  val api =
    post("sessions" :: jsonBody[CreateSessionPayload] )      { createSession     } :+:
    get (session :: "status")                                { getSessionStatus  } :+:
    get (session :: "schema")                                { getSchema         } :+:
    put (session :: "status" :: jsonBody[Status])            { setSessionStatus  } :+:
    get (session :: "topics" )                               { listTopics        } :+:
    post(session :: "topics" :: jsonBody[Topic])             { createTopic       } :+:
    put (session :: "votes"  :: userId :: jsonBody[Vote])    { placeVote         }


  private def createSession = (payload: CreateSessionPayload) => Ok(Session(UUID.randomUUID(), payload.title, owner = payload.userId))
  private def getSessionStatus = (sessionId: SessionId) => Output.unit(Status.Locked)
  private def setSessionStatus = (sessionId: SessionId, status: Status) => Ok()
  private def getSchema = (sessionId: SessionId) =>  Output.unit(Status.Locked)
  private def createTopic = (sessionId: SessionId, topic: Topic) => Ok()
  private def listTopics = (sessionId: SessionId) => Ok(Seq.empty[Topic])
  private def placeVote = (sessionId: SessionId, userId: UserId, vote: Vote) => Ok()


}
