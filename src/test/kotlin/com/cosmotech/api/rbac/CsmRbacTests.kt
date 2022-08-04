// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmAccessForbiddenException
import com.cosmotech.api.exceptions.CsmClientException
import com.cosmotech.api.security.ROLE_ORGANIZATION_USER
import com.cosmotech.api.security.ROLE_PLATFORM_ADMIN
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication

@Suppress("MaxLineLength")
const val ADMIN_TOKEN =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSIsImtpZCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSJ9.eyJhdWQiOiJodHRwOi8vZGV2LmFwaS5jb3Ntb3RlY2guY29tIiwiaXNzIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvZTQxM2I4MzQtOGJlOC00ODIyLWEzNzAtYmU2MTk1NDVjYjQ5LyIsImlhdCI6MTY1OTUyNjEzNCwibmJmIjoxNjU5NTI2MTM0LCJleHAiOjE2NTk1MzE3OTUsImFjciI6IjEiLCJhaW8iOiJFMlpnWUhEc3k5K2I5bzNWaUR0Qm5wZG5Bc3V2NHllUGRqak4xdForb0d4MDJVVHRyamtBIiwiYW1yIjpbInB3ZCJdLCJhcHBpZCI6IjVlOTk4MzViLTRjY2QtNGMxNi04NGM3LWU5Nzk2YmUxMDc3MiIsImFwcGlkYWNyIjoiMCIsImZhbWlseV9uYW1lIjoiQ2FybHVlciIsImdpdmVuX25hbWUiOiJWaW5jZW50IiwiaXBhZGRyIjoiODAuMTE5LjExOS4yNDQiLCJuYW1lIjoiVmluY2VudCBDYXJsdWVyIiwib2lkIjoiM2E4Njk5MDUtZTlmNS00ODUxLWE3YTktMzA3OWFhZDQ5ZGZmIiwicmgiOiIwLkFURUFOTGdUNU9pTElraWpjTDVobFVYTFNSblYtX1pUbW10TXFydEpHYnN0RWI0eEFMVS4iLCJyb2xlcyI6WyJQbGF0Zm9ybS5BZG1pbiJdLCJzY3AiOiJwbGF0Zm9ybSIsInN1YiI6IkgyVTllWDBSLUtHS0lqeDdMb1ZEd3ZUVnF4TU9PekZyYWVlUkpiR0NHVm8iLCJ0aWQiOiJlNDEzYjgzNC04YmU4LTQ4MjItYTM3MC1iZTYxOTU0NWNiNDkiLCJ1bmlxdWVfbmFtZSI6InZpbmNlbnQuY2FybHVlckBjb3Ntb3RlY2guY29tIiwidXBuIjoidmluY2VudC5jYXJsdWVyQGNvc21vdGVjaC5jb20iLCJ1dGkiOiJZXzU5MVV1dG1FYWFRckJPcUpWTUFBIiwidmVyIjoiMS4wIn0.ozHgi5e4wDKliSejryxBhETAGpA-v2M5LUIyu4KvzWOZ_wJLlNx5vOIdUdhY2TyIFykow6g8UXQ98KfrV-WjKLbFYvRPaIyg-HUgTkOfoOdzvo-q9lcKJRgo00y-hTJBof4CIBCpz9Y1CkQU5YxGiDXF7XRC3XVW1h7msN_5mRyE36orqKaBGCtQTDa9OI23XiF7Q4EhkU_XKXCvdjTAMaiEgI1I3cFda8LSa2BUpZibieO7_0glcRIWif_gTS1Hp5xVVQ-Ho_ZUV1WtZuE0orwPcQuvZYeEDs_hXuG1pLBPUXKzFzbUJzx-UZ6_0Uc3K5OFIgdS2as1Cg1urCzFXA"

