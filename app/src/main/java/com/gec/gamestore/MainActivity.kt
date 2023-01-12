package com.gec.gamestore

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gec.gamestore.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bindingActivityMain: ActivityMainBinding
    private lateinit var messagesListener: ValueEventListener

    private val database = Firebase.database
    private val listVideoGames: MutableList<VideoGame> = ArrayList()
    private val myRef = database.getReference("game")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        bindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        val view = bindingActivityMain.root

        setContentView(view)
        fullScreen()

        bindingActivityMain.addImageView.setOnClickListener { v ->
            val intent = Intent(this, AddActivity::class.java)
            v.context.startActivity(intent)
        }

        listVideoGames.clear()
        setupRecyclerView(bindingActivityMain.recyclerView)


        reglasBtn.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Reglas y modos de uso:")
            builder.setMessage(
                "1. La regla más imporante es respetar a los demás usuarios." +
                        "\n" + "\n" + "2. Pulsa el botón + y sube todos los videojuegos que quieras." +
                        "\n" + "\n" + "3. Para ver los detalles de los videojuegos anunciados, haz click en ellos." +
                        "\n" + "\n" + "4. Para contactar con el anunciante manten pulsado el videojuego que te interese." +
                        "\n" + "\n" + "5. Todas las publicaciones serán revisadas, las que no respeten el formato se borrarán automáticamente." +
                        "\n" + "\n" + "6. Pasados 60 días la publicación se borrará automáticamente." +
                        "\n" + "\n" + "7. Para cualquier problema o sugerencia pulsa en Servicio Técnico."

            )
            builder.setPositiveButton("Aceptar", null)



            builder.setNeutralButton("Servicio Técnico") { dialog, which ->
                val intentEmail = Intent(Intent.ACTION_SEND, Uri.parse("email"))

                //Marcamos asunto y mensaje
                intentEmail.type = "plain/text"
                intentEmail.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "SAT: Incidencias, sugerencias y valoraciones G-Swap"
                )
                intentEmail.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hola, tengo el/la siguiente problema/sugerencia con G-Swap:" +
                            "\n" + "\n" + "\n" + "\n" + "Me gustaría dejar la siguiente valoración sobre G-Swap:"
                )

                //Elegimos los mail receptores
                intentEmail.putExtra(
                    Intent.EXTRA_EMAIL,

                    //Mandamos correo al correo creado por Firebase
                    arrayOf("g.swap.sat@gmail.com")
                )
                startActivity(
                    Intent.createChooser(
                        intentEmail,
                        "Elige tu cliente de correo preferido"
                    )
                )
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        messagesListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listVideoGames.clear()
                dataSnapshot.children.forEach { resp ->
                    val mVideoGame =
                        VideoGame(
                            resp.child("name").value as String?,
                            resp.child("date").value as String?,
                            resp.child("price").value as String?,
                            resp.child("description").value as String?,
                            resp.child("url").value as String?,
                            resp.key
                        )
                    mVideoGame.let { listVideoGames.add(it) }
                }
                recyclerView.adapter = VideoGameViewAdapter(listVideoGames)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)


    }

    class VideoGameViewAdapter(private val values: List<VideoGame>) :
        RecyclerView.Adapter<VideoGameViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_game_content, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val mVideoGame = values[position]
            holder.mNameTextView.text = mVideoGame.name
            holder.mPriceTextView.text = mVideoGame.price + "€"
            holder.mPosterImageView.let {
                Glide.with(holder.itemView.context)
                    .load(mVideoGame.url)
                    .into(it)
            }

            holder.itemView.setOnClickListener { v ->
                val intent = Intent(v.context, DetailActivity::class.java).apply {
                    putExtra("key", mVideoGame.key)
                }
                v.context.startActivity(intent)
            }

            holder.itemView.setOnLongClickListener { v ->
                val intent = Intent(v.context, EditActivity::class.java).apply {
                    putExtra("key", mVideoGame.key)
                }
                v.context.startActivity(intent)
                true
            }

        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mNameTextView: TextView = view.findViewById(R.id.nameTextView) as TextView
            val mPriceTextView: TextView = view.findViewById(R.id.priceTextView) as TextView
            val mPosterImageView: ImageView = view.findViewById(R.id.posterImageView) as ImageView
        }
    }


    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            }
        }
    }
}
