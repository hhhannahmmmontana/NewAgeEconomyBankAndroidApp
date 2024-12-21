package com.volodya.newageeconomybank.androidapplication.logic.api.extra

sealed class RespondType {
    class SuccessfulAuthorization(val token: String) : RespondType()

    class Collision(val username: String, val statusCode: StatusCode) : RespondType()

    class Failure : RespondType()

    class AdminError : RespondType()

    class NotFound : RespondType()

    class OkWithData<T>(val data: T) : RespondType()

    class Unauthorized : RespondType()

    class Ok : RespondType()
}