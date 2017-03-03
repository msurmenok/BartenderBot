package com.bartender.bot.service

import com.bartender.bot.service.domain.Location
import com.bartender.bot.service.fb.FbActionButtons.{BarDetailsButton, CocktailReceiptButton, ShowMoreCocktailsButton, ShowNexBarButton}
import com.bartender.bot.service.fb._
import org.scalatest.{FlatSpec, Matchers}

class FbActionButtonsTest extends FlatSpec with Matchers {

  "create show next bar button" should "FbTemplateButton" in {

    val actual = FbActionButtons.createFbTemplateButton(ShowNexBarButton(Location(55.695738, 37.624188), 1))

    actual shouldEqual FbTemplateButton(
      FbTemplateButtonsType.postback,
      title = "Show one more",
      payload = Some("SHOW_NEXT_BAR;55.695738;37.624188;1"))
  }

  "parsing show next bar payload" should "offset and location" in {

    val actual = FbActionButtons.Factory.create("SHOW_NEXT_BAR;55.695738;37.624188;1")

    actual.get shouldEqual ShowNexBarButton(Location(55.695738, 37.624188), 1)
  }

  "parsing bar details payload" should "bar id" in {

    val actual = FbActionButtons.Factory.create("BAR_DETAILS;ChIJN1t_tDeuEmsRUsoyG83frY4")

    actual.get shouldEqual BarDetailsButton("ChIJN1t_tDeuEmsRUsoyG83frY4")
  }

  "create bar details button" should "FbTemplateButton" in {

    val actual = FbActionButtons.createFbTemplateButton(BarDetailsButton("ChIJN1t_tDeuEmsRUsoyG83frY4"))

    actual shouldEqual FbTemplateButton(
      FbTemplateButtonsType.postback,
      title = "Details",
      payload = Some("BAR_DETAILS;ChIJN1t_tDeuEmsRUsoyG83frY4"))
  }

  "parsing cocktail receipt payload" should "cocktail id" in {

    val actual = FbActionButtons.Factory.create("COCKTAIL_RECEIPT;1234")

    actual.get shouldEqual CocktailReceiptButton("1234")
  }

  "create cocktail receipt button" should "FbTemplateButton" in {

    val actual = FbActionButtons.createFbTemplateButton(CocktailReceiptButton("1234"))

    actual shouldEqual FbTemplateButton(
      FbTemplateButtonsType.postback,
      title = "Receipt",
      payload = Some("COCKTAIL_RECEIPT;1234"))
  }

  "parsing show more cocktails payload" should "alcohol and offset" in {

    val actual = FbActionButtons.Factory.create("SHOW_MORE_COCKTAILS_BY_ALCOHOL;vodka;1")

    actual.get shouldEqual ShowMoreCocktailsButton("vodka", 1)
  }

  "create show more cocktails button" should "FbTemplateButton" in {

    val actual = FbActionButtons.createFbTemplateButton(ShowMoreCocktailsButton("vodka", 1))

    actual shouldEqual FbTemplateButton(
      FbTemplateButtonsType.postback,
      title = "Show more",
      payload = Some("SHOW_MORE_COCKTAILS_BY_ALCOHOL;vodka;1"))
  }
}
