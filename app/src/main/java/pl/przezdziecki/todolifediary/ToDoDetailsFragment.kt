package pl.przezdziecki.todolifediary

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.datetime.Clock
import pl.przezdziecki.todolifediary.data.ToDoPhotoEx
import pl.przezdziecki.todolifediary.databinding.FragmentTodoDetailsBinding
import pl.przezdziecki.todolifediary.db.ToDoComment
import pl.przezdziecki.todolifediary.db.ToDoItem
import java.io.File
import java.nio.file.Files.createFile
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path

private var TAG: String = "ToDoDetailsFragment"

class ToDoDetailsFragment : Fragment() {
    private lateinit var toDoPhotoEx: ToDoPhotoEx
    private var uri: Uri? = null
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
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToDateListFragment()
            findNavController().navigate(action)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ToDoDetailsFragment", "onViewCreated")

        val adapter = ItemCommentListAdapter {
            Log.d(TAG, "kliknął ${it.commentUuid}")
            if(it.fileType=="JPG")
            {
               val toDoPhotoEx = ToDoPhotoEx()
                toDoPhotoEx.todoComment=it
                val date =
                    SimpleDateFormat("yyyy-MM-dd").format(Date(it.insertDateTime))
                val imageDirectory  = binding.root.context.getExternalFilesDir("Photos").toString() + "/"+date+"/"+it.commentUuid.toString() +".jpg"
                Log.d(TAG,"imageDirectoryURI: ${Uri.parse(imageDirectory)}")
                toDoPhotoEx.photoUriString= Uri.parse(imageDirectory).toString()
                val action =
                    ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToCommentPhotoFragment(
                        toDoPhotoEx,
                        "EDITPHOTO"
                    )
                this.findNavController().navigate(action)
            }else {
                val action =
                    ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToCommentFragment(
                        it,
                        "EDIT"
                    )
                this.findNavController().navigate(action)
            }
        }

        Log.d("DateListFragment", "onViewCreated")
        binding.recyclerComments.layoutManager = GridLayoutManager(this.context, 1)
        binding.recyclerComments.adapter = adapter
        toDoLifeViewModel.getToDoItemComments(navigationArgs.todoUuid)
            .observe(this.viewLifecycleOwner) { items ->
                items.let {
                    adapter.submitList(it)
                }
            }

        toDoLifeViewModel.loadToDoItem(navigationArgs.todoUuid)
            .observe(this.viewLifecycleOwner) { selectedItem ->
                itemToDo = selectedItem
                bind(itemToDo)
            }
        binding.buttonAddComment.setOnClickListener {
            var actionClose = "ADD"
            var todoComment: ToDoComment = ToDoComment(
                UUID.randomUUID(),
                itemToDo.todo_uuid,
                "",
                0.0,
                Clock.System.now().toEpochMilliseconds(),
                Clock.System.now().toEpochMilliseconds()
            )
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToCommentFragment(
                todoComment,
                actionClose
            )
            this.findNavController().navigate(action)
        }
        binding.buttonTodoAdd.setOnClickListener {
            val action =
                ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToAddToDoFragment(itemToDo.dateday)
            this.findNavController().navigate(action)
        }
        binding.buttonAddPhoto.setOnClickListener {
            takePhoto()
        }
        binding.buttonClose.setOnClickListener {
            var todoComment: ToDoComment
            var actionClose: String
            if (itemToDo.closeDateTime == 0L) {
                //toto tutaj trzeba  zgłoszenie zamknąć
                actionClose = "CLOSE"
                todoComment = ToDoComment(
                    UUID.randomUUID(),
                    itemToDo.todo_uuid,
                    "",
                    0.0,
                    Clock.System.now().toEpochMilliseconds(),
                    Clock.System.now().toEpochMilliseconds()
                )
            } else {
                actionClose = "OPEN"
                todoComment = ToDoComment(
                    UUID.randomUUID(),
                    itemToDo.todo_uuid,
                    "",
                    0.0,
                    Clock.System.now().toEpochMilliseconds(),
                    Clock.System.now().toEpochMilliseconds()
                )
                // a tutaj otworzyć bo jest data zamknięcia
            }
            val action = ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToCommentFragment(
                todoComment,
                actionClose
            )
            this.findNavController().navigate(action)
        }
        binding.buttonEdit.setOnClickListener {
            val action =
                ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToToDoEditFragment(itemToDo.todo_uuid)
            this.findNavController().navigate(action)
        }
    }

    private fun takePhoto() {
        if (hasCameraPermission() == PERMISSION_GRANTED && hasExternalStoragePermission() == PERMISSION_GRANTED) {
            Log.i(TAG, "Permision to takePhoto granted")
            invokeCamera()
        } else {
            requestMultipermissionsLuncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun invokeCamera() {
        toDoPhotoEx = ToDoPhotoEx()
        toDoPhotoEx.todoComment = ToDoComment(
            UUID.randomUUID(),
            itemToDo.todo_uuid,
            "",
            0.0,
            Clock.System.now().toEpochMilliseconds(),
            Clock.System.now().toEpochMilliseconds()
        )

        val file = createImageFile(toDoPhotoEx)
        try {
            uri = FileProvider.getUriForFile(
                context!!,
                "pl.przezdziecki.todolifediary.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error ${e.message}")
            var foo = e.message
        }
        getCameraImage.launch(uri)
    }

    private val getCameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                toDoPhotoEx.photoUriString = uri.toString()
                //crate photo object and open photo add fragment
                Log.i(TAG, "Photo location ${uri}")
                val action =
                    ToDoDetailsFragmentDirections.actionToDoDetailsFragmentToCommentPhotoFragment(
                        toDoPhotoEx,
                        "ADDPHOTO"
                    )
                this.findNavController().navigate(action)
            } else {
                Log.e(TAG, "Photo not saved ${uri}")
            }
        }

    private val requestMultipermissionsLuncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { resultsMap ->
            var permisionGranted = false
            resultsMap.forEach {
                if (it.value == true) {
                    permisionGranted = it.value
                } else {
                    permisionGranted = false
                    return@forEach
                }
            }
            if (permisionGranted) {
                invokeCamera()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.photo_camera_permissions),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun createImageFile(todophoto: ToDoPhotoEx): File {
        val date =
            SimpleDateFormat("yyyy-MM-dd").format(Date(todophoto.todoComment!!.insertDateTime))
        val imageDirectory = context!!.getExternalFilesDir("Photos")

        val f=File("${imageDirectory}/${date}")
        f.mkdirs()
        Log.i(TAG, "imagedirectory ${imageDirectory}")
        return createFile(Path("${imageDirectory}/${date}/${todophoto.todoComment!!.commentUuid.toString()}.jpg"))
            .toFile()
    }


    fun hasCameraPermission() =
        context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }

    fun hasExternalStoragePermission() = context?.let {
        ContextCompat.checkSelfPermission(
            it,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
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
            if (toDoItem.closeDateTime > 0L) {
                todofulldate.text =
                    toDoLifeViewModel.getFormattedDateTimeE(toDoItem.startDateTime) + " <--->" + toDoLifeViewModel.getFormattedDateTimeE(
                        toDoItem.closeDateTime
                    )
            } else {
                todofulldate.text = toDoLifeViewModel.getFormattedDateTimeE(toDoItem.startDateTime)

            }
            title.text = toDoItem.title
            description.text = toDoItem.description
        }
        if (toDoItem.closeDateTime > 0) {
            binding.buttonClose.text = "Reopen"
        }
    }
}


