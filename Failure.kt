package package.core.utils


sealed class Failure {

    data class Exception(val description: Any?): Failure()
    object SomeFailure: Failure()

}