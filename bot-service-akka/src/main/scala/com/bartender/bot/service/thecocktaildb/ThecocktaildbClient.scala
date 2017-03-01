package com.bartender.bot.service.thecocktaildb

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.bartender.bot.service.common.{Config, Logging}
import com.bartender.bot.service.thecocktaildb.ThecocktaildbModel.{ThecocktaildbCommonDrinkInfo, ThecocktaildbDrink, ThecocktaildbResponse}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

trait ThecocktaildbClient {

  def cocktailsByAlcohol(alcohol: String): Seq[ThecocktaildbCommonDrinkInfo]

  def randomCocktail(): Option[ThecocktaildbDrink]

  def cocktailDetails(id: String): Option[ThecocktaildbDrink]
}

class ThecocktaildbClientHttp extends ThecocktaildbClient with ThecocktaildbJsonSupport with Config with Logging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val url = thecocktaildbConfig.getString("url")

  def cocktailsByAlcohol(alcohol: String): Seq[ThecocktaildbCommonDrinkInfo] = {
    rootLogger.info(s"request cocktails base on $alcohol")
    cocktailsRequest(s"filter.php?i=$alcohol").getOrElse(Seq.empty).map(_.commonInfo)
  }

  def randomCocktail(): Option[ThecocktaildbDrink] = {
    rootLogger.info("request random cocktail")
    cocktailRequest("random.php")
  }

  def cocktailDetails(id: String): Option[ThecocktaildbDrink] = {
    rootLogger.info(s"request cocktail[$id] details")
    cocktailRequest(s"lookup.php?i=$id")
  }

  private def cocktailsRequest(path: String): Option[Seq[ThecocktaildbDrink]] = {
    val httpRequest = HttpRequest(method = HttpMethods.GET, uri = s"$url$path")
    val response = Await.result(Http().singleRequest(httpRequest), Duration.Inf)

    rootLogger.info(s"response status: ${response.status}")

    if (response.status.isSuccess()) {
      Await.result(Unmarshal(response.entity).to[ThecocktaildbResponse], Duration.Inf).drinks
    } else {
      None
    }
  }

  private def cocktailRequest(path: String): Option[ThecocktaildbDrink] = {
    cocktailsRequest(path).map(_.headOption).getOrElse(None)
  }

}
