package com.example.canasta_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.canasta_frontend.ui.navigation.CanastaNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // CanastaNavGraph owns CanastaTheme (single source, respeta SYSTEM)
            CanastaNavGraph()
        }
    }
}
