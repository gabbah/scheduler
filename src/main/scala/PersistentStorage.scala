import com.datastax.driver.core
import org.cassandraunit.CQLDataLoader
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet
import org.cassandraunit.utils.EmbeddedCassandraServerHelper

object PersistentStorage {
  def setupCassandra: core.Session = {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra(30000)
    val cluster = EmbeddedCassandraServerHelper.getCluster
    val session = EmbeddedCassandraServerHelper.getSession
    val dataLoader = new CQLDataLoader(session)
    val dataSet = new ClassPathCQLDataSet("tables.cql","scheduler")
    dataLoader.load(dataSet)
    dataLoader.getSession
  }
}