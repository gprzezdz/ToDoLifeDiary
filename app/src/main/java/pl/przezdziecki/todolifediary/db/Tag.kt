package pl.przezdziecki.todolifediary.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "tag_table",
    indices = [Index(value = ["stag"], unique = true),Index(value = ["utag"], unique = true)]
)
data class Tag(
    @PrimaryKey
    @ColumnInfo(name = "tag_uuid")
    @NonNull
    var tagUuid: UUID,

    @ColumnInfo(name = "stag")
    @NonNull
    var sTag: String ,

    @ColumnInfo(name = "utag")
    @NonNull
    var uTag: String ,

    @ColumnInfo(name = "description")
    @NonNull
    var description: String = "",

    @NonNull
    @ColumnInfo(name = "insert_date_time")
    var insertDateTime:Long,
): Serializable