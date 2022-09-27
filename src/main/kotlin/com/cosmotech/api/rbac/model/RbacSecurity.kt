package com.cosmotech.api.rbac.model

import com.cosmotech.api.rbac.ROLE_VIEWER

open class RbacSecurity (
    open var default: String = ROLE_VIEWER,
    open var accessControlList: kotlin.collections.MutableList<RbacAccessControl> = mutableListOf()
    )
