// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
@file:Suppress("MatchingDeclarationName")

package com.cosmotech.api.events

/** Event throw when a graph import job is requested */
class TwingraphImportEvent(
    publisher: Any,
    val jobId: String,
    val organizationId: String,
    val twingraphId: String,
    val sourceName: String,
    val sourceLocation: String,
    val sourcePath: String,
    val sourceType: String,
    val version: String?,
    val queries: MutableList<String>?,
) : CsmRequestResponseEvent<Map<String, Any>>(publisher)

class TwingraphImportJobInfoRequest(
    publisher: Any,
    val jobId: String,
    val organizationId: String,
) : CsmRequestResponseEvent<String>(publisher)
