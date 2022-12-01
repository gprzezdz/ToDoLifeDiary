package pl.przezdziecki.todolifediary

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.datetime.Clock
import pl.przezdziecki.todolifediary.databinding.FragmentCommentBinding
import pl.przezdziecki.todolifediary.db.ToDoComment
import java.text.SimpleDateFormat
import java.util.*

class CommentFragment : Fragment() {

    private var _binding: FragmentCommentBinding? = null
    private val navigationArgs: CommentFragmentArgs by navArgs()
    private val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }
    private val binding get() = _binding!!
    private lateinit var itemComment: ToDoComment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemComment = navigationArgs.todoComment
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.buttonClose.setOnClickListener {
            saveComment()
        }
        binding.buttonDate.setOnClickListener {
            showDatePicker()
        }
        binding.buttonTime.setOnClickListener {
            showTimePicker()
        }
        binding.buttonDeleteComment.setOnClickListener {
         val  builder: AlertDialog.Builder =AlertDialog.Builder(activity)
            builder.setTitle("Delete comment")
            builder.setMessage("Are you want delete this comment")
            builder.setPositiveButton("Yes") { dialog, _ ->
                deleteComment()
                dialog.cancel()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            val alertDialog:AlertDialog =builder.create()
            alertDialog.show()
        }
        bind(itemComment)
    }


    private fun showDatePicker() {
        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
            .Builder
            .datePicker()
            .setSelection(itemComment.comDateTime)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText("Date to do")
            .build()
        datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener {
            val sdf = SimpleDateFormat("yyyy-MM-dd E", Locale.getDefault())
            val date = sdf.format(it)
            Log.d("AddToDoFragment", "selected todo date $date")

            itemComment.comDateTime = stringToLocalDateTime(date + " " + binding.buttonTime.text)
            setButtonsDateTimeText(itemComment.comDateTime)
        }


    }

    private fun showTimePicker() {
        val dash = SimpleDateFormat("HH", Locale.getDefault())
        val scumm = SimpleDateFormat("mm", Locale.getDefault())
        val picker =
            MaterialTimePicker.Builder()
                .setHour(dash.format(itemComment.comDateTime).toInt())
                .setMinute(scumm.format(itemComment.comDateTime).toInt())
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select Appointment time")
                .build()
        picker.show(requireActivity().supportFragmentManager, "TimePicker")
        picker.addOnPositiveButtonClickListener {
            Log.d("AddToDoFragment", "hour: ${picker.hour}")
            Log.d("AddToDoFragment", "minute: ${picker.minute}")
            itemComment.comDateTime =
                stringToLocalDateTime(binding.buttonDate.text.toString() + " " + picker.hour + ":" + picker.minute)
            setButtonsDateTimeText(itemComment.comDateTime)
            Log.d("AddToDoFragment", "hour: ${picker.hour}")
            Log.d("AddToDoFragment", "minute: ${picker.minute}")
        }
    }

    private fun stringToLocalDateTime(parString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd E HH:mm")
        val  date: Date = dateFormat.parse(parString) as Date
        return date.toInstant().toEpochMilli()
    }

    private fun setButtonsDateTimeText(parStartDateTime: Long) {
        binding.buttonDate.text = toDoLifeViewModel.getFormattedDateE(parStartDateTime)
        binding.buttonTime.text = toDoLifeViewModel.getFormattedTime(parStartDateTime)
        //if startdatetime different from curent time then buttonDate will be red.
        binding.buttonDate.setBackgroundColor( resources.getColor(R.color.purple_500,null))
        if(toDoLifeViewModel.getFormattedDateE(parStartDateTime)!=toDoLifeViewModel.getFormattedDateE(Clock.System.now().toEpochMilliseconds()))
        {
            binding.buttonDate.setBackgroundColor(  resources.getColor(R.color.red_700,null))
        }
        Log.d("CommentFragment", "setButtonsDateTimeText date ${binding.buttonDate.text}")
        Log.d("CommentFragment", "setButtonsDateTimeText time ${binding.buttonTime.text}")
    }
    private fun deleteComment() {
        toDoLifeViewModel.deleteToDoComment(itemComment)
        val action =
            CommentFragmentDirections.actionCommentFragmentToToDoDetailsFragment(itemComment.todoUuid)
        this.findNavController().navigate(action)
    }

    private fun saveComment() {
        binding.apply {
            itemComment.comment = commentDescription.text.toString()
            if (commentCost.text.toString().isEmpty()) {
                itemComment.cost = 0.0
            } else
                itemComment.cost = commentCost.text.toString().toDouble()
        }
        if (navigationArgs.actionClose == "EDIT") {
            toDoLifeViewModel.saveToDoComment(itemComment)
        }
        if (navigationArgs.actionClose == "ADD") {
            toDoLifeViewModel.insertToDoComment(itemComment)
        }
        if (navigationArgs.actionClose == "CLOSE") {
            toDoLifeViewModel.insertToDoComment(itemComment)
            toDoLifeViewModel.closeToDo(itemComment.todoUuid, itemComment.comDateTime)
        }
        if (navigationArgs.actionClose == "OPEN") {
            toDoLifeViewModel.insertToDoComment(itemComment)
            toDoLifeViewModel.closeToDo(itemComment.todoUuid,0L)
        }

        val action =
            CommentFragmentDirections.actionCommentFragmentToToDoDetailsFragment(itemComment.todoUuid)
        this.findNavController().navigate(action)
    }

    private fun bind(todoComment: ToDoComment) {
        binding.apply {
            binding.buttonDate.setBackgroundColor( resources.getColor(R.color.purple_500,null))
            if(toDoLifeViewModel.getFormattedDateE(todoComment.comDateTime)!=toDoLifeViewModel.getFormattedDateE(Clock.System.now().toEpochMilliseconds()))
            {
                binding.buttonDate.setBackgroundColor(  resources.getColor(R.color.red_700,null))
            }
            buttonDate.text = toDoLifeViewModel.getFormattedDateE(todoComment.comDateTime)
            buttonTime.text = toDoLifeViewModel.getFormattedTime(todoComment.comDateTime)
            commentDescription.setText(todoComment.comment)
            commentCost.setText(todoComment.cost.toString())
        }
        if(navigationArgs.actionClose=="OPEN")
        {
            binding.buttonClose.text="Open"
        }
        if(navigationArgs.actionClose=="ADD")
        {
            binding.buttonClose.text="Save"
        }
        if(navigationArgs.actionClose=="EDIT")
        {
            binding.buttonDeleteComment.visibility=View.VISIBLE
            binding.buttonClose.text="Update"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

}