// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.exceptions

import java.net.URI
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
open class CsmExceptionHandling : ResponseEntityExceptionHandler() {

  private val httpStatusCodeTypePrefix = "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/"

  @ExceptionHandler
  fun handleIllegalArgumentException(exception: IllegalArgumentException): ProblemDetail {
    val badRequestStatus = HttpStatus.BAD_REQUEST
    val problemDetail = ProblemDetail.forStatus(badRequestStatus)
    problemDetail.type = URI.create(httpStatusCodeTypePrefix + badRequestStatus.value())

    if (exception.message != null) {
      problemDetail.detail = exception.message
    }
    return problemDetail
  }

  @ExceptionHandler
  fun handleInsufficientAuthenticationException(
      exception: InsufficientAuthenticationException
  ): ProblemDetail {
    val unauthorizedStatus = HttpStatus.UNAUTHORIZED
    val problemDetail = ProblemDetail.forStatus(unauthorizedStatus)
    problemDetail.type = URI.create(httpStatusCodeTypePrefix + unauthorizedStatus.value())

    if (exception.message != null) {
      problemDetail.detail = exception.message
    }
    return problemDetail
  }

  @ExceptionHandler
  fun handleCsmClientException(exception: CsmClientException): ProblemDetail {
    val badRequestStatus = HttpStatus.BAD_REQUEST
    val problemDetail = ProblemDetail.forStatus(badRequestStatus)
    problemDetail.type = URI.create(httpStatusCodeTypePrefix + badRequestStatus.value())
    problemDetail.detail = exception.message
    return problemDetail
  }

  @ExceptionHandler(AuthenticationServiceException::class)
  fun handleAuthenticationServiceException(
      exception: AuthenticationServiceException
  ): ProblemDetail {
    val response = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    val internalServerErrorStatus = HttpStatus.INTERNAL_SERVER_ERROR
    response.type = URI.create(httpStatusCodeTypePrefix + internalServerErrorStatus.value())
    if (exception.message != null) {
      response.detail = exception.message
    }
    return response
  }

  @ExceptionHandler(IndexOutOfBoundsException::class)
  fun handleIndexOutOfBoundsException(exception: IndexOutOfBoundsException): ProblemDetail {
    val response = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    val internalServerErrorStatus = HttpStatus.INTERNAL_SERVER_ERROR
    response.type = URI.create(httpStatusCodeTypePrefix + internalServerErrorStatus.value())
    if (exception.message != null) {
      response.detail = exception.message
    }
    return response
  }
}
