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
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class CsmRbacTests {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  private lateinit var csmPlatformProperties: CsmPlatformProperties
  private lateinit var rbac : CsmRbac

  @BeforeTest
  fun beforeEachTest() {
    logger.trace("Begin test")
    csmPlatformProperties = mockk<CsmPlatformProperties>(relaxed = true)
    every { csmPlatformProperties.rbac.enabled } answers { true }
    rbac = CsmRbac(csmPlatformProperties)
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
}
