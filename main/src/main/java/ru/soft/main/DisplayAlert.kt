package ru.soft.main

import android.content.*
import android.content.DialogInterface.*
import android.os.*
import androidx.appcompat.app.*
import androidx.core.content.*


class DisplayAlert : AppCompatActivity() {
    private var title: String? = null
    private var body: String? = null
    private var guid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = intent.extras
        if (b != null) {
            title = b!!.getString("title")
            body = b!!.getString("body")
            guid = b!!.getString("guid")
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setTitle(title).setMessage(body)
                .setCancelable(false)
                .setNegativeButton("Закрыть") { _, _ ->
                    finish()
                }
                .setPositiveButton("Открыть") { _, _ ->
                    val addIntent = Intent(this, MainActivity::class.java)
                    addIntent.putExtra("guid", guid)
                    addIntent.putExtra("cord", false)
                    ContextCompat.startActivity(this, addIntent, null)
                    finish()
                }

            val alertDialog = builder.create()
            alertDialog.setOnDismissListener {
                finish()
            }
            alertDialog.setOnShowListener(OnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
            })
            alertDialog.show()
        }
    }

    companion object {
        var b: Bundle? = null
    }
}