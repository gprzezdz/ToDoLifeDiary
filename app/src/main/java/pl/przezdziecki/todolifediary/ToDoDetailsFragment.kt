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
import kotlinx.datetime.Clock
import pl.przezdziecki.todolifediary.databinding.FragmentTodoDetailsBinding
import pl.przezdziecki.todolifediary.db.ToDoComment
import pl.przezdziecki.todolifediary.db.ToDoItem
import pl.przezdziecki.todolifediary.db.getFormattedDateTime
import java.util.*

class ToDoDetailsFragment : Fragment() {
    private var _binding: FragmentTodoDetailsBinding? = null
    private val navigationArgs: ToDoDetailsFragmentArgs by navArgs()
    private val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }
    private val binding get() = _binding!!
    lateinit var itemToDo: ToDoItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToDateListFragment()
           findNavController().navigate(action)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ToDoDetailsFragment", "onViewCreated")

        val adapter = ItemCommentListAdapter {
            Log.d("ToDoDetailsFragment","kliknął ${it.commentUuid}")
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToCommentFragment(it,"EDIT")
            this.findNavController().navigate(action)
        }

        Log.d("DateListFragment","onViewCreated")
        binding.recyclerComments.layoutManager = GridLayoutManager(this.context,1)
        binding.recyclerComments.adapter=adapter
        toDoLifeViewModel.getToDoItemComments(navigationArgs.todoUuid).observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        toDoLifeViewModel.loadToDoItem(navigationArgs.todoUuid).observe(this.viewLifecycleOwner) { selectedItem ->
            itemToDo = selectedItem
            bind(itemToDo)
        }
        binding.buttonAddComment.setOnClickListener {
            var actionClose ="ADD"
            var todoComment:ToDoComment = ToDoComment(UUID.randomUUID(), itemToDo.todo_uuid,"",0.0, Clock.System.now().toEpochMilliseconds(),Clock.System.now().toEpochMilliseconds())
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToCommentFragment(todoComment,actionClose)
            this.findNavController().navigate(action)
        }
        binding.buttonTodoAdd.setOnClickListener {
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToAddToDoFragment(itemToDo.dateday)
            this.findNavController().navigate(action)
        }
        binding.buttonClose.setOnClickListener {
            var todoComment:ToDoComment
            var actionClose: String
            if(itemToDo.closeDateTime==0L)
            {
                //toto tutaj trzeba  zgłoszenie zamknąć
                actionClose="CLOSE"
                todoComment= ToDoComment(UUID.randomUUID(), itemToDo.todo_uuid,"",0.0,Clock.System.now().toEpochMilliseconds(), Clock.System.now().toEpochMilliseconds())
            }else
            {
                actionClose="OPEN"
                todoComment= ToDoComment(UUID.randomUUID(), itemToDo.todo_uuid,"",0.0, Clock.System.now().toEpochMilliseconds(),Clock.System.now().toEpochMilliseconds())
                // a tutaj otworzyć bo jest data zamknięcia
            }
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToCommentFragment(todoComment,actionClose)
            this.findNavController().navigate(action)
        }
        binding.buttonEdit.setOnClickListener {
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToToDoEditFragment(itemToDo.todo_uuid)
            this.findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTodoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun bind(toDoItem: ToDoItem) {
        binding.apply {
          if(toDoItem.closeDateTime>0L) {
              todofulldate.text = toDoLifeViewModel.getFormattedDateTimeE(toDoItem.startDateTime)              +" <--->"+toDoLifeViewModel.getFormattedDateTimeE(toDoItem.closeDateTime)
          }else
          {
              todofulldate.text = toDoLifeViewModel.getFormattedDateTimeE(toDoItem.startDateTime)

          }
            title.text=toDoItem.title
            description.text=toDoItem.description
        }
        if(toDoItem.closeDateTime>0)
        {
            binding.buttonClose.text="Reopen"
        }
    }
}


