// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.springframework.data.domain.PageRequest

fun constructPageRequest(page: Int?, size: Int?, defaultPageSize: Int): PageRequest? {
  return when {
    page != null && size != null -> PageRequest.of(page, size)
    page != null && size == null -> PageRequest.of(page, defaultPageSize)
    page == null && size != null -> PageRequest.of(0, size)
    else -> null
  }
}

fun <T> findAllPaginated(
    maxResult: Int,
    findAllLambda: (pageRequest: PageRequest) -> MutableList<T>
): MutableList<T> {
  var pageRequest = PageRequest.ofSize(maxResult)
  var list = mutableListOf<T>()
  do {
    var objectList = findAllLambda(pageRequest)
    pageRequest = pageRequest.next()
    list.addAll(objectList)
  } while (objectList.isNotEmpty())
  return list
}

/**
 * Zip a map of file names and byte arrays into a single byte array
 * @param fileNameByteArray The map of file names and byte arrays to zip
 */
fun zipBytesWithFileNames(fileNameByteArray: Map<String, ByteArray>): ByteArray? {
  if (fileNameByteArray.isEmpty()) return null
  val byteArrayOutputStream = ByteArrayOutputStream()
  val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
  for (file in fileNameByteArray) {
    val entry = ZipEntry(file.key).apply { size = file.value.size.toLong() }
    zipOutputStream.putNextEntry(entry)
    zipOutputStream.write(file.value)
  }
  zipOutputStream.closeEntry()
  zipOutputStream.close()
  return byteArrayOutputStream.toByteArray()
}
