package example

import io.getquill._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

case class PII(value: String)

case class City(id: Int, 
                name: PII, 
                countryCode: String, 
                district: String, 
                population: Int)

case class Country(code: String, 
                   name: PII, 
                   continent: String, 
                   region: String,
                   surfaceArea: Double,
                   indepYear: Option[Int],
                   population: Int,
                   lifeExpectancy: Option[Double],
                   gnp: Option[scala.math.BigDecimal],
                   gnpold: Option[scala.math.BigDecimal],
                   localName: String,
                   governmentForm: String,
                   headOfState: Option[String],
                   capital: Option[Int],
                   code2: String)

case class CountryLanguage(countrycode: String, 
                           language: String,
                           isOfficial: Boolean,
                           percentage: Double)


object Hello extends Greeting with App {
  println(greeting)

  val pgDataSource = new org.postgresql.ds.PGSimpleDataSource()
  pgDataSource.setUser("raka")
  pgDataSource.setPassword("password")
  pgDataSource.setDatabaseName("raka")
  val config = new HikariConfig()
  config.setDataSource(pgDataSource)
  val ctx = new PostgresJdbcContext(LowerCase, new HikariDataSource(config))
  import ctx._

  implicit val encodePII = MappedEncoding[PII, String]{value =>
    println("I AM ENCODING")
    val ret: String = s"${value.value}_xxx"
    ret
  }
  
  implicit val decodePII = MappedEncoding[String, PII]{ value =>
    println("I AM DECODING")
    val ret : PII = PII(value.dropRight(4))
    ret
  }

  val res = ctx.run(query[City]).filter(_.id == 3208)
  println(res)

  ctx.run(query[City].insert(City(20004, PII("dest"), "DST", "Dest City", 0)))
}

trait Greeting {
  lazy val greeting: String = "hello"
}
