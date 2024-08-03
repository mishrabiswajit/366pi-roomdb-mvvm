package com.example.a366pi

// Importing necessary packages
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

// Driver code
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userViewModel: UserViewModel = viewModel()
            MyApp(userViewModel)
        }
    }
}

@Composable
fun MyApp(userViewModel: UserViewModel) {

    // initializing list of users
    val users by userViewModel.users.observeAsState(emptyList())

    // UI components
    var showAddUserPage by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    val errorMessage by userViewModel.errorMessage.observeAsState("")
    val snackbarHostState = remember { SnackbarHostState() }

    // Page Changing Logic
    if (showAddUserPage) {

        // Add New User
        AddUserPage(
            userViewModel = userViewModel,
            onBack = { showAddUserPage = false }
        )
    } else if (selectedUser != null) {

        // Show User Details
        UserDetailPage(
            user = selectedUser!!,
            onBack = { selectedUser = null }
        )
    } else {

        // Show homepage
        HomePage(
            users = users,
            errorMessage = errorMessage,
            onAddUserClicked = { showAddUserPage = true },
            onUserClicked = { user -> selectedUser = user },
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    users: List<User>,
    errorMessage: String,
    onAddUserClicked: () -> Unit,
    onUserClicked: (User) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(

        // Top Bar Section
        topBar = {
            TopAppBar(

                // Top Bar - Title
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // Top Bar - Title - Logo
                        Icon(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        )

                        // Top Bar - Title - App Name
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("366pi", color = Color.White)
                    }
                },

                // Top Bar - Title - Styling
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },

        // Initializing SnackbarHost
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        // Adding Floating Add Button
        floatingActionButton = {
            FloatingActionButton(onClick = onAddUserClicked) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // If the error message is not empty
            if (errorMessage.isNotEmpty()) {

                // Show error message with a sad emoji
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sad_emoji),
                        contentDescription = "Sad Emoji",
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = Color.Red)
                }
            } else if (users.isEmpty()) { // If the list if users is empty

                // Show "No Users Available" message
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No Users Available",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            // If the error message is empty proceed with the working
            else {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Passing fetched users data for displaying
                    UserList(users, onUserClicked)
                }
            }
        }
    }
}

// Getting the list of users
@Composable
fun UserList(users: List<User>, onUserClicked: (User) -> Unit) {

    LazyColumn {

        // Iterating through each user
        items(users) { user ->

            // Passing each user data for displaying in HomePage
            UserItem(user, onClick = { onUserClicked(user) })
        }
    }
}

// Displaying the fetched users data
@Composable
fun UserItem(user: User, onClick: () -> Unit) {

    // UserItem - Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {

        // UserItem - Card - Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // UserItem - Card - Row - Column
            Column {

                // UserItem - Card - Row - Column - Text1
                Text(
                    text = "${user.first_name} ${user.last_name}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // UserItem - Card - Row - Column - Text2
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}


// Using experimental material3 api for TopAppBar as old is depreciated
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserPage(userViewModel: UserViewModel, onBack: () -> Unit) {

    // User Details
    var employeeFirstname by remember { mutableStateOf("") }
    var employeeLastname by remember { mutableStateOf("") }
    var employeeID by remember { mutableStateOf("") }
    var employeeEmail by remember { mutableStateOf("") }

    // Misc
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by userViewModel.errorMessage.observeAsState("")

    // Input Regex
    val namePattern = Regex("^[a-zA-Z]*$")
    val integerRegex = Regex("^[0-9]+\$")

    Scaffold(

        // topbar Section
        topBar = {
            TopAppBar(

                // topbar - title
                title = { Text("Add User") },

                // topbar - styling
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),

                // topbar - navigation [back button]
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Asking for first name
            OutlinedTextField(
                value = employeeFirstname,
                onValueChange = {

                    // Checks for invalid characters
                    scope.launch {
                        if (namePattern.matches(it)) {
                            employeeFirstname = it
                        } else {
                            snackbarHostState.showSnackbar("Name cannot contain special characters or numbers")
                        }
                    }
                },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Asking for last name
            OutlinedTextField(
                value = employeeLastname,
                onValueChange = {

                    // Checks for invalid characters
                    scope.launch {
                        if (namePattern.matches(it)) {
                            employeeLastname = it
                        } else {
                            snackbarHostState.showSnackbar("Name cannot contain special characters or numbers")
                        }
                    }
                },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Asking for Employee ID
            OutlinedTextField(
                value = employeeID,
                onValueChange = {
                    scope.launch {
                        if (integerRegex.matches(it)) {
                            employeeID = it
                        } else {
                            snackbarHostState.showSnackbar("Employee ID contains only numbers")
                        }
                    }
                },
                label = { Text("Employee ID") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Asking for Employee Email
            OutlinedTextField(
                value = employeeEmail,
                onValueChange = {
                    employeeEmail = it
                },
                label = { Text("Employee Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // AddUser Button
            Button(
                onClick = {
                    scope.launch {
                        if (employeeFirstname.isEmpty()) {
                            snackbarHostState.showSnackbar("Employee First Name cannot be empty")
                        } else if (employeeLastname.isEmpty()) {
                            snackbarHostState.showSnackbar("Employee Last Name cannot be empty")
                        } else if (employeeID.isEmpty()) {
                            snackbarHostState.showSnackbar("Employee ID cannot be empty")
                        } else if (employeeID.length != 6) {
                            snackbarHostState.showSnackbar("Employee ID must be of 6 digits")
                        } else if (employeeEmail.isEmpty()) {
                            snackbarHostState.showSnackbar("Employee Email ID cannot be empty")
                        } else {

                            // Creating a new user
                            val newUser = User(
                                id = employeeID.toInt(),
                                first_name = employeeFirstname,
                                last_name = employeeLastname,
                                email = employeeEmail
                            )

                            userViewModel.addUser(newUser)

                            if (errorMessage.isNotEmpty()) {
                                snackbarHostState.showSnackbar(errorMessage)
                            } else {
                                snackbarHostState.showSnackbar("User created: ${newUser.first_name} ${newUser.last_name}")
                                onBack() // Navigate back after successful addition
                            }
                        }
                    }
                },

                // AddUser Button - Styling
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text("Add User")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailPage(user: User, onBack: () -> Unit) {
    Scaffold(

        // topbar
        topBar = {
            TopAppBar(

                // topbar - title
                title = { Text("User Details") },

                // topbar - styling
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),

                // topbar - navigation [back button]
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // display employee name
            Text(
                text = "${user.first_name} ${user.last_name}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // display employee email
            Text(
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            // display employee ID
            Text(
                text = "Employee ID: ${user.id}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}