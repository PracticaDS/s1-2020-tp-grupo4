package ar.edu.unq.pdes.myprivateblog.data

import android.content.Context
import android.graphics.Color
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.Serializable

typealias EntityID = Int

@Database(entities = [BlogEntry::class], version = 3)
@TypeConverters(ThreeTenTimeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun blogEntriesDao(): BlogEntriesDao

    companion object {
        fun generateDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "myprivateblog.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

}

@Entity(
    tableName = "BlogEntries"
)
data class BlogEntry(

    @PrimaryKey(autoGenerate = true)
    var uid: EntityID = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "bodyPath")
    val bodyPath: String? = null,

    @ColumnInfo(name = "imagePath")
    val imagePath: String? = null,

    @ColumnInfo(name = "is_deleted")
    val deleted: Boolean = false,

    @ColumnInfo(name = "is_synced")
    var synced: Boolean = false,

    @ColumnInfo(name = "salt", typeAffinity = ColumnInfo.BLOB)
    var salt: ByteArray? = null,

    @ColumnInfo(name = "date")
    val date: OffsetDateTime? = null,

    @ColumnInfo(name = "cardColor")
    val cardColor: Int = Color.WHITE

) : Serializable

@Dao
interface BlogEntriesDao {

    @Query("SELECT * FROM BlogEntries ORDER BY date DESC")
    fun getAll(): Flowable<List<BlogEntry>>

    // * We put a guard in case that a user has data from a previous version
    // where the entry does not have the is_synced column.
    @Query("""
        SELECT * FROM BlogEntries
        WHERE (:deleted IS NULL OR is_deleted = :deleted)
        AND (:synced IS NULL OR is_synced = :synced)
        ORDER BY date DESC
    """)
    fun getAll(
        deleted: Boolean? = null,
        synced:  Boolean? = null
    ): Flowable<List<BlogEntry>>

    @Query("SELECT * FROM BlogEntries WHERE uid = :entryId LIMIT 1")
    fun loadById(entryId: EntityID): Flowable<BlogEntry>

    @Insert
    fun insertAll(entries: List<BlogEntry>): Completable

    @Insert
    fun insert(entries: BlogEntry): Single<Long>

    @Query("SELECT COUNT(*) FROM BlogEntries WHERE is_deleted = :deleted")
    fun getDataCount(deleted: Boolean): Int

    @Update
    fun updateAll(entries: List<BlogEntry>): Completable

    @Update
    fun update(entry: BlogEntry): Completable

    @Delete
    fun delete(entry: BlogEntry): Completable
}

object ThreeTenTimeTypeConverters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
}