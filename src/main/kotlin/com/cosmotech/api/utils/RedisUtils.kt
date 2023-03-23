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
 * Zip a list of ByteArray with their file names.
 * @param nameFilePair a list of Pair<String, ByteArray> where the first element is the file name
 * and the second element is the byte array to zip
 */
fun zipBytesWithFileNames(nameFilePair: List<Pair<String, ByteArray>>): ByteArray? {
  if (nameFilePair.isEmpty()) return null
  val byteArrayOutputStream = ByteArrayOutputStream()
  val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
  for (file in nameFilePair) {
    val entry = ZipEntry(file.first).apply { size = file.second.size.toLong() }
    zipOutputStream.putNextEntry(entry)
    zipOutputStream.write(file.second)
  }
  zipOutputStream.closeEntry()
  zipOutputStream.close()
  return byteArrayOutputStream.toByteArray()
}
