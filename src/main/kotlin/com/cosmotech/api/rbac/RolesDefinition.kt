// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

public const val ROLE_ADMIN = "admin"
data class RolesDefinition(
  val permissions: Map<String, List<String>>,
  val adminRole: String = ROLE_ADMIN
)
