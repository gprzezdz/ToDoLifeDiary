package pl.przezdziecki.todolifediary


import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import pl.przezdziecki.todolifediary.db.*
import java.text.SimpleDateFormat
import java.util.*

class ToDoLifeViewModel(private val itemDao: ToDoDAO) : ViewModel() {


    var currentDateDay: Long = 0L
    var currentHomeButton: String = "DAY"
    var todoItemList: LiveData<List<ToDoItem>> = itemDao.getToDoItemByDate(-1).asLiveData()

    var tagList: LiveData<List<Tag>> = itemDao.getAllTags().asLiveData()

    fun getDay(currentDateDay: Long): LiveData<List<ToDoItem>> {
        return itemDao.getDay(currentDateDay).asLiveData()
    }

    fun getWeek(currentDateDay: Long): LiveData<List<ToDoItem>> {
        return itemDao.getWeek(currentDateDay).asLiveData()
    }

    fun getMonth(currentDateDay: Long): LiveData<List<ToDoItem>> {
        return itemDao.getMonth(currentDateDay).asLiveData()
    }

    fun getYear(currentDateDay: Long): LiveData<List<ToDoItem>> {
        return itemDao.getYear(currentDateDay).asLiveData()
    }

    fun saveTag(tag: Tag) {
        viewModelScope.launch { itemDao.insertTag(tag) }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch { itemDao.deleteTag(tag) }
    }

    fun insertToDoItem(toDoItem: ToDoItem) {
        viewModelScope.launch { itemDao.insertToDoItem(toDoItem) }
    }

    fun deleteToDoItem(toDoItem: ToDoItem) {
        viewModelScope.launch { itemDao.deleteToDoItem(toDoItem) }
    }

    fun saveToDoItem(toDoItem: ToDoItem) {
        viewModelScope.launch { itemDao.insertToDoItem(toDoItem) }
    }

    fun saveToDoComment(toDoComment: ToDoComment) {
        viewModelScope.launch { itemDao.insertToDoItemComment(toDoComment) }
    }

    fun deleteToDoComment(toDoComment: ToDoComment) {
        viewModelScope.launch { itemDao.deleteToDoItemComment(toDoComment) }
    }

    fun loadToDoItems(time: Long): LiveData<List<ToDoItem>> {
        return itemDao.getToDoItemByDate(time).asLiveData()
    }

    fun getToDoItemComments(todoUuid: UUID): LiveData<List<ToDoComment>> {
        return itemDao.getToDoItemComments(todoUuid).asLiveData()
    }

    fun loadToDoItem(todo_uuid: UUID): LiveData<ToDoItem> {
        return itemDao.getToDoItem(todo_uuid).asLiveData()
    }

    fun getFormattedDateTimeE(startDateTime: Long): String {
        return SimpleDateFormat("yyyy-MM-dd E HH:mm", Locale.getDefault()).format(startDateTime)
    }

    fun getFormattedDateE(startDateTime: Long): String {
        return SimpleDateFormat("yyyy-MM-dd E", Locale.getDefault()).format(startDateTime)
    }

    fun getFormattedTime(startDateTime: Long): CharSequence? {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(startDateTime)
    }

    fun closeToDo(todoUuid: UUID, closeDateTime: Long) {
        viewModelScope.launch { itemDao.closeToDo(todoUuid, closeDateTime) }
    }

    fun loadTags(): LiveData<List<Tag>> {
        return itemDao.getAllTags().asLiveData()
    }
    class ToDoLifeViewModelFactory(private val itemDao: ToDoDAO) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            Log.d("ToDoLifeViewModel", "Init da ToDoLifeViewModelFactory")
            if (modelClass.isAssignableFrom(ToDoLifeViewModel::class.java)) {
                Log.d("ToDoLifeViewModel", "Init da modelClass.isAssignable")
                @Suppress("UNCHECKED_CAST")
                return ToDoLifeViewModel(itemDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}