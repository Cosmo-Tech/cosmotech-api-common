// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.metrics

private const val DEFAULT_PROVIDER = "cosmotech"

data class PersistentMetric(
    val service: String,
    val name: String,
    val value: Double,
    val incrementBy: Int = 0,
    val tags: Map<String, String> = emptyMap(),
    val qualifier: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val vendor: String = DEFAULT_PROVIDER,
    val retention: Long = 0,
)
