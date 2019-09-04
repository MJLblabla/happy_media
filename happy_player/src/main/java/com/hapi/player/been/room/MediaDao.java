package com.hapi.player.been.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.hapi.player.been.MediaParams;

@Dao
public interface MediaDao {

    @Query("SELECT * FROM mediaparams WHERE path=:path")
    MediaParams getMediaParams(String path);


    @Insert
    void insert(MediaParams mediaParams);
}
