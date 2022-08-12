package pl.przezdziecki.todolifediary

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


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private var currentDay:Long = 0
    private val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }
    init {
        val now: Instant = Clock.System.now()
        val today: LocalDate = now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val mDate: Date = sdf.parse(today.toString()) as Date;
        currentDay=mDate.time
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
            Log.d("HomeFragment", "kliknął ${it.todo_uuid}")
            val action =
                HomeFragmentDirections.actionHomeFragmentToToDoDetailsFragment(it.todo_uuid)
            this.findNavController().navigate(action)
        }

        Log.d("HomeFragment", "onViewCreated")
        binding.recyclerViewDate.layoutManager = GridLayoutManager(this.context, 1)
        binding.recyclerViewDate.adapter = adapter
        toDoLifeViewModel.todoItemList= toDoLifeViewModel.getToDoItemTodayAndNotClosed(  currentDay)
        toDoLifeViewModel.todoItemList.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        binding.recyclerViewDate.setHasFixedSize(true)
    }

}