package com.d3if3082.ricocell.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.d3if3082.ricocell.BuildConfig
import com.d3if3082.ricocell.R
import com.d3if3082.ricocell.model.HandPhone
import com.d3if3082.ricocell.model.User
import com.d3if3082.ricocell.network.ApiStatus
import com.d3if3082.ricocell.network.HandphoneApi
import com.d3if3082.ricocell.network.UserDataStore
import com.d3if3082.ricocell.ui.theme.RIcoCellTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ScreenContent(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    when (status) {
        ApiStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
}
        }

        ApiStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier
                    .fillMaxSize()
                    .padding(4.dp),
                columns = GridCells.Fixed(2),
            ) {
                items(data) { handphone ->
                    ListItem(handPhone = handphone, onDelete = { handphone ->
                        viewModel.deleteData(handphone.id)
                    })
                }
            }
        }

        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.error),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.retrieveData() },
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
        }
    }
}

@Composable
fun ListItem(handPhone: HandPhone, onDelete: (HandPhone) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(HandphoneApi.getHandphoneUrl(handPhone.gambar))
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.gambar, handPhone.nama),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.loading_img),
                error = painterResource(id = R.drawable.baseline_broken_image_24),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f) // Take up remaining space
                ) {
                    Text(
                        text = handPhone.nama,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.harga, handPhone.harga),
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                IconButton(onClick = { onDelete(handPhone) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = stringResource(id = R.string.delete),
                        tint = Color.White
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    RIcoCellTheme {
        MainScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()
    val openDialog = remember { mutableStateOf(false) }
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        }
                        else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { openDialog.value = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        ScreenContent(Modifier.padding(padding), viewModel)
        if (showDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }

        if (openDialog.value) {
            AddDataDialog(onDismiss = { openDialog.value = false }, onConfirm = { handPhone ->
                viewModel.addHanphone(handPhone)
                openDialog.value = false
            })
        }
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

@Composable
fun AddDataDialog(onDismiss: () -> Unit, onConfirm: (HandPhone) -> Unit) {
    var nama by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var gambar by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.add_hanphone))
        },
        text = {
            Column {
                TextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text(text = stringResource(id = R.string.nama_hp)) }
                )
                TextField(
                    value = harga,
                    onValueChange = { harga = it },
                    label = { Text(text = stringResource(id = R.string.harga_hp)) }
                )
                TextField(
                    value = gambar,
                    onValueChange = { gambar = it },
                    label = { Text(text = stringResource(id = R.string.tambah_gambar)) }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(HandPhone(id = "", nama = nama, harga = harga, gambar = gambar))
            }) {
                Text(text = stringResource(id = R.string.add))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleIdToken.displayName ?: ""
            val email = googleIdToken.id
            val photoUrl = googleIdToken.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    }
    else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}
