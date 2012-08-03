package nl.sense_os.app.appwidget;

import java.util.HashMap;
import java.util.Map;

import nl.sense_os.app.R;
import nl.sense_os.app.badges.BadgeDB;
import nl.sense_os.app.tags.TagDB;
import nl.sense_os.app.tags.Tags;
import nl.sense_os.app.tags.Tags.TagId;
import nl.sense_os.app.tags.Tags.TagInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * This class is the Base AppWidgetProvider for "Tagging" widgets.
 *
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public class BaseTagProvider extends AppWidgetProvider {

	private static final String TAG = "BaseTagProvider";

	/** Flag in the intent extras indicating this is an update due to an alarm going off. */
	public static final String EXTRA_ALARM = "alarmUpdate";

	/** The configuration for the widget this provider is working for. */
	private final TagWidgetConfiguration mConfig;

	/** The map of tags for this provider. */
	private final Map<Integer, TagId> mTags = new HashMap<Integer, TagId>();

	/**
	 * Constructs a new provider for the given widget configuration.
	 * @param config the configuration for the widget.
	 */
	public BaseTagProvider(TagWidgetConfiguration config) {
		mConfig = config;
		for (TagId tag : config.mTags) {
			mTags.put(tag.mId, tag);
		}
	}

	/* Uses deprecated Notification API for backwards compatibility. */
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Got intent: {}" + intent);
		if (intent.hasExtra(EXTRA_ALARM)) {
			// Get what the new tag should be from the intent.
			TagId tag = TagDB.getLastTag(context, mConfig.mSensorName, null);

			// Put the new one based on this alarm.
			if (tag != null && tag.mId != mConfig.mDefaultTagId.mId) {
				Log.d(TAG, "Resetting to default.");
				TagDB.putTag(context, mConfig.mDefaultTagId);

				BaseTagReceiver.sendTagData(context, mConfig, tag);
				BadgeDB.updateBadgeCounts(context, mConfig.mDefaultTagId);

				// Setup the notification
				TagInfo defaultInfo = Tags.getTagInfo(mConfig.mDefaultTagId);
				int icon = defaultInfo.mDrawable;
				CharSequence contentTitle = context.getString(R.string.notification_tag_expired, mConfig.mDisplayName);
				CharSequence tickerText = context.getString(R.string.notification_tag_set_new, mConfig.mDisplayName);				long when = System.currentTimeMillis();

				Notification notification = new Notification(icon, tickerText, when);
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.defaults = Notification.DEFAULT_VIBRATE;

				// Build a pending intent for when the user clicks.
				Intent newIntent = new Intent();
				newIntent.setClassName(context, mConfig.mReceiver.getName());

				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);

				notification.setLatestEventInfo(context, contentTitle, tickerText, pendingIntent);

				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				nm.notify(mConfig.mSensorName.hashCode(), notification);

				BaseTagProvider.forceUpdate(context, mConfig.mProvider.getName());
			}
		}

		super.onReceive(context, intent);
	}

	@Override
	public final void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
			final int[] appWidgetIds) {
		Log.d(TAG, "onUpdate: {}" + appWidgetIds);
		final int N = appWidgetIds.length;

		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			Log.d(TAG, "Updating widget: {}" + appWidgetId);

			// Create an Intent to launch LifeTagReceiver
			Intent intent = new Intent(context, mConfig.mReceiver);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			RemoteViews views = getUpdateViews(context);

			views.setOnClickPendingIntent(R.id.lifetag_button, pendingIntent);

			// Tell the AppWidgetManager to perform an update on widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	private RemoteViews getUpdateViews(final Context context) {
		// Get the layout for the App Widget and attach
		// an on-click listener to the button
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tag_widget);

		TagId tagId = TagDB.getLastTag(context, mConfig.mSensorName, mConfig.mDefaultTagId);
		Log.d(TAG, "Viewing: " + tagId);

		TagInfo tagInfo = Tags.getTagInfo(tagId);
		views.setImageViewResource(R.id.lifetag_button, tagInfo.mDrawable);

		views.setTextViewText(R.id.mood_label, context.getString(tagInfo.mDescription));
		// views.setInt(R.id.mood_button, "setAlpha", 128);

		return views;
	}

	/**
	 * Forces an update of the given widget.
	 * @param context the context to work in
	 * @param componentName the widget to force update for.
	 */
	public static void forceUpdate(Context context, String componentName) {
		Log.d(TAG, "forcing update.");
		Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		ComponentName component = new ComponentName(context, componentName);
		intent.setComponent(component);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(context)
				.getAppWidgetIds(component));
		// Log.d(TAG,"Intent: {}", intent);
		context.sendBroadcast(intent);
	}
}