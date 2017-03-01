package com.bartender.bot.service

import com.bartender.bot.service.thecocktaildb.ThecocktaildbJsonSupport
import com.bartender.bot.service.thecocktaildb.ThecocktaildbModel.{ThecocktaildbCommonDrinkInfo, ThecocktaildbDrink, ThecocktaildbIngredient, ThecocktaildbResponse}
import org.scalatest.{FlatSpec, Matchers}
import spray.json._


class ThecocktaildbJsonSupportTest extends FlatSpec with Matchers with ThecocktaildbJsonSupport {

  "json response" should "case class ThecocktaildbResponse" in {

    val actual = textMessageResponse.parseJson.convertTo[ThecocktaildbResponse]

    actual shouldEqual ThecocktaildbResponse(drinks = Some(Seq(
      ThecocktaildbDrink(
        ThecocktaildbCommonDrinkInfo(
          idDrink = "15112",
          strDrink = "Alamo Splash",
          strCategory = Some("Ordinary Drink"),
          strAlcoholic = Some("Alcoholic"),
          strGlass = Some("Collins glass"),
          strInstructions = Some("Mix with cracked ice and strain into collins glass."),
          strDrinkThumb = None,
          dateModified = None
        ),
        Seq(
          ThecocktaildbIngredient("Tequila", "1 1/2 oz "),
          ThecocktaildbIngredient("Orange juice", "1 oz "),
          ThecocktaildbIngredient("Pineapple juice", "1/2 oz "),
          ThecocktaildbIngredient("Lemon-lime soda", "1 splash ")
        )
      )
    )))
  }


  "short json response" should "case class ThecocktaildbResponse" in {

    val actual = shortMessageResponse.parseJson.convertTo[ThecocktaildbResponse]

    actual shouldEqual ThecocktaildbResponse(drinks = Some(Seq(
      ThecocktaildbDrink(
        ThecocktaildbCommonDrinkInfo(
          idDrink = "11052",
          strDrink = "Archbishop",
          strCategory = None,
          strAlcoholic = None,
          strGlass = None,
          strInstructions = None,
          strDrinkThumb = Some("http://www.thecocktaildb.com/images/media/drink/xpqwrt1441207307.jpg"),
          dateModified = None
        ), Seq.empty),

      ThecocktaildbDrink(
        ThecocktaildbCommonDrinkInfo(
          idDrink = "12876",
          strDrink = "Berry Deadly",
          strCategory = None,
          strAlcoholic = None,
          strGlass = None,
          strInstructions = None,
          strDrinkThumb = Some("http://www.thecocktaildb.com/images/media/drink/xqutpr1461867477.jpg"),
          dateModified = None
        ), Seq.empty),

      ThecocktaildbDrink(
        ThecocktaildbCommonDrinkInfo(
          idDrink = "11255",
          strDrink = "Clove Cocktail",
          strCategory = None,
          strAlcoholic = None,
          strGlass = None,
          strInstructions = None,
          strDrinkThumb = Some("http://www.thecocktaildb.com/images/media/drink/qxvtst1461867579.jpg"),
          dateModified = None
        ), Seq.empty),

      ThecocktaildbDrink(
        ThecocktaildbCommonDrinkInfo(
          idDrink = "12388",
          strDrink = "Thriller",
          strCategory = None,
          strAlcoholic = None,
          strGlass = None,
          strInstructions = None,
          strDrinkThumb = Some("http://www.thecocktaildb.com/images/media/drink/rvuswq1461867714.jpg"),
          dateModified = None
        ), Seq.empty),

      ThecocktaildbDrink(
        ThecocktaildbCommonDrinkInfo(
          idDrink = "12518",
          strDrink = "Whisky Mac",
          strCategory = None,
          strAlcoholic = None,
          strGlass = None,
          strInstructions = None,
          strDrinkThumb = Some("http://www.thecocktaildb.com/images/media/drink/yvvwys1461867858.jpg"),
          dateModified = None
        ), Seq.empty)
    )))
  }
  
  val shortMessageResponse =
    """
      {
      "drinks": [
      {
      "strDrink": "Archbishop",
      "strDrinkThumb": "http://www.thecocktaildb.com/images/media/drink/xpqwrt1441207307.jpg",
      "idDrink": "11052"
      },
      {
      "strDrink": "Berry Deadly",
      "strDrinkThumb": "http://www.thecocktaildb.com/images/media/drink/xqutpr1461867477.jpg",
      "idDrink": "12876"
      },
      {
      "strDrink": "Clove Cocktail",
      "strDrinkThumb": "http://www.thecocktaildb.com/images/media/drink/qxvtst1461867579.jpg",
      "idDrink": "11255"
      },
      {
      "strDrink": "Thriller",
      "strDrinkThumb": "http://www.thecocktaildb.com/images/media/drink/rvuswq1461867714.jpg",
      "idDrink": "12388"
      },
      {
      "strDrink": "Whisky Mac",
      "strDrinkThumb": "http://www.thecocktaildb.com/images/media/drink/yvvwys1461867858.jpg",
      "idDrink": "12518"
      }
      ]
      }
    """.stripMargin

  val textMessageResponse =
    """
      {
      "drinks": [
      {
      "idDrink": "15112",
      "strDrink": "Alamo Splash",
      "strCategory": "Ordinary Drink",
      "strAlcoholic": "Alcoholic",
      "strGlass": "Collins glass",
      "strInstructions": "Mix with cracked ice and strain into collins glass.",
      "strDrinkThumb": null,
      "strIngredient1": "Tequila",
      "strIngredient2": "Orange juice",
      "strIngredient3": "Pineapple juice",
      "strIngredient4": "Lemon-lime soda",
      "strIngredient5": "",
      "strIngredient6": "",
      "strIngredient7": "",
      "strIngredient8": "",
      "strIngredient9": "",
      "strIngredient10": "",
      "strIngredient11": "",
      "strIngredient12": "",
      "strIngredient13": "",
      "strIngredient14": "",
      "strIngredient15": "",
      "strMeasure1": "1 1/2 oz ",
      "strMeasure2": "1 oz ",
      "strMeasure3": "1/2 oz ",
      "strMeasure4": "1 splash ",
      "strMeasure5": " ",
      "strMeasure6": " ",
      "strMeasure7": " ",
      "strMeasure8": " ",
      "strMeasure9": " ",
      "strMeasure10": "",
      "strMeasure11": "",
      "strMeasure12": "",
      "strMeasure13": "",
      "strMeasure14": "",
      "strMeasure15": "",
      "dateModified": null
      }
      ]
      }
    """
}
