// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.redis

import com.redislabs.redisgraph.impl.api.RedisGraph
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool

@Configuration
open class RedisGraphConfig {
  @Bean
  open fun csmRedisGraph(csmJedisPool: JedisPool): RedisGraph {
    return RedisGraph(csmJedisPool)
  }
}
