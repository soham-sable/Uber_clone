package com.example.uberclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons
    .filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs



// ==========================================
// 0. DARK THEME CONFIGURATION
// ==========================================
private val BlackColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color(0xFFBB86FC),
    background = Color.Black,
    surface = Color(0xFF121212),
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color.White,
    outline = Color.Gray
)

// ==========================================
// 1. MAIN ACTIVITY & NAVIGATION SWITCH
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = BlackColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var appState by remember { mutableStateOf(AppState.SPLASH) }

                    when (appState) {
                        AppState.SPLASH -> AnimatedSplashScreen(onTimeout = { appState = AppState.LOGIN })
                        AppState.LOGIN -> LoginScreen(onLoginSuccess = { appState = AppState.VERIFIED })
                        AppState.VERIFIED -> VerifiedSplashScreen(onTimeout = { appState = AppState.HOME })
                        AppState.HOME -> RideBookingScreen()
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. STATE ENUMS & DATA CLASSES
// ==========================================
enum class AppState { SPLASH, LOGIN, VERIFIED, HOME }

enum class BookingStep { INPUT_LOCATIONS, CHOOSE_RIDE, FINDING_DRIVER, BOOKED }

data class RideOption(val id: String, val name: String, val timeAway: String, val price: Int, val description: String)


// ==========================================
// 3. INITIAL SPLASH SCREEN
// ==========================================
@Composable
fun AnimatedSplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 800))
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 800))
        delay(1500L)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "UBER", color = Color.White, fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp,
                modifier = Modifier.scale(scale.value).alpha(alpha.value)
            )
        }
    }
}

// ==========================================
// 4. VERIFIED SPLASH SCREEN
// ==========================================
@Composable
fun VerifiedSplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(targetValue = 1.2f, animationSpec = tween(durationMillis = 400))
        scale.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 400))
        delay(1500L)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black), // Set to Black for theme consistency
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified",
                modifier = Modifier.size(120.dp).scale(scale.value).alpha(alpha.value),
                tint = Color(0xFF00C853)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Verified",
                color = Color.White, // Set to White for Black theme
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}

