package pl.przezdziecki.todolifediary

import android.R
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import pl.przezdziecki.todolifediary.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

private var TAG: String = "HomeFragment"

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private var currentDay: Long = 0
    private val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }

    init {
        val now: Instant = Clock.System.now()
        val today: LocalDate =
            now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val mDate: Date = sdf.parse(today.toString()) as Date;
        currentDay = mDate.time
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ItemHomeToDoListAdapter {
            Log.d(TAG, "kliknął ${it.todo_uuid}")
            val action =
                HomeFragmentDirections.actionHomeFragmentToToDoDetailsFragment(it.todo_uuid)
            this.findNavController().navigate(action)
        }

        Log.d("HomeFragment", "onViewCreated")
        binding.recyclerViewDate.layoutManager = GridLayoutManager(this.context, 1)
        binding.recyclerViewDate.adapter = adapter
        loadListToDo("DAY")
        bindingButtonsAction()
        (activity?.application as ToDoLiveDiaryApplication).lastFragment="HOME"
    }

    private fun bindingButtonsAction() {
        binding.apply {
            buttonDay.setOnClickListener {
                Log.d(TAG, "buttonDay click")
                setDefaultButtonsStyle("DAY")
                loadListToDo("DAY")
            }
            buttonWeek.setOnClickListener {
                Log.d(TAG, "buttonWeek click")
                setDefaultButtonsStyle("WEEK")
                loadListToDo("WEEK")
            }
            buttonMonth.setOnClickListener {
                Log.d(TAG, "buttonMonth click")
                setDefaultButtonsStyle("MONTH")
                loadListToDo("MONTH")
            }
            buttonYear.setOnClickListener {
                Log.d(TAG, "buttonYear click")
                setDefaultButtonsStyle("YEAR")
                loadListToDo("YEAR")
            }
        }
    }

    private fun setDefaultButtonsStyle(s: String) {
        binding.apply {

            buttonDay.setTypeface(buttonDay.typeface, Typeface.NORMAL)
            buttonDay.setTextColor(resources.getColor(R.color.holo_blue_light, null))

            buttonWeek.setTypeface(buttonWeek.typeface, Typeface.NORMAL)
            buttonWeek.setTextColor(resources.getColor(R.color.holo_blue_light, null))

            buttonMonth.setTypeface(buttonMonth.typeface, Typeface.NORMAL)
            buttonMonth.setTextColor(resources.getColor(R.color.holo_blue_light, null))

            buttonYear.setTypeface(buttonYear.typeface, Typeface.NORMAL)
            buttonYear.setTextColor(resources.getColor(R.color.holo_blue_light, null))
        }
    }


    private fun loadListToDo(s: String) {
        Log.d(TAG, "loadDayListToDo s ${s}")
        when (s) {
            "DAY" -> {
                toDoLifeViewModel.currentHomeButton = s
                binding.apply {
                    buttonDay.setTypeface(buttonDay.typeface, Typeface.BOLD)
                    buttonDay.setTextColor(resources.getColor(R.color.black, null))
                }
                toDoLifeViewModel.todoItemList = toDoLifeViewModel.getDay(currentDay)
            }
            "WEEK" -> {
                Log.d(TAG, "loadDayListToDo WEEK sel")
                toDoLifeViewModel.currentHomeButton = s
                binding.apply {
                    buttonWeek.setTypeface(buttonWeek.typeface, Typeface.BOLD)
                    buttonWeek.setTextColor(resources.getColor(R.color.black, null))
                }
                toDoLifeViewModel.todoItemList = toDoLifeViewModel.getWeek(currentDay)
            }
            "MONTH" -> {
                toDoLifeViewModel.currentHomeButton = s
                binding.apply {
                    buttonMonth.setTypeface(buttonMonth.typeface, Typeface.BOLD)
                    buttonMonth.setTextColor(resources.getColor(R.color.black, null))
                }
                toDoLifeViewModel.todoItemList = toDoLifeViewModel.getMonth(currentDay)
            }
            "YEAR" -> {
                toDoLifeViewModel.currentHomeButton = s
                binding.apply {

                    buttonYear.setTypeface(buttonYear.typeface, Typeface.BOLD)
                    buttonYear.setTextColor(resources.getColor(R.color.black, null))
                }
                toDoLifeViewModel.todoItemList = toDoLifeViewModel.getYear(currentDay)
            }
        }

        toDoLifeViewModel.todoItemList.observe(this.viewLifecycleOwner) { items ->
            items.let {
                (binding.recyclerViewDate.adapter as ItemHomeToDoListAdapter).submitList(it)
            }
        }
        binding.recyclerViewDate.setHasFixedSize(true)
    }
}