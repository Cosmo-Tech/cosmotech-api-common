// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.config

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


@Configuration
open class RedisConfig {

    @Bean
    open fun jedisConnectionFactory(): JedisConnectionFactory? {
        return JedisConnectionFactory()
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<*, *> {
        val template = RedisTemplate<ByteArray, ByteArray>()
        template.connectionFactory = redisConnectionFactory!!
        return template
    }
}
