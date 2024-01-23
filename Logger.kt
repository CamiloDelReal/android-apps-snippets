package package.core.utils

import timber.log.Timber


inline fun <reified T> debug(message: String, vararg args: Any) {
    Timber.tag(T::class.java.simpleName).d(message, args)
}

inline fun <reified T> debug(throwable: Throwable, message: String, vararg args: Any) {
    Timber.tag(T::class.java.simpleName).d(throwable, message, args)
}

inline fun <reified T> info(message: String, vararg args: Any) {
    Timber.tag(T::class.java.simpleName).i(message, args)
}

inline fun <reified T> info(throwable: Throwable, message: String, vararg args: Any) {
    Timber.tag(T::class.java.simpleName).i(throwable, message, args)
}

inline fun <reified T> error(message: String, vararg args: Any) {
    Timber.tag(T::class.java.simpleName).e(message, args)
}

inline fun <reified T> error(throwable: Throwable) {
    Timber.tag(T::class.java.simpleName).e(throwable)
}

inline fun <reified T> error(throwable: Throwable, message: String, vararg args: Any) {
    Timber.tag(T::class.java.simpleName).e(throwable, message, args)
}

inline fun <reified T> warning(message: String, vararg args: Any) {
    Timber.tag(T::class.java.simpleName).w(message, args)
}

inline fun <reified T> warning(throwable: Throwable, message: String, vararg args: Any) {
    Timber.tag(T::class.java.simpleName).w(throwable, message, args)
}
