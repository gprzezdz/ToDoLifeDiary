package pl.przezdziecki.todolifediary.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "todoitem_table")
data class ToDoItem(
    @PrimaryKey
    @ColumnInfo(name = "todo_uuid")
    @NonNull
    var todo_uuid:UUID,

    @ColumnInfo(name = "dateday")
    @NonNull
    var dateday: Long=0,

    @NonNull
    @ColumnInfo(name = "title")
    var title :String="",

    @NonNull
    @ColumnInfo(name = "description")
    var description :String="",
    @NonNull
    @ColumnInfo(name = "start_date_time")
    var startDateTime :Long,

    @NonNull
    @ColumnInfo(name = "close_date_time")
    var closeDateTime :Long=0,

    @NonNull
    @ColumnInfo(name = "insert_date_time")
    var insertDateTime:Long=0,

    @NonNull
    @ColumnInfo(name = "todo_type")
    var todoType :String="DAY",

    )

fun ToDoItem.getFormattedTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(startDateTime)
}
fun ToDoItem.getFormattedDateTime(): String {
    return SimpleDateFormat("yyyy-MM-dd E HH:mm", Locale.getDefault()).format(startDateTime)
}