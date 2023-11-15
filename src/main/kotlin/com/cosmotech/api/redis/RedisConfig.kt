// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisClientConfig
import redis.clients.jedis.Protocol
import redis.clients.jedis.UnifiedJedis

@Configuration
open class RedisConfig {

  @Value("\${csm.platform.twincache.host}") private lateinit var twincacheHost: String

  @Value("\${csm.platform.twincache.port}") private lateinit var twincachePort: String

  @Value("\${csm.platform.twincache.password}") private lateinit var twincachePassword: String

  @Bean
  open fun redisStandaloneConfiguration(): RedisStandaloneConfiguration {
    return RedisStandaloneConfiguration().apply {
      hostName = twincacheHost
      port = twincachePort.toInt()
      password = RedisPassword.of(twincachePassword)
    }
  }

  @Bean
  open fun jedisConnectionFactory(
      redisStandaloneConfiguration: RedisStandaloneConfiguration
  ): JedisConnectionFactory {
    return JedisConnectionFactory(redisStandaloneConfiguration)
  }

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
