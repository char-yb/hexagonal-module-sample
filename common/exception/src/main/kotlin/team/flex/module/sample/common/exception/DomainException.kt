/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

package team.flex.module.sample.common.exception

abstract class DomainException(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

abstract class NotFoundException(
    message: String? = null
) : DomainException(message)
