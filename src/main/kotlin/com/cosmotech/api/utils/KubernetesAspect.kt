// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import io.kubernetes.client.openapi.ApiException
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class KubernetesAspect {

  private val logger = LoggerFactory.getLogger(KubernetesAspect::class.java)

  @AfterThrowing(
      pointcut = "execution(* com.cosmotech.api.utils.KubernetesService.deleteSecret(..))",
      throwing = "exception")
  fun logDeleteKubernetesSecrets(exception: ApiException) {
    logger.error("Kubernetes API Exception on deleting secret: ${exception.message}")
  }

  @AfterThrowing(
      pointcut = "execution(* com.cosmotech.api.utils.KubernetesService.createOrReplaceSecret(..))",
      throwing = "exception")
  fun logCreateReplaceKubernetesSecrets(exception: ApiException) {
    logger.error("Kubernetes API Exception on creation/updating secret: ${exception.message}")
  }
}
