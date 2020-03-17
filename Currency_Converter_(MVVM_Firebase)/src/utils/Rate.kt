@file:JvmName("Rate")

import android.content.Context
import android.widget.Toast

fun Context.rateNow(){
    when {
        isConnectedToNetwork() ->
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("www.google.com")))
            Toast.makeText(this, "Rating", Toast.LENGTH_SHORT).show()
    }
}


