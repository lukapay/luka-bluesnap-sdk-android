package com.luka.sdk.bluesnap.lukasdk.presentation.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bluesnap.androidapi.services.CardinalManager
import com.luka.sdk.bluesnap.lukasdk.R
import com.luka.sdk.bluesnap.lukasdk.presentation.view.ui.theme.BluesnapSdkAppTheme
import com.luka.sdk.bluesnap.lukasdk.presentation.view.ui.theme.LukaBlue
import com.luka.sdk.bluesnap.lukasdk.presentation.view.ui.theme.LukaDisabled
import com.luka.sdk.bluesnap.lukasdk.presentation.view.ui.theme.openSansFamily
import com.luka.sdk.bluesnap.lukasdk.presentation.viewmodel.AddCardDetailsViewModel
import com.luka.sdk.bluesnap.lukasdk.presentation.viewmodel.AddCardsUiEvent

class AddCardDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.extras?.getString("email") ?: return finish()
        val lukaCustomerId = intent.extras?.getString("customerId")
        setContent {
            BluesnapSdkAppTheme {
                val viewModel = remember {
                    AddCardDetailsViewModel()
                }

                val card by viewModel.card.collectAsState()
                val cardNumberValue by viewModel.cardNumberValue.collectAsState()
                val cardExpiryDateValue by viewModel.cardExpiryDateValue.collectAsState()
                val cardCvv by viewModel.cardCvvValue.collectAsState()
                val endActivity by viewModel.endActivity.collectAsState()

                val context = LocalContext.current

                val showErrorDialog by viewModel.showErrorDialog.collectAsState()
                val errorMsg by viewModel.errorMsg.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()


                val textFieldColors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    cursorColor = Color.Gray,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                )

                LaunchedEffect(key1 = true, block = {
                    viewModel.setUpBluesnap(this@AddCardDetailsActivity)
                })

                LaunchedEffect(key1 = endActivity, block = {
                    if (endActivity) {
                        finish()
                    }
                })

                val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                    // we will receive data updates in onReceive method.
                    override fun onReceive(context: Context?, intent: Intent) {
                        // handle event
                        when(CardinalManager.getInstance().threeDSAuthResult) {
                            "AUTHENTICATION_SUCCEEDED", "AUTHENTICATION_BYPASSED" -> {
                                viewModel.cont(email, lukaCustomerId)
                            }

                            "AUTHENTICATION_UNAVAILABLE" -> {
                                viewModel.showError(
                                    getString(R.string.auth_unavailable_3ds)
                                )
                            }

                            "AUTHENTICATION_FAILED" -> {
                                viewModel.showError(
                                    getString(R.string.auth_failed_3ds)
                                )
                            }

                            "THREE_DS_ERROR" -> {
                                viewModel.showError(
                                    getString(R.string.auth_error_3ds)
                                )
                            }

                            else -> {
                                // show error msg
                                viewModel.showError(
                                    getString(R.string.auth_error_3ds)
                                )
                            }
                        }


                    }
                }

                LocalBroadcastManager.getInstance(context).registerReceiver(
                    broadcastReceiver, IntentFilter(CardinalManager.THREE_DS_AUTH_DONE_EVENT)
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {


                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 24.dp)) {

                            Spacer(modifier = Modifier.weight(1.0f))
                            
                            Text(text = stringResource(id = R.string.card_details), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)

                            Spacer(modifier = Modifier.weight(1.0f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))


                        Text(text = stringResource(id = R.string.enter_card_details), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.height(24.dp))

                        Column {
                            Text(text = stringResource(id = R.string.card_number), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = cardNumberValue,
                                onValueChange = {
                                    viewModel.updateCard(AddCardsUiEvent.UpdateCardNumber(it))
                                },
                                placeholder = {
                                    Text(text = "**** **** **** ****", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                ,
                                colors = textFieldColors,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        Column {
                            Text(text = stringResource(id = R.string.name),style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = card.cardHolderName,
                                onValueChange = {
                                    viewModel.updateCard(AddCardsUiEvent.UpdateCardHolderName(it))
                                },
                                placeholder = {
                                    Text(
                                        text = stringResource(id = R.string.card_holder_name),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                colors = textFieldColors,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Next
                                ),
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Row {
                                Column(modifier = Modifier.padding(end = 12.dp)) {
                                    Text(text = stringResource(id = R.string.expiry_date),
                                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    TextField(
                                        value = cardExpiryDateValue,
                                        onValueChange = {
                                            viewModel.updateCard(AddCardsUiEvent.UpdateExpiryDate(it))
                                        },
                                        placeholder = {
                                            Text(
                                                text = stringResource(id = R.string.expiry_date_hint),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .height(60.dp)
                                            .width(this@BoxWithConstraints.maxWidth / 2)
                                            .clip(RoundedCornerShape(8.dp)),
                                        colors = textFieldColors,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Next
                                        ),
                                    )
                                }

                                Column(modifier = Modifier.padding(start = 12.dp)){
                                    Text(text = stringResource(id = R.string.cvv), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextField(
                                        value = cardCvv,
                                        onValueChange = {
                                            viewModel.updateCard(AddCardsUiEvent.UpdateCvvNumber(it))
                                        },
                                        placeholder = {
                                            Text(
                                                text = stringResource(id = R.string.cvv),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .height(60.dp)
                                            .width(this@BoxWithConstraints.maxWidth / 2)
                                            .clip(RoundedCornerShape(8.dp)),
                                        colors = textFieldColors,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        ),
                                    )
                                }
                            }
                        }


                        Spacer(modifier = Modifier.weight(1f))

                        val isValidCard by viewModel.isValidCard.collectAsState()

                        Button(
                            onClick = {
                                viewModel.updateCard(AddCardsUiEvent.Submit(this@AddCardDetailsActivity,email, customerId = lukaCustomerId))
                            },
                            modifier = Modifier
                                .padding(bottom = 24.dp)
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LukaBlue,
                                contentColor = Color.White,
                                disabledContainerColor = LukaDisabled,
                                disabledContentColor = Color.White
                            ),
                            enabled = isValidCard
                        ) {
                            if (isLoading)  {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(8.dp))
                                return@Button
                            }
                            Text(
                                text = stringResource(id = R.string.add),
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, fontFamily = openSansFamily)
                            )
                        }
                    }

                    if (showErrorDialog) {

                        ErrorDialog(
                            onDismissRequest = {
                                viewModel.dismissError()
                            },
                            dialogText = errorMsg,
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun ErrorDialog(
    onDismissRequest: () -> Unit,
    dialogText: String
) {

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround

            ) {

                Text(text = dialogText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    )


                Button(onClick = {
                    onDismissRequest()
                },
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LukaBlue,
                        contentColor = Color.White,
                        disabledContainerColor = LukaDisabled,
                        disabledContentColor = Color.White
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.accept),
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, fontFamily = openSansFamily)
                    )
                }
            }
        }
    }
}