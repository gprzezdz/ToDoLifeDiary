package pl.przezdziecki.todolifediary

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import pl.przezdziecki.todolifediary.databinding.FragmentAddTodoBinding
import pl.przezdziecki.todolifediary.db.Tag
import pl.przezdziecki.todolifediary.db.ToDoDate
import pl.przezdziecki.todolifediary.db.ToDoItem
import pl.przezdziecki.todolifediary.db.ToDoTagRel
import java.text.SimpleDateFormat
import java.time.format.TextStyle
import java.util.*

private var TAG: String = "AddToDoFragment"

class AddToDoFragment : Fragment() {
    private lateinit var toDoItem: ToDoItem
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
        changeBinding()
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
        binding.buttonDate.setBackgroundColor(
            resources.getColor(
                pl.przezdziecki.todolifediary.R.color.purple_500,
                null
            )
        )
        if (sdfd.format(parStartDateTime) != sdfd.format(
                Clock.System.now().toEpochMilliseconds()
            )
        ) {
            binding.buttonDate.setBackgroundColor(
                resources.getColor(
                    pl.przezdziecki.todolifediary.R.color.red_700,
                    null
                )
            )
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

    private fun textWat(): TextWatcher {
       val test:TextWatcher=object : TextWatcher {
           override fun afterTextChanged(s: Editable?) {
               Log.d(TAG, "cs.toString() afterTextChanged:")
           }

           override fun beforeTextChanged(
               s: CharSequence?,
               start: Int,
               count: Int,
               after: Int
           ) {
               Log.d(TAG, "cs.toString() beforeTextChanged:")
           }

           override fun onTextChanged(cs: CharSequence, start: Int, before: Int, count: Int) {
               Log.d(TAG, "cs.toString() onTextChanged: ${cs.toString()} ")
               if (cs.toString().trim().isEmpty()) {
                   return
               }
               if (cs.isEmpty() || start >= cs.length || start < 0) {
                   return
               }
               Log.d(TAG, "cs.toString(): ${cs.toString()} ")
               if (cs.subSequence(start, start + 1).toString().equals(" ", true)) {
                   Log.d(TAG, "tagInput.setOnKeyListener: KEYCODE_SPACE")
                   val chip = Chip(context)
                   chip.text = cs.toString().trim()
                   chip.chipIcon = ContextCompat.getDrawable(
                       requireContext(),
                       R.drawable.ic_launcher_background
                   )
                   chip.isChipIconVisible = false
                   chip.isCloseIconVisible = true
                   chip.isClickable = true
                   chip.isCheckable = false
                  binding.chipGroup.addView(chip as View)
                   chip.setOnCloseIconClickListener { binding.chipGroup.removeView(chip as View) }
                   binding.tagInput.setText("")
               }
           }
       }
        return test
    }
    private fun changeBinding() {

        binding.apply {
            tagInput.addTextChangedListener(textWat())
            todoTitle.addTextChangedListener(textWat())
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

        toDoItem = ToDoItem(
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

        binding.chipGroup.forEach {
            val chip = it as Chip
            var tag = Tag(
                chip.text.toString().uppercase(),
                chip.text.toString(),
                "",
                Clock.System.now().toEpochMilliseconds()
            )
            toDoLifeViewModel.insertTag(tag)
            var rel =
                ToDoTagRel(toDoItem.todo_uuid, tag.uTag, Clock.System.now().toEpochMilliseconds())
            toDoLifeViewModel.insertToDoTagRel(rel)
            Log.d(TAG, "chip.text: ${chip.text}")
        }

        val action =
            AddToDoFragmentDirections.actionAddToDoFragmentToToDoDetailsFragment(toDoItem.todo_uuid)
        this.findNavController().navigate(action)
    }
}