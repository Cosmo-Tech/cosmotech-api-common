// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.clients.impl

import com.cosmotech.api.clients.ResultDataClient
import com.cosmotech.api.scenariorun.DataIngestionState
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Component

@Component
@Deprecated("Will be removed once cosmotech-api-scenario and cosmotech-api-scenario are removed")
@ConditionalOnExpression("'\${csm.platform.internalResultServices.enabled}' == 'true'")
class DummyResultDataClient : ResultDataClient {

  override fun deleteDataFromADXbyExtentShard(
      organizationId: String,
      workspaceKey: String,
      extentShard: String
  ): String {
    TODO("Not yet implemented")
  }

  override fun getStateFor(
      organizationId: String,
      workspaceKey: String,
      scenarioRunId: String,
      csmSimulationRun: String
  ): DataIngestionState {
    TODO("Not yet implemented")
  }
}
