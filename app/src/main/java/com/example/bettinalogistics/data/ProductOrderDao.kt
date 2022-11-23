package com.example.bettinalogistics.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bettinalogistics.model.Product

@Dao
interface ProductOrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Product)

    // below is the delete method
    // for deleting our note.
    @Query("DELETE FROM MyModel")
    open fun delete()

    // below is the method to read all the notes
    // from our database we have specified the query for it.
    // inside the query we are arranging it in ascending
    // order on below line and we are specifying
    // the table name from which
    // we have to get the data.
    @Query("Select * from notesTable order by id ASC")
    fun getAllNotes(): LiveData<List<Note>>

    // below method is use to update the note.
    @Update
    suspend fun update(note: Note)
}