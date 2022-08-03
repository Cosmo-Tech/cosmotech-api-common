// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.rbac.CsmRbac
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertEquals
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

const val PERM_READ = "readtestperm"
const val PERM_WRITE = "writetestperm"

const val ROLE_READER = "readertestrole"
const val ROLE_WRITER = "writertestrole"
const val ROLE_BAD = "badtestrole"

const val USER_WRITER = "usertestwriter@cosmotech.com"
const val USER_READER = "usertestreader@cosmotech.com"
const val USER_NONE = "usertestnone@cosmotech.com"

const val USER_NEW_READER = "usertestnew@cosmotech.com"

const val OWNER_ID = "3a869905-e9f5-4851-a7a9-3079aad49dfa"
const val USER_ID = "2a869905-e9f5-4851-a7a9-3079aad49dfb"

class CsmRbacTests {
  private val ROLE_NONE_PERMS: List<String> = listOf()
  private val ROLE_READER_PERMS = listOf(PERM_READ)
  private val ROLE_WRITER_PERMS = listOf(PERM_READ, PERM_WRITE)

  private val USER_READER_ROLES  = listOf(ROLE_READER)
  private val USER_WRITER_ROLES  = listOf(ROLE_READER, ROLE_WRITER)
  private val USER_NONE_ROLES: List<String>  = listOf()

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  private lateinit var csmPlatformProperties: CsmPlatformProperties
  private lateinit var rbac : CsmRbac
  private var noRbac : CsmNoRbac = CsmNoRbac()
  private var admin : CsmAdmin = CsmAdmin()
  private lateinit var securityContext: SecurityContext
  private lateinit var adminAuthentication: BearerTokenAuthentication
  private lateinit var userAuthentication: BearerTokenAuthentication
  private lateinit var ownerAuthentication: BearerTokenAuthentication

  private val resourceSecurity = ResourceSecurity(
    default = listOf(ROLE_READER),
    accessControlList = UsersAccess(roles = mutableMapOf(
        USER_WRITER to USER_WRITER_ROLES,
        USER_READER to USER_READER_ROLES,
        USER_NONE to USER_NONE_ROLES,
      )
    )
  )

  @BeforeTest
  fun beforeEachTest() {
    logger.trace("Begin test")
    csmPlatformProperties = mockk<CsmPlatformProperties>(relaxed = true)
    every { csmPlatformProperties.rbac.enabled } answers { true }
    every { csmPlatformProperties.authorization.rolesJwtClaim } answers { "roles" }
    val roleDefinition = RolesDefinition(permissions=mapOf(
      ROLE_READER to ROLE_READER_PERMS,
      ROLE_WRITER to ROLE_WRITER_PERMS,
    ))
    rbac = CsmRbac(roleDefinition, resourceSecurity)
    rbac.csmPlatformProperties = csmPlatformProperties
    com.cosmotech.api.utils.configuration = csmPlatformProperties

    adminAuthentication = mockk<BearerTokenAuthentication>(relaxed = true)
    every { adminAuthentication.name } answers { USER_ID }
    every { adminAuthentication.token.tokenValue } answers { ADMIN_TOKEN }

    userAuthentication = mockk<BearerTokenAuthentication>(relaxed = true)
    every { userAuthentication.name } answers { USER_ID }
    every { userAuthentication.token.tokenValue } answers { USER_TOKEN }

    ownerAuthentication = mockk<BearerTokenAuthentication>(relaxed = true)
    every { ownerAuthentication.name } answers { OWNER_ID }
    every { ownerAuthentication.token.tokenValue } answers { USER_TOKEN }

    securityContext = mockk<SecurityContext>(relaxed = true)
    every { securityContext.authentication } returns (adminAuthentication as Authentication)

    mockkStatic("org.springframework.security.core.context.SecurityContextHolder")
    every { SecurityContextHolder.getContext() } returns securityContext
  }

  @Test
  fun `rbac option is true by default`() {
    assertTrue(CsmPlatformProperties.CsmRbac().enabled)
  }

  // CsmAdmin tests
  @Test
  fun `role Platform Admin OK`() {
    val userRoles = listOf(ROLE_PLATFORM_ADMIN)
    assertTrue(admin.verifyRolesAdmin(userRoles))
  }

  @Test
  fun `roles with Platform Admin OK`() {
    val userRoles = listOf(ROLE_PLATFORM_ADMIN, ROLE_ORGANIZATION_USER)
    assertTrue(admin.verifyRolesAdmin(userRoles))
  }

  @Test
  fun `role Organization User KO`() {
    val userRoles = listOf(ROLE_ORGANIZATION_USER)
    assertFalse(admin.verifyRolesAdmin(userRoles))
  }

  @Test
  fun `No role KO`() {
    val userRoles: List<String> = listOf()
    assertFalse(admin.verifyRolesAdmin(userRoles))
  }

  @Test
  fun `current user role Admin OK`() {
    assertTrue(admin.verifyCurrentRolesAdmin())
  }

