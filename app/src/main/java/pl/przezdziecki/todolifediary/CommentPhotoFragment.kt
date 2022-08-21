package pl.przezdziecki.todolifediary

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import pl.przezdziecki.todolifediary.data.ToDoPhotoEx
import pl.przezdziecki.todolifediary.databinding.FragmentCommentPhotoBinding
import java.io.File


private var TAG: String = "CommentPhotoFragment"

class CommentPhotoFragment : Fragment() {
    private var zoomOut: Boolean = false
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

                val  builder: AlertDialog.Builder =AlertDialog.Builder(activity)
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
                val alertDialog:AlertDialog =builder.create()
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
            if (navigationArgs.actionClose == "ADDPHOTO" || navigationArgs.actionClose == "EDITPHOTO") {
                savePhoto()
            }

            findNavController().navigateUp()
        }
        setFullImage()
    }

    private fun setFullImage() {
        binding.photoId.setOnClickListener {

            val img: ImageView = ImageView(context)
            with(img) {
                setImageBitmap(binding.photoId.drawable.toBitmap())
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER;
            }
            val root = RelativeLayout(activity)
            root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            val dialog = Dialog(context!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(img)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            img.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
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