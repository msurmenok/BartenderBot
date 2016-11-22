package com.bartender.bot.service.domain

case class Recipient(id: String)

case class RecipientInfo(name: Option[String] = None,
                         likeDrink: Option[Boolean] = None,
                         lastResponse: Message)

