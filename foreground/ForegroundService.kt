package package.services.foreground

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


open class ForegroundService : Service() {
    protected var started = false

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(FOREGROUND_CHANNEL_ID, FOREGROUND_SERVICE_CHANNEL, false)
        }
        debug<ForegroundService>("onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (action != null) {
                when (action) {
                    ACTION_START_FOREGROUND_SERVICE -> {
                        val extras = intent.extras
                        startForegroundService(extras)
                    }
                    ACTION_STOP_FOREGROUND_SERVICE -> stopForegroundService()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    open fun startForegroundService(extras: Bundle?) {
        if (!started) {
            debug<ForegroundService>("Start foreground service.")
            val classname = extras!!.getString(ACTIVITY_CLASSNAME, "")
            val notificationTitle = extras.getString(NOTIFICATION_TITLE, "")
            val notificationMessage = extras.getString(NOTIFICATION_MESSAGE, "")
            var activityClass: Class<*>? = null
            try {
                activityClass = Class.forName(classname)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

            val notification: Notification =
                createNotification(notificationTitle, notificationMessage, activityClass)

            startForeground(1, notification)
            started = true
        }
    }

    protected fun createNotification(
        notificationTitle: String?,
        notificationMessage: String?,
        activityClass: Class<*>?
    ): Notification {
        val intent: Intent
        if (activityClass != null) {
            debug<ForegroundService>("Activity class valid, opening app $activityClass")
            intent = Intent(this, activityClass)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        } else {
            debug<ForegroundService>("Activity class not valid")
            intent = Intent()
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
        builder
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//            .setLargeIcon(largeIconBitmap)
            .setContentIntent(pendingIntent)

        return builder.build()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected fun createNotificationChannel(
        channelId: String?,
        channelName: String?,
        headsUp: Boolean
    ) {
        val importance =
            if (headsUp) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
        val chan = NotificationChannel(
            channelId,
            channelName, importance
        )
        chan.lightColor = Color.BLUE
        val visibility: Int =
            if (headsUp) Notification.VISIBILITY_PUBLIC else Notification.VISIBILITY_PRIVATE
        chan.lockscreenVisibility = visibility
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        service?.createNotificationChannel(chan)
    }

    open fun stopForegroundService() {
        debug<ForegroundService>("Stop foreground service.")

        stopForeground(true)

        stopSelf()
        started = false
    }

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTIVITY_CLASSNAME = "ACTIVITY_CLASSNAME"
        const val NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE"

        const val NOTIFICATION_TITLE = "NOTIFICATION_TITLE"
        const val FOREGROUND_CHANNEL_ID = "Foreground_channel_id"
        const val FOREGROUND_SERVICE_CHANNEL = "Foreground service"

        fun start(title: String?, message: String?, context: Context) {
            val foregroundService = Intent(context, ForegroundService::class.java)
            foregroundService.action = ACTION_START_FOREGROUND_SERVICE

            foregroundService.putExtra(NOTIFICATION_TITLE, title)
            foregroundService.putExtra(NOTIFICATION_MESSAGE, message)
            foregroundService.putExtra(ACTIVITY_CLASSNAME, context::class.java.name)

            context.startService(foregroundService)
        }

        fun stop(context: Context) {
            val foregroundService = Intent(context, ForegroundService::class.java)
            foregroundService.action = ACTION_STOP_FOREGROUND_SERVICE
            context.startService(foregroundService)
        }
    }
}