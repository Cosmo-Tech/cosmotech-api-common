// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.metrics

import com.cosmotech.api.events.CsmEventPublisher
import com.cosmotech.api.events.PersistentMetricEvent
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
import org.springframework.stereotype.Component

private const val SERVICE_NAME = "API"

@Aspect
@Component
class MonitorServiceAspect(
    private var meterRegistry: MeterRegistry,
    private val eventPublisher: CsmEventPublisher,
) {
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

  @Pointcut(
      "within(@org.springframework.web.bind.annotation.RestController *) && within(com.cosmotech..*Controller)")
  @Suppress("EmptyFunctionBlock")
  fun cosmotechPointcut() {}

  @Before("cosmotechPointcut()")
  fun monitorBefore(joinPoint: JoinPoint) {
    val signature: CodeSignature = joinPoint.signature as CodeSignature
    val args = joinPoint.args
    val parameterNames = signature.parameterNames
    logger.debug("$signature: $args")
    logger.debug("$signature: $parameterNames")
    val argsTags =
        List(parameterNames.filter { listOfArgs.contains(it.toString()) }.size) { idx ->
          Tag.of(parameterNames[idx], args[idx] as String)
        }
    val name = signature.name
    val user = getCurrentAuthenticatedUserName()
    var issuer = getCurrentAuthenticatedIssuer()
    Counter.builder("cosmotech.$name")
        .description("$name")
        .tag("method", name)
        .tag("user", user)
        .tag("issuer", issuer)
        .tags(argsTags)
        .register(meterRegistry)
        .increment()

    val metric =
        PersistentMetric(
            service = SERVICE_NAME,
            name = user,
            value = 1.0,
            labels = mapOf("usage" to "licensing", "user" to user, "group" to "user"),
            qualifier = "call",
            type = PersitentMetricType.COUNTER,
            downSampling = true,
            downSamplingAggregation = DownSamplingAggregationType.MAX,
        )
    eventPublisher.publishEvent(PersistentMetricEvent(this, metric))
  }
}
