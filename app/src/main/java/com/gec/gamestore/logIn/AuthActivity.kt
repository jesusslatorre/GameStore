package com.gec.gamestore.logIn

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gec.gamestore.*
import com.gec.gamestore.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    private val googleSignIn = 100

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

        googleButton.setOnClickListener {

            // Configuración autenticación con Google
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, googleSignIn)
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

    //Autenticación con Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == googleSignIn) {

            //Variable task para autenticar con Google
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            //Bloque try, catch que obtiene el token de Google y registra en Firebase
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {

                            if (it.isSuccessful) {

                                Toast.makeText(baseContext, "¡Bienvenido/a!", Toast.LENGTH_SHORT)
                                    .show()
                                showScroll()
                            } else {

                                //Manejamos error, llamamos a diálogo
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {

                //Manejamos error, llamamos a diálogo
                showAlert()
            }
        }
    }

}



