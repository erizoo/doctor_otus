package ru.soft.med.service

import android.annotation.*
import android.app.*
import android.content.*
import android.media.*
import android.net.*
import android.os.*
import android.preference.*
import android.util.*
import androidx.core.app.*
import androidx.localbroadcastmanager.content.*
import com.google.firebase.messaging.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_auth.di.*
import ru.soft.main.*
import ru.soft.main.R
import javax.inject.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val TAG = "MyFirebaseMessagingService"
        const val FIREBASE_TOKEN = "FIREBASE_TOKEN"
    }

    private var guidM = ""
    private var id = System.currentTimeMillis().toInt()

    override fun onNewToken(token: String) {
        val pref = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit()
        pref.putString(FIREBASE_TOKEN, token)
        pref.apply()
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onCreate() {
        super.onCreate()
        id = System.currentTimeMillis().toInt()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(ru.soft.med.R.string.app_name) // Замените на ID вашего канала
            val channelName =
                getString(ru.soft.med.R.string.app_name) // Замените на имя вашего канала
            val channelDescription =
                getString(ru.soft.med.R.string.app_name) // Замените на описание вашего канала
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("LogNotTimber")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            if (remoteMessage.data["body"] != null && remoteMessage.data["title"] != null && remoteMessage.data["guid"] != null) {
                val mDisplayAlert = Intent(this, DisplayAlert::class.java)
                mDisplayAlert.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mDisplayAlert.putExtra("title", remoteMessage.data["title"])
                mDisplayAlert.putExtra("body", remoteMessage.data["body"])
                mDisplayAlert.putExtra("guid", remoteMessage.data["guid"])
                startActivity(mDisplayAlert)
            }

            if (remoteMessage.data["guid"] != null) {
                guidM = remoteMessage.data["guid"].toString()
            }
            sendNotification(
                messageBody = remoteMessage.data["body"].toString(),
                title = remoteMessage.data["title"].toString()
            )
        }
    }

    private fun sendNotification(messageBody: String, title: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.sendNotification(messageBody, title, applicationContext)
    }

    @SuppressLint("UnspecifiedImmutableFlag", "WrongConstant")
    fun NotificationManager.sendNotification(
        messageBody: String,
        title: String,
        applicationContext: Context,

        ) {
        val notifyIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("guid", guidM)
            putExtra("cord", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext, id, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(
            applicationContext,
            getString(ru.soft.med.R.string.default_notification_channel_id)
        )
            .setSmallIcon(ru.soft.ui_atoms.R.drawable.ic_logo)
            .setContentTitle(title)
            .setChannelId(getString(ru.soft.med.R.string.app_name))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notify(id, builder.build())
    }

}