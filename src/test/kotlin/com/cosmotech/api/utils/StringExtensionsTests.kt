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
    val input = "te.st@cosmotech.com"
    val expected = "te\\.st\\@cosmotech\\.com"
    val actual = input.sanitizeForRedis()
    assertEquals(expected, actual)
  }
}
