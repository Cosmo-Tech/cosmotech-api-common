// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.metrics

private const val DEFAULT_PROVIDER = "cosmotech"

private const val DEFAULT_QUALIFIER = "data"

private const val DEFAULT_SCOPE = "metric"

// key will be ts:scope:vendor:service:name:qualifier
data class PersistentMetric(
    val service: String,
    val name: String,
    val value: Double,
    val incrementBy: Int = 0,
    val labels: Map<String, String> = emptyMap(),
    val qualifier: String = DEFAULT_QUALIFIER,
    val timestamp: Long = System.currentTimeMillis(),
    val vendor: String = DEFAULT_PROVIDER,
    val retention: Long = 0,
    val type: PersitentMetricType = PersitentMetricType.COUNTER,
    val scope: String = DEFAULT_SCOPE,
)
