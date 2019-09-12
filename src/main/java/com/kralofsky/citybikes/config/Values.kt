package com.kralofsky.citybikes.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class Values (
    @Value("\${bot.token}") val botToken: String,
    @Value("\${bot.username}") val botUsername: String,
    @Value("\${bot.creatorId}") val creatorId: Int,
    @Value("\${cacheDuration}") val cacheDuration: Int
)
