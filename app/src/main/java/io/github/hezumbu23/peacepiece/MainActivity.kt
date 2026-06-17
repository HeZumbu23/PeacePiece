package io.github.hezumbu23.peacepiece

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var openButton: Button
    private lateinit var closeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        statusText  = findViewById(R.id.statusText)
        openButton  = findViewById(R.id.openButton)
        closeButton = findViewById(R.id.closeButton)

        openButton.setOnClickListener {
            val url = prefs().getString("open_url", "").orEmpty()
            if (url.isBlank()) showSetupHint() else sendRequest(url, getString(R.string.action_opening))
        }

        closeButton.setOnClickListener {
            val url = prefs().getString("close_url", "").orEmpty()
            if (url.isBlank()) showSetupHint() else sendRequest(url, getString(R.string.action_closing))
        }
    }

    private fun sendRequest(urlStr: String, actionLabel: String) {
        setButtonsEnabled(false)
        statusText.text = getString(R.string.status_sending)

        val username = prefs().getString("username", "").orEmpty()
        val password = prefs().getString("password", "").orEmpty()

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val conn = URL(urlStr).openConnection() as HttpURLConnection
                    conn.connectTimeout = 5_000
                    conn.readTimeout    = 5_000
                    conn.requestMethod  = "GET"

                    if (username.isNotBlank()) {
                        val credentials = Base64.encodeToString(
                            "$username:$password".toByteArray(Charsets.UTF_8),
                            Base64.NO_WRAP
                        )
                        conn.setRequestProperty("Authorization", "Basic $credentials")
                    }

                    val code = conn.responseCode
                    conn.disconnect()
                    if (code in 200..299) null else "HTTP $code"
                } catch (e: Exception) {
                    e.localizedMessage ?: e.javaClass.simpleName
                }
            }
            setButtonsEnabled(true)
            statusText.text = if (result == null) {
                getString(R.string.status_success, actionLabel)
            } else {
                getString(R.string.status_error, result)
            }
        }
    }

    private fun showSetupHint() {
        statusText.text = getString(R.string.status_not_configured)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        openButton.isEnabled  = enabled
        closeButton.isEnabled = enabled
    }

    private fun prefs() = getSharedPreferences("peacepiece_prefs", MODE_PRIVATE)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
