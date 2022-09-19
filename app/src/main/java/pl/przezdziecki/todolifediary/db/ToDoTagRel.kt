package pl.przezdziecki.todolifediary.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "todo_tag_rel",
    foreignKeys = [ForeignKey(
        entity = ToDoItem::class,
        childColumns = ["todo_uuid"],
        parentColumns = ["todo_uuid"],
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys= ["todo_uuid","utag"],
    indices = [
        Index(value = ["todo_uuid"], unique = false),
        Index(value = ["utag"], unique = false)
    ]
)
data class ToDoTagRel(
    @ColumnInfo(name = "todo_uuid")
    @NonNull
    var todoUuid: UUID,
    @ColumnInfo(name = "utag")
    @NonNull
    var uTag: String ,
    @NonNull
    @ColumnInfo(name = "insert_date_time")
    var insertDateTime:Long,
): Serializable
