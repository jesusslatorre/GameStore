package com.gec.gamestore.logIn

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.gec.gamestore.R
import com.gec.gamestore.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_auth.fullnameEditText
import kotlinx.android.synthetic.main.activity_auth.passwordEditText
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // Setup
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        loginAuth.visibility = View.VISIBLE
    }

    //Función Session para establecer las variables del login
    private fun session() {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email != null) {
            authLayout.visibility = View.INVISIBLE

        }
    }

    //Función de login
    private fun setup() {
        title = "Registro"

        //Botón para hacer el Registro
        signUpButton.setOnClickListener {
            if (fullnameEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        fullnameEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).addOnCompleteListener {

                        if (it.isSuccessful) {

                            //Creo un diálogo de bienvenida al registrar satisfactoriamente al usuario
                            val builder2 = AlertDialog.Builder(this)
                            builder2.setTitle("Usuario registrado:")
                            builder2.setMessage(
                                "Bienvenido a G-Swap." +
                                        "\n" + "\n" + "Aquí podrás vender y cambiar tus videojuegos con los demás usuarios."

                            )
                            builder2.setPositiveButton("Aceptar") { dialogInterface: DialogInterface, i: Int ->

                                showScroll()
                            }
                            val dialog2: AlertDialog = builder2.create()
                            dialog2.show()

                        } else {

                            //LLamo a diálogo de error
                            showAlert()

                        }
                    }
            }
        }

        //Botón de vuelta
        volverRegistrobtn.setOnClickListener {
            finish()
        }
    }

    //Diálogo de error
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error de registro:")
        builder.setMessage("Introduce un mail válido y una contraseña segura.")
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

