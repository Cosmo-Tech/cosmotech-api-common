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

@Suppress("MaxLineLength")
const val USER_TOKEN=
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSIsImtpZCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSJ9.eyJhdWQiOiJodHRwOi8vZGV2LmFwaS5jb3Ntb3RlY2guY29tIiwiaXNzIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvZTQxM2I4MzQtOGJlOC00ODIyLWEzNzAtYmU2MTk1NDVjYjQ5LyIsImlhdCI6MTY1OTUyNjcxMSwibmJmIjoxNjU5NTI2NzExLCJleHAiOjE2NTk1MzIwNzUsImFjciI6IjEiLCJhaW8iOiJBU1FBMi84VEFBQUFqRzY4cEwrUGI4dER5bnJSejMzOURoRkxqWFcrdnpJZ3V0NlR0UXFrM1I4PSIsImFtciI6WyJwd2QiXSwiYXBwaWQiOiI1ZTk5ODM1Yi00Y2NkLTRjMTYtODRjNy1lOTc5NmJlMTA3NzIiLCJhcHBpZGFjciI6IjAiLCJmYW1pbHlfbmFtZSI6IkNhcmx1ZXIiLCJnaXZlbl9uYW1lIjoiVmluY2VudCIsImlwYWRkciI6IjgwLjExOS4xMTkuMjQ0IiwibmFtZSI6IlZpbmNlbnQgQ2FybHVlciIsIm9pZCI6IjNhODY5OTA1LWU5ZjUtNDg1MS1hN2E5LTMwNzlhYWQ0OWRmZiIsInJoIjoiMC5BVEVBTkxnVDVPaUxJa2lqY0w1aGxVWExTUm5WLV9aVG1tdE1xcnRKR2JzdEViNHhBTFUuIiwicm9sZXMiOlsiT3JnYW5pemF0aW9uLlVzZXIiXSwic2NwIjoicGxhdGZvcm0iLCJzdWIiOiJIMlU5ZVgwUi1LR0tJang3TG9WRHd2VFZxeE1PT3pGcmFlZVJKYkdDR1ZvIiwidGlkIjoiZTQxM2I4MzQtOGJlOC00ODIyLWEzNzAtYmU2MTk1NDVjYjQ5IiwidW5pcXVlX25hbWUiOiJ2aW5jZW50LmNhcmx1ZXJAY29zbW90ZWNoLmNvbSIsInVwbiI6InZpbmNlbnQuY2FybHVlckBjb3Ntb3RlY2guY29tIiwidXRpIjoicERBd2FnZDI4VUdqLVlhSk9FaGVBQSIsInZlciI6IjEuMCJ9.j5g7hHcusxnftE-1GDceKBgDpeeCijsL4KoUAPNOb5dd2H-pN0-p7za5xbvZscH_Tw5YF8rY5b_MeqMa-6qJQZhG4tUpRml92qIIjzuvF-v3JkVhpUVqE34MAfRMfp8NMR-ATY-XMZ_HekpD_aH0SDWQzoeSlvqhrzMnJ6l4G4v5kSwMeP8MgNxu8TGElPS65PP-639IguHvsgtaaiAJOjHbZ4jQtZdDm34IEpSzJj6eBIxkPv3ADn06A4bbQm63owUKZFRmnKuQIESzHCdI-3jAz-YH-gGbquD-dGUxKTsmi80rsYNsZZg1Nb_lHeSLaTdiJ8NNZkl1WAOUVALglQ"

class CsmRbacTests {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  private lateinit var csmPlatformProperties: CsmPlatformProperties
  private lateinit var rbac : CsmRbac
  private lateinit var securityContext: SecurityContext
  private lateinit var adminAuthentication: BearerTokenAuthentication
  private lateinit var userAuthentication: BearerTokenAuthentication

  @BeforeTest
  fun beforeEachTest() {
    logger.trace("Begin test")
    csmPlatformProperties = mockk<CsmPlatformProperties>(relaxed = true)
    every { csmPlatformProperties.rbac.enabled } answers { true }
    rbac = CsmRbac(csmPlatformProperties)

    adminAuthentication = mockk<BearerTokenAuthentication>(relaxed = true)
    every { adminAuthentication.name } answers { "owner" }
    every { adminAuthentication.token.tokenValue } answers { ADMIN_TOKEN }

    userAuthentication = mockk<BearerTokenAuthentication>(relaxed = true)
    every { userAuthentication.name } answers { "user" }
    every { userAuthentication.token.tokenValue } answers { USER_TOKEN }

    securityContext = mockk<SecurityContext>(relaxed = true)
    every { securityContext.authentication } returns (adminAuthentication as Authentication)

    mockkStatic("org.springframework.security.core.context.SecurityContextHolder")
    every { SecurityContextHolder.getContext() } returns securityContext
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
