
import java.util.UUID
import io.circe.generic.auto._
import io.finch.circe._
import io.finch._

case class Test(content: String)
case class Session(id: UUID, title: Title)
case class Topic(id: UUID, title: Title, description: String)
case class Vote(user: UserId, list: Seq[Topic])
case class UserId(id: UUID)
case class SessionId(id: UUID)
case class Title(value: String){
  assert(value.length < 50)
}

case class FinchScheduleService() {
  private val userId: Endpoint[UserId] = uuid.map(UserId)
  private val sessionId: Endpoint[SessionId] = uuid.map(SessionId)

  val api =
    post("sessions" :: jsonBody[Title] )                                     { createSession }  :+:
    get ("sessions" :: sessionId :: "topics" )                               { listTopics    }  :+:
    post("sessions" :: sessionId :: "topics" :: jsonBody[Topic])             { createTopic   }  :+:
    put ("sessions" :: sessionId :: "votes"  :: userId :: jsonBody[Vote])    { placeVote     }


  private def createSession = (title: Title) => Ok(Session(UUID.randomUUID(), title))
  private def createTopic = (sessionId: SessionId, topic: Topic) => Ok()
  private def listTopics = (sessionId: SessionId) => Ok(Seq.empty[Topic])
  private def placeVote = (sessionId: SessionId, userId: UserId, vote: Vote) => Ok()


}
