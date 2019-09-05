package com.hapi.player.room;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import com.hapi.player.been.MediaParams;

@Database(entities = {MediaParams.class},version = 1,exportSchema = false)
public abstract class MediaDataBase  extends RoomDatabase {


    private static final String DB_NAME = "MediaDataBase.db";
    private static volatile MediaDataBase instance;

    public static synchronized MediaDataBase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static MediaDataBase create(final Context context) {
        return Room.databaseBuilder(
                context,
                MediaDataBase  .class,
                DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public abstract MediaDao getMediaDao();


}
