// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.metrics

import com.redislabs.redistimeseries.Aggregation

enum class DownSamplingAggregationType(val value: String) {
  AVG("avg"),
  SUM("sum"),
  MIN("min"),
  MAX("max"),
  RANGE("range"),
  COUNT("count"),
  FIRST("first"),
  LAST("last"),
  STDP("std.p"),
  STDS("std.s)"),
  VARP("var.p"),
  VARS("var.s"),
}

fun DownSamplingAggregationType.toRedisAggregation(): Aggregation {
  return when (this) {
    DownSamplingAggregationType.AVG -> Aggregation.AVG
    DownSamplingAggregationType.SUM -> Aggregation.SUM
    DownSamplingAggregationType.MIN -> Aggregation.MIN
    DownSamplingAggregationType.MAX -> Aggregation.MAX
    DownSamplingAggregationType.RANGE -> Aggregation.RANGE
    DownSamplingAggregationType.COUNT -> Aggregation.COUNT
    DownSamplingAggregationType.FIRST -> Aggregation.FIRST
    DownSamplingAggregationType.LAST -> Aggregation.LAST
    DownSamplingAggregationType.STDP -> Aggregation.STD_P
    DownSamplingAggregationType.STDS -> Aggregation.STD_S
    DownSamplingAggregationType.VARP -> Aggregation.VAR_P
    DownSamplingAggregationType.VARS -> Aggregation.VAR_S
  }
}
