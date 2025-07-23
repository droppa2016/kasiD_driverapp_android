package co.za.kasi.dao


import androidx.room.*
import co.za.kasi.model.superApp.a.waybillData.Waybills

@Dao
interface WaybillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaybills(waybills: List<Waybills>)

    @Query("SELECT * FROM waybills")
    suspend fun getWaybills(): List<Waybills>

    @Query("SELECT * FROM waybills ORDER BY number LIMIT :limit OFFSET :offset")
    suspend fun getWaybillsPaged(limit: Int, offset: Int): List<Waybills>

    @Query("SELECT * FROM waybills WHERE number = :waybillNumber")
    suspend fun getWaybillByNumber(waybillNumber: String): Waybills?

    @Query("""
    SELECT * FROM waybills
    WHERE LOWER(number) LIKE '%' || LOWER(:query) || '%'
       OR LOWER(serviceType) LIKE '%' || LOWER(:query) || '%'
""")
    suspend fun getWaybillByNumberOrServiceType(query: String): List<Waybills>

    @Query("DELETE FROM waybills")
    suspend fun deleteAllWaybills()

    @Query("DELETE FROM waybills WHERE number = :waybillNumber")
    suspend fun deleteWaybillByNumber(waybillNumber: String)
}