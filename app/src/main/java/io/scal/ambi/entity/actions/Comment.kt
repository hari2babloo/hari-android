package io.scal.ambi.entity.actions

import io.scal.ambi.entity.user.User
import org.joda.time.DateTime

data class Comment(val id: String,
                   val user: User,
                   val message: String,
                   val dateTime: DateTime)