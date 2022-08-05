package pl.przezdziecki.todolifediary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import pl.przezdziecki.todolifediary.databinding.FragmentTodoListBinding
import java.text.SimpleDateFormat
import java.util.*

class ToDoListFragment : Fragment() {
    private val navigationArgs: ToDoListFragmentArgs by navArgs()
    private  val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action = ToDoListFragmentDirections.actionToDoListFragmentToDateListFragment()
            findNavController().navigate(action)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ItemToDoListAdapter {
            Log.d("ToDoListFragment","kliknął ${it.todo_uuid}")
            //  this.findNavController().navigate(R.id.action_DateListFragment_to_addToDoFragment)
            val action = ToDoListFragmentDirections.actionToDoListFragmentToToDoDetailsFragment(it.todo_uuid)
            this.findNavController().navigate(action)
        }
        val sdfd = SimpleDateFormat("yyyy-MM-dd E", Locale.getDefault())
        binding.dateDay.text=sdfd.format(navigationArgs.dateDay)
        binding.recyclerView.layoutManager = GridLayoutManager(this.context,1)
        binding.recyclerView.adapter=adapter


        Log.d("ToDoListFragment","navigationArgs.dateDay ${navigationArgs.dateDay}")
        toDoLifeViewModel.loadToDoItems(navigationArgs.dateDay)
        toDoLifeViewModel.todoItemList.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        binding.buttonTodoAdd.setOnClickListener {
            val action = ToDoListFragmentDirections.actionToDoListFragmentToAddToDoFragment(navigationArgs.dateDay)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.setHasFixedSize(true)
    }
}