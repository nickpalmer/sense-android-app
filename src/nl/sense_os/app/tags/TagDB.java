package nl.sense_os.app.tags;

import java.util.ArrayList;
import java.util.List;

import nl.sense_os.app.tags.Tags.TagId;
import nl.sense_os.app.tags.Tags.TagInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Manages the database of tags made by the user.
 *
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public class TagDB {

	private static final String TAG_DB_NAME = "tags.db";
	private static final int TAG_DB_VERSION = 1;
	private static final String TAG_TABLE_NAME = "tag";

	private static final String SENSOR_COLUMN_NAME = "sensor";
	private static final String TAGID_COLUMN_NAME = "tagid";
	private static final String TAGNAME_COLUMN_NAME = "tagname";
	private static final String TIMESTAMP_COLUMN_NAME = "timestamp";
	private static final String _ID_COLUMN_NAME = "_id";

	private static final String SENSOR_SELECTION = SENSOR_COLUMN_NAME + "=?";
	private static final String[] TAGID_PROJECTION = {TAGID_COLUMN_NAME, TAGNAME_COLUMN_NAME};
	private static final String LIMIT_ONE = "1";
	private static final String ORDERBY_TIMESTAMP_DESC = "TIMESTAMP DESC";

	private static final String[] TAG_PROJECTION = {SENSOR_COLUMN_NAME, TAGNAME_COLUMN_NAME,
		TAGID_COLUMN_NAME, TIMESTAMP_COLUMN_NAME};

	private static class TagDBHelper extends SQLiteOpenHelper {

		public TagDBHelper(Context context, String name, CursorFactory factory,
				int version, DatabaseErrorHandler errorHandler) {
			super(context, name, factory, version, errorHandler);
		}

		public TagDBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.beginTransaction();
			try {
				db.execSQL("CREATE TABLE " + TAG_TABLE_NAME +" ( " + _ID_COLUMN_NAME +" INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ SENSOR_COLUMN_NAME + " TEXT NOT NULL, "
						+ TAGNAME_COLUMN_NAME + " TEXT NOT NULL, "
						+ TAGID_COLUMN_NAME + " INTEGER NOT NULL, "
						+ TIMESTAMP_COLUMN_NAME + " TEXT DEFAULT CURRENT_TIMESTAMP)");
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}

	}

	/**
	 * Stores the given tag to the database.
	 * @param context the context to work in
	 * @param id the id of the tag to store.
	 */
	public static void putTag(Context context, TagId id) {
		SQLiteDatabase db = getTagDb(context, true);
		try {
			db.beginTransaction();
			try {
				ContentValues values = new ContentValues();
				values.put(SENSOR_COLUMN_NAME, id.mSensor);
				values.put(TAGNAME_COLUMN_NAME, id.mName);
				values.put(TAGID_COLUMN_NAME, id.mId);
				db.insert(TAG_TABLE_NAME, _ID_COLUMN_NAME, values);
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
	 * @param mSensorName the name of the tag widget being requested
	 * @param mDefaultTagId the default tag to use
	 * @return the last tag made by the user or the default provided if no tag has yet been made.
	 */
	public static TagId getLastTag(Context context, String mSensorName,
			TagId mDefaultTagId) {
		TagId ret = mDefaultTagId;
		SQLiteDatabase db = getTagDb(context, false);
		try {
			db.beginTransaction();
			try {
				Cursor c = db.query(TAG_TABLE_NAME, TAGID_PROJECTION,
						SENSOR_SELECTION, new String[] {mSensorName},
						null, null, ORDERBY_TIMESTAMP_DESC, LIMIT_ONE);
				if (c != null) {
					try {
						if (c.moveToFirst()) {
							ret = new TagId(mSensorName, c.getString(1), c.getInt(0));
						}
					} finally {
						c.close();
					}
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		} finally {
			db.close();
		}

		return ret;
	}

	/**
	 * @param context the context to work in.
	 * @return all tags made by the user
	 */
	public static List<TagInfo> getAllTags(
			Context context) {
		SQLiteDatabase db = getTagDb(context, false);
		List<TagInfo> tags = new ArrayList<TagInfo>();

		try {
			db.beginTransaction();
			try {
				Cursor c = db.query(TAG_TABLE_NAME, TAG_PROJECTION,
						null, null,
						null, null, ORDERBY_TIMESTAMP_DESC);
				if (c != null && c.getCount() > 0) {
					do {
						c.moveToNext();

						TagId id = new TagId(c.getString(0), c.getString(1), c.getInt(2));
						TagInfo info = Tags.getTagInfo(id);
						info.timestamp = c.getString(3);

						tags.add(info);
					} while (!c.isLast());
				}
				db.setTransactionSuccessful();

			} finally {
				db.endTransaction();
			}
		} finally {
			db.close();
		}

		return tags;
	}

	private static SQLiteDatabase getTagDb(Context context, boolean writable) {
		TagDBHelper helper = getTagDbHelper(context);

		if (writable) {
			return helper.getWritableDatabase();
		}

		return helper.getReadableDatabase();
	}

	private static TagDBHelper getTagDbHelper(Context context) {
		return new TagDBHelper(context, TAG_DB_NAME, null, TAG_DB_VERSION);
	}

}
