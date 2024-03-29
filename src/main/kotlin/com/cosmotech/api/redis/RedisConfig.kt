// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisClientConfig
import redis.clients.jedis.Protocol
import redis.clients.jedis.UnifiedJedis

@Configuration
open class RedisConfig {

  @Value("\${spring.data.redis.host}") private lateinit var twincacheHost: String

  @Value("\${spring.data.redis.port}") private lateinit var twincachePort: String

  @Value("\${spring.data.redis.password}") private lateinit var twincachePassword: String

  @Bean
  open fun csmJedisClientConfig(): JedisClientConfig =
      DefaultJedisClientConfig.builder()
          .password(twincachePassword)
          .timeoutMillis(Protocol.DEFAULT_TIMEOUT)
          .build()

  @Bean
  open fun unifiedJedis(csmJedisClientConfig: JedisClientConfig): UnifiedJedis {
    return UnifiedJedis(HostAndPort(twincacheHost, twincachePort.toInt()), csmJedisClientConfig)
  }
}
