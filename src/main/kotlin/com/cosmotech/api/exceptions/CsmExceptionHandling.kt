// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
open class CsmExceptionHandling : ResponseEntityExceptionHandler() {

  @ExceptionHandler
  fun handleIllegalArgumentException(
      exception: IllegalArgumentException
  ): ProblemDetail {
    if (exception.message == null) {
      return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
    }
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,exception.message!!)
  }

    @ExceptionHandler
    fun handleInsufficientAuthenticationException(
        exception: InsufficientAuthenticationException
    ): ProblemDetail{
      if (exception.message == null) {
        return ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)
      }
      return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,exception.message!!)
    }

}
