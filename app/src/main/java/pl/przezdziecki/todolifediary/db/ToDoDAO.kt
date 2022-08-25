package pl.przezdziecki.todolifediary.db


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface ToDoDAO {

    @Query("SELECT * from tododate_table ORDER BY dateday desc")
    fun getDateItems(): Flow<List<ToDoDate>>

    @Query("SELECT * from todoitem_table WHERE dateday = :id order by start_date_time asc ")
    fun getToDoItemByDate(id: Long): Flow<List<ToDoItem>>

    @Query("SELECT * from todoitem_table WHERE (dateday < :id and close_date_time=0) or dateday = :id " +
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

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDateItem(item: ToDoDate)

    @Update
    suspend fun updateDateItem(item: ToDoDate)

    @Delete
    suspend fun deleteDateItem(item: ToDoDate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDoItem(item: ToDoItem)

    @Delete
    suspend fun deleteToDoItem(item: ToDoItem)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDoItemComment(item: ToDoComment)

    @Delete
    suspend fun deleteToDoItemComment(item: ToDoComment)

    @Query("UPDATE todoitem_table set close_date_time=:closeDateTime WHERE todo_uuid = :todoUuid")
    suspend fun closeToDo(todoUuid: UUID,closeDateTime:Long)

}