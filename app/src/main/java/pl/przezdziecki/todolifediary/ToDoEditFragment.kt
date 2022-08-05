package pl.przezdziecki.todolifediary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import pl.przezdziecki.todolifediary.databinding.FragmentTodoDetailsBinding
import pl.przezdziecki.todolifediary.databinding.FragmentTodoEditBinding
import pl.przezdziecki.todolifediary.db.ToDoItem
import pl.przezdziecki.todolifediary.db.getFormattedDateTime
import java.text.SimpleDateFormat
import java.util.*

class ToDoEditFragment : Fragment() {

    private var _binding: FragmentTodoEditBinding? = null
    private val navigationArgs: ToDoEditFragmentArgs by navArgs()
    private val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }
    private val binding get() = _binding!!
    lateinit var itemToDo: ToDoItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTodoEditBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ToDoEditFragment", "onViewCreated")
        toDoLifeViewModel.loadToDoItem(navigationArgs.todoUuid)
            .observe(this.viewLifecycleOwner) { selectedItem ->
                if(selectedItem==null)
                {
                    val action = ToDoEditFragmentDirections.actionToDoEditFragmentToToDoListFragment(itemToDo.dateday)
                    this.findNavController().navigate(action)

                }else {
                    itemToDo = selectedItem
                    bind(itemToDo)
                }
            }
        binding.buttonUpdateTodo.setOnClickListener {
            updateToDo()
            val action = ToDoEditFragmentDirections.actionToDoEditFragmentToToDoDetailsFragment(itemToDo.todo_uuid)
            this.findNavController().navigate(action)
        }
        binding.buttonDeleteTodo.setOnClickListener {
            deleteToDo()
           // toDoLifeViewModel.todoDateList.
            val action = ToDoEditFragmentDirections.actionToDoEditFragmentToToDoListFragment(itemToDo.dateday)
            this.findNavController().navigate(action)
        }
        binding.buttonDate.setOnClickListener {
            showDatePicker()
        }
        binding.buttonTime.setOnClickListener {
            showTimePicker()
        }
        binding.buttonCancelTodo.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun bind(toDoItem: ToDoItem) {
        binding.apply {
            todoTitle.setText(toDoItem.title)
            todoDescription.setText(toDoItem.description)
            setButtonsDateTimeText(toDoItem.startDateTime)
        }
    }

    private fun updateToDo() {
        Log.d("ToDoEditFragment", "updateToDo")
        itemToDo.title= binding.todoTitle.text.toString()
        toDoLifeViewModel.saveToDoItem(itemToDo)
    }
    private fun deleteToDo() {
        Log.d("ToDoEditFragment", "updateToDo")
        toDoLifeViewModel.deleteToDoItem(itemToDo)
    }

    private fun stringToLocalDateTime(parString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd E HH:mm")
        var date: Date = dateFormat.parse(parString) as Date
        return date.toInstant().toEpochMilli()
    }
    private fun setButtonsDateTimeText(parStartDateTime: Long) {
        val sdfd = SimpleDateFormat("yyyy-MM-dd E", Locale.getDefault())
        val sdft = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.buttonDate.text = sdfd.format(parStartDateTime)
        binding.buttonTime.text = sdft.format(parStartDateTime)
        Log.d("AddToDoFragment", "setButtonsDateTimeText date ${binding.buttonDate.text}")
        Log.d("AddToDoFragment", "setButtonsDateTimeText time ${binding.buttonTime.text}")
    }

    private fun showTimePicker() {
        val sdfhh = SimpleDateFormat("HH", Locale.getDefault())
        val sdfmm = SimpleDateFormat("mm", Locale.getDefault())
        val picker =
            MaterialTimePicker.Builder()
                .setHour(sdfhh.format(itemToDo.startDateTime).toInt())
                .setMinute(sdfmm.format(itemToDo.startDateTime).toInt())
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select Appointment time")
                .build()
        picker.show(requireActivity().supportFragmentManager, "TimePicker")
        picker.addOnPositiveButtonClickListener {
            Log.d("AddToDoFragment", "hour: ${picker.hour}")
            Log.d("AddToDoFragment", "minute: ${picker.minute}")
            itemToDo.startDateTime =
                stringToLocalDateTime(binding.buttonDate.text.toString() + " " + picker.hour + ":" + picker.minute)
            setButtonsDateTimeText(itemToDo.startDateTime)
            Log.d("AddToDoFragment", "hour: ${picker.hour}")
            Log.d("AddToDoFragment", "minute: ${picker.minute}")
        }
    }
    private fun showDatePicker() {
        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
            .Builder
            .datePicker()
            .setSelection(itemToDo.startDateTime)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText("Date to do")
            .build()
        datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener {
            val sdf = SimpleDateFormat("yyyy-MM-dd E", Locale.getDefault())
            val date = sdf.format(it)
            Log.d("AddToDoFragment", "selected todo date $date")
            itemToDo.startDateTime = stringToLocalDateTime(date + " " + binding.buttonTime.text)
            setButtonsDateTimeText(itemToDo.startDateTime)
        }

    }
}