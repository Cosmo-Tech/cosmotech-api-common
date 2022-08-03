// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

public const val COMMON_ROLE_ADMIN = "common_role_admin"
public const val COMMON_ROLE_WRITER = "common_role_writer"
public const val COMMON_ROLE_CREATOR = "common_role_creator"
public const val COMMON_ROLE_READER = "common_role_reader"

public const val COMMON_PERMISSION_ADMIN = "common_permission_admin"
public const val COMMON_PERMISSION_DELETE = "common_permission_delete"
public const val COMMON_PERMISSION_WRITE = "common_permission_write"
public const val COMMON_PERMISSION_CREATE_CHILDREN = "common_permission_create_children"
public const val COMMON_PERMISSION_READ = "common_permission_read"

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

data class RolesDefinition(
    val permissions: MutableMap<String, List<String>>,
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
