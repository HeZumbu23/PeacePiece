package io.github.hezumbu23.peacepiece

import android.content.Intent
import android.os.Bundle
import android.util.Base64
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

    companion object { private const val TAG = "PeacePiece" }

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

        @Suppress("DEPRECATION")
        val versionName = try { packageManager.getPackageInfo(packageName, 0).versionName } catch (e: Exception) { "?" }
        findViewById<TextView>(R.id.versionText).text = "v$versionName"

        openButton.setOnClickListener {
            sendCoverCommand("open_cover", getString(R.string.action_opening))
        }
        closeButton.setOnClickListener {
            sendCoverCommand("close_cover", getString(R.string.action_closing))
        }
    }

    private fun sendCoverCommand(action: String, actionLabel: String) {
        val p        = prefs()
        val baseUrl  = p.getString("base_url", "").orEmpty().trimEnd('/')
        val entityId = p.getString("entity_id", "").orEmpty()
        val username = p.getString("username", "").orEmpty()
        val password = p.getString("password", "").orEmpty()

        if (baseUrl.isBlank() || entityId.isBlank()) {
            statusText.text = getString(R.string.status_not_configured)
            return
        }

        setButtonsEnabled(false)
        statusText.text = getString(R.string.status_sending)

        val url  = "$baseUrl/api/services/cover/$action"
        val body = """{"entity_id":"$entityId"}"""

        AppLog.append("→ POST $url")
        Log.d(TAG, "→ POST $url  body=$body  auth=${username.isNotBlank()}")

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.connectTimeout = 5_000
                    conn.readTimeout    = 5_000
                    conn.requestMethod  = "POST"
                    conn.doOutput       = true
                    conn.setRequestProperty("Content-Type", "application/json")

                    if (username.isNotBlank()) {
                        val credentials = Base64.encodeToString(
                            "$username:$password".toByteArray(Charsets.UTF_8),
                            Base64.NO_WRAP
                        )
                        conn.setRequestProperty("Authorization", "Basic $credentials")
                    }

                    conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

                    val code = conn.responseCode
                    conn.disconnect()
                    Log.d(TAG, "← HTTP $code")
                    if (code in 200..299) null else "HTTP $code"
                } catch (e: Exception) {
                    Log.e(TAG, "Request failed", e)
                    e.localizedMessage ?: e.javaClass.simpleName
                }
            }
            setButtonsEnabled(true)
            if (result == null) {
                AppLog.append("← HTTP 200 OK")
                statusText.text = getString(R.string.status_success, actionLabel)
            } else {
                AppLog.append("✗ $result")
                statusText.text = getString(R.string.status_error, result)
            }
        }
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
