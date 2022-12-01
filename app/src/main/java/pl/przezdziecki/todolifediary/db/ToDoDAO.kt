package pl.przezdziecki.todolifediary.db


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface ToDoDAO {

    @Query("SELECT * from tag_table order by utag ")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * from todoitem_table WHERE dateday = :id order by start_date_time asc ")
    fun getToDoItemByDate(id: Long): Flow<List<ToDoItem>>

    @Query("SELECT * from todoitem_table WHERE (dateday < :id and close_date_time=0) or dateday = :id or   close_date_time=:id" +
            " or todo_uuid in (select todo_uuid from todocomment_table where com_date_time>=:id and com_date_time<=(:id +24*60*60*1000  ))" +
            " order by start_date_time asc ")
    fun getDay(id: Long): Flow<List<ToDoItem>>

    @Query("SELECT * from todoitem_table  WHERE (dateday >= (:id +24*60*60*1000  ) and dateday <= (:id +24*60*60*1000*7  )) or (dateday >= :id and todo_type='WEEK' )" +
            " order by start_date_time asc ")
    fun getWeek(id: Long): Flow<List<ToDoItem>>

    @Query("SELECT * from todoitem_table  WHERE  (dateday >= :id and todo_type='MONTH' ) " +
            " order by start_date_time asc ")
    fun getMonth(id: Long): Flow<List<ToDoItem>>

    @Query("SELECT * from todoitem_table  WHERE (dateday >= :id and todo_type='YEAR') " +
            " order by start_date_time asc ")
    fun getYear(id: Long): Flow<List<ToDoItem>>

    @Query("SELECT * from todoitem_table WHERE todo_uuid = :id ")
     fun getToDoItem(id: UUID):  Flow<ToDoItem>

    @Query("SELECT * from todocomment_table WHERE todo_uuid = :id order by com_date_time desc")
    fun getToDoItemComments(id: UUID):  Flow<List<ToDoComment>>

    @Query("SELECT * from todocomment_table WHERE comment_uuid = :id ")
    fun getToDoItemComment(id: UUID):    Flow<ToDoComment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(item: Tag)
    @Delete
    suspend fun deleteTag(item: Tag)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTag(item: Tag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDoTagRel(item: ToDoTagRel)

    @Query("SELECT * from tag_table WHERE utag in(select utag from todo_tag_rel where todo_uuid = :id) order by utag asc")
    fun getToDoItemTags(id: UUID):  Flow<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDoItem(item: ToDoItem)

    @Delete
    suspend fun deleteToDoItem(item: ToDoItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateToDoItem(item: ToDoItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDoItemComment(item: ToDoComment)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateToDoItemComment(item: ToDoComment)
    @Delete
    suspend fun deleteToDoItemComment(item: ToDoComment)

    @Query("UPDATE todoitem_table set close_date_time=:closeDateTime WHERE todo_uuid = :todoUuid")
    suspend fun closeToDo(todoUuid: UUID,closeDateTime:Long)

    @Query("delete  from todo_tag_rel where todo_uuid = :todoUuid")
    suspend fun deleteAllToDoTagRel(todoUuid: UUID)
}