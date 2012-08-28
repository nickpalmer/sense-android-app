/*
 * Copyright (c) 2008-2012 Vrije Universiteit, The Netherlands All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Vrije Universiteit nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package nl.sense_os.app.badges;

import java.util.ArrayList;
import java.util.List;

import nl.sense_os.app.badges.Badges.BadgeId;
import nl.sense_os.app.badges.Badges.BadgeInfo;
import nl.sense_os.app.tags.Tags.TagId;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/**
 * This class manages the database of badges earned by the user while tagging.
 * It also does some book keeping of tags to help manage the earning of badges.
 *
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public class BadgeDB {

    private static final String TAG = "Badge";

    private static final String BADGE_DB_NAME = "badges.db";
    private static final int BADGE_DB_VERSION = 1;
    private static final String TAG_COUNT_TABLE_NAME = "tagCount";
    private static final String BADGE_TABLE_NAME = "badges";

    public static final String TAG_COLUMN_NAME = "tag";
    public static final String COUNT_COLUMN_NAME = "count";
    private static final String _ID_COLUMN_NAME = "_id";

    public static final String TYPE_COLUMN_NAME = "type";
    public static final String TIMESTAMP_COLUMN_NAME = "timestamp";

    private static final String[] COUNT_PROJECTION = new String[] {
        COUNT_COLUMN_NAME};
    private static final String COUNT_SELECTION = TAG_COLUMN_NAME + "=?";

    private static final String[] BADGE_PROJECTION = new String[] {
        TYPE_COLUMN_NAME, TAG_COLUMN_NAME, COUNT_COLUMN_NAME, TIMESTAMP_COLUMN_NAME};
    private static final String BADGE_ORDER = TIMESTAMP_COLUMN_NAME + " DESC";

    private static final String TOTAL_TAGS_COUNT = "badge_total_tags";
    private static final String BADGE_TOTAL = "badge_total_";


    private static class BadgeDBHelper extends SQLiteOpenHelper {

        @TargetApi(11)
        public BadgeDBHelper(Context context, String name, CursorFactory factory,
                int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        public BadgeDBHelper(Context context, String name, CursorFactory factory,
                int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            try {
                db.execSQL("CREATE TABLE " + TAG_COUNT_TABLE_NAME +" ( "
                        + _ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + TAG_COLUMN_NAME +" TEXT NOT NULL, "
                        + COUNT_COLUMN_NAME + " INTEGER NOT NULL"
                        + ")");
                db.execSQL("CREATE TABLE " + BADGE_TABLE_NAME +" ( "
                        + _ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + TYPE_COLUMN_NAME +" TEXT, "
                        + TAG_COLUMN_NAME +" TEXT, "
                        + COUNT_COLUMN_NAME + " INTEGER, "
                        + TIMESTAMP_COLUMN_NAME + " TEXT DEFAULT CURRENT_TIMESTAMP"
                        + ")");

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // onCreate(db);
        }

    }

    /**
     * Updates the counts for the given tag and returns ids for the badges earned.
     * @param context the context to work in
     * @param tag the tag
     * @return the ids of any badges earned.
     */
    public static BadgeId[] updateBadgeCounts(Context context, TagId tag) {
        SQLiteDatabase db = getDb(context);

        BadgeId[] badges = new BadgeId[3];

        Log.d(TAG, "Updating counts for: " + tag.mSensor + " " + tag.mId);


        try {
            db.beginTransaction();

            try {
            // Now increment counts for badges of the three different types
            badges[0] = new BadgeId(null, -1, incrementCount(db, TOTAL_TAGS_COUNT));
            badges[1] = new BadgeId(tag.mSensor, -1, incrementCount(db, BADGE_TOTAL + tag.mSensor));
            badges[2] = new BadgeId(tag.mSensor, tag.mId, incrementCount(db, BADGE_TOTAL + tag.mSensor + tag.mId));

            db.setTransactionSuccessful();

            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }

        return badges;
    }

    private static SQLiteDatabase getDb(Context context) {
        BadgeDBHelper helper = getDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        return db;
    }

    private static BadgeDBHelper getDBHelper(Context context) {
        BadgeDBHelper helper = new BadgeDBHelper(context, BADGE_DB_NAME, null, BADGE_DB_VERSION);
        return helper;
    }

    /**
     * Increments the counts for the given tag.
     * @param db the db to work in
     * @param tagName the tag to increment
     * @return the new count for this tag.
     */
    private static int incrementCount(SQLiteDatabase db, String tagName) {
        Cursor c = null;
        int count = 1;
        try {
            c = db.query(TAG_COUNT_TABLE_NAME, COUNT_PROJECTION, COUNT_SELECTION,
                    new String[] {tagName}, null, null, null);
            ContentValues values = new ContentValues();
            // If it already exists
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
                Log.d(TAG, "Got count: " + count + " for : " + tagName);
                count = count + 1;
                values.put(COUNT_COLUMN_NAME, count);
                db.update(TAG_COUNT_TABLE_NAME, values, COUNT_SELECTION, new String[] {tagName});
            } else {
                Log.d(TAG, "Inserting for: " + tagName);
                values.put(COUNT_COLUMN_NAME, count);
                values.put(TAG_COLUMN_NAME, tagName);
                db.insert(TAG_COUNT_TABLE_NAME, _ID_COLUMN_NAME, values);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count;
    }

    /**
     * Stores the given badge.
     * @param context the context to work in
     * @param totalBadge the badge to store
     */
    public static void earnedBadge(Context context, BadgeId totalBadge) {
        SQLiteDatabase db = getDb(context);

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(TYPE_COLUMN_NAME, totalBadge.type);
            values.put(TAG_COLUMN_NAME, totalBadge.tag);
            values.put(COUNT_COLUMN_NAME, totalBadge.count);

            try {
                Log.d(TAG, "Inserting badge: " + values);
                db.insert(BADGE_TABLE_NAME, _ID_COLUMN_NAME, values);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

        } finally {
            db.close();
        }
    }

    /**
     * @param context the context to work in
     * @return a list of all badges earned.
     */
    public static List<BadgeInfo> getAllEarnedBadges(Context context) {
        SQLiteDatabase db = getDb(context);
        List<BadgeInfo> ret = new ArrayList<BadgeInfo>();

        try {
            db.beginTransaction();
            Cursor c = null;
            try {
                c = db.query(BADGE_TABLE_NAME, BADGE_PROJECTION, null,
                        null, null, null, BADGE_ORDER);
                if (c != null && c.getCount() > 0) {
                    do {
                        c.moveToNext();

                        BadgeId id = new BadgeId(c.getString(0), c.getInt(1), c.getInt(2));
                        Log.d(TAG, "Loading badge: " + id);
                        BadgeInfo info = Badges.getBadgeInfo(id);

                        info.timestamp = c.getString(3);

                        ret.add(info);

                    } while (!c.isLast());
                }
                db.setTransactionSuccessful();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        } finally {
            db.endTransaction();
            db.close();
        }

        return ret;
    }

}
