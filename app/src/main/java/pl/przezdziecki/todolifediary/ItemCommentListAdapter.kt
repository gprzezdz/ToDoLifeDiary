package pl.przezdziecki.todolifediary

import android.graphics.Color
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.przezdziecki.todolifediary.databinding.ItemCommentListBinding
import pl.przezdziecki.todolifediary.db.ToDoComment
import pl.przezdziecki.todolifediary.db.getFormattedDateTime
import java.text.SimpleDateFormat
import java.util.*

private var TAG: String = "ItemCommentListAdapter"
class ItemCommentListAdapter (private val onItemClicked: (ToDoComment) -> Unit) :
    ListAdapter<ToDoComment, ItemCommentListAdapter.CommentListViewHolder>(DiffCallback) {

    class CommentListViewHolder(private var binding: ItemCommentListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ToDoComment) {
            binding.commentTextList.text=item.getFormattedDateTime()+" " +" ("+item.cost +")" +
                    "\n"+ item.comment
            Log.d(TAG,"File type: ${item.fileType}")
            if(item.fileType=="JPG") {
                val date =
                    SimpleDateFormat("yyyy-MM-dd").format(Date(item.insertDateTime))
                val imageDirectory  = binding.root.context.getExternalFilesDir("Photos").toString() + "/"+date+"/"+item.commentUuid.toString() +".jpg"
                Log.d(TAG,"imageDirectoryURI: ${Uri.parse(imageDirectory)}")
                binding.photoComment.setImageURI(Uri.parse(imageDirectory))
                binding.photoComment.visibility=View.VISIBLE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListViewHolder {
        return CommentListViewHolder(
            ItemCommentListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: CommentListViewHolder, position: Int) {
        Log.d("ItemToDoListAdapter", "item position: $position")
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
            object : CountDownTimer(200, 200) {
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    onItemClicked(current)
                }
            }.start()
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ToDoComment>() {
            override fun areItemsTheSame(oldItem: ToDoComment, newItem: ToDoComment): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ToDoComment, newItem: ToDoComment): Boolean {
                return oldItem.commentUuid== newItem.commentUuid
            }
        }
    }
    }