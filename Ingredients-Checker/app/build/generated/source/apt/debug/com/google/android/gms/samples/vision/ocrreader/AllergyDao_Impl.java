package com.google.android.gms.samples.vision.ocrreader;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.database.Cursor;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AllergyDao_Impl implements AllergyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfAllergy;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfAllergy;

  public AllergyDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAllergy = new EntityInsertionAdapter<Allergy>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `allergy`(`id`,`allergy_name`) VALUES (nullif(?, 0),?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Allergy value) {
        stmt.bindLong(1, value.getId());
        if (value.getAllergyName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getAllergyName());
        }
      }
    };
    this.__deletionAdapterOfAllergy = new EntityDeletionOrUpdateAdapter<Allergy>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `allergy` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Allergy value) {
        stmt.bindLong(1, value.getId());
      }
    };
  }

  @Override
  public void insertAllergy(Allergy allergy) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfAllergy.insert(allergy);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllergy(Allergy allergy) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfAllergy.handle(allergy);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Allergy> getAll() {
    final String _sql = "SELECT * FROM allergy";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfAllergyName = _cursor.getColumnIndexOrThrow("allergy_name");
      final List<Allergy> _result = new ArrayList<Allergy>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Allergy _item;
        _item = new Allergy();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpAllergyName;
        _tmpAllergyName = _cursor.getString(_cursorIndexOfAllergyName);
        _item.setAllergyName(_tmpAllergyName);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Allergy findByAllergyName(String allergyName) {
    final String _sql = "SELECT * FROM allergy WHERE allergy_name = ? ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (allergyName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, allergyName);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfAllergyName = _cursor.getColumnIndexOrThrow("allergy_name");
      final Allergy _result;
      if(_cursor.moveToFirst()) {
        _result = new Allergy();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpAllergyName;
        _tmpAllergyName = _cursor.getString(_cursorIndexOfAllergyName);
        _result.setAllergyName(_tmpAllergyName);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
