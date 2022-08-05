package pl.przezdziecki.todolifediary

import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.przezdziecki.todolifediary.databinding.ItemDateListBinding
import pl.przezdziecki.todolifediary.db.ToDoDate


class ItemDateListAdapter (private val onItemClicked: (ToDoDate) -> Unit) :
    ListAdapter<ToDoDate, ItemDateListAdapter.DateListViewHolder>(DiffCallback) {



    class DateListViewHolder(private var binding: ItemDateListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ToDoDate) {
            binding.dateday.text = item.dateday_s.split(":")[0]
            binding.dayofweek.text="("+ item.dateday_s.split(":")[1] +")"
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateListViewHolder {
        return DateListViewHolder(
            ItemDateListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: DateListViewHolder, position: Int) {
        Log.d("DateListAdapter", "item position: $position")
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
        private val DiffCallback = object : DiffUtil.ItemCallback<ToDoDate>() {
            override fun areItemsTheSame(oldItem: ToDoDate, newItem: ToDoDate): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ToDoDate, newItem: ToDoDate): Boolean {
                return oldItem.dateday == newItem.dateday
            }
        }
    }
    }