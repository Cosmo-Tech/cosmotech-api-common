// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.exceptions

import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
open class CsmExceptionHandling : ResponseEntityExceptionHandler() {

/*
  @ExceptionHandler
  fun handleIllegalArgumentException(
      exception: IllegalArgumentException
  ): ProblemDetail {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message)
  }

  @ExceptionHandler
  fun handleInsufficientAuthenticationException(
      exception: InsufficientAuthenticationException,
      request: NativeWebRequest
  ): ResponseEntity<Problem> = create(Status.UNAUTHORIZED, exception, request)*/
}