  @Test
  fun `current user role Admin KO`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertFalse(admin.verifyCurrentRolesAdmin())
  }

  // CsmNoRBac tests
  @Test
  fun `ownerId OK`() {
    assertTrue(noRbac.verifyOwner(OWNER_ID, OWNER_ID))
  }

  @Test
  fun `ownerId KO`() {
    assertFalse(noRbac.verifyOwner(USER_ID, OWNER_ID))
  }

  @Test
  fun `current user OK`() {
    every { securityContext.authentication } returns (ownerAuthentication as Authentication)
    assertTrue(noRbac.verifyCurrentOwner(OWNER_ID))
  }

  @Test
  fun `current user KO`() {
    assertFalse(noRbac.verifyCurrentOwner(OWNER_ID))
  }

  @Test
  fun `owner with PLATFORM USER token admin`() {
    every { securityContext.authentication } returns (ownerAuthentication as Authentication)
    assertTrue(noRbac.isAdmin(OWNER_ID))
  }

  @Test
  fun `user with PLATFORM ADMIN token admin`() {
    assertTrue(noRbac.isAdmin(OWNER_ID))
  }

  // CsmRBac tests
  @Test
  fun `verify permission read OK`() {
    assertTrue(rbac.verifyPermission(PERM_READ, ROLE_READER_PERMS))
  }

  @Test
  fun `verify permission read KO`() {
    assertFalse(rbac.verifyPermission(PERM_READ, ROLE_NONE_PERMS))
  }

  @Test
  fun `get permission from role`() {
    assertEquals(ROLE_READER_PERMS, rbac.getRolePermissions(ROLE_READER))
  }

  @Test
  fun `get permission from bad role`() {
    assertEquals(listOf(), rbac.getRolePermissions(ROLE_BAD))
  }

  @Test
  fun `verify permission read from role reader OK`() {
    assertTrue(rbac.verifyPermissionFromRole(PERM_READ, ROLE_READER))
  }

  @Test
  fun `verify permission write from role reader KO`() {
    assertFalse(rbac.verifyPermissionFromRole(PERM_WRITE, ROLE_READER))
  }

  @Test
  fun `verify permission read from role writer OK`() {
    assertTrue(rbac.verifyPermissionFromRole(PERM_READ, ROLE_WRITER))
  }

  @Test
  fun `verify permission writer from roles reader writer OK`() {
    assertTrue(rbac.verifyPermissionFromRoles(PERM_READ, USER_WRITER_ROLES))
  }

  @Test
  fun `find roles for user from resource security`() {
    assertEquals(listOf(ROLE_READER), rbac.getRoles(USER_READER))
  }

  @Test
  fun `verify permission read for user writer OK`() {
    assertTrue(rbac.verifyUser(PERM_READ, USER_READER))
  }

  @Test
  fun `verify permission write for user writer KO`() {
    assertFalse(rbac.verifyUser(PERM_WRITE, USER_READER))
  }

  @Test
  fun `verify permission read for user none KO`() {
    assertFalse(rbac.verifyUser(PERM_READ, USER_NONE))
  }

  @Test
  fun `verify permission read from default security OK`() {
    assertTrue(rbac.verifyDefault(PERM_READ))
  }

  @Test
  fun `verify permission write from default security KO`() {
    assertFalse(rbac.verifyDefault(PERM_WRITE))
  }

  @Test
  fun `add new reader user and verify read permission OK`() {
    rbac.setUserRoles(USER_NEW_READER, USER_READER_ROLES)
    assertTrue(rbac.verifyUser(PERM_READ, USER_NEW_READER))
  }

  @Test
  fun `add new reader user and verify write permission KO`() {
    rbac.setUserRoles(USER_NEW_READER, USER_READER_ROLES)
    assertFalse(rbac.verifyUser(PERM_WRITE, USER_NEW_READER))
  }

  @Test
  fun `remove new reader user and verify read permission KO`() {
    rbac.setUserRoles(USER_NEW_READER, USER_READER_ROLES)
    rbac.removeUser(USER_NEW_READER)
    assertFalse(rbac.verifyUser(PERM_READ, USER_NEW_READER))
  }

  @Test
  fun `update existing new user and verify write permission OK`() {
    rbac.setUserRoles(USER_NEW_READER, USER_READER_ROLES)
    rbac.setUserRoles(USER_NEW_READER, USER_WRITER_ROLES)
    assertTrue(rbac.verifyUser(PERM_WRITE, USER_NEW_READER))
  }

  @Test
  fun `user with no roles has default read permission`() {
    assertTrue(rbac.verifyRbac(PERM_READ, USER_NONE))
  }

  @Test
  fun `update default security to no roles and verify read KO for none user`() {
    rbac.setDefault(listOf())
    assertFalse(rbac.verifyRbac(PERM_READ, USER_NONE))
  }

  @Test
  fun `update default security to no roles and verify read OK for reader user`() {
    rbac.setDefault(listOf())
    assertTrue(rbac.verifyRbac(PERM_READ, USER_READER))
  }

  @Test
  fun `update default security to writer role and verify write OK for reader user`() {
    rbac.setDefault(USER_WRITER_ROLES)
    assertTrue(rbac.verifyRbac(PERM_WRITE, USER_READER))
  }

  @Test
  fun `verify none user with PLATFORM ADMIN token role write OK`() {
    assertTrue(rbac.verify(PERM_WRITE, USER_NONE))
  }

  @Test
  fun `verify none user with PLATFORM USER token role write KO`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertFalse(rbac.verify(PERM_WRITE, USER_NONE))
  }
}