@Suppress("MaxLineLength")
const val USER_TOKEN =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSIsImtpZCI6IjJaUXBKM1VwYmpBWVhZR2FYRUpsOGxWMFRPSSJ9.eyJhdWQiOiJodHRwOi8vZGV2LmFwaS5jb3Ntb3RlY2guY29tIiwiaXNzIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvZTQxM2I4MzQtOGJlOC00ODIyLWEzNzAtYmU2MTk1NDVjYjQ5LyIsImlhdCI6MTY1OTUyNjcxMSwibmJmIjoxNjU5NTI2NzExLCJleHAiOjE2NTk1MzIwNzUsImFjciI6IjEiLCJhaW8iOiJBU1FBMi84VEFBQUFqRzY4cEwrUGI4dER5bnJSejMzOURoRkxqWFcrdnpJZ3V0NlR0UXFrM1I4PSIsImFtciI6WyJwd2QiXSwiYXBwaWQiOiI1ZTk5ODM1Yi00Y2NkLTRjMTYtODRjNy1lOTc5NmJlMTA3NzIiLCJhcHBpZGFjciI6IjAiLCJmYW1pbHlfbmFtZSI6IkNhcmx1ZXIiLCJnaXZlbl9uYW1lIjoiVmluY2VudCIsImlwYWRkciI6IjgwLjExOS4xMTkuMjQ0IiwibmFtZSI6IlZpbmNlbnQgQ2FybHVlciIsIm9pZCI6IjNhODY5OTA1LWU5ZjUtNDg1MS1hN2E5LTMwNzlhYWQ0OWRmZiIsInJoIjoiMC5BVEVBTkxnVDVPaUxJa2lqY0w1aGxVWExTUm5WLV9aVG1tdE1xcnRKR2JzdEViNHhBTFUuIiwicm9sZXMiOlsiT3JnYW5pemF0aW9uLlVzZXIiXSwic2NwIjoicGxhdGZvcm0iLCJzdWIiOiJIMlU5ZVgwUi1LR0tJang3TG9WRHd2VFZxeE1PT3pGcmFlZVJKYkdDR1ZvIiwidGlkIjoiZTQxM2I4MzQtOGJlOC00ODIyLWEzNzAtYmU2MTk1NDVjYjQ5IiwidW5pcXVlX25hbWUiOiJ2aW5jZW50LmNhcmx1ZXJAY29zbW90ZWNoLmNvbSIsInVwbiI6InZpbmNlbnQuY2FybHVlckBjb3Ntb3RlY2guY29tIiwidXRpIjoicERBd2FnZDI4VUdqLVlhSk9FaGVBQSIsInZlciI6IjEuMCJ9.j5g7hHcusxnftE-1GDceKBgDpeeCijsL4KoUAPNOb5dd2H-pN0-p7za5xbvZscH_Tw5YF8rY5b_MeqMa-6qJQZhG4tUpRml92qIIjzuvF-v3JkVhpUVqE34MAfRMfp8NMR-ATY-XMZ_HekpD_aH0SDWQzoeSlvqhrzMnJ6l4G4v5kSwMeP8MgNxu8TGElPS65PP-639IguHvsgtaaiAJOjHbZ4jQtZdDm34IEpSzJj6eBIxkPv3ADn06A4bbQm63owUKZFRmnKuQIESzHCdI-3jAz-YH-gGbquD-dGUxKTsmi80rsYNsZZg1Nb_lHeSLaTdiJ8NNZkl1WAOUVALglQ"

const val RESOURCE_ID = "t-resourceid"
const val PERM_READ = "readtestperm"
const val PERM_WRITE = "writetestperm"
const val PERM_ADMIN = "admintestperm"

const val ROLE_READER = "readertestrole"
const val ROLE_WRITER = "writertestrole"
const val ROLE_ADMIN = "adminrole"
const val ROLE_NOTIN = "notintestrole"

const val USER_WRITER = "usertestwriter@cosmotech.com"
const val USER_READER = "usertestreader@cosmotech.com"
const val USER_NONE = "usertestnone@cosmotech.com"
const val USER_ADMIN = "usertestadmin@cosmotech.com"
const val USER_NOTIN = "usertestnotin@cosmotech.com"
const val USER_MAIL_TOKEN = "vincent.carluer@cosmotech.com"

const val USER_NEW_READER = "usertestnew@cosmotech.com"

