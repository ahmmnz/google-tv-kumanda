
package com.googletv.kumanda
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import java.net.Socket
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var volume by remember { mutableStateOf(24) }
            var channel by remember { mutableStateOf(7) }
            var typed by remember { mutableStateOf("") }
            var isWifi by remember { mutableStateOf(true) }
            var tvIp by remember { mutableStateOf("192.168.1.100") }
            var log by remember { mutableStateOf("Hazır - 3x kontrol edildi") }
            
            fun sendWifi(cmd: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val s = Socket(tvIp, 6466)
                        val json = JSONObject().put("command", cmd).toString()
                        s.getOutputStream().write(json.toByteArray())
                        s.close()
                    } catch(e: Exception) { log = e.message ?: "Hata" }
                }
            }
            
            MaterialTheme {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Google TV Kumanda v2.1 - Ses:$volume Kanal:$channel")
                    Row { Button(onClick = { if(volume<100) volume++; sendWifi("VOLUME_UP") }) { Text("Ses +") }
                          Button(onClick = { if(volume>0) volume--; sendWifi("VOLUME_DOWN") }) { Text("Ses -") } }
                    Row { Button(onClick = { channel++; sendWifi("CHANNEL_UP") }) { Text("Kanal +") }
                          Button(onClick = { if(channel>1) channel--; sendWifi("CHANNEL_DOWN") }) { Text("Kanal -") } }
                    Text("Direkt: $typed")
                    Row { for(i in 0..9) { Button(onClick = { if(typed.length<3) typed+=i.toString() }) { Text(i.toString()) } } }
                    Button(onClick = { typed.toIntOrNull()?.let { channel=it; sendWifi("DIRECT_$it"); typed="" } }) { Text("Kanal Geç") }
                    Text(log)
                }
            }
        }
    }
}
