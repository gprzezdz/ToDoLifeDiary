package pl.przezdziecki.todolifediary.tags

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import pl.przezdziecki.todolifediary.*
import pl.przezdziecki.todolifediary.databinding.FragmentTagEditBinding


class TagEditFragment : Fragment() {

    private var _binding: FragmentTagEditBinding? = null
    private val navigationArgs: TagEditFragmentArgs by navArgs()
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
        _binding = FragmentTagEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBinding()
        formBinding()
    }



    private fun formBinding() {
        binding.apply {
            stag.setText(navigationArgs.tag.sTag)
            utag.text = navigationArgs.tag.uTag
            tagDescription.setText(navigationArgs.tag.description)
        }
    }


    private fun btnBinding() {
        binding.apply {
            btnCancel.setOnClickListener {
                findNavController().navigateUp()
            }
            if (navigationArgs.actionType == "ADD") {
                btnDelete.visibility = View.INVISIBLE
                btnSave.text = "Save"
                btnSave.setOnClickListener {
                    if (!stag.toString().trim().isEmpty()) {
                        saveTag()
                        findNavController().navigateUp()
                    }
                }
            }
            btnDelete.setOnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                builder.setTitle("Delete tag")
                builder.setMessage("Are you want delete this tag")
                builder.setPositiveButton("Yes") { dialog, _ ->
                    deleteTag()
                    findNavController().navigateUp()
                    dialog.cancel()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()

            }
            if (navigationArgs.actionType == "EDIT") {
                btnDelete.visibility = View.VISIBLE
                btnSave.text = "Update"
                btnSave.setOnClickListener {
                    if (!stag.toString().trim().isEmpty()) {
                        saveTag()
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun deleteTag() {
        toDoLifeViewModel.deleteTag(navigationArgs.tag)
    }

    private fun saveTag() {

        var tag = navigationArgs.tag
        binding.apply {
            tag.sTag = stag.text.toString()
            tag.uTag = tag.sTag.uppercase()
            tag.description = tagDescription.text.toString()
        }
        toDoLifeViewModel.saveTag(tag)
    }
}