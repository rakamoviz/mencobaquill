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

  //https://scalac.io/blog/introduction-to-programming-with-zio-functional-effects/

  final class PII private (value: Either[String, String]) {
    lazy val encrypted : String = {
      println("ENKRIPTINK")
      value.fold(encryptedStr => encryptedStr, decryptedStr => s"${decryptedStr}_xxx")
    }
    lazy val decrypted : String = {
      println("DEKRIPTINK")
      value.fold(encryptedStr => encryptedStr.dropRight(4), decryptedStr => decryptedStr)
    }
  }

  object PII {
    def fromEncrypted(encryptedStr: String): PII = new PII(Left(encryptedStr)) 
    def fromDecrypted(decryptedStr: String): PII = new PII(Right(decryptedStr)) 
  }

  doItWithTrait(PIIString("Yes"))
  
  case class City(id: Int, 
                  name: PII, 
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

  implicit val encodePIIString = MappedEncoding[PIIString, String]{ piiStr =>
    println("I AM ENCODING")
    val ret: String = s"${piiStr}_xxx"
    ret
  }
  
  implicit val decodePIIString = MappedEncoding[String, PIIString]{ str =>
    println("I AM DECODING")
    val ret : PIIString = PIIString(str.dropRight(4))
    ret
  }
  
  implicit val encodePII = MappedEncoding[PII, String]{ pii =>
    println(s"ENCODING PII ${pii}")
    val ret: String = pii.encrypted
    ret
  }
  
  implicit val decodePII = MappedEncoding[String, PII]{ str =>
    println("DECODING PII")
    val ret : PII = PII.fromEncrypted(str)
    ret
  }

  ctx.run(query[City].insert(City(20016, lift(PII.fromDecrypted("pest")), "PST", "Pest City", 0)))
  val res = ctx.run(query[City]).filter(_.id == 20016)
  println(res.head)
  println(res.head.name.decrypted)
}

trait Greeting {
  lazy val greeting: String = "hello"
}
