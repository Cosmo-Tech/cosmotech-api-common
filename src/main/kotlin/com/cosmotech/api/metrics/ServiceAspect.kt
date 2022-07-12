// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.metrics

import com.cosmotech.api.utils.getCurrentAuthenticatedIssuer
import com.cosmotech.api.utils.getCurrentAuthenticatedUserName
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.CodeSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class ServiceAspect {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  private val listOfArgs =
      setOf(
          "organizationId",
          "workspaceId",
          "scenarioId",
          "solutionId",
          "scenariorunId",
          "datasetId",
          "connectorId")

  @Autowired private lateinit var meterRegistry: MeterRegistry

  @Pointcut(
      "within(@org.springframework.stereotype.Service *)" + " && within(com.cosmotech..*Impl)")
  @Suppress("EmptyFunctionBlock")
  fun cosmotechPointcut() {}

  @Before("cosmotechPointcut()")
  fun logBefore(joinPoint: JoinPoint) {
    val signature: CodeSignature = joinPoint.signature as CodeSignature
    val args = joinPoint.args
    logger.debug("$signature: $args")
    val argsTags =
        args.filter() { listOfArgs.contains(it.toString()) }.mapIndexed() { idx, arg ->
          Tag.of(signature.parameterNames[idx], arg.toString())
        }
    Counter.builder("cosmotech.${signature.name}")
        .description("${signature.name}")
        .tag("method", signature.name)
        .tag("user", getCurrentAuthenticatedUserName())
        .tag("issuer", getCurrentAuthenticatedIssuer())
        .tags(argsTags)
        .register(meterRegistry)
        .increment()
  }
}
