package com.bartender.bot.service.domain

case class BotResponse(
                        message: Message,
                        action: Option[BotActions.EnumA] = None
                      )
