package io.scal.ambi.model.data.server

class ServerResponseException(throwable: Throwable, private val code: Int, val serverMessage: String) : Exception(throwable) {

    val serverError = codeServerError == code
    val notAuthorized = codeNotAuthorized == code
    val requiresLogin = codeRequiresLogin == code
    val notFound = codeNotFound == code
    val badRequest = codeBadRequest == code

    companion object {

        private const val codeServerError = 500
        private const val codeNotAuthorized = 401
        private const val codeRequiresLogin = 403
        private const val codeNotFound = 404
        private const val codeBadRequest = 406
    }
}