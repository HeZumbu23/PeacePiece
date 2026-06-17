package io.github.hezumbu23.peacepiece

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLog {
    private val entries = ArrayDeque<String>()
    private val fmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun append(msg: String) {
        if (entries.size >= 200) entries.removeFirst()
        entries.addLast("${fmt.format(Date())} $msg")
    }

    fun getText(): String = entries.joinToString("\n")

    fun clear() = entries.clear()
}
