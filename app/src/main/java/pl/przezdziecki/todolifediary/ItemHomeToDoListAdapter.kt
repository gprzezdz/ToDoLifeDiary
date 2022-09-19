package pl.przezdziecki.todolifediary

import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import pl.przezdziecki.todolifediary.databinding.ItemHomeTodoListBinding
import pl.przezdziecki.todolifediary.db.ToDoItem
import pl.przezdziecki.todolifediary.db.getFormattedTime
import java.text.SimpleDateFormat
import java.util.*

class ItemHomeToDoListAdapter (private val onItemClicked: (ToDoItem) -> Unit) :
    ListAdapter<ToDoItem, ItemHomeToDoListAdapter.HomeToDoListViewHolder>(DiffCallback) {
    val mDate: Date
    class HomeToDoListViewHolder(private var binding: ItemHomeTodoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ToDoItem) {
            binding.todoStartTime.text = item.getFormattedTime() +" " +item.title
        }
    }
    init {
        val now: Instant = Clock.System.now()
        val today: LocalDate =
            now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val sdf = SimpleDateFormat("yyyy-MM-dd")
         mDate  = sdf.parse(today.toString()) as Date;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHomeToDoListAdapter.HomeToDoListViewHolder {
        return HomeToDoListViewHolder(
            ItemHomeTodoListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: HomeToDoListViewHolder, position: Int) {
        Log.d("ItemHomeToDoListAdapter", "item position: $position")
        val current = getItem(position)
        holder.itemView.setBackgroundColor(Color.WHITE)
        if(current.closeDateTime>0L)
        {
            holder.itemView.setBackgroundResource(R.color.light_green)
        }
        else if(current.closeDateTime==0L && current.dateday<mDate.time)
        {
            holder.itemView.setBackgroundColor(Color.RED)
        }
        else if(current.closeDateTime==0L && current.startDateTime<Clock.System.now().toEpochMilliseconds())
        {
            Log.d("ItemHomeToDoListAdapter","current.startDateTime ${current.startDateTime} Clock.System.now().toEpochMilliseconds(): ${Clock.System.now().toEpochMilliseconds()}")
            holder.itemView.setBackgroundColor(Color.YELLOW)
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