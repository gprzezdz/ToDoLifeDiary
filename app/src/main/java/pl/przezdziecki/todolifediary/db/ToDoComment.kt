package pl.przezdziecki.todolifediary.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(
    tableName = "todocomment_table",
    foreignKeys = [ForeignKey(
        entity = ToDoItem::class,
        childColumns = ["todo_uuid"],
        parentColumns = ["todo_uuid"],
        onDelete = CASCADE
    )]
)
data class ToDoComment(
    @PrimaryKey
    @ColumnInfo(name = "comment_uuid")
    @NonNull
    var commentUuid: UUID,
    @ColumnInfo(name = "todo_uuid")
    @NonNull
    var todoUuid: UUID,
    @ColumnInfo(name = "comment")
    @NonNull
    var comment: String = "",
    @ColumnInfo(name = "cost")
    @NonNull
    var cost: Double,
    @NonNull
    @ColumnInfo(name = "com_date_time")
    var comDateTime: Long,
    @NonNull
    @ColumnInfo(name = "insert_date_time")
    var insertDateTime: Long,
    @NonNull
    @ColumnInfo(name = "file_type")
    var fileType: String="",
) : Serializable

fun ToDoComment.getFormattedDateTime(): String {
    return SimpleDateFormat("yyyy-MM-dd E HH:mm", Locale.getDefault()).format(comDateTime)
}