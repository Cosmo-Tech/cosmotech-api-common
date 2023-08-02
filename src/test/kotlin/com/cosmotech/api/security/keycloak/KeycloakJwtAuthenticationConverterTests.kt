// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.security.keycloak

import com.cosmotech.api.config.CsmPlatformProperties
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class KeycloakJwtAuthenticationConverterTests {

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  private lateinit var csmPlatformProperties: CsmPlatformProperties

  lateinit var keycloakJwtAuthenticationConverter: KeycloakJwtAuthenticationConverter

  lateinit var jwt: Jwt

  @BeforeTest
  fun beforeEachTest() {
    logger.trace("Begin test")
    csmPlatformProperties = mockk<CsmPlatformProperties>(relaxed = true)
    jwt = mockk<Jwt>(relaxed = true)
    keycloakJwtAuthenticationConverter = KeycloakJwtAuthenticationConverter(csmPlatformProperties)
  }

  @Test
  fun `convertRolesToAuthorities with correct values`() {
    val claims =
        mutableMapOf(
            "claim1" to "10",
            "claimRoles" to listOf("role1", "role2", "role3"),
            "claimName" to "myClaimName",
            "sub" to "my.principal@me.com")
    val expectedSimpleGrantedAuthorities =
        listOf(
            SimpleGrantedAuthority("role1"),
            SimpleGrantedAuthority("role2"),
            SimpleGrantedAuthority("role3"))

    every { jwt.claims } returns claims
    every { csmPlatformProperties.authorization.rolesJwtClaim } returns "claimRoles"

    val jwtConverted = keycloakJwtAuthenticationConverter.convert(jwt)

    assertEquals(
        JwtAuthenticationToken(jwt, expectedSimpleGrantedAuthorities, "my.principal@me.com"),
        jwtConverted)
  }

  @Test
  fun `convertRolesToAuthorities with non-existing role claim values`() {
    val claims =
        mutableMapOf(
            "claim1" to "10",
            "claimRoles" to listOf("role1", "role2", "role3"),
            "claimName" to "myClaimName",
            "sub" to "my.principal@me.com")

    every { jwt.claims } returns claims
    every { csmPlatformProperties.authorization.rolesJwtClaim } returns "unexisting-role-claim"

    val jwtConverted = keycloakJwtAuthenticationConverter.convert(jwt)

    assertEquals(JwtAuthenticationToken(jwt, emptyList(), "my.principal@me.com"), jwtConverted)
  }

  @Test
  fun `convertRolesToAuthorities with existing role claim but no roles defined`() {
    val claims =
        mutableMapOf(
            "claim1" to "10",
            "claimRoles" to emptyList<String>(),
            "claimName" to "myClaimName",
            "sub" to "my.principal@me.com")

    every { jwt.claims } returns claims
    every { csmPlatformProperties.authorization.rolesJwtClaim } returns "claimRoles"

    val jwtConverted = keycloakJwtAuthenticationConverter.convert(jwt)

    assertEquals(JwtAuthenticationToken(jwt, emptyList(), "my.principal@me.com"), jwtConverted)
  }
}
