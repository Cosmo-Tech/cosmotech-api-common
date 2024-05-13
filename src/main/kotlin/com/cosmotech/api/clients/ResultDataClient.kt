// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.clients

import com.cosmotech.api.scenariorun.DataIngestionState

@Deprecated("Will be removed once cosmotech-api-scenario and cosmotech-api-scenariorun are removed")
interface ResultDataClient {

  fun deleteDataFromADXbyExtentShard(
      organizationId: String,
      workspaceKey: String,
      extentShard: String
  ): String

  fun getStateFor(
      organizationId: String,
      workspaceKey: String,
      scenarioRunId: String,
      csmSimulationRun: String
  ): DataIngestionState
}
