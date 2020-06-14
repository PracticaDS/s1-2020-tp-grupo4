package ar.edu.unq.pdes.myprivateblog.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            ALTER TABLE BlogEntries
            ADD COLUMN is_synced INTEGER DEFAULT 0 NOT NULL
        """)
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            ALTER TABLE BlogEntries
            ADD COLUMN salt BLOB
        """)
    }
}