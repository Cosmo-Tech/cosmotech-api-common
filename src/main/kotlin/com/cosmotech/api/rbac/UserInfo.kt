// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

data class UserInfo(
    val id: String,
    val roles: List<String>,
    val permissions: List<String>,
)
