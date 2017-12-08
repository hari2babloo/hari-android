package io.scal.ambi.model.repository.auth

import io.scal.ambi.entity.User

data class AuthResult(val token: String,
                      val user: User)