package package.core.utils

import android.content.Context
import android.os.Environment
import java.io.File

object PathUtils {

    @JvmStatic
    fun getAppStorageFolder(context: Context): String {
        var storageFolder: File?

        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            val externalFileDir = context.getExternalFilesDir(null)
            if (externalFileDir == null) {
                storageFolder = context.filesDir
            } else {
                storageFolder = externalFileDir
            }
        } else {
            storageFolder = context.filesDir
        }
        return storageFolder!!.absolutePath
    }

}