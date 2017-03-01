package com.bartender.bot.service.thecocktaildb

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bartender.bot.service.thecocktaildb.ThecocktaildbModel.{ThecocktaildbCommonDrinkInfo, ThecocktaildbDrink, ThecocktaildbIngredient, ThecocktaildbResponse}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat}

trait ThecocktaildbJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val thecocktaildbCommonDrinkInfoFormat = jsonFormat8(ThecocktaildbCommonDrinkInfo)

  implicit object ThecocktaildbDrinkJsonFormat extends RootJsonFormat[ThecocktaildbDrink] {

    def read(value: JsValue) = {
      val ingredients = (1 to 15).map { number =>
        value.asJsObject.getFields(s"strIngredient$number", s"strMeasure$number") match {
          case Seq(JsString(ingredient), JsString(measure)) => (ingredient, measure)
          case _ => ("", "")
        }
      }.filter(res => res._1.nonEmpty && res._2.nonEmpty)
        .map(res => ThecocktaildbIngredient(res._1, res._2))

      ThecocktaildbDrink(commonInfo = value.convertTo[ThecocktaildbCommonDrinkInfo], ingredients = ingredients)
    }

    override def write(obj: ThecocktaildbDrink): JsValue = ???
  }

  implicit val thecocktaildbResponseFormat = jsonFormat1(ThecocktaildbResponse)
}
