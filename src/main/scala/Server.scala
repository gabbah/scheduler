import com.twitter.finagle.{Http, param}
import com.twitter.finagle.server.StackServer
import com.twitter.finagle.tracing.NullTracer
import com.twitter.util.Await
import io.finch.circe._
import io.circe.generic.auto._

object Server extends App {
  val port = 8080
  val cassandraSession = PersistentStorage.setupCassandra

  val service = FinchScheduleService()

  val server = Http
    .server
    .withParams(StackServer.defaultParams + param.Tracer(NullTracer))
    .serve(s":$port", service.api.toService)

  Await.result(server)
}
