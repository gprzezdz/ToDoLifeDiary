package pl.przezdziecki.todolifediary.tags

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
import pl.przezdziecki.todolifediary.databinding.ItemTagListBinding
import pl.przezdziecki.todolifediary.db.Tag
import java.text.SimpleDateFormat
import java.util.*

private var TAG: String = "ItemTagListAdapter"
class ItemTagListAdapter (private val onItemClicked: (Tag) -> Unit) :
    ListAdapter<Tag, ItemTagListAdapter.TagListViewHolder>(DiffCallback) {
    class TagListViewHolder(private var binding: ItemTagListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Tag) {
            binding.stag.text = item.sTag
            binding.tagDescription.text=item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTagListAdapter.TagListViewHolder {
        return ItemTagListAdapter.TagListViewHolder(
            ItemTagListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemTagListAdapter.TagListViewHolder, position: Int) {
        Log.d(TAG, "item position: $position")
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Tag>() {
            override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
                return oldItem.uTag== newItem.uTag
            }
        }
    }
    }