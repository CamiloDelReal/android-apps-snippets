package package.core.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import core.utils.debug
import core.utils.error
import core.utils.info
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject


class ConnectivityTrackerService @Inject constructor(
    private val context: Context
) {

    private val _connectivityFlow = MutableSharedFlow<Boolean>(replay = 1)

    val connectivityFlow: SharedFlow<Boolean> = _connectivityFlow

    fun isConnectedToInternet(): Boolean {
        info<ConnectivityTrackerService>("Requesting connectivity status")
        val connectivityService =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectivity = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            debug<ConnectivityTrackerService>("Checking connectivity for Android version less than and equal to P (28) ")
            @Suppress("DEPRECATION")
            val activeNetwork = connectivityService.activeNetworkInfo
            @Suppress("DEPRECATION")
            activeNetwork?.isConnected ?: false
        } else {
            debug<ConnectivityTrackerService>("Checking connectivity for Android version great than to P (28) ")
            val activeNetwork = connectivityService.activeNetwork
            val capabilities = connectivityService.getNetworkCapabilities(activeNetwork)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        }
        info<ConnectivityTrackerService>("Connectivity available $connectivity")
        return connectivity
    }

    fun start() {
        info<ConnectivityTrackerService>("Network tracker is starting")
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    info<ConnectivityTrackerService>("Possible gateway to internet detected")
                    _connectivityFlow.tryEmit(true)
                }

                override fun onLost(network: Network) {
                    info<ConnectivityTrackerService>("Possible gateway to internet has lost")
                    _connectivityFlow.tryEmit(false)
                }
            })
            info<ConnectivityTrackerService>("Network tracker started")
        } catch (ex: Exception) {
            error<ConnectivityTrackerService>(ex)
        }
    }

}