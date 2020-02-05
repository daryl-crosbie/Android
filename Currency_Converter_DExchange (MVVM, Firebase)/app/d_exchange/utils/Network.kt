@file:JvmName("Local")

import android.content.Context
import android.net.ConnectivityManager

@Suppress("DEPRECATION")
fun Context.isConnectedToNetwork(): Boolean{
    val connectivity = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivity.activeNetworkInfo?.isConnectedOrConnecting == true
}