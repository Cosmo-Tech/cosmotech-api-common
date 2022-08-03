// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

data class ResourceSecurity(
    var default: List<String> = listOf(),
    val accessControlList: UsersAccess = UsersAccess(),
)

fun createResourceSecurity(user: String, rolesDefinition: RolesDefinition): ResourceSecurity {
  val adminRoles = listOf(rolesDefinition.adminRole)
  return ResourceSecurity(accessControlList = UsersAccess(roles = mutableMapOf(user to adminRoles)))
}

fun createResourceSecurity(
    users: List<String>,
    rolesDefinition: RolesDefinition
): ResourceSecurity {
  val adminRoles = listOf(rolesDefinition.adminRole)
  return ResourceSecurity(
      accessControlList = UsersAccess(roles = users.associate { it to adminRoles }.toMutableMap()))
}
