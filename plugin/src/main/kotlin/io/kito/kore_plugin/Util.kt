package io.kito.kore_plugin

import java.net.HttpURLConnection
import java.net.URI
import java.io.BufferedReader
import java.io.InputStreamReader
import org.json.JSONObject


fun fetchJson(urlString: String) : JSONObject {
    val connection = URI(urlString).toURL().openConnection() as HttpURLConnection

    connection.requestMethod = "GET"
    connection.setRequestProperty("Accept", "application/json")

    return JSONObject(
        BufferedReader(InputStreamReader(connection.inputStream)).run {
            StringBuilder().also { b ->
                forEachLine { b.append(it) }
                close()
            }
        }
    )
}