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
import org.springframework.stereotype.Component

@Aspect
@Component
class MonitorServiceAspect(private var meterRegistry: MeterRegistry) {
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
      "within(@org.springframework.stereotype.Service *) && within(com.cosmotech..*Impl)" +
          " && !execution(* com.cosmotech.scenariorun.azure.ScenarioRunServiceImpl.onScenarioDeleted(..))" +
          " && !execution(* com.cosmotech.workspace.api.WorkspaceApiService.findWorkspaceById(..))")
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
