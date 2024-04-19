// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.clients

import com.cosmotech.api.scenario.MetaData

@Deprecated("Will be removed once cosmotech-api-scenario and cosmotech-api-scenariorun are removed")
interface EventBusClient {
  fun sendMetaData(
      fullyQualifiedNamespace: String,
      eventHubName: String,
      sharedAccessPolicy: String,
      sharedAccessKey: String,
      metaData: MetaData
  )

  fun sendMetaData(fullyQualifiedNamespace: String, eventHubName: String, metaData: MetaData)
}
