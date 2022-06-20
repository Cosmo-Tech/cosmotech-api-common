// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.scenario

data class ScenarioMetaData(
    val organizationId: String,
    val workspaceId: String,
    val scenarioId: String,
    val name: String,
    val description: String,
    val parentId: String,
    val solutionName: String,
    val runTemplateName: String,
    val validationStatus: String,
    val updateTime: String
) : MetaData
