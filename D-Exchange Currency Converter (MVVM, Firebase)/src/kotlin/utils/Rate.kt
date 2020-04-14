@file:JvmName("Rate")

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun Context.rateNow(){
    when {
        isConnectedToNetwork() ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(
                "market://details?id=dev.daryl.d_exchange")))
        else -> Toast.makeText(applicationContext, "No network connection", Toast.LENGTH_SHORT).show()
    }
}


