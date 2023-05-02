// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

const val BUFFER_SIZE = 2048

data class UnzippedFile(val filename: String, val content: InputStream)

fun unzip(file: InputStream, prefixName: String, fileExtension: String): List<UnzippedFile> =
    ZipInputStream(file).use { zipInputStream ->
      generateSequence { zipInputStream.nextEntry }
          .filterNot { it.isDirectory }
          .filter { it.name.startsWith(prefixName, true) }
          .filter { it.name.endsWith(fileExtension, true) }
          .map {
            UnzippedFile(
                filename = it.name.cutFileNameFromPath(), content = zipInputStream.toInputStream())
          }
          .toList()
    }

@Throws(IOException::class)
fun ZipInputStream.toInputStream(): InputStream {
  val data = ByteArray(BUFFER_SIZE)
  val out = ByteArrayOutputStream()
  while (this.read(data, 0, BUFFER_SIZE) != -1) {
    out.write(data)
  }
  return ByteArrayInputStream(out.toByteArray())
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
