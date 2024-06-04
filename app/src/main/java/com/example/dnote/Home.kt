package com.example.dnote

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Home : Fragment() {

    private lateinit var folderAdapter: FolderAdapter
    private lateinit var folderRecyclerView: RecyclerView
    private lateinit var addFolderButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        folderRecyclerView = view.findViewById(R.id.folderRecyclerView)
        addFolderButton = view.findViewById(R.id.addFolderButton)
        val searchView: SearchView = view.findViewById(R.id.searchView)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                openSearchActivity()
                searchView.clearFocus()
            }
        }

        setupRecyclerView()
        setupAddFolderButton()
        return view
    }

    private fun setupRecyclerView() {
        val savedFolders = FolderManager.getFolders(requireContext())
        folderAdapter = FolderAdapter(requireContext(), savedFolders,
            { folderName -> showRenameFolderDialog(folderName) },
            { folderName -> showColorPickerDialog(folderName) },
            { folderName -> showDeleteConfirmationDialog(folderName) },
            { folderName -> openFolderDetailActivity(folderName) })
        folderRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = folderAdapter
        }
    }

    private fun setupAddFolderButton() {
        addFolderButton.setOnClickListener {
            showAddFolderDialog()
        }
    }

    private fun openSearchActivity() {
        val intent = Intent(requireContext(), SearchNotesActivity::class.java)
        startActivity(intent)
    }

    private fun addFolder(folderName: String) {
        folderAdapter.addFolder(folderName)
        FolderManager.saveFolders(requireContext(), folderAdapter.getFolders())
    }

    private fun showAddFolderDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter folder name")

        val input = EditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val folderName = input.text.toString()
            if (folderName.isNotEmpty()) {
                addFolder(folderName)
            } else {
                Toast.makeText(requireContext(), "Please enter a folder name", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showDeleteConfirmationDialog(folderName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Folder")
            .setMessage("Do you really want to delete the folder '$folderName'?")
            .setPositiveButton("Delete") { _, _ ->
                deleteFolder(folderName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteFolder(folderName: String) {
        folderAdapter.deleteFolder(folderName)
        FolderManager.saveFolders(requireContext(), folderAdapter.getFolders())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForContextMenu(folderRecyclerView)
    }

    private fun showRenameFolderDialog(folderName: String) {
        if (folderName == "Diary") {
            Toast.makeText(requireContext(), "Diary folder cannot be renamed.", Toast.LENGTH_SHORT).show()
            return
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Rename Folder")

        val input = EditText(requireContext())
        input.setText(folderName)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val newFolderName = input.text.toString()
            if (newFolderName.isNotEmpty()) {
                renameFolder(folderName, newFolderName)
            } else {
                Toast.makeText(requireContext(), "Please enter a new folder name", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun renameFolder(oldName: String, newName: String) {
        folderAdapter.renameFolder(oldName, newName)
        FolderManager.saveFolders(requireContext(), folderAdapter.getFolders())
    }

    private fun showColorPickerDialog(folderName: String) {
        Toast.makeText(requireContext(), "Color picker for $folderName not implemented yet.", Toast.LENGTH_SHORT).show()
    }

    private fun openFolderDetailActivity(folderName: String) {
        val intent = Intent(requireContext(), FolderDetailActivity::class.java)
        intent.putExtra("folder_name", folderName)
        startActivity(intent)
    }
}
