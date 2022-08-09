package pl.przezdziecki.todolifediary

import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.przezdziecki.todolifediary.databinding.ItemTodoListBinding
import pl.przezdziecki.todolifediary.db.ToDoItem
import pl.przezdziecki.todolifediary.db.getFormattedTime

class ItemToDoListAdapter (private val onItemClicked: (ToDoItem) -> Unit) :
    ListAdapter<ToDoItem, ItemToDoListAdapter.ToDoListViewHolder>(DiffCallback) {

    class ToDoListViewHolder(private var binding: ItemTodoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ToDoItem) {
            binding.todoStartTime.text = item.getFormattedTime() +" " +item.title
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoListViewHolder {
        return ToDoListViewHolder(
            ItemTodoListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ToDoListViewHolder, position: Int) {
        Log.d("ItemToDoListAdapter", "item position: $position")
        val current = getItem(position)
        if(current.closeDateTime>0L)
        {
            holder.itemView.setBackgroundResource(R.color.light_green)
        }else
        {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
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
        private val DiffCallback = object : DiffUtil.ItemCallback<ToDoItem>() {
            override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
                return oldItem.todo_uuid== newItem.todo_uuid
            }
        }
    }
    }