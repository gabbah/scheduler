
import java.util.UUID

import com.twitter.finagle.http.Status
import io.circe.generic.auto._
import io.finch.circe._
import io.finch._

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

case class FinchScheduleService() {
  private val userId: Endpoint[UserId] = uuid.map(UserId)
  private val sessionId: Endpoint[SessionId] = uuid.map(SessionId)

  private val sessionE = "sessions" :: sessionId
  val api =
    post("sessions" :: jsonBody[CreateSessionPayload] )       { createSession     } :+:
    get (sessionE :: "status")                                { getSessionStatus  } :+:
    put (sessionE :: "status" :: jsonBody[Status])            { setSessionStatus  } :+:
    get (sessionE :: "topics" )                               { listTopics        } :+:
    post(sessionE :: "topics" :: jsonBody[Topic])             { createTopic       } :+:
    put (sessionE :: "votes"  :: userId :: jsonBody[Vote])    { placeVote         }


  private def createSession = (payload: CreateSessionPayload) => Ok(Session(UUID.randomUUID(), payload.title, owner = payload.userId))
  private def getSessionStatus = (sessionId: SessionId) => Ok(Locked)
  private def setSessionStatus = (sessionId: SessionId, status: Status) => Ok()
  private def createTopic = (sessionId: SessionId, topic: Topic) => Ok()
  private def listTopics = (sessionId: SessionId) => Ok(Seq.empty[Topic])
  private def placeVote = (sessionId: SessionId, userId: UserId, vote: Vote) => Ok()


}
