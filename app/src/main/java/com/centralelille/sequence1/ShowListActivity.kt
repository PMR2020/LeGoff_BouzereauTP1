package com.centralelille.sequence1

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centralelille.sequence1.adapters.TaskAdapter
import com.centralelille.sequence1.data.ItemToDo
import com.centralelille.sequence1.data.ListeToDo
import com.centralelille.sequence1.data.ProfilListeToDo
import com.google.gson.Gson

class ShowListActivity : AppCompatActivity(), View.OnClickListener, TaskAdapter.OnItemListener {

    private val adapter = newAdapter()

    private lateinit var refOkBtn: Button
    private lateinit var refTxtNewItem: EditText
    private lateinit var listOfTask: RecyclerView

    private lateinit var prefs: SharedPreferences
    private lateinit var prefsListes: SharedPreferences

    private lateinit var pseudoRecu: String
    private lateinit var listeRecue: String
    private lateinit var listeItemToDo: ArrayList<ItemToDo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        val bundleData: Bundle = this.getIntent().getExtras()

        pseudoRecu = bundleData.getString("pseudo")
        listeRecue = bundleData.getString("titre")
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefsListes = getSharedPreferences("DATA", 0)

        refOkBtn = findViewById(R.id.buttonNewItem)
        listOfTask = findViewById(R.id.listOfItem)
        refTxtNewItem = findViewById(R.id.editTextItem)

        listeItemToDo = getItems(pseudoRecu, listeRecue)

        adapter.showData(listeItemToDo)
        listOfTask.adapter = adapter
        listOfTask.layoutManager = LinearLayoutManager(this)

        // ClickListener associé au boutton pour créer les items
        refOkBtn.setOnClickListener(this)
    }

    private fun getItems(pseudo: String, titreListe: String): ArrayList<ItemToDo> {
        val profil: String = prefsListes.getString(pseudo, "New")
        val gson = Gson()
        Log.i("testchoixlistact", pseudo)

        val profilListeToDo: ProfilListeToDo = gson.fromJson(profil, ProfilListeToDo::class.java)
        Log.i(
            "testshowlistact",
            "récupérations listes ancien profil " + profilListeToDo.toString() + profilListeToDo.listesToDo.toString()
        )

        return findList(profilListeToDo.listesToDo, titreListe)
    }

    private fun findList(liste: ArrayList<ListeToDo>, titreListe: String): ArrayList<ItemToDo> {
        var toReturn: ArrayList<ItemToDo> = liste[0].itemsToDo
        for (i in 0..liste.size) {
            if (liste[i].equals(titreListe))
                toReturn = liste[i].itemsToDo
        }
        return toReturn
    }

    override fun onClick(v: View?) {
        val newItemDescription = refTxtNewItem.text.toString()
        val liste: String? = prefsListes.getString(listeRecue, "New")
        val gson = Gson()

        val currentList: ListeToDo = gson.fromJson(liste, ListeToDo::class.java)

        when (v?.id) {
            R.id.buttonNewItem -> {
                alert(listeRecue)

                // Par défaut l'item n'est pas fait
                val item = ItemToDo(newItemDescription, false)
                currentList.itemsToDo.add(item)
                Log.i("testshowlistact", currentList.toString())
                Log.i("testshowlistact", "Items " + currentList.itemsToDo.toString())
            }
        }
        val newListeJSON: String = gson.toJson(currentList)

        val editor: SharedPreferences.Editor = prefsListes.edit()
        editor.clear()
        editor.putString(listeRecue, newListeJSON)
        editor.apply()
    }

    //Gestion deboguage
    private fun alert(s: String) {
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, s, duration)
        toast.show()
    }

    private fun newAdapter(): TaskAdapter {
        return TaskAdapter(onItemListener = this)
    }

    /**
     * Quand un item est cliqué l'utilisateur peut l'éditer
     *
     * @param item
     */
    override fun onItemClicked(item: ItemToDo) {
        Toast.makeText(this, item.description, Toast.LENGTH_LONG).show()
        TODO("Not yet implemented")
    }
}