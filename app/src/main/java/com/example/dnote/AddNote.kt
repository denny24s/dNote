package com.example.dnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dnote.databinding.ActivityAddNoteBinding
import java.text.SimpleDateFormat
import java.util.Date

class AddNote : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    private lateinit var note: Note
    private lateinit var old_note: Note
    private var isUpdate = false
    private var folderName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        folderName = intent.getStringExtra("folder_name") // Get folder name from intent

        try {
            old_note = intent.getSerializableExtra("current_note") as Note
            binding.etTitle.setText(old_note.title)
            binding.etNote.setText(old_note.note)
            isUpdate = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.imgCheck.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val noteDesc = binding.etNote.text.toString()

            if (title.isNotEmpty() || noteDesc.isNotEmpty()) {
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")
                val date = formatter.format(Date())

                if (isUpdate) {
                    note = Note(
                        old_note.id, title, noteDesc, date, old_note.folderName
                    )
                } else {
                    note = Note(
                        null, title, noteDesc, date, folderName // Include folder name here
                    )
                }

                val intent = Intent()
                intent.putExtra("note", note)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this@AddNote, "Please enter some data", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imgBackArrow.setOnClickListener {
            onBackPressed()
        }
    }
}
