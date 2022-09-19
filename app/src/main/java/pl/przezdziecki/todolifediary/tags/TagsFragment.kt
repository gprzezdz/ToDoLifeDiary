package pl.przezdziecki.todolifediary.tags

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.datetime.Clock
import pl.przezdziecki.todolifediary.*
import pl.przezdziecki.todolifediary.databinding.FragmentHomeBinding
import pl.przezdziecki.todolifediary.databinding.FragmentTagsBinding
import pl.przezdziecki.todolifediary.db.Tag
import pl.przezdziecki.todolifediary.db.ToDoItem
import java.util.*

private var TAG: String = "TagsFragment"

class TagsFragment : Fragment() {
    private var _binding: FragmentTagsBinding? = null
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
        _binding = FragmentTagsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonTagAdd.setOnClickListener {
        val tag:Tag=Tag("","","", Clock.System.now().toEpochMilliseconds())

            val action =
                TagsFragmentDirections.actionTagsFragmentToTagEditFragment(tag,"ADD")
            this.findNavController().navigate(action)
        }
        val adapter = ItemTagListAdapter {
            Log.d(TAG, "kliknął ${it.sTag}")
            val action =
             TagsFragmentDirections.actionTagsFragmentToTagEditFragment(it,"EDIT")
            this.findNavController().navigate(action)
        }

        Log.d(TAG, "onViewCreated")
        binding.recyclerViewDate.layoutManager = GridLayoutManager(this.context,1)
        binding.recyclerViewDate.adapter = adapter
        toDoLifeViewModel.tagList=toDoLifeViewModel.loadTags()
        toDoLifeViewModel.tagList.observe(this.viewLifecycleOwner) { items ->
            items.let {
                (binding.recyclerViewDate.adapter as ItemTagListAdapter).submitList(it)
            }
        }
        binding.recyclerViewDate.setHasFixedSize(true)
    }
}