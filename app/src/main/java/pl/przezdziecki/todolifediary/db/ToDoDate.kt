package pl.przezdziecki.todolifediary.db
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * w UTC val currentMoment: Instant = Clock.System.now()
 * https://github.com/Kotlin/kotlinx-datetime#using-in-your-projects
 */
@Entity (tableName = "tododate_table")
data class ToDoDate(
    @PrimaryKey
    @ColumnInfo(name = "dateday")
    @NonNull
    var dateday: Long=0,
    @NonNull
    @ColumnInfo(name = "dateday_s",)
    var dateday_s:String="",
)


