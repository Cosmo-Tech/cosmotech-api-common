// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtensionsTests {

  @Test
  fun `sanitizeForKubernetes should replace special characters with '-' and takeLast`() {
    val input = "ALL.my/namespace:with-SPECIAL_chars"
    val expected = "all-my-namespace-with-special-chars"
    var actual = input.sanitizeForKubernetes()
    assertEquals(expected, actual)
    actual = input.sanitizeForKubernetes(13)
    assertEquals("special-chars", actual)
  }

  @Test
  fun `sanitizeForRedis should escape special characters`() {
    val input = "te.st@cosmo-tech.com"
    val expected = "te\\.st\\@cosmo\\-tech\\.com"
    val actual = input.sanitizeForRedis()
    assertEquals(expected, actual)
  }

  @Test
  fun `shaHash should return a SHA-256 hash of the input string`() {
    val input = "test"
    val expected = "9F86D081884C7D659A2FEAA0C55AD015A3BF4F1B2B0B822CD15D6C15B0F00A08"
    val actual = input.shaHash()
    assertEquals(expected, actual)
  }

  @Test
  fun `redisMetadataKey should return the metadata key for the given key`() {
    val input = "test"
    val expected = "testMetaData"
    val actual = input.redisMetaDataKey()
    assertEquals(expected, actual)
  }
}
