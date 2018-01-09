package io.scal.ambi.model.repository.auth

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.model.data.server.AuthApi
import io.scal.ambi.model.data.server.LoginRequest
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject


class AuthRepository @Inject constructor(private val authApi: AuthApi) : IAuthRepository {

    override fun login(email: String, password: String): Single<AuthResult> =
        authApi.login(LoginRequest(email, password))
            .map { it.parse() }
            .onErrorResumeNext { t -> Single.error(t.toServerResponseException()) }
//            .onErrorReturnItem(AuthResult("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVhMzlkYWU5YzI4MjZhNTE4ZjYzNmY5YSIsInNjaG9vbCI6IjVhMzlkYWU3YzI4MjZhNTE4ZjYzNmY4ZSIsImZpcnN0TmFtZSI6ImdlbmV2aWV2ZSIsImxhc3ROYW1lIjoid2lsbGlhbXMiLCJlbWFpbCI6ImdlbmV2aWV2ZUBhbWJpLndvcmsiLCJ0eXBlIjoiU3R1ZGVudCIsInNldHRpbmdzIjp7ImJhY2tncm91bmQiOiIiLCJzb3VuZHMiOnRydWUsIndlYXRoZXJGb3JtYXQiOiJmIiwiY2xvY2tGb3JtYXQiOjEyLCJzZWVuVHV0b3JpYWwiOnRydWUsIndpZGdldHMiOnsiZGF0ZUFuZFRpbWUiOnRydWUsImRhaWx5UXVvdGVzIjp0cnVlLCJ3ZWF0aGVyIjpmYWxzZSwiZGFpbHlJbnRlcmVzdCI6dHJ1ZSwiY291bnRkb3duIjp0cnVlLCJmZWVkYmFjayI6dHJ1ZSwiY291bnRkb3duRXZlbnQiOiIiLCJjb3VudGRvd25EYXRlIjpudWxsLCJkYWlseUludGVyZXN0VGV4dCI6IiJ9fSwicHJvZmlsZVBpY3R1cmUiOnsiX2lkIjoiNWEzOWRhZTljMjgyNmE1MThmNjM2Zjk0IiwibmFtZSI6ImRlZmF1bHQtcHJvZmlsZS1waWMtNi5zdmciLCJ1cmwiOiIvaW1hZ2VzL3NpZ251cC9kZWZhdWx0LXByb2ZpbGUtcGljLTYuc3ZnIiwiZmlsZVR5cGUiOiIiLCJfX3YiOjAsImNyZWF0ZWRBdCI6IjIwMTctMTItMjBUMDM6Mzc6MTMuMzQzWiIsInVwZGF0ZWRBdCI6IjIwMTctMTItMjBUMDM6Mzc6MTMuMzQzWiJ9LCJzbHVnIjoiZ2VuZXZpZXZlLWFtYmktd29yayIsImlhdCI6MTUxNTQ4NzQzMCwiZXhwIjoxNTE1NTczODMwfQ.1E9C5eXIUYn1Wdpsn-M9Bh2pgfNZC6CTbW1XueurXdo"))

    override fun recover(email: String): Completable =
        authApi.recover()
}