package com.gec.gamestore.logIn

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.gec.gamestore.*
import com.gec.gamestore.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {

        //Splash de inicio
        Thread.sleep(500)
        setTheme(R.style.Theme_gamestore)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Eventos personalizados a Google Analytics
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firbease completada.")
        analytics.logEvent("InitScreen", bundle)


        // Setup funciones
        setup()
        session()

        //Google
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        findViewById<Button>(R.id.googleButton).setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {

        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }


        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {

            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }


        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(baseContext, "¡Bienvenido/a!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }

    }


    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    //Establecemos las variables para autenticar
    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        //Reglas autenticación
        if (email != null) {
            authLayout.visibility = View.INVISIBLE

        }
    }

    //Función de login y autenticación
    private fun setup() {
        title = "LogIn"

        //Botón de LogIn
        loginButton.setOnClickListener {
            if (fullnameEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {

                //Logeamos al usuario con los campos que meta el usuario en email y password
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        fullnameEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).addOnCompleteListener {

                        //If, Else para llamar a showHome o showAlert
                        if (it.isSuccessful) {

                            showScroll()
                            Toast.makeText(baseContext, "¡Bienvenido/a!", Toast.LENGTH_SHORT).show()

                        } else {
                            showAlert()
                        }
                    }
            }
        }

        //Botón hacia ForgotPassword
        contrasenaOlvidadaBtn.setOnClickListener {
            val contrasenaIntent = Intent(this, ForgotPassword::class.java)
            startActivity(contrasenaIntent)

        }
        //Botón hacia Login
        cuentaBtn.setOnClickListener {
            val cuentaIntent = Intent(this, LoginActivity::class.java).apply {}
            startActivity(cuentaIntent)
        }

    }

    private fun showAlert() {

        //Diálogo de error
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error de autenticación:")
        builder.setMessage("Revisa el mail introducido y la contraseña.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    //Función para cargar la MainActivity (pantalla de anuncios)
    private fun showScroll() {

        val scrollIntent = Intent(this, MainActivity::class.java)
        startActivity(scrollIntent)
    }
}



