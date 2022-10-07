// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac.model

import com.cosmotech.api.rbac.ROLE_NONE

open class RbacSecurity(
    open var id: String?,
    open var default: String = ROLE_NONE,
    open var accessControlList: kotlin.collections.MutableList<RbacAccessControl> = mutableListOf()
)
