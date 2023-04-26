// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.xml.bind.annotation.adapters.HexBinaryAdapter

private const val KUBERNETES_RESOURCE_NAME_MAX_LENGTH = 63

/**
 * Sanitize the given String for use as a Kubernetes resource name.
 *
 * By convention, the names of Kubernetes resources should be up to maximum length of 253 characters
 * and consist of lower case alphanumeric characters, -, and ., but certain resources have more
 * specific restrictions.
 *
 * See https://kubernetes.io/docs/concepts/overview/working-with-objects/names/
 *
 * @throws IllegalArgumentException if [maxLength] is negative.
 */
fun String.sanitizeForKubernetes(maxLength: Int = KUBERNETES_RESOURCE_NAME_MAX_LENGTH) =
    this.replace("/", "-")
        .replace(":", "-")
        .replace("_", "-")
        .replace(".", "-")
        .lowercase()
        .takeLast(maxLength)

fun String.sanitizeForRedis() = this.replace("@", "\\@").replace(".", "\\.").replace("-", "\\-")

fun String.toSecurityConstraintQuery() =
    "((-@security_default:{none})|(@security_accessControlList_id:{${this.sanitizeForRedis()}}" +
        " -@security_accessControlList_role:{none}))"

fun String.shaHash(): String {
  val messageDigest = MessageDigest.getInstance("SHA-256")
  messageDigest.update(this.toByteArray(StandardCharsets.UTF_8))
  return (HexBinaryAdapter()).marshal(messageDigest.digest())
}

fun String.toRedisMetaDataKey() = "${this}MetaData"

fun String.formatQuery(map: Map<String, String>): String {
  var newValue = this
  map.forEach { (key, value) ->
    var sanitizedValue =
        if (value.isNullOrBlank()) {
          "null"
        } else {
          "\"${value.replace("\"","\\\"")}\""
        }
    newValue = newValue.replace("$$key", sanitizedValue)
  }
  return newValue
}
