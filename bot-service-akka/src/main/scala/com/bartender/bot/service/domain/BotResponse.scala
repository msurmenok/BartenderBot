package com.bartender.bot.service.domain

case class BotResponse(
                        message: Message,
                        action: Option[BotAction] = None
                      )
