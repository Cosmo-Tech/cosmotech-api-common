package com.cosmotech.api.rbac.model

import com.cosmotech.api.rbac.ROLE_VIEWER

open class RbacAccessControl (
    open var id: String = "emptyId",
    open var role: String = ROLE_VIEWER
    )
