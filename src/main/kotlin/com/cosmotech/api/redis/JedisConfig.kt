// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.redis

import com.cosmotech.api.config.CsmPlatformProperties
import com.redislabs.redistimeseries.RedisTimeSeries
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

private const val DEFAULT_POOL_SIZE: Int = 100

private const val DEFAULT_TIMEOUT = 10000

@Configuration
internal open class JedisConfig {

  val logger: Logger = LoggerFactory.getLogger(JedisConfig::class.java)

  @Bean
  @Suppress("MagicNumber")
  // JedisPool must be use with .use autocloseable method
  open fun csmJedisPool(csmPlatformProperties: CsmPlatformProperties): JedisPool {
    val twincacheProperties = csmPlatformProperties.twincache!!
    val password = twincacheProperties.password
    val host = twincacheProperties.host
    val port = twincacheProperties.port.toInt()
    val timeout = DEFAULT_TIMEOUT
    val poolConfig = JedisPoolConfig()
    logger.info(
        "Starting Redis with Host:{}, Port:{}, Timeout(ms):{}, PoolConfig:{}",
        host,
        port,
        timeout,
        poolConfig)
    return JedisPool(poolConfig, host, port, timeout, password)
  }

  @Bean
  open fun csmRedisTimeSeries(csmPlatformProperties: CsmPlatformProperties): RedisTimeSeries {
    val twincacheProperties = csmPlatformProperties.twincache!!
    val username = twincacheProperties.username
    val password = twincacheProperties.password
    val host = twincacheProperties.host
    val port = twincacheProperties.port.toInt()
    val timeout = DEFAULT_TIMEOUT
    return RedisTimeSeries(host, port, timeout, DEFAULT_POOL_SIZE, password)
  }
}
