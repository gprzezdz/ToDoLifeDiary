package pl.przezdziecki.todolifediary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.datetime.Clock
import pl.przezdziecki.todolifediary.databinding.FragmentDateListBinding
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DateListFragment : Fragment() {
    private var _binding: FragmentDateListBinding? = null
    private val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDateListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ItemToDoListAdapter {
            Log.d("ToDoListFragment", "kliknął ${it.todo_uuid}")
            val action =
                DateListFragmentDirections.actionDateListFragmentToToDoDetailsFragment(it.todo_uuid)
            this.findNavController().navigate(action)
        }
        initCalendar()
        Log.d("DateListFragment", "onViewCreated")
        binding.recyclerViewDate.layoutManager = GridLayoutManager(this.context, 1)
        binding.recyclerViewDate.adapter = adapter
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val mDate: Date = sdf.parse("05/8/2022") as Date;
        Log.d("mDate.time 1", "" + mDate.time + " " + mDate.toString())
        toDoLifeViewModel.loadToDoItems(mDate.time)
        toDoLifeViewModel.todoItemList.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        binding.buttonTodoAdd.setOnClickListener {
            findNavController().navigate(R.id.action_DateListFragment_to_addToDoFragment)
        }
        binding.recyclerViewDate.setHasFixedSize(true)
    }

    private fun initCalendar() {
        binding.apply {
            calendarView.date = Clock.System.now().toEpochMilliseconds()
            calendarView.firstDayOfWeek = 2
        }
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth -> // display the selected date by using a toast
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val miesiac = month + 1
            val mDate: Date = sdf.parse("$dayOfMonth/$miesiac/$year") as Date;
            Log.d("mDate.time", "month" + month + " " + mDate.time + " " + mDate.toString())
            toDoLifeViewModel.loadToDoItems(mDate.time)
            toDoLifeViewModel.todoItemList.observe(this.viewLifecycleOwner) { items ->
                items.let {
                    (binding.recyclerViewDate.adapter as ItemToDoListAdapter).submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}