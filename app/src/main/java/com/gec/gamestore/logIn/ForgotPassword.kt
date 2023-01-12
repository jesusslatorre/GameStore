package com.gec.gamestore.logIn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gec.gamestore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        //Botón de recuperado de contraseña
        recuperarBtn.setOnClickListener() {

            if (txtEmailCambio.text.isNotEmpty()) {
                recuperarContrasena(txtEmailCambio.text.toString())
            } else {

                //Toast de error
                Toast.makeText(baseContext, "Introduce un email válido", Toast.LENGTH_SHORT).show()
            }
        }

        //LLama a la plataforma de autenticación de Firebase
        firebaseAuth = Firebase.auth

        //Botón de vuelta
        volverLogin.setOnClickListener {

            finish()
        }
    }

    //Función de recuperado de password
    private fun recuperarContrasena(email: String) {

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener() { task ->

                if (task.isSuccessful) {

                    //Diálogo satisfactorio
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Correo enviado exitosamente:")
                    builder.setMessage("Es posible que el correo le aparezca en la bandeja de Spam.")
                    builder.setPositiveButton("Aceptar", null)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                    //Toast de error
                } else {
                    Toast.makeText(
                        baseContext,
                        "No fue posible recuperar su contraseña",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }
}