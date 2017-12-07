package io.scal.ambi.entity.actions

import io.scal.ambi.entity.User
import org.joda.time.LocalDateTime

data class Comment(val user: User,
                   val message: String,
                   val dateTime: LocalDateTime)