// ==========================================
// 5. LOGIN SCREEN
// ==========================================
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter your mobile number", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text("We will send a code to verify your number.", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) phoneNumber = it },
            label = { Text("Phone Number") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
            prefix = { Text("+91  ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onLoginSuccess() },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
            enabled = phoneNumber.length == 10
        ) {
            Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
            Text("  or  ", color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { onLoginSuccess() },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("Continue with Google", fontSize = 16.sp)
        }
    }
}

// ==========================================
// 6. PRO RIDE BOOKING SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideBookingScreen() {
    var currentStep by remember { mutableStateOf(BookingStep.INPUT_LOCATIONS) }
    var pickupLocation by remember { mutableStateOf("") }
    var destinationLocation by remember { mutableStateOf("") }
    var selectedRideId by remember { mutableStateOf("go") }
    var selectedPayment by remember { mutableStateOf("Cash") }
    
    val mumbaiLocations = listOf(
        "Bandra", "Colaba", "Juhu", "Andheri", "Borivali",
        "Dadar", "Worli", "Malad", "Goregaon", "Kurla",
        "Chembur", "Powai", "Mulund", "Ghatkopar", "Vile Parle"
    )

    val distance = remember(pickupLocation, destinationLocation) {
        if (pickupLocation.isNotBlank() && destinationLocation.isNotBlank()) {
            val pIdx = mumbaiLocations.indexOf(pickupLocation)
            val dIdx = mumbaiLocations.indexOf(destinationLocation)
            if (pIdx == dIdx) 2 else abs(pIdx - dIdx) * 3 + 4
        } else 0
    }

    val rideOptions = remember(distance) {
        listOf(
            RideOption("moto", "Uber Moto", "1 min away", distance * 10 + 40, "Affordable motorcycle rides"),
            RideOption("go", "Uber Go", "3 min away", distance * 15 + 60, "Affordable, compact rides"),
            RideOption("sedan", "Uber Sedan", "5 min away", distance * 22 + 100, "Comfortable sedans, top quality"),
            RideOption("xl", "Uber XL", "7 min away", distance * 30 + 150, "Comfortable SUVs for up to 6")
        )
    }

    if (currentStep == BookingStep.FINDING_DRIVER) {
        LaunchedEffect(Unit) {
            delay(2000L)
            currentStep = BookingStep.BOOKED
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (currentStep == BookingStep.INPUT_LOCATIONS || currentStep == BookingStep.CHOOSE_RIDE) {
            Text(
                text = if (currentStep == BookingStep.INPUT_LOCATIONS) "Plan your ride" else "Choose a ride",
                fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 16.dp),
                color = Color.White
            )
        }

        AnimatedVisibility(
            visible = currentStep == BookingStep.INPUT_LOCATIONS || currentStep == BookingStep.CHOOSE_RIDE,
            enter = fadeIn(), exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LocationDropdown(
                        label = "Pickup Location",
                        selectedLocation = pickupLocation,
                        locations = mumbaiLocations,
                        onLocationSelected = { pickupLocation = it },
                        enabled = currentStep == BookingStep.INPUT_LOCATIONS,
                        icon = Icons.Default.Person,
                        iconColor = Color.Cyan
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LocationDropdown(
                        label = "Destination",
                        selectedLocation = destinationLocation,
                        locations = mumbaiLocations,
                        onLocationSelected = { destinationLocation = it },
                        enabled = currentStep == BookingStep.INPUT_LOCATIONS,
                        icon = Icons.Default.LocationOn,
                        iconColor = Color.Red
                    )
                    
                    if (currentStep == BookingStep.CHOOSE_RIDE) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Distance: $distance km",
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (currentStep) {
            BookingStep.INPUT_LOCATIONS -> {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { if (pickupLocation.isNotBlank() && destinationLocation.isNotBlank()) currentStep = BookingStep.CHOOSE_RIDE },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    enabled = pickupLocation.isNotBlank() && destinationLocation.isNotBlank() && pickupLocation != destinationLocation,
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Search Rides", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            }

            BookingStep.CHOOSE_RIDE -> {
                Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    rideOptions.forEach { ride ->
                        val isSelected = selectedRideId == ride.id
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF333333) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(2.dp, Color.White) else BorderStroke(1.dp, Color.DarkGray),
                            onClick = { selectedRideId = ride.id }
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = "Car", modifier = Modifier.size(32.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ride.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                                    Text(ride.timeAway, color = Color.Gray, fontSize = 12.sp)
                                }
                                Text("₹${ride.price}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Payment:", fontWeight = FontWeight.Bold, color = Color.White)
                        Row {
                            listOf("Cash", "UPI", "Card").forEach { method ->
                                TextButton(
                                    onClick = { selectedPayment = method },
                                    colors = ButtonDefaults.textButtonColors(contentColor = if (selectedPayment == method) Color.White else Color.Gray)
                                ) { Text(method, fontWeight = if (selectedPayment == method) FontWeight.Bold else FontWeight.Normal) }
                            }
                        }
                    }
                }

                Button(
                    onClick = { currentStep = BookingStep.FINDING_DRIVER },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Confirm ${rideOptions.find { it.id == selectedRideId }?.name}", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            }

            BookingStep.FINDING_DRIVER -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(60.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Contacting nearby drivers...", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This should just take a moment.", color = Color.Gray)
                }
            }

            BookingStep.BOOKED -> {
                val bookedRide = rideOptions.find { it.id == selectedRideId }!!
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 20.dp).weight(1f)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF00C853), modifier = Modifier.size(100.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ride Confirmed!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${bookedRide.name} is on its way", fontSize = 18.sp, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Estimated Arrival", color = Color.Gray)
                                Text("4 mins", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Driver Name", color = Color.Gray)
                                Text("Rajesh Kumar", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Vehicle No.", color = Color.Gray)
                                Text("MH 01 AB 1234", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { currentStep = BookingStep.INPUT_LOCATIONS },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text("Back to Home", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    label: String,
    selectedLocation: String,
    locations: List<String>,
    onLocationSelected: (String) -> Unit,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedLocation,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = iconColor) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E1E1E))
        ) {
            locations.forEach { location ->
                DropdownMenuItem(
                    text = { Text(location, color = Color.White) },
                    onClick = {
                        onLocationSelected(location)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color.White
                    )
                )
            }
        }
    }
}