const val OWNER_ID = "3a869905-e9f5-4851-a7a9-3079aad49dfa"
const val USER_ID = "2a869905-e9f5-4851-a7a9-3079aad49dfb"

class CsmRbacTests {
  private val ROLE_NONE_PERMS: List<String> = listOf()
  private val ROLE_READER_PERMS = listOf(PERM_READ)
  private val ROLE_WRITER_PERMS = listOf(PERM_READ, PERM_WRITE)
  private val ROLE_ADMIN_PERMS = listOf(PERM_ADMIN)

  private val USER_READER_ROLES = listOf(ROLE_READER)
  private val USER_WRITER_ROLES = listOf(ROLE_READER, ROLE_WRITER)
  private val USER_ADMIN_ROLES = listOf(ROLE_ADMIN)
  private val USER_NONE_ROLES: List<String> = listOf()

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  private lateinit var csmPlatformProperties: CsmPlatformProperties
  private lateinit var rbac: CsmRbac
  private var noRbac: CsmNoRbac = CsmNoRbac()
  private var admin: CsmAdmin = CsmAdmin()
  private lateinit var securityContext: SecurityContext
  private lateinit var adminAuthentication: BearerTokenAuthentication
  private lateinit var userAuthentication: BearerTokenAuthentication
  private lateinit var ownerAuthentication: BearerTokenAuthentication

  private lateinit var rolesDefinition: RolesDefinition

  private val resourceSecurity =
      ResourceSecurity(
          default = listOf(ROLE_READER),
          accessControlList =
              UsersAccess(
                  roles =
                      mutableMapOf(
                          USER_WRITER to USER_WRITER_ROLES,
                          USER_READER to USER_READER_ROLES,
                          USER_NONE to USER_NONE_ROLES,
                          USER_ADMIN to USER_ADMIN_ROLES,
                          USER_MAIL_TOKEN to USER_READER_ROLES,
                      )))

