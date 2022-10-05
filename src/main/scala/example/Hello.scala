package example

import io.getquill._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import shapeless.tag
import shapeless.tag.@@




object Hello extends Greeting with App {
  trait PIITag
  type PIIString = String @@ PIITag
  object PIIString {
    def apply(str: String) : PIIString = tag[PIITag][String](str)
    def unapply(piiStr: PIIString) : String = piiStr
  }

  def doItWithTrait(theVal: PIIString) = println(theVal)

  doItWithTrait(PIIString("Yes"))
  
  case class PII(value: String)
  case class City(id: Int, 
                  name: PIIString, 
                  countryCode: String, 
                  district: String, 
                  population: Int)
  
  case class Country(code: String, 
                     name: PIIString, 
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
  println(greeting)

  val pgDataSource = new org.postgresql.ds.PGSimpleDataSource()
  pgDataSource.setUser("raka")
  pgDataSource.setPassword("password")
  pgDataSource.setDatabaseName("raka")
  val config = new HikariConfig()
  config.setDataSource(pgDataSource)
  val ctx = new PostgresJdbcContext(LowerCase, new HikariDataSource(config))
  import ctx._

  implicit val encodePII = MappedEncoding[PIIString, String]{ piiStr =>
    println("I AM ENCODING")
    val ret: String = s"${piiStr}_xxx"
    ret
  }
  
  implicit val decodePII = MappedEncoding[String, PIIString]{ str =>
    println("I AM DECODING")
    val ret : PIIString = PIIString(str.dropRight(4))
    ret
  }

  ctx.run(query[City].insert(City(20011, lift(PIIString("kest")), "KST", "Kest City", 0)))
  val res = ctx.run(query[City]).filter(_.id == 20011)
  println(res)
}

trait Greeting {
  lazy val greeting: String = "hello"
}
