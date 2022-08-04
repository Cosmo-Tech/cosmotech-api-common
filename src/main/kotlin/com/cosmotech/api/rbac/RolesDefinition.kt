// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import org.springframework.stereotype.Component

// openapi generator takes only last term if _ in name or bad parsing if -
public const val COMMON_ROLE_ADMIN = "commonroleadmin"
public const val COMMON_ROLE_WRITER = "commonrolewriter"
public const val COMMON_ROLE_CREATOR = "commonrolecreator"
public const val COMMON_ROLE_READER = "commonrolereader"

// apply same format rules for permission for consistency
public const val COMMON_PERMISSION_ADMIN = "commonpermissionadmin"
public const val COMMON_PERMISSION_DELETE = "commonpermissiondelete"
public const val COMMON_PERMISSION_WRITE = "commonpermissionwrite"
public const val COMMON_PERMISSION_CREATE_CHILDREN = "commonpermissioncreatechildren"
public const val COMMON_PERMISSION_READ = "commonpermissionread"

public val COMMON_ROLE_ADMIN_PERMISSIONS =
    listOf(
        COMMON_PERMISSION_CREATE_CHILDREN,
        COMMON_PERMISSION_READ,
        COMMON_PERMISSION_WRITE,
        COMMON_PERMISSION_DELETE,
        COMMON_PERMISSION_ADMIN)
public val COMMON_ROLE_WRITER_PERMISSIONS =
    listOf(COMMON_PERMISSION_CREATE_CHILDREN, COMMON_PERMISSION_READ, COMMON_PERMISSION_WRITE)
public val COMMON_ROLE_CREATOR_PERMISSIONS =
    listOf(COMMON_PERMISSION_CREATE_CHILDREN, COMMON_PERMISSION_READ)
public val COMMON_ROLE_READER_PERMISSIONS = listOf(COMMON_PERMISSION_READ)

@Component
data class RolesDefinition(
    val permissions: MutableMap<String, List<String>> = mutableMapOf(),
    val adminRole: String = COMMON_ROLE_ADMIN
)

fun getCommonRolesDefinition(): RolesDefinition {
  return RolesDefinition(
      permissions =
          mutableMapOf(
              COMMON_ROLE_ADMIN to COMMON_ROLE_ADMIN_PERMISSIONS,
              COMMON_ROLE_WRITER to COMMON_ROLE_WRITER_PERMISSIONS,
              COMMON_ROLE_CREATOR to COMMON_ROLE_CREATOR_PERMISSIONS,
              COMMON_ROLE_READER to COMMON_ROLE_READER_PERMISSIONS,
          ),
      adminRole = COMMON_ROLE_ADMIN)
}
