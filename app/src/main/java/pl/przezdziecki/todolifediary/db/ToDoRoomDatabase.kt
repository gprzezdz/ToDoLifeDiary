/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.przezdziecki.todolifediary.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(
    entities = [ ToDoItem::class, ToDoComment::class,Tag::class],
    version = 9,
    exportSchema = false
)
abstract class ToDoRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): ToDoDAO

    companion object {
        @Volatile
        private var INSTANCE: ToDoRoomDatabase? = null

        fun getDatabase(context: Context): ToDoRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_6_8 = object : Migration(6,8) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("drop table tododate_table")
                    }
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoRoomDatabase::class.java,
                    "todo_database"
                )
                  //  .addMigrations(MIGRATION_4_5)
                //    .addMigrations(MIGRATION_5_6)
                    .build()
                Log.d("ToDoRoomDatabase", "instancje null")
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}