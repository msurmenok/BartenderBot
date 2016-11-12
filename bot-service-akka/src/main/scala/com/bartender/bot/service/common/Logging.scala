package com.bartender.bot.service.common

import org.slf4j.LoggerFactory

trait Logging {
  val rootLogger = LoggerFactory.getLogger("ROOT")
}
