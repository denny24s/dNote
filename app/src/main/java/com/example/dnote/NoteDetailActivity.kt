package com.example.dnote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dnote.databinding.ActivityNoteDetailBinding

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("note_title")
        val noteText = intent.getStringExtra("note_text")
        val date = intent.getStringExtra("note_date")

        binding.noteTitle.text = title
        binding.noteText.text = noteText
        binding.noteDate.text = date
    }
}
