package com.example.scogo.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scogo.R
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "Extension"

fun Activity.startNewActivity(context: Context, java: Class<*>) {
    startActivity(Intent(context, java))
}

fun Activity.startNewActivityWithFinish(context: Context, java: Class<*>) {
    startActivity(Intent(context, java))
    finish()
}

fun Context.showToastShort(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showToastLong(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.showDialogWithMessage(msg: String) {
    val alertDialog = AlertDialog.Builder(this)
    alertDialog.setMessage(msg)
    alertDialog.setCancelable(false)
    alertDialog.setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
    }
    alertDialog.show()
}

fun Context.isOnline(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } else {
            null
        }
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
    }
    showDialogWithMessage(getString(R.string.no_connection))
    return false
}

fun Context.showErrorBuilder(jsonObj: JSONObject?) {
    val message: StringBuilder = StringBuilder()
    if (jsonObj!!.has("errors")) {
        val jsonObject: JSONObject = jsonObj.optJSONObject("errors")
        val it = jsonObject.keys()
        while (it.hasNext()) {
            val key = it.next()
            Log.d(TAG, "doInBackground: $key")
            message.append("${jsonObject.getJSONArray(key)[0]}\n")
        }
    } else {
        message.append(jsonObj.optString("message"))
    }
    showDialogWithMessage(message.toString())
}

fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

fun Context.showDialogNotCancelable(
    title: String,
    message: String,
    confirmTitle: String,
    dismissTitle: String,
    okListener: DialogInterface.OnClickListener
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(confirmTitle, okListener)
        .setNegativeButton(dismissTitle) { dialog, which -> dialog.dismiss() }
        .setCancelable(false)
        .create()
        .show()
}

fun getRealDateFromServerDate(date: String): String {
    val dateOnly = date.split("T")
    val dateFormatted = dateOnly[0].split("-")
    val timeFormatted = dateOnly[1].split(":")
    val amPm = if (timeFormatted[0].toInt() > 11) "PM" else "AM"
    val finalDate = "${dateFormatted[2]}/${dateFormatted[1]}/${dateFormatted[0]}  " +
            "${timeFormatted[0]}:${timeFormatted[1]}:${timeFormatted[2].split(".")[0]}  $amPm"
    println(finalDate)
    return finalDate
}

fun getUTCToIST(stringDate: String): String {
    val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val date = dateFromUTC(df.parse(stringDate))
    return formatter.format(date)
}

private fun dateFromUTC(date: Date): Date? {
    return Date(date.time + Calendar.getInstance().timeZone.getOffset(Date().time))
}

fun Activity.sendEmail(email: String, subject: String, body: String, shareMessage: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, body)
    startActivity(Intent.createChooser(intent, shareMessage))
}

fun Context.openInstagram(userId: String) {
    val uri = Uri.parse("http://instagram.com/_u/$userId")
    val likeIng = Intent(Intent.ACTION_VIEW, uri)
    likeIng.setPackage("com.instagram.android")
    try {
        this.startActivity(likeIng)
    } catch (e: ActivityNotFoundException) {
        this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/$userId")))
    }
}

fun Context.openMap(latlng: String) {
    if (latlng.contains(",")) {
        val splitter = latlng.split(",")

        val intentUri = Uri.Builder().apply {
            scheme("https")
            authority("www.google.com")
            appendPath("maps")
            appendPath("dir")
            appendPath("")
            appendQueryParameter("api", "1")
            appendQueryParameter("destination", "${splitter[0]},${splitter[1]}")
        }.build()
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = intentUri
        })
    } else showToastShort("Location not available")

}


/*

fun Activity.openWhatsApp(mobileNumber: String) {
    try {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(this.resources.getString(R.string.whatsapp_link, mobileNumber))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            i.setPackage("com.whatsapp")
        }
        this.startActivity(i)
    } catch (activityNotFoundException: ActivityNotFoundException) {
        this.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(this.resources.getString(R.string.whatsapp_link, mobileNumber))))
    }
}

fun Activity.openDialer(mobileNo: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$mobileNo")
    startActivity(intent)
}

fun Activity.checkAndOpen(position: Int, social: String) {
    when {
        social.isEmpty() -> {
            showToastShort("Contact information not available!")
        }

        else -> {
            when (position) {
                AppConstants.CALL -> {
                    openDialer(social)
                }
                AppConstants.WHATSAPP -> {
                    openWhatsApp(social)
                }
                AppConstants.EMAIL -> {
                    sendEmail(social, "", "", "")
                }
                AppConstants.INSTAGRAM -> {
                    openInstagram(social)
                }
                AppConstants.LOCATION -> {
                    openMap(social)
                }
            }
        }
    }

}


fun List<String>.getWorkingDay(): String {
    val stringBuilder = StringBuilder()
    for (day in this) {
        stringBuilder.append("${AppConstants.daysArray[day.toInt()]} , ")
    }
//    val outputString = stringBuilder.toString().trim()
//    return outputString.substring(0, outputString.length - 1)
    return stringBuilder.toString().trim().dropLast(1)
}

*/


fun Context.setClipboard(text: String) {
    val clipboard =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        } else {
            TODO("VERSION.SDK_INT < HONEYCOMB")
        }
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
    showToastShort("Copied")
}

fun String.isDigitOnly() = this.all { it.isDigit() }

fun TextInputEditText.setEditableText(string: String) {
    this.text = Editable.Factory.getInstance().newEditable(string)
}


fun getTodayCalendar(): Array<Int> {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    return arrayOf(year, month, day)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"


fun String.getDifference(): Long {
//    2022-02-28 11:07 PM
    val date = SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(this)
    val today = Date().time
    return date.time - today
}

fun Long.getActualTime(): String {
    val diff = this / 1000
    val hours = diff / 3600
    val minutes = (diff % 3600) / 60
    val rSeconds = diff % 60
    return String.format("%02d:%02d:%02d", hours, minutes, rSeconds)
}

fun TextInputEditText.leftDrawable(@DrawableRes id: Int = 0, @DimenRes sizeRes: Int) {
    val drawable = ContextCompat.getDrawable(context, id)
    val size = resources.getDimensionPixelSize(sizeRes)
    drawable?.setBounds(0, 0, size, size)
    this.setCompoundDrawables(drawable, null, null, null)
}
