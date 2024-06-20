package com.example.dnote

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Home : Fragment() {

    private lateinit var folderAdapter: FolderAdapter
    private lateinit var folderRecyclerView: RecyclerView
    private lateinit var addFolderButton: FloatingActionButton
    private lateinit var emptyMessageTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        folderRecyclerView = view.findViewById(R.id.folderRecyclerView)
        addFolderButton = view.findViewById(R.id.addFolderButton)
        emptyMessageTextView = view.findViewById(R.id.emptyMessageTextView)
        val searchView: SearchView = view.findViewById(R.id.searchView)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                openSearchActivity()
                searchView.clearFocus()
            }
        }

        setupRecyclerView()
        setupAddFolderButton()
        updateEmptyMessageVisibility()

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
        updateEmptyMessageVisibility()
    }

    private fun showAddFolderDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.enter_folder_name))

        val input = EditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val folderName = input.text.toString()
            if (folderName.isNotEmpty()) {
                addFolder(folderName)
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.please_enter_a_folder_name), Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextAppearance(R.style.DialogButtonText)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextAppearance(R.style.DialogButtonText)
    }

    private fun showDeleteConfirmationDialog(folderName: String) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_folder))
            .setMessage(getString(R.string.do_you_really_want_to_delete_the_folder, folderName))
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteFolder(folderName)
            }
            .setNegativeButton(R.string.cancel, null)

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextAppearance(R.style.DialogButtonText)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextAppearance(R.style.DialogButtonText)
    }

    private fun deleteFolder(folderName: String) {
        folderAdapter.deleteFolder(folderName)
        FolderManager.saveFolders(requireContext(), folderAdapter.getFolders())
        updateEmptyMessageVisibility()
    }

    private fun updateEmptyMessageVisibility() {
        if (folderAdapter.itemCount == 0) {
            emptyMessageTextView.visibility = View.VISIBLE
        } else {
            emptyMessageTextView.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForContextMenu(folderRecyclerView)
    }

    private fun showRenameFolderDialog(folderName: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.rename_folder))

        val input = EditText(requireContext())
        input.setText(folderName)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val newFolderName = input.text.toString()
            if (newFolderName.isNotEmpty()) {
                renameFolder(folderName, newFolderName)
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.please_enter_a_new_folder_name), Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextAppearance(R.style.DialogButtonText)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextAppearance(R.style.DialogButtonText)
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
