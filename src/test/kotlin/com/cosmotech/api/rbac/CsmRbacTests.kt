// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.rbac.CsmRbac
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.cosmotech.api.security.ROLE_PLATFORM_ADMIN
import com.cosmotech.api.security.ROLE_ORGANIZATION_USER
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication


class CsmRbacTests {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  private lateinit var csmPlatformProperties: CsmPlatformProperties
  private lateinit var rbac : CsmRbac
  private lateinit var securityContext: SecurityContext
  private lateinit var adminAuthentication: BearerTokenAuthentication

  @BeforeTest
  fun beforeEachTest() {
    logger.trace("Begin test")
    csmPlatformProperties = mockk<CsmPlatformProperties>(relaxed = true)
    every { csmPlatformProperties.rbac.enabled } answers { true }
    rbac = CsmRbac(csmPlatformProperties)
    mockkStatic("org.springframework.security.core.context.SecurityContextHolder")
    securityContext = mockk<SecurityContext>(relaxed = true)
    adminAuthentication = mockk<BearerTokenAuthentication>(relaxed = true)
    every { adminAuthentication.name } answers { "owner" }
    every { SecurityContextHolder.getContext() } returns securityContext
    every { securityContext.authentication } returns (adminAuthentication as Authentication)
  }

  @Test
  fun `rbac option is true by default`() {
    assertTrue(CsmPlatformProperties.CsmRbac().enabled)
  }

  @Test
  fun `ownerId OK`() {
    val userId = "owner"
    val ownerId = "owner"
    assertTrue(rbac.verifyOwner(userId, ownerId))
  }

  @Test
  fun `ownerId KO`() {
    val userId = "user"
    val ownerId = "owner"
    assertFalse(rbac.verifyOwner(userId, ownerId))
  }
  @Test
  fun `current user OK`() {
    val ownerId = "owner"
    assertTrue(rbac.verifyCurrentOwner(ownerId))
  }


  @Test
  fun `role Platform Admin OK`() {
    val userRoles = listOf(ROLE_PLATFORM_ADMIN)
    assertTrue(rbac.verifyRoles(userRoles))
  }

  @Test
  fun `roles with Platform Admin OK`() {
    val userRoles = listOf(ROLE_PLATFORM_ADMIN, ROLE_ORGANIZATION_USER)
    assertTrue(rbac.verifyRoles(userRoles))
  }

  @Test
  fun `role Organization User KO`() {
    val userRoles = listOf(ROLE_ORGANIZATION_USER)
    assertFalse(rbac.verifyRoles(userRoles))
  }

  @Test
  fun `No role KO`() {
    val userRoles: List<String> = listOf()
    assertFalse(rbac.verifyRoles(userRoles))
  }
}
