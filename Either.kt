package package.core.utils


sealed class Either<out L, out R> {

    data class Error<out L>(val error: L) : Either<L, Nothing>()
    data class Success<out R>(val success: R) : Either<Nothing, R>()

    val isSuccess get() = this is Success<R>
    val isError get() = this is Error<L>

    fun either(fnL: (L) -> Unit, fnR: (R) -> Unit): Any =
        when (this) {
            is Error -> fnL(error)
            is Success -> fnR(success)
        }

    suspend inline fun <T> coMapSuccess(
        crossinline transform: suspend (R) -> T
    ): Either<L, T> {
        return when (this) {
            is Success -> transform(this.success).toSuccess()
            is Error -> this
        }
    }

    inline fun <T> mapSuccess(
        crossinline transform: (R) -> T
    ): Either<L, T> {
        return when (this) {
            is Success -> transform(this.success).toSuccess()
            is Error -> this
        }
    }

    fun getSuccessOrNull(): R? = if (this is Success<R>) {
        this.success
    } else {
        null
    }

    fun getFailureOrNull(): L? = if (this is Error<L>) {
        this.error
    } else {
        null
    }
}

fun <R> R.toSuccess(): Either.Success<R> {
    return Either.Success(this)
}

fun <L> L.toError(): Either.Error<L> {
    return Either.Error(this)
}