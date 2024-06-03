package com.example.dnote

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(
    private val context: Context,
    private val folders: MutableList<String>,
    private val onRenameClickListener: (String) -> Unit,
    private val onChangeColorClickListener: (String) -> Unit,
    private val onDeleteClickListener: (String) -> Unit,
    private val onFolderClickListener: (String) -> Unit // Added this parameter
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folderName = folders[position]
        holder.bind(folderName)
    }

    override fun getItemCount(): Int = folders.size

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderIcon: ImageView = itemView.findViewById(R.id.folderIcon)
        private val folderName: TextView = itemView.findViewById(R.id.folderName)

        fun bind(folderName: String) {
            this.folderName.text = folderName
            // Set background drawable based on folder name
            if (folderName == "Diary") {
                itemView.background = ContextCompat.getDrawable(context, R.drawable.rounded_diary_background)
            } else {
                itemView.background = ContextCompat.getDrawable(context, R.drawable.rounded_background)
            }

            // Bind click listener
            itemView.setOnClickListener {
                onFolderClickListener.invoke(folderName)
            }

            // Bind long click listener
            itemView.setOnLongClickListener {
                // Show context menu
                showContextMenu(itemView, folderName)
                true
            }
        }

        private fun showContextMenu(view: View, folderName: String) {
            val popup = PopupMenu(context, view)
            popup.inflate(R.menu.folder_context_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.rename_folder -> onRenameClickListener.invoke(folderName)
                    R.id.change_color -> onChangeColorClickListener.invoke(folderName)
                    R.id.delete_folder -> onDeleteClickListener.invoke(folderName)
                }
                true
            }
            popup.show()
        }
    }

    fun addFolder(folderName: String) {
        if (folderName.isNotEmpty()) {
            folders.add(folderName)
            notifyItemInserted(folders.size - 1)
        } else {
            Toast.makeText(context, "Please enter a folder name", Toast.LENGTH_SHORT).show()
        }
    }

    fun renameFolder(oldName: String, newName: String) {
        if (oldName != "Diary") {
            val position = folders.indexOf(oldName)
            if (position != -1) {
                folders[position] = newName
                notifyItemChanged(position)
            }
        }
    }

    fun deleteFolder(folderName: String) {
        if (folderName != "Diary") {
            val position = folders.indexOf(folderName)
            if (position != -1) {
                folders.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun getFolders(): MutableList<String> {
        return folders
    }
}
