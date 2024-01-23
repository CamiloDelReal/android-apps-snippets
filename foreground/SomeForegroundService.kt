package package.core.services.foreground

import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.RingtoneManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import core.valerianademo.R
import core.services.SomeService
import core.services.SettingsService
import core.utils.debug
import utils.warning
import ui.home.HomeActivity
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SomeForegroundService : ForegroundService() {

    @Inject
    lateinit var SomeService: SomeService

    @Inject
    lateinit var settingsService: SettingsService

    var isRunning: Boolean = false
        private set

    var isWorking: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()
        debug<SomeForegroundService>("Service created")

        createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, true)
        debug<SomeForegroundService>("Native service notification channel created")

        isRunning = true
    }

    override fun onDestroy() {
        super.onDestroy()
        debug<SomeForegroundService>("Service destroyed")
        isRunning = false
    }

    @Synchronized
    override fun startForegroundService(extras: Bundle?) {
        super.startForegroundService(extras)
        debug<SomeForegroundService>("startForegroundService")
        isWorking = true
        with(NotificationManagerCompat.from(this)) {
            cancel(NOTIF_ID)
        }
        SomeService.start()
    }

    override fun stopForegroundService() {
        super.stopForegroundService()
        debug<SomeForegroundService>("stopForegroundService")
        SomeService.stop()
        isWorking = false
    }

    private fun notify(title: String, msg: String) {
        val bundle = Bundle()
        bundle.putString("content", msg)
        if (!isAnyActivityReceivingResults) {
            debug<SomeForegroundService>("notify through system notification with value $msg")
            createNotification(bundle, title, msg)
        }
    }

    private val isAnyActivityReceivingResults: Boolean
        get() = activityListening

    private fun createNotification(bundle: Bundle, title: String, info: String) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent: PendingIntent = getActivity(this, 100, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        val notification = builder.build()

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIF_ID, notification)
        }
    }

    var activityListening: Boolean = false
    var binder: IBinder = ServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        debug<SomeForegroundService>("Service onBind request")
        return binder
    }

    inner class ServiceBinder : Binder() {
        val serviceInstance: SomeService
            get() = this@SomeService
    }

    companion object {
        const val CHANNEL_NAME = "service"
        const val CHANNEL_ID = "CHANNEL_ID"
        const val NOTIF_ID = 2255

        fun start(context: Context, connection: ServiceConnection?) {
            debug<SomeForegroundService>("Service create request")
            val service = Intent(context, SomeService::class.java)
            context.bindService(service, connection!!, BIND_AUTO_CREATE)

            context.startService(service)
            debug<SomeForegroundService>("Service creation request sended")
        }

        fun start(title: String?, message: String?, context: Context) {
            debug<SomeForegroundService>("Service start request")
            val service = Intent(context, SomeService::class.java)
            service.action = ACTION_START_FOREGROUND_SERVICE
            service.putExtra(NOTIFICATION_TITLE, title)
            service.putExtra(NOTIFICATION_MESSAGE, message)
            service.putExtra(ACTIVITY_CLASSNAME, HomeActivity::class.java.name)
            debug<SomeForegroundService>("Starting Service")
            context.startService(service)
            debug<SomeForegroundService>("Service starter request sended")
        }

        fun stop(context: Context, connection: ServiceConnection?) {
            debug<SomeForegroundService>("Service stop request")
            connection?.let {
                try {
                    context.unbindService(connection)
                } catch (ex: Exception) {
                    warning<SomeForegroundService>("Service already unbinded")
                }
            }
            val service = Intent(context, SomeService::class.java)
            service.action = ACTION_STOP_FOREGROUND_SERVICE
            context.startService(service)
            debug<SomeForegroundService>("Service stop request sended")
        }

        fun bindService(context: Context, connection: ServiceConnection?) {
            debug<SomeForegroundService>("Service bind request")
            val service = Intent(context, SomeService::class.java)
            context.bindService(service, connection!!, BIND_AUTO_CREATE)
            debug<SomeForegroundService>("Service binded")
        }

    }
}