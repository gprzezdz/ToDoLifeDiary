package pl.przezdziecki.todolifediary

import android.content.DialogInterface
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import pl.przezdziecki.todolifediary.databinding.FragmentAddTodoBinding
import pl.przezdziecki.todolifediary.db.ToDoDate
import pl.przezdziecki.todolifediary.db.ToDoItem
import java.text.SimpleDateFormat
import java.time.format.TextStyle
import java.util.*


class AddToDoFragment : Fragment() {
    private val navigationArgs: AddToDoFragmentArgs by navArgs()
    private var _binding: FragmentAddTodoBinding? = null
    private val binding get() = _binding!!
    private var startDateTime: Long = 0

    private val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        startDateTime = navigationArgs.dateDay
        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSaveTodo.setOnClickListener {
            saveToDo()
        }
        binding.buttonDate.setOnClickListener {
            showDatePicker()
        }
        binding.buttonTime.setOnClickListener {
            showTimePicker()
        }
        binding.buttonCancellTodo.setOnClickListener {
            findNavController().navigateUp()
        }
        Log.d("AddToDoFragment", "startDateTime  ${startDateTime}")
        if (startDateTime == 0.toLong()) {
            startDateTime = Clock.System.now().toEpochMilliseconds()
        }
        setButtonsDateTimeText(startDateTime)
        binding.buttonTodoType.setOnClickListener {
            toDoTypeDialog()
        }
    }

    private fun toDoTypeDialog() {
        val types = arrayOf("Year", "Month", "Week", "Day")
        val ii = types.indexOf(binding.buttonTodoType.text)

        val m = MaterialAlertDialogBuilder(context!!)
            .setTitle("Chose type")
            .setSingleChoiceItems(types, ii, DialogInterface.OnClickListener { dialogInterface, i ->
                binding.buttonTodoType.text = types[i]
                dialogInterface.dismiss()
            })
            .create()
        m.show()
    }
    private fun stringToLocalDateTime(parString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd E HH:mm")
        var date: Date = dateFormat.parse(parString) as Date
        return date.toInstant().toEpochMilli()
    }

    private fun setButtonsDateTimeText(parStartDateTime: Long) {


        val sdfd = SimpleDateFormat("yyyy-MM-dd E", Locale.getDefault())
        val sdft = SimpleDateFormat("HH:mm", Locale.getDefault())
        //if startdatetime different from curent time then buttonDate will be red.
        binding.buttonDate.setBackgroundColor( resources.getColor(pl.przezdziecki.todolifediary.R.color.purple_500,null))
        if(sdfd.format(parStartDateTime)!=sdfd.format(Clock.System.now().toEpochMilliseconds()))
        {
            binding.buttonDate.setBackgroundColor(  resources.getColor(pl.przezdziecki.todolifediary.R.color.red_700,null))
        }
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
                .setHour(sdfhh.format(startDateTime).toInt())
                .setMinute(sdfmm.format(startDateTime).toInt())
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select Appointment time")
                .build()
        picker.show(requireActivity().supportFragmentManager, "TimePicker")
        picker.addOnPositiveButtonClickListener {
            Log.d("AddToDoFragment", "hour: ${picker.hour}")
            Log.d("AddToDoFragment", "minute: ${picker.minute}")
            startDateTime =
                stringToLocalDateTime(binding.buttonDate.text.toString() + " " + picker.hour + ":" + picker.minute)
            setButtonsDateTimeText(startDateTime)
            Log.d("AddToDoFragment", "hour: ${picker.hour}")
            Log.d("AddToDoFragment", "minute: ${picker.minute}")
        }
    }

    private fun LocalDateTimeToUTC(par: Long): Long {
        return Instant.fromEpochMilliseconds(par).toLocalDateTime(TimeZone.currentSystemDefault())
            .toInstant(TimeZone.UTC).toEpochMilliseconds()
    }


    private fun showDatePicker() {
        Log.d("AddToDoFragment", "clock:" + Clock.System.now().toString())
        Log.d(
            "AddToDoFragment",
            " showDatePicker startDateTime  ${startDateTime} : " + toDoLifeViewModel.getFormattedDateE(
                startDateTime
            )
        )
        Log.d(
            "AddToDoFragment",
            " showDatePicker startDateTime  ${LocalDateTimeToUTC(startDateTime)} : " + toDoLifeViewModel.getFormattedDateE(
                LocalDateTimeToUTC(startDateTime)
            )
        )

        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
            .Builder
            .datePicker()
            .setSelection(LocalDateTimeToUTC(startDateTime))
            .setTitleText("Date to do")
            .build()
        datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener {
            val sdf = SimpleDateFormat("yyyy-MM-dd E", Locale.getDefault())
            val date = sdf.format(it)
            Log.d("AddToDoFragment", "selected todo date $date")
            startDateTime = stringToLocalDateTime(date + " " + binding.buttonTime.text)
            setButtonsDateTimeText(startDateTime)
        }

    }

    private fun saveToDo() {
        startDateTime =
            stringToLocalDateTime(binding.buttonDate.text.toString() + " " + binding.buttonTime.text)
        Log.d("AddToDoFragment", "binding.todoName.text: ${binding.todoTitle.text}")
        val now: Instant = Instant.fromEpochMilliseconds(startDateTime)
        val today: LocalDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        var todo = ToDoDate()
        todo.dateday_s = today.toString() + ":" + today.dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        ) + ""
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val mDate: Date = sdf.parse(today.toString()) as Date;
        todo.dateday = mDate.time
        //toDoLifeViewModel.saveDateItem(todo)
        var toDoItem = ToDoItem(
            UUID.randomUUID(),
            todo.dateday,
            binding.todoTitle.text.toString(),
            binding.todoDescription.text.toString(),
            startDateTime,
            0,
            Clock.System.now().toEpochMilliseconds(),
            binding.buttonTodoType.text.toString().uppercase()
        )
        toDoLifeViewModel.insertToDoItem(toDoItem)
        val action =
            AddToDoFragmentDirections.actionAddToDoFragmentToToDoDetailsFragment(toDoItem.todo_uuid)
        this.findNavController().navigate(action)
    }
}