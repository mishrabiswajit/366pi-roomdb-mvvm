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
import androidx.compose.material.icons.filled.CalendarToday
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
                    text = "${user.firstName} ${user.lastName}",
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
    var employeeAddress by remember { mutableStateOf("") }
    var employeePhoneNumber by remember { mutableStateOf("") }
    var employeeCity by remember { mutableStateOf("") }
    var employeeState by remember { mutableStateOf("") }
    var employeePincode by remember { mutableStateOf("") }
    var employeeCountry by remember { mutableStateOf("") }
    val employeeDOB = remember { mutableStateOf("") }

    // Initial state setup for the DatePickerDialog. Specifies to show the picker initially
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)

    // State to control the visibility of the DatePickerDialog
    val openDialog = remember { mutableStateOf(false) }

    // Define the main color for the calendar picker
    val calendarPickerMainColor = Color(0xFF722276)

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
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

                // Asking for employee DOB
                OutlinedTextField(
                    value = if (employeeDOB.value.isEmpty()) "Select Date of Birth" else employeeDOB.value,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            openDialog.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.CalendarToday,
                                contentDescription = "Select Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    label = { Text("Date of Birth") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            openDialog.value = true
                        }
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (openDialog.value) {
                    // DatePickerDialog component with custom colors and button behaviors
                    DatePickerDialog(
                        colors = DatePickerDefaults.colors(
                            containerColor = Color(0xFFF5F0FF),
                        ),
                        onDismissRequest = {
                            // Action when the dialog is dismissed without selecting a date
                            openDialog.value = false
                        },
                        confirmButton = {
                            // Confirm button with custom action and styling
                            TextButton(
                                onClick = {
                                    // Action to set the selected date and close the dialog
                                    openDialog.value = false
                                    employeeDOB.value =
                                        datePickerState.selectedDateMillis?.convertMillisToDate()
                                            ?: ""
                                }
                            ) {
                                Text("OK", color = calendarPickerMainColor)
                            }
                        },
                        dismissButton = {
                            // Dismiss button to close the dialog without selecting a date
                            TextButton(
                                onClick = {
                                    openDialog.value = false
                                }
                            ) {
                                Text("CANCEL", color = calendarPickerMainColor)
                            }
                        }
                    ) {
                        // The actual DatePicker component within the dialog
                        DatePicker(
                            state = datePickerState,
                            colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = calendarPickerMainColor,
                                selectedDayContentColor = Color.White,
                                selectedYearContainerColor = calendarPickerMainColor,
                                selectedYearContentColor = Color.White,
                                todayContentColor = calendarPickerMainColor,
                                todayDateBorderColor = calendarPickerMainColor
                            )
                        )
                    }
                }

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
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Asking for Employee Address
                OutlinedTextField(
                    value = employeeAddress,
                    onValueChange = {
                        employeeAddress = it
                    },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Asking for Employee Phone Number
                OutlinedTextField(
                    value = employeePhoneNumber,
                    onValueChange = {
                        scope.launch {
                            if (integerRegex.matches(it)) {
                                employeePhoneNumber = it
                            } else {
                                snackbarHostState.showSnackbar("Phone number must be digits")
                            }
                        }
                    },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Asking for City
                OutlinedTextField(
                    value = employeeCity,
                    onValueChange = {
                        scope.launch {
                            if (namePattern.matches(it)) {
                                employeeCity = it
                            } else {
                                snackbarHostState.showSnackbar("City Name cannot contain special characters or numbers")
                            }
                        }
                    },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Asking for State
                OutlinedTextField(
                    value = employeeState,
                    onValueChange = {
                        scope.launch {
                            if (namePattern.matches(it)) {
                                employeeState = it
                            } else {
                                snackbarHostState.showSnackbar("State Name cannot contain special characters or numbers")
                            }
                        }
                    },
                    label = { Text("State") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Asking for Zip Code
                OutlinedTextField(
                    value = employeePincode,
                    onValueChange = {
                        scope.launch {
                            if (integerRegex.matches(it)) {
                                employeePincode = it
                            } else {
                                snackbarHostState.showSnackbar("Please Enter a valid input")
                            }
                        }
                    },
                    label = { Text("Zip Code") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Asking for Country
                OutlinedTextField(
                    value = employeeCountry,
                    onValueChange = {
                        scope.launch {
                            if (namePattern.matches(it)) {
                                employeeCountry = it
                            } else {
                                snackbarHostState.showSnackbar("Country Name cannot contain special characters or numbers")
                            }
                        }
                    },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // AddUser Button
                Button(
                    onClick = {
                        scope.launch {
                            if (employeeFirstname.isEmpty()) {
                                snackbarHostState.showSnackbar("First Name cannot be empty")
                            } else if (employeeLastname.isEmpty()) {
                                snackbarHostState.showSnackbar("Last Name cannot be empty")
                            } else if (employeeID.isEmpty() || employeeID.length != 6) {
                                snackbarHostState.showSnackbar("Employee ID must be of 6 digits")
                            } else if (employeeDOB.value.isEmpty()) {
                                snackbarHostState.showSnackbar("Employee DOB must be filled")
                            } else if (employeeEmail.isEmpty()) {
                                snackbarHostState.showSnackbar("Employee Email ID cannot be empty")
                            } else if (employeeAddress.isEmpty()) {
                                snackbarHostState.showSnackbar("Address cannot be empty")
                            } else if (employeePhoneNumber.isEmpty() || employeePhoneNumber.length != 10) {
                                snackbarHostState.showSnackbar("Phone number must be 10 digits")
                            } else if (employeeCity.isEmpty()) {
                                snackbarHostState.showSnackbar("City name cannot be empty")
                            } else if (employeeState.isEmpty()) {
                                snackbarHostState.showSnackbar("State name cannot be empty")
                            } else if (employeePincode.isEmpty() || employeePincode.length != 6) {
                                snackbarHostState.showSnackbar("Pincode must be of 6 digits")
                            } else if (employeeCountry.isEmpty()) {
                                snackbarHostState.showSnackbar("Country name cannot be empty")
                            } else {
                                // Creating a new user
                                val newUser = User(
                                    id = employeeID.toInt(),
                                    firstName = employeeFirstname,
                                    lastName = employeeLastname,
                                    email = employeeEmail,
                                    address = employeeAddress,
                                    phoneNumber = employeePhoneNumber,
                                    city = employeeCity,
                                    state = employeeState,
                                    pinCode = employeePincode,
                                    country = employeeCountry,
                                    dob = employeeDOB.value
                                )

                                userViewModel.addUser(newUser)

                                if (errorMessage.isNotEmpty()) {
                                    snackbarHostState.showSnackbar(errorMessage)
                                } else {
                                    snackbarHostState.showSnackbar("User created: ${newUser.firstName} ${newUser.lastName}")
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailPage(user: User, onBack: () -> Unit) {
    Scaffold(

        // userdetail - topbar
        topBar = {
            TopAppBar(

                // userdetail - topbar - title
                title = { Text("User Details") },

                // userdetail - topbar - styling
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),

                // userdetail - topbar - navigation
                navigationIcon = {
                    IconButton(onClick = onBack) {

                        // userdetail - topbar - navigation - icon
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

        // userdetail - column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // userdetail - column - card
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                // userdetail - column - card - box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {

                    // userdetail - column  - card - box - icon (Profile Pic)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_happy_emoji), // displaying dummy profile pic until functionality added
                        contentDescription = "User Profile Picture",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Display user name
            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Displaying user details
            DetailRow(label = "Date of Birth", value = user.dob)
            DetailRow(label = "Email", value = user.email)
            DetailRow(label = "Employee ID", value = user.id.toString())
            DetailRow(label = "Address", value = user.address)
            DetailRow(label = "Phone Number", value = user.phoneNumber)
            DetailRow(label = "City", value = user.city)
            DetailRow(label = "State", value = user.state)
            DetailRow(label = "Zip Code", value = user.pinCode)
            DetailRow(label = "Country", value = user.country)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {

    // detailrow - card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {

        // detailrow - card - row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // detailrow - card - text1 (Parameter)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // detailrow - card - text2 (Value)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


fun Long.convertMillisToDate(): String {
    // Create a calendar instance in the default time zone
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@convertMillisToDate

        // Adjust for the time zone offset to get the correct local date
        val zoneOffset = get(Calendar.ZONE_OFFSET)
        val dstOffset = get(Calendar.DST_OFFSET)
        add(Calendar.MILLISECOND, -(zoneOffset + dstOffset))
    }
    // Format the calendar time in the specified format
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    return sdf.format(calendar.time)
}