  @BeforeTest
  fun beforeEachTest() {
    logger.trace("Begin test")
    csmPlatformProperties = mockk<CsmPlatformProperties>(relaxed = true)
    every { csmPlatformProperties.rbac.enabled } answers { true }
    every { csmPlatformProperties.authorization.rolesJwtClaim } answers { "roles" }
    every { csmPlatformProperties.authorization.mailJwtClaim } answers { "upn" }
    rolesDefinition =
        RolesDefinition(
            adminRole = ROLE_ADMIN,
            permissions =
                mutableMapOf(
                    ROLE_READER to ROLE_READER_PERMS,
                    ROLE_WRITER to ROLE_WRITER_PERMS,
                    ROLE_ADMIN to ROLE_ADMIN_PERMS,
                ))
    rbac = CsmRbac(csmPlatformProperties, rolesDefinition, RESOURCE_ID, resourceSecurity)
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

  @Test
  fun `user with PLATFORM USER token not admin`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertFalse(noRbac.isAdmin(OWNER_ID))
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
    assertEquals(listOf(), rbac.getRolePermissions(ROLE_NOTIN))
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
  fun `update default security to roles not in permission KO`() {
    assertThrows<CsmClientException> { rbac.setDefault(listOf(ROLE_NOTIN)) }
  }

  @Test
  fun `check admin user with PLATFORM USER token role write OK`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbac.check(PERM_WRITE, USER_ADMIN))
  }

  @Test
  fun `check none user with PLATFORM ADMIN token role write OK`() {
    assertTrue(rbac.check(PERM_WRITE, USER_NONE))
  }

  @Test
  fun `check writer user with PLATFORM USER token role write OK`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbac.check(PERM_WRITE, USER_WRITER))
  }

  @Test
  fun `check none user with PLATFORM USER token role write KO`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertFalse(rbac.check(PERM_WRITE, USER_NONE))
  }

  @Test
  fun `check reader user with PLATFORM USER token role write KO`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertFalse(rbac.check(PERM_WRITE, USER_READER))
  }

  @Test
  fun `check return OK if rbac flag set to false`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    every { csmPlatformProperties.rbac.enabled } answers { false }
    assertTrue(rbac.check(PERM_WRITE, USER_READER))
  }

  @Test
  fun `verify KO throw exception`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertThrows<CsmAccessForbiddenException> { rbac.verify(PERM_WRITE, USER_READER) }
  }

  @Test
  fun `verify OK does not throw exception`() {
    assertDoesNotThrow { rbac.verify(PERM_WRITE, USER_WRITER) }
  }

  @Test
  fun `verify KO current user throw exception`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertThrows<CsmAccessForbiddenException> { rbac.verify(PERM_WRITE) }
  }

  @Test
  fun `verify OK current user does not throw exception`() {
    assertDoesNotThrow { rbac.verify(PERM_READ) }
  }

  @Test
  fun `verify return OK if rbac flag set to false`() {
    every { csmPlatformProperties.rbac.enabled } answers { false }
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertDoesNotThrow { rbac.verify(PERM_WRITE) }
  }

  @Test
  fun `owner with PLATFORM USER token not admin with rbac`() {
    every { securityContext.authentication } returns (ownerAuthentication as Authentication)
    assertFalse(rbac.isAdmin(USER_NONE))
  }

  @Test
  fun `user with PLATFORM ADMIN token admin with rbac`() {
    assertTrue(rbac.isAdmin(USER_NONE))
  }

  @Test
  fun `get special admin role`() {
    assertEquals(ROLE_ADMIN, rbac.getAdminRole())
  }

  @Test
  fun `user has admin role`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbac.verifyAdminRole(USER_ADMIN))
  }

  @Test
  fun `user has not admin role`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertFalse(rbac.verifyAdminRole(USER_READER))
  }

  @Test
  fun `readerRole is not admin`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertFalse(rbac.isAdmin(USER_READER))
  }

  @Test
  fun `adminRole is admin`() {
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbac.isAdmin(USER_ADMIN))
  }

  @Test
  fun `user with PLATFORM ADMIN token is admin`() {
    assertTrue(rbac.isAdmin(USER_ADMIN))
  }

  @Test
  fun `get count of users with admin role`() {
    assertEquals(1, rbac.getAdminCount())
  }

  @Test
  fun `get count of users with new admin role`() {
    rbac.setUserRoles(USER_NEW_READER, USER_ADMIN_ROLES)
    assertEquals(2, rbac.getAdminCount())
  }

  @Test
  fun `throw exception if last admin deleted`() {
    assertThrows<CsmAccessForbiddenException> { rbac.removeUser(USER_ADMIN) }
  }

  @Test
  fun `throw exception if last admin from two is deleted`() {
    rbac.setUserRoles(USER_NEW_READER, USER_ADMIN_ROLES)
    rbac.removeUser(USER_NEW_READER)
    assertThrows<CsmAccessForbiddenException> { rbac.removeUser(USER_ADMIN) }
  }

  @Test
  fun `throw exception if last admin removed from setRole`() {
    assertThrows<CsmAccessForbiddenException> { rbac.setUserRoles(USER_ADMIN, USER_READER_ROLES) }
  }

  @Test
  fun `throw exception if role does not exist with setRole`() {
    assertThrows<CsmClientException> { rbac.setUserRoles(USER_READER, listOf(ROLE_NOTIN)) }
  }

  @Test
  fun `add new user roles throw exception if not in the allowed list`() {
    assertThrows<CsmClientException> {
      rbac.setUserRoles(USER_NEW_READER, USER_READER_ROLES, listOf(USER_ADMIN, USER_WRITER))
    }
  }

  @Test
  fun `add new user roles OK if in the allowed list`() {
    assertDoesNotThrow {
      rbac.setUserRoles(
          USER_NEW_READER, USER_READER_ROLES, listOf(USER_ADMIN, USER_WRITER, USER_NEW_READER))
    }
  }

  @Test
  fun `get user info id`() {
    assertEquals(USER_WRITER, rbac.getUserInfo(USER_WRITER).id)
  }

  @Test
  fun `get user info roles`() {
    assertEquals(USER_WRITER_ROLES, rbac.getUserInfo(USER_WRITER).roles)
  }

  @Test
  fun `get user info permissions`() {
    assertEquals(ROLE_WRITER_PERMS, rbac.getUserInfo(USER_WRITER).permissions)
  }

  @Test
  fun `get user not in info id`() {
    assertEquals(USER_NOTIN, rbac.getUserInfo(USER_NOTIN).id)
  }

  @Test
  fun `get user not in info roles`() {
    assertEquals(listOf(), rbac.getUserInfo(USER_NOTIN).roles)
  }

  @Test
  fun `get user not in info permissions`() {
    assertEquals(listOf(), rbac.getUserInfo(USER_NOTIN).permissions)
  }

  @Test
  fun `create resource security with default admin`() {
    val resourceSecurity = createResourceSecurity(USER_ADMIN, rolesDefinition)
    assertTrue(
        resourceSecurity
            .accessControlList
            .roles
            .get(USER_ADMIN)
            ?.contains(rolesDefinition.adminRole)
            ?: false)
  }

  @Test
  fun `create resource security with two admins`() {
    val resourceSecurity = createResourceSecurity(listOf(USER_ADMIN, USER_READER), rolesDefinition)
    assertTrue(
        (resourceSecurity
            .accessControlList
            .roles
            .get(USER_ADMIN)
            ?.contains(rolesDefinition.adminRole)
            ?: false) &&
            (resourceSecurity
                .accessControlList
                .roles
                .get(USER_READER)
                ?.contains(rolesDefinition.adminRole)
                ?: false))
  }

  // Role definition tests
  @Test
  fun `get default role definition permissions`() {
    val expected: MutableMap<String, List<String>> =
        mutableMapOf(
            COMMON_ROLE_ADMIN to COMMON_ROLE_ADMIN_PERMISSIONS,
            COMMON_ROLE_WRITER to COMMON_ROLE_WRITER_PERMISSIONS,
            COMMON_ROLE_CREATOR to COMMON_ROLE_CREATOR_PERMISSIONS,
            COMMON_ROLE_READER to COMMON_ROLE_READER_PERMISSIONS,
        )
    assertEquals(expected, getCommonRolesDefinition().permissions)
  }

  @Test
  fun `get default role definition default admin`() {
    assertEquals(COMMON_ROLE_ADMIN, getCommonRolesDefinition().adminRole)
  }

  @Test
  fun `add custom role definition`() {
    val definition = getCommonRolesDefinition()
    val customRole = "custom_role"
    val customRolePermissions = listOf(COMMON_PERMISSION_READ, "custom_permission")
    definition.permissions.put(customRole, customRolePermissions)
    val expected: MutableMap<String, List<String>> =
        mutableMapOf(
            COMMON_ROLE_ADMIN to COMMON_ROLE_ADMIN_PERMISSIONS,
            COMMON_ROLE_WRITER to COMMON_ROLE_WRITER_PERMISSIONS,
            COMMON_ROLE_CREATOR to COMMON_ROLE_CREATOR_PERMISSIONS,
            COMMON_ROLE_READER to COMMON_ROLE_READER_PERMISSIONS,
            customRole to customRolePermissions,
        )
    assertEquals(expected, definition.permissions)
  }

  @Test
  fun `check new permission custom ok`() {
    val definition = getCommonRolesDefinition()
    val customRole = "custom_role"
    val customPermission = "custom_permission"
    val customRolePermissions = listOf(COMMON_PERMISSION_READ, customPermission)
    definition.permissions.put(customRole, customRolePermissions)
    val rbacTest = CsmRbac(csmPlatformProperties, definition, RESOURCE_ID)
    rbacTest.setUserRoles(USER_NEW_READER, listOf(customRole))
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbacTest.check(customPermission, USER_NEW_READER))
  }

  // Utilitary methods for rbac creation
  @Test
  fun `can add resource id and resource security in a second step`() {
    val definition = getCommonRolesDefinition()
    val rbacTest = CsmRbac(csmPlatformProperties, definition)
    rbacTest.resourceId = RESOURCE_ID
    rbacTest.setUserRoles(USER_READER, listOf(COMMON_ROLE_READER))
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbacTest.check(COMMON_PERMISSION_READ, USER_READER))
  }

  @Test
  fun `can add resource id and resource security in one call`() {
    val definition = getCommonRolesDefinition()
    val rbacTest = CsmRbac(csmPlatformProperties, definition)
    val security =
        ResourceSecurity(
            accessControlList =
                UsersAccess(roles = mutableMapOf(USER_READER to listOf(COMMON_ROLE_READER))))
    rbacTest.setResourceInfo(RESOURCE_ID, security)
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbacTest.check(COMMON_PERMISSION_READ, USER_READER))
  }

  @Test
  fun `can create resource security from list and map directly`() {
    val definition = getCommonRolesDefinition()
    val rbacTest = CsmRbac(csmPlatformProperties, definition)
    val default = listOf(COMMON_ROLE_READER)
    val roles = mutableMapOf(USER_WRITER to listOf(COMMON_ROLE_WRITER))
    val security = createResourceSecurity(default, roles)
    rbacTest.setResourceInfo(RESOURCE_ID, security)
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbacTest.check(COMMON_PERMISSION_READ, USER_READER))
  }

  @Test
  fun `can create resource security from list and map directly KO`() {
    val definition = getCommonRolesDefinition()
    val rbacTest = CsmRbac(csmPlatformProperties, definition)
    val default = listOf(COMMON_ROLE_READER)
    val roles = mutableMapOf(USER_WRITER to listOf(COMMON_ROLE_WRITER))
    val security = createResourceSecurity(default, roles)
    rbacTest.setResourceInfo(RESOURCE_ID, security)
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertFalse(rbacTest.check(COMMON_PERMISSION_WRITE, USER_READER))
  }

  @Test
  fun `can create resource security with current user as admin`() {
    val definition = getCommonRolesDefinition()
    val rbacTest = CsmRbac(csmPlatformProperties, definition)
    val security = createResourceSecurityCurrentAdmin(definition)
    rbacTest.setResourceInfo(RESOURCE_ID, security)
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbacTest.check(COMMON_PERMISSION_ADMIN, USER_MAIL_TOKEN))
  }

  @Test
  fun `can create resource security from list and map directly writer`() {
    val definition = getCommonRolesDefinition()
    val rbacTest = CsmRbac(csmPlatformProperties, definition)
    val default = listOf(COMMON_ROLE_READER)
    val roles = mutableMapOf(USER_WRITER to listOf(COMMON_ROLE_WRITER))
    val security = createResourceSecurity(default, roles)
    rbacTest.setResourceInfo(RESOURCE_ID, security)
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbacTest.check(COMMON_PERMISSION_WRITE, USER_WRITER))
  }

  @Test
  fun `can create resource security from list and map directly token`() {
    val definition = getCommonRolesDefinition()
    val rbacTest = CsmRbac(csmPlatformProperties, definition)
    val default = listOf(COMMON_ROLE_READER)
    val roles = mutableMapOf(USER_MAIL_TOKEN to listOf(COMMON_ROLE_WRITER))
    val security = createResourceSecurity(default, roles)
    rbacTest.setResourceInfo(RESOURCE_ID, security)
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertDoesNotThrow { rbacTest.verify(COMMON_PERMISSION_WRITE) }
  }

  @Test
  fun `can create resource security from list and map directly in rbac writer`() {
    val definition = getCommonRolesDefinition()
    val rbacTest = CsmRbac(csmPlatformProperties, definition)
    val default = listOf(COMMON_ROLE_READER)
    val roles = mutableMapOf(USER_WRITER to listOf(COMMON_ROLE_WRITER))
    rbacTest.setResourceInfo(RESOURCE_ID, default, roles)
    every { securityContext.authentication } returns (userAuthentication as Authentication)
    assertTrue(rbacTest.check(COMMON_PERMISSION_WRITE, USER_WRITER))
  }
}
