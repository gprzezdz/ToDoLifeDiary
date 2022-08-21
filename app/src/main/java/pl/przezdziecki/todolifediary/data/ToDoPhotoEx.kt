package pl.przezdziecki.todolifediary.data

import pl.przezdziecki.todolifediary.db.ToDoComment
import java.io.Serializable

data class ToDoPhotoEx(
     var todoComment: ToDoComment?,
     var photoUriString: String?
) : Serializable {
     constructor() : this(null,null)
}