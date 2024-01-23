package package.core.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


@SuppressLint("SimpleDateFormat")
fun Date.parseToString(format: String? = null): String {
    val pattern = format ?: "HH:mm:ss yyyy-MM-dd"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(this)
}