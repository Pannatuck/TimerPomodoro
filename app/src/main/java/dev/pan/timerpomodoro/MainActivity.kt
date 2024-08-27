package dev.pan.timerpomodoro

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dev.pan.timerpomodoro.ui.theme.TimerPomodoroTheme

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        


        // Initialize Firebase Auth
        auth = Firebase.auth

        // Access to logged in user info like this
        val user = Firebase.auth.currentUser
        user?.let {
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            // unique id of current user
            val uid = it.uid

            // checking if user verified email
            val emailVerified = it.isEmailVerified

        }

        enableEdgeToEdge()
        setContent {
            TimerPomodoroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(text = "Hello World!", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null){
            openMainScree()
        }

    }

    private fun openMainScree() {
        TODO("Not yet implemented")
    }



    // TODO: Add Composable for login that will do something when login button is clicked
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){

                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Authentication Success. Welcome ${user?.email}", Toast.LENGTH_SHORT).show()
                    /*Do something here on success login*/
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    /*Do something here on failure login*/
                }
            }
    }




}
