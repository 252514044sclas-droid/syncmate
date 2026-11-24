package com.example.syncmate.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HousingDao {

    @Query("SELECT * FROM housing_listings")
    fun getAllListings(): Flow<List<HousingListingEntity>>

    @Query("SELECT * FROM housing_listings WHERE isSaved = 1")
    fun getSavedListings(): Flow<List<HousingListingEntity>>

    @Query("SELECT * FROM housing_listings WHERE id = :id")
    fun getListingById(id: String): HousingListingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListings(listings: List<HousingListingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListing(listing: HousingListingEntity)

    @Query("UPDATE housing_listings SET isSaved = :isSaved WHERE id = :id")
    fun updateListingSaveStatus(id: String, isSaved: Boolean)

    @Query("DELETE FROM housing_listings")
    fun clearAllListings()
}
