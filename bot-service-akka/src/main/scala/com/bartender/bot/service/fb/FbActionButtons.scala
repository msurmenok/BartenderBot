package com.bartender.bot.service.fb

import com.bartender.bot.service.domain.Location


object FbActionButtons {

  object Tags extends Enumeration {
    type EnumA = Value
    val BAR_DETAILS, SHOW_NEXT_BAR, COCKTAIL_RECEIPT, SHOW_MORE_COCKTAILS = Value
  }

  private val PARAMS_DIVIDER = ";"

  def createFbTemplateButton(fbActionButton: FbActionButton[_]): FbTemplateButton = FbTemplateButton(
    FbTemplateButtonsType.postback,
    title = fbActionButton.title,
    payload = Some(s"${fbActionButton.tag}${fbActionButton.paramsToStr().mkString(PARAMS_DIVIDER, PARAMS_DIVIDER, "")}"))

  abstract class FbActionButton[Params](val params: Params, val title: String, val tag: Tags.Value) {
    def paramsToStr(): Seq[String]
  }

  case class BarDetailsButton(barId: String) extends FbActionButton[String](
    params = barId,
    title = "Details",
    tag = Tags.BAR_DETAILS
  ) {

    def paramsToStr(): Seq[String] = Seq(barId)
  }

  case class ShowNexBarButton(location: Location, offset: Int) extends FbActionButton[(Location, Int)](
    params = (location, offset),
    title = "Show one more",
    tag = Tags.SHOW_NEXT_BAR
  ) {

    def paramsToStr(): Seq[String] = Seq(location.lat.toString, location.lng.toString, offset.toString)
  }

  case class CocktailReceiptButton(cocktailId: String) extends FbActionButton[String](
    params = cocktailId,
    title = "Receipt",
    tag = Tags.COCKTAIL_RECEIPT
  ) {

    def paramsToStr(): Seq[String] = Seq(cocktailId)
  }

  case class ShowMoreCocktailsButton(alcohol: String, offset: Int) extends FbActionButton[(String, Int)](
    params = (alcohol, offset),
    title = "Show more",
    tag = Tags.SHOW_MORE_COCKTAILS
  ) {

    def paramsToStr(): Seq[String] = Seq(alcohol, offset.toString)
  }

  object Factory {

    def create(payload: String): Option[FbActionButton[_]] = {
      val params = payload.split(PARAMS_DIVIDER)
      Tags.values.find(tag => params(0).equals(tag.toString)).map {
        case Tags.BAR_DETAILS => BarDetailsButton(params(1))
        case Tags.SHOW_NEXT_BAR => ShowNexBarButton(Location(params(1).toDouble, params(2).toDouble), params(3).toInt)
        case Tags.SHOW_MORE_COCKTAILS => ShowMoreCocktailsButton(params(1), params(2).toInt)
        case Tags.COCKTAIL_RECEIPT => CocktailReceiptButton(params(1))
      }
    }

  }

}

