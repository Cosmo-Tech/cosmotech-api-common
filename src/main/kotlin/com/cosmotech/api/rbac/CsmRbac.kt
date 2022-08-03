package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import org.springframework.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CsmRbac(@Autowired(required = true) val csmPlatformProperties: CsmPlatformProperties) {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun verifyOwner(ownerId: String, userId: String): Boolean {
    logger.debug("Verifying $userId is Owner")
    return ownerId.equals(userId)
  }
}
