package pl.przezdziecki.todolifediary

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import pl.przezdziecki.todolifediary.data.ToDoPhotoEx
import pl.przezdziecki.todolifediary.databinding.FragmentCommentPhotoBinding
import java.io.File


private var TAG: String = "CommentPhotoFragment"

class CommentPhotoFragment : Fragment() {
    private var _binding: FragmentCommentPhotoBinding? = null
    private val navigationArgs: CommentPhotoFragmentArgs by navArgs()
    private val binding get() = _binding!!
    lateinit var toDoPhotoEx: ToDoPhotoEx
    private val toDoLifeViewModel: ToDoLifeViewModel by activityViewModels {
        ToDoLifeViewModel.ToDoLifeViewModelFactory(
            (activity?.application as ToDoLiveDiaryApplication).database.itemDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toDoPhotoEx = navigationArgs.todoPhotoEx
        bind(toDoPhotoEx)
        if (navigationArgs.actionClose == "EDITPHOTO") {
            binding.buttonDeletePhoto.visibility = View.VISIBLE
            binding.buttonDeletePhoto.setOnClickListener {

                val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                builder.setTitle("Delete  photo")
                builder.setMessage("Are you want delete this photo")
                builder.setPositiveButton("Yes") { dialog, _ ->
                    toDoLifeViewModel.deleteToDoComment(toDoPhotoEx.todoComment!!)
                    val f = File(Uri.parse(toDoPhotoEx.photoUriString).path)
                    Log.d(TAG, "Delete file ${f.absolutePath}")
                    f.canonicalFile.delete()
                    Log.d(TAG, "File deleted")
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            }
        }
        binding.buttonCancelPhoto.setOnClickListener {
            if (navigationArgs.actionClose == "ADDPHOTO") {
                val f = File(Uri.parse(toDoPhotoEx.photoUriString).path)
                Log.d(TAG, "Delete file ${f.absolutePath}")
                f.canonicalFile.delete()
                Log.d(TAG, "File deleted")
            }
            findNavController().navigateUp()
        }
        binding.buttonSavePhoto.setOnClickListener {
            if (navigationArgs.actionClose == "EDITPHOTO") {
                savePhoto()
            }
            if (navigationArgs.actionClose == "ADDPHOTO" ) {
                insertPhoto()
            }

            findNavController().navigateUp()
        }
        setFullImage()
    }

    private fun setFullImage() {
        binding.photoId.setOnClickListener {
            val file = File(Uri.parse(toDoPhotoEx.photoUriString).path)
            val uri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID+".provider",
                file
            )
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            Log.d(TAG, "File image to open ${uri}")
            intent.setDataAndType(uri, "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent)
        }
    }
    private fun insertPhoto() {
        binding.apply {
            toDoPhotoEx.todoComment!!.comment = commentDescription.text.toString()
        }
        toDoPhotoEx.todoComment!!.fileType = "JPG"
        Log.d(TAG, "insert photo: ${toDoPhotoEx.todoComment!!.fileType}")
        toDoLifeViewModel.insertToDoComment(toDoPhotoEx.todoComment!!)
    }
    private fun savePhoto() {
        binding.apply {
            toDoPhotoEx.todoComment!!.comment = commentDescription.text.toString()
        }
        toDoPhotoEx.todoComment!!.fileType = "JPG"
        Log.d(TAG, "save photo: ${toDoPhotoEx.todoComment!!.fileType}")
        toDoLifeViewModel.saveToDoComment(toDoPhotoEx.todoComment!!)
    }

    private fun bind(toDoPhotoEx: ToDoPhotoEx) {
        binding.apply {
            photoId.setImageURI(Uri.parse(toDoPhotoEx.photoUriString))
            commentDescription.setText(toDoPhotoEx.todoComment!!.comment)
        }
    }
}