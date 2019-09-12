package com.kralofsky.citybikes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.ApiContextInitializer

import java.util.Locale

@SpringBootApplication
class CitybikeTelegramBotApp

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    ApiContextInitializer.init()
    runApplication<CitybikeTelegramBotApp>(*args)
}