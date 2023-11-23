package com.luka.sdk.bluesnap.bluesnapsdksample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.luka.sdk.bluesnap.bluesnapsdksample.ui.theme.BluesnapSdkAppTheme
import com.luka.sdk.bluesnap.lukasdk.Config
import com.luka.sdk.bluesnap.lukasdk.Credentials
import com.luka.sdk.bluesnap.lukasdk.Environment
import com.luka.sdk.bluesnap.lukasdk.LukaBluesnap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel()

        LukaBluesnap.setUpConfig(
            config = Config(env = Environment.Sandbox, credentials = Credentials(username = "payco", password = "12345678")),
                callbacks = object : Config.Callbacks {
                    override fun onSuccess() {
                        viewModel.getCards()
                    }

                    override fun onError() {

                    }
                })


        setContent {
            BluesnapSdkAppTheme {

                val cards by viewModel.cards.collectAsState()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column {
                        LazyColumn {
                            items(cards) {
                                Text(text = it.cardLast4,
                                    modifier = Modifier.clickable {
                                        LukaBluesnap.processPayment(
                                            "e1555a98-881a-48a5-b958-fc1c6f37f258",
                                            card = it,
                                            amount = 10.0,
                                            email = "jmoran@lukapay.io"
                                        )
                                            .onSuccess {
                                                println(it)
                                            }.onError {
                                                println(it.localizedMessage)
                                            }
                                            .onLoading {
                                                println("loading")
                                            }
                                            .start()
                                    }
                                )
                            }
                        }

                        Button(onClick = {
                            LukaBluesnap.addNewCard(context = this@MainActivity, email = "jmoran@lukapay.io")
                            .onSuccess {
                                println(it)
                            }.onError {
                               println(it.localizedMessage)
                            }
                            .onLoading {
                                println("loading")
                            }
                            .start()
                        }) {
                         Text(text = "Press me")
                        }
                    }

                }
            }
        }
    }
}
