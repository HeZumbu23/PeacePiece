package io.github.hezumbu23.peacepiece

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    companion object {
        private const val TAG = "PeacePiece"
        private const val ENTITY_GARAGE  = "cover.00241a49a7690b"
        private const val ENTITY_ESPRESSO = "switch.nous_steckdosenschalter_4"
    }

    private lateinit var statusText: TextView
    private lateinit var openButton: Button
    private lateinit var closeButton: Button
    private lateinit var espressoOnButton: Button
    private lateinit var espressoOffButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        statusText       = findViewById(R.id.statusText)
        openButton       = findViewById(R.id.openButton)
        closeButton      = findViewById(R.id.closeButton)
        espressoOnButton  = findViewById(R.id.espressoOnButton)
        espressoOffButton = findViewById(R.id.espressoOffButton)

        @Suppress("DEPRECATION")
        val versionName = try { packageManager.getPackageInfo(packageName, 0).versionName } catch (e: Exception) { "?" }
        findViewById<TextView>(R.id.versionText).text = "v$versionName"

        openButton.setOnClickListener {
            sendCommand("cover", "open_cover", ENTITY_GARAGE, getString(R.string.action_opening))
        }
        closeButton.setOnClickListener {
            sendCommand("cover", "close_cover", ENTITY_GARAGE, getString(R.string.action_closing))
        }
        espressoOnButton.setOnClickListener {
            sendCommand("switch", "turn_on", ENTITY_ESPRESSO, getString(R.string.action_on))
        }
        espressoOffButton.setOnClickListener {
            sendCommand("switch", "turn_off", ENTITY_ESPRESSO, getString(R.string.action_off))
        }
    }

    private fun sendCommand(domain: String, service: String, entityId: String, actionLabel: String) {
        val p              = prefs()
        val baseUrl        = p.getString("base_url",          "").orEmpty().trimEnd('/')
        val cfClientId     = p.getString("cf_client_id",      "").orEmpty().trim()
        val cfClientSecret = p.getString("cf_client_secret",  "").orEmpty().trim()
        val haToken        = p.getString("ha_token",           "").orEmpty().trim()

        if (baseUrl.isBlank() || haToken.isBlank()) {
            statusText.text = getString(R.string.status_not_configured)
            return
        }

        setAllButtonsEnabled(false)
        statusText.text = getString(R.string.status_sending)

        val url  = "$baseUrl/api/services/$domain/$service"
        val body = """{"entity_id":"$entityId"}"""

        AppLog.append("→ POST $url")
        Log.d(TAG, "→ POST $url  body=$body")

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.connectTimeout = 5_000
                    conn.readTimeout    = 5_000
                    conn.requestMethod  = "POST"
                    conn.doOutput       = true
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.setRequestProperty("Authorization", "Bearer $haToken")

                    if (cfClientId.isNotBlank()) {
                        conn.setRequestProperty("CF-Access-Client-Id",     cfClientId)
                        conn.setRequestProperty("CF-Access-Client-Secret", cfClientSecret)
                    }

                    conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

                    val code = conn.responseCode
                    Log.d(TAG, "← HTTP $code")
                    if (code in 200..299) {
                        conn.disconnect()
                        null
                    } else {
                        val errorBody = conn.errorStream
                            ?.bufferedReader()?.readText()?.take(300) ?: ""
                        conn.disconnect()
                        "HTTP $code | $errorBody"
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Request failed", e)
                    e.localizedMessage ?: e.javaClass.simpleName
                }
            }
            setAllButtonsEnabled(true)
            if (result == null) {
                AppLog.append("← HTTP 200 OK")
                statusText.text = getString(R.string.status_success, actionLabel)
            } else {
                AppLog.append("✗ $result")
                statusText.text = getString(R.string.status_error, result)
            }
        }
    }

    private fun setAllButtonsEnabled(enabled: Boolean) {
        openButton.isEnabled        = enabled
        closeButton.isEnabled       = enabled
        espressoOnButton.isEnabled  = enabled
        espressoOffButton.isEnabled = enabled
    }

    private fun prefs() = getSharedPreferences("peacepiece_prefs", MODE_PRIVATE)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_log -> {
                startActivity(Intent(this, LogActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
