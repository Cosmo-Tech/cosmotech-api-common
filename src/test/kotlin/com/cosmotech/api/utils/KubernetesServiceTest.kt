// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import io.kubernetes.client.openapi.ApiException
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.lang.reflect.UndeclaredThrowableException
import kotlin.test.BeforeTest
import kotlin.test.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory
import org.springframework.aop.framework.DefaultAopProxyFactory

class KubernetesServiceTest {

  private lateinit var service: SecretManager
  private var aspect = spyk(KubernetesAspect())
  @BeforeTest
  fun beforeEachTest() {
    val aspectJProxyFactory = AspectJProxyFactory(spyk(KubernetesService(mockk())))
    aspectJProxyFactory.addAspect(aspect)

    val proxyFactory = DefaultAopProxyFactory()
    val aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory)
    service = aopProxy.proxy as SecretManager
  }

  @Test
  fun `call Advice AfterThrowing when deleteSecret throws ApiException`() {
    val apiException = ApiException()
    every { service.deleteSecret(any(), any()) } throws apiException
    assertThrows<UndeclaredThrowableException> { service.deleteSecret("", "") }
    verify(exactly = 1) { aspect.logDeleteKubernetesSecrets(apiException) }
  }

  @Test
  fun `call Advice AfterThrowing when createOrReplaceSecret throws ApiException`() {
    val apiException = ApiException()
    every { service.createOrReplaceSecret(any(), any(), any()) } throws apiException
    assertThrows<UndeclaredThrowableException> { service.createOrReplaceSecret("", "", mapOf()) }
    verify(exactly = 1) { aspect.logCreateReplaceKubernetesSecrets(apiException) }
  }
}
