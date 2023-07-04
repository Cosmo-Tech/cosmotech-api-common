// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.redis

import com.cosmotech.api.config.CsmPlatformProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisClientConfig
import redis.clients.jedis.UnifiedJedis


private const val DEFAULT_TIMEOUT = 10000

@Configuration
open class JedisConfig {

  val logger: Logger = LoggerFactory.getLogger(JedisConfig::class.java)


//  @Bean
//  // JedisPool must be use with .use autocloseable method
//  open fun csmJedisPool(csmPlatformProperties: CsmPlatformProperties): JedisPool {
//    val twincacheProperties = csmPlatformProperties.twincache
//    val password = twincacheProperties.password
//    val host = twincacheProperties.host
//    val port = twincacheProperties.port.toInt()
//    val timeout = DEFAULT_TIMEOUT
//    val poolConfig = JedisPoolConfig()
//    logger.info(
//        "Starting Redis with Host:{}, Port:{}, Timeout(ms):{}, PoolConfig:{}",
//        host,
//        port,
//        timeout,
//        poolConfig)
//    return JedisPool(poolConfig, host, port, timeout, password)
//  }


    @Bean
    open fun redisStandaloneConfiguration(csmPlatformProperties: CsmPlatformProperties):
    RedisStandaloneConfiguration {
        return RedisStandaloneConfiguration().apply {
            hostName = csmPlatformProperties.twincache.host
            port = csmPlatformProperties.twincache.port.toInt()
            password = RedisPassword.of(csmPlatformProperties.twincache.password)
        }
    }

  @Bean
  open fun jedisConnectionFactory(redisStandaloneConfiguration: RedisStandaloneConfiguration): JedisConnectionFactory {
    return JedisConnectionFactory(redisStandaloneConfiguration)
  }

    @Bean
    open fun csmJedisClientConfig(csmPlatformProperties: CsmPlatformProperties): JedisClientConfig =
        DefaultJedisClientConfig.builder()
            .password(csmPlatformProperties.twincache.password)
            .timeoutMillis(DEFAULT_TIMEOUT)
            .build()


    @Bean
    open fun unifiedJedis(csmPlatformProperties: CsmPlatformProperties,
                          csmJedisClientConfig: JedisClientConfig): UnifiedJedis {
        return UnifiedJedis(HostAndPort(csmPlatformProperties.twincache.host,
            csmPlatformProperties.twincache.port.toInt()),
            csmJedisClientConfig)
    }

}
