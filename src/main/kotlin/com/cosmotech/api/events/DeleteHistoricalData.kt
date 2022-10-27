// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.events

class DeleteHistoricalDataOrganization(
    publisher: Any,
    val organizationId: String,
    val deleteUnknown: Boolean = false
) : CsmEvent(publisher)

class DeleteHistoricalDataWorkspace(
    publisher: Any,
    val organizationId: String,
    val workspaceId: String,
    val deleteUnknown: Boolean = false
) : CsmEvent(publisher)

class DeleteHistoricalDataScenario(
    publisher: Any,
    val organizationId: String,
    val workspaceId: String,
    val scenarioId: String,
    val deleteUnknown: Boolean = false
) : CsmEvent(publisher)
