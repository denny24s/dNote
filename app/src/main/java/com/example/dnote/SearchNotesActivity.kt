package com.example.dnote

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dnote.databinding.ActivitySearchNotesBinding

class SearchNotesActivity : AppCompatActivity(), NotesAdapter.NotesClickListener {

    private lateinit var binding: ActivitySearchNotesBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            onBackPressed()
        }

        adapter = NotesAdapter(this, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        searchEditText.requestFocus()
        searchEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString()
                searchNotes(query)
                true
            } else {
                false
            }
        }

        searchEditText.addTextChangedListener {
            val query = it.toString()
            searchNotes(query)
        }
    }

    private fun searchNotes(query: String) {
        viewModel.allNotes.observe(this) { list ->
            list?.let {
                val filteredNotes = it.filter { note ->
                    note.title?.contains(query, ignoreCase = true) == true ||
                            note.note?.contains(query, ignoreCase = true) == true
                }
                adapter.updateList(filteredNotes)
            }
        }
    }

    override fun onItemClicked(note: Note) {
        val intent = Intent(this, NoteDetailActivity::class.java).apply {
            putExtra("note_title", note.title)
            putExtra("note_text", note.note)
            putExtra("note_date", note.date)
        }
        startActivity(intent)
    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {
        // Handle item long click
    }
}
