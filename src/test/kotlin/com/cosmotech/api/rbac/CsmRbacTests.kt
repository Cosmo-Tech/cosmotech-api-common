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

@Suppress("MaxLineLength")
const val ADMIN_TOKEN=
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSIsImtpZCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSJ9.eyJhdWQiOiJodHRwOi8vZGV2LmFwaS5jb3Ntb3RlY2guY29tIiwiaXNzIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvZTQxM2I4MzQtOGJlOC00ODIyLWEzNzAtYmU2MTk1NDVjYjQ5LyIsImlhdCI6MTY1OTUyNjEzNCwibmJmIjoxNjU5NTI2MTM0LCJleHAiOjE2NTk1MzE3OTUsImFjciI6IjEiLCJhaW8iOiJFMlpnWUhEc3k5K2I5bzNWaUR0Qm5wZG5Bc3V2NHllUGRqak4xdForb0d4MDJVVHRyamtBIiwiYW1yIjpbInB3ZCJdLCJhcHBpZCI6IjVlOTk4MzViLTRjY2QtNGMxNi04NGM3LWU5Nzk2YmUxMDc3MiIsImFwcGlkYWNyIjoiMCIsImZhbWlseV9uYW1lIjoiQ2FybHVlciIsImdpdmVuX25hbWUiOiJWaW5jZW50IiwiaXBhZGRyIjoiODAuMTE5LjExOS4yNDQiLCJuYW1lIjoiVmluY2VudCBDYXJsdWVyIiwib2lkIjoiM2E4Njk5MDUtZTlmNS00ODUxLWE3YTktMzA3OWFhZDQ5ZGZmIiwicmgiOiIwLkFURUFOTGdUNU9pTElraWpjTDVobFVYTFNSblYtX1pUbW10TXFydEpHYnN0RWI0eEFMVS4iLCJyb2xlcyI6WyJQbGF0Zm9ybS5BZG1pbiJdLCJzY3AiOiJwbGF0Zm9ybSIsInN1YiI6IkgyVTllWDBSLUtHS0lqeDdMb1ZEd3ZUVnF4TU9PekZyYWVlUkpiR0NHVm8iLCJ0aWQiOiJlNDEzYjgzNC04YmU4LTQ4MjItYTM3MC1iZTYxOTU0NWNiNDkiLCJ1bmlxdWVfbmFtZSI6InZpbmNlbnQuY2FybHVlckBjb3Ntb3RlY2guY29tIiwidXBuIjoidmluY2VudC5jYXJsdWVyQGNvc21vdGVjaC5jb20iLCJ1dGkiOiJZXzU5MVV1dG1FYWFRckJPcUpWTUFBIiwidmVyIjoiMS4wIn0.ozHgi5e4wDKliSejryxBhETAGpA-v2M5LUIyu4KvzWOZ_wJLlNx5vOIdUdhY2TyIFykow6g8UXQ98KfrV-WjKLbFYvRPaIyg-HUgTkOfoOdzvo-q9lcKJRgo00y-hTJBof4CIBCpz9Y1CkQU5YxGiDXF7XRC3XVW1h7msN_5mRyE36orqKaBGCtQTDa9OI23XiF7Q4EhkU_XKXCvdjTAMaiEgI1I3cFda8LSa2BUpZibieO7_0glcRIWif_gTS1Hp5xVVQ-Ho_ZUV1WtZuE0orwPcQuvZYeEDs_hXuG1pLBPUXKzFzbUJzx-UZ6_0Uc3K5OFIgdS2as1Cg1urCzFXA"

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
    every { adminAuthentication.token.tokenValue } answers { ADMIN_TOKEN }
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
