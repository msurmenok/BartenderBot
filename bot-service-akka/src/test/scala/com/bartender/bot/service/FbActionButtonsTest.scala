package com.bartender.bot.service

import com.bartender.bot.service.domain.Location
import com.bartender.bot.service.fb.FbActionButtons
import org.scalatest.{FlatSpec, Matchers}

class FbActionButtonsTest extends FlatSpec with Matchers {


  "parsing show next bar payload" should "offset and location" in {

    val actual = FbActionButtons.getBarShowNextPayload("Show one more;55.695738;37.624188;1")

    actual._2 shouldEqual 1
    actual._1 shouldEqual Location(55.695738, 37.624188)
  }

  "parsing bar details payload" should "offset and location" in {

    val actual = FbActionButtons.getBarDetailsPayload("Details;ChIJN1t_tDeuEmsRUsoyG83frY4")

    actual shouldEqual "ChIJN1t_tDeuEmsRUsoyG83frY4"
  }
}
