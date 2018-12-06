package com.google.android.gms.samples.vision.ocrreader;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class AppDatabase_Impl extends AppDatabase {
  private volatile AllergyDao _allergyDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `allergy` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `allergy_name` TEXT)");
        _db.execSQL("CREATE UNIQUE INDEX `index_allergy_allergy_name` ON `allergy` (`allergy_name`)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"b3250417251e814455fc3a624e15524a\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `allergy`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsAllergy = new HashMap<String, TableInfo.Column>(2);
        _columnsAllergy.put("id", new TableInfo.Column("id", "INTEGER", true, 1));
        _columnsAllergy.put("allergy_name", new TableInfo.Column("allergy_name", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAllergy = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAllergy = new HashSet<TableInfo.Index>(1);
        _indicesAllergy.add(new TableInfo.Index("index_allergy_allergy_name", true, Arrays.asList("allergy_name")));
        final TableInfo _infoAllergy = new TableInfo("allergy", _columnsAllergy, _foreignKeysAllergy, _indicesAllergy);
        final TableInfo _existingAllergy = TableInfo.read(_db, "allergy");
        if (! _infoAllergy.equals(_existingAllergy)) {
          throw new IllegalStateException("Migration didn't properly handle allergy(com.google.android.gms.samples.vision.ocrreader.Allergy).\n"
                  + " Expected:\n" + _infoAllergy + "\n"
                  + " Found:\n" + _existingAllergy);
        }
      }
    }, "b3250417251e814455fc3a624e15524a", "c6b7b54c0a9d9116dbfe92b4b6f356d0");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "allergy");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `allergy`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public AllergyDao allergyDao() {
    if (_allergyDao != null) {
      return _allergyDao;
    } else {
      synchronized(this) {
        if(_allergyDao == null) {
          _allergyDao = new AllergyDao_Impl(this);
        }
        return _allergyDao;
      }
    }
  }
}
