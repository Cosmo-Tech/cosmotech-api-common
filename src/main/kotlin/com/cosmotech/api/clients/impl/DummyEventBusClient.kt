// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.clients.impl

import com.cosmotech.api.clients.EventBusClient
import com.cosmotech.api.scenario.MetaData
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Component

@Component
@Deprecated("Will be removed once cosmotech-api-scenario and cosmotech-api-scenario are removed")
@ConditionalOnExpression("'\${csm.platform.internalResultServices.enabled}' == 'true'")
class DummyEventBusClient : EventBusClient {

  override fun sendMetaData(
      fullyQualifiedNamespace: String,
      eventHubName: String,
      sharedAccessPolicy: String,
      sharedAccessKey: String,
      metaData: MetaData
  ) {
    TODO("Not yet implemented")
  }

  override fun sendMetaData(
      fullyQualifiedNamespace: String,
      eventHubName: String,
      metaData: MetaData
  ) {
    TODO("Not yet implemented")
  }
}
