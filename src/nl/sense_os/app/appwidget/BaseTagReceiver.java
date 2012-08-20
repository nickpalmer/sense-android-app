package nl.sense_os.app.appwidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sense_os.app.SenseApp;
import nl.sense_os.app.badges.Badges;
import nl.sense_os.app.badges.Badges.BadgeInfo;
import nl.sense_os.app.tags.TagDB;
import nl.sense_os.app.tags.Tags;
import nl.sense_os.app.tags.Tags.TagId;
import nl.sense_os.app.tags.Tags.TagInfo;
import nl.sense_os.service.constants.SensorData.DataPoint;
import nl.vu.lifetag.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BaseTagReceiver extends FragmentActivity {

	private static final String TAG = "BaseTagReceiver";

	private final TagWidgetConfiguration mConfig;

	// Hack around a bug in the compatibility layer.
	public static SenseApp app;

	public void finish() {
		Log.d(TAG, "Finish Called: " + getCallingActivity());
		setResult(Activity.RESULT_OK);
		super.finish();

		// Work around for bug in the Compatibility layer.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (app != null) {
				app.finish();
			}
		}
	}

	public BaseTagReceiver(TagWidgetConfiguration config) {
		super();

		mConfig = config;

		new TagSensorRegistrator(this, mConfig.mSensorName, mConfig.mDisplayName,
				mConfig.mDescription, mConfig.mDataType);
	}

	/**
	 * Fragment with actual dialog announcing new badges earned
	 */
	private class EarnedBadgesDialog extends DialogFragment {

		private List<BadgeInfo> badges;
		private ListAdapter listAdapter;

		public EarnedBadgesDialog(List<BadgeInfo> badges) {
			this.badges = badges;
		}

		public void dismiss() {
			Log.d(TAG, "Badges Dismiss called.");
			super.dismiss();
			BaseTagReceiver.this.setResult(Activity.RESULT_OK);
			BaseTagReceiver.this.finish();
		}

		private OnClickListener getOnClickListener() {
			return new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
				}

			};
		}

		@TargetApi(11)
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			listAdapter = getBadgeListAdapter(getActivity(), badges);

			// create builder
			AlertDialog.Builder builder;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// specifically set dark theme for Android 3.0+
				builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
			} else {
				builder = new AlertDialog.Builder(getActivity());
			}

			builder.setAdapter(listAdapter, getOnClickListener());
			if (badges.size() == 1) {
				builder.setTitle(R.string.dialog_badge_title_single);
			} else {
				builder.setTitle(R.string.dialog_badge_title_multiple);
			}
			builder.setPositiveButton(R.string.badge_earned_yeah, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
				}

			});

			// builder.setCancelable(false);
			AlertDialog dialog = builder.create();

			setCancelable(true);

			return dialog;
		}

		private ListAdapter getBadgeListAdapter(FragmentActivity activity, List<BadgeInfo> badges2) {
			List<Map<String, Object>> data = getBadgeList(activity, badges);
			SimpleAdapter adapter = new SimpleAdapter(activity, data, R.layout.item_list_item,
					new String[] { "i", "t" }, new int[] { R.id.item_image, R.id.item_label }) {

				// Disable everything in the list.
				public boolean isEnabled(int position) {
					return false;
				}
			};

			return adapter;
		}

		private List<Map<String, Object>> getBadgeList(FragmentActivity activity,
				List<BadgeInfo> badges) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (BadgeInfo badgeInfo : badges) {
				list.add(badgeInfo.asMap(activity));
			}
			return list;
		}
	}

	private class TagTimeDialog extends DialogFragment {

		private ListAdapter listAdapter;

		public void dismiss(boolean finish) {
			Log.d(TAG, "Time Dismiss called.");
			super.dismiss();
			if (finish) {
				Log.d(TAG, "Finishing.");
				BaseTagReceiver.this.setResult(Activity.RESULT_OK);
				BaseTagReceiver.this.finish();
			}
		}

		public void dismiss() {
			dismiss(true);
		}

		@TargetApi(11)
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			listAdapter = getTimeListAdapter(getActivity());

			// create builder
			AlertDialog.Builder builder;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// specifically set dark theme for Android 3.0+
				Log.d(TAG, "Theme dark.");
				builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
			} else {
				builder = new AlertDialog.Builder(getActivity());
			}

			builder.setAdapter(listAdapter, getOnClickListener());
			builder.setTitle(R.string.dialog_for_how_long);

			// builder.setCancelable(false);
			AlertDialog dialog = builder.create();

			setCancelable(false);

			return dialog;
		}

		private ListAdapter getTimeListAdapter(FragmentActivity context) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					android.R.layout.select_dialog_singlechoice, context.getResources()
							.getStringArray(R.array.time_names));

			return adapter;
		}

		private OnClickListener getOnClickListener() {
			return new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int which) {
					AlarmManager manager = (AlarmManager) getActivity().getSystemService(
							Context.ALARM_SERVICE);
					int[] times = getActivity().getResources().getIntArray(R.array.time_values);

					// Build the pending intent
					Intent intent = new Intent();
					intent.putExtra(BaseTagProvider.EXTRA_ALARM, true);
					intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
					intent.setClassName(getActivity(), mConfig.mProvider.getName());
					PendingIntent operation = PendingIntent.getBroadcast(getActivity(), 0, intent,
							0);

					// Cancel any equivalent alarms
					manager.cancel(operation);
					long time = System.currentTimeMillis() + (times[which] * 60 * 1000);
					Log.d(TAG, "Setting Alarm:" + times[which] + " : " + time + " "
							+ (time - System.currentTimeMillis()));

					// And set a new alarm
					manager.set(AlarmManager.RTC_WAKEUP, time, operation);

					TagId tag = TagDB.getLastTag(getActivity(), mConfig.mSensorName,
							mConfig.mDefaultTagId);

					List<BadgeInfo> badges = Badges.getEarnedBadges(getActivity(), tag);
					if (badges == null) {
						// Dismiss with a finish of the activity
						TagTimeDialog.this.dismiss();
					} else {
						EarnedBadgesDialog badgeDialog = new EarnedBadgesDialog(badges);
						badgeDialog.show(getSupportFragmentManager(), mConfig.mSensorName
								+ "badges");
						// Go straight to the top to dismiss without finishing.
						TagTimeDialog.this.dismiss(false);
					}
				}
			};
		}
	}

	/**
	 * Fragment with actual dialog that is displayed by the activity
	 */
	private class TagChoiceDialog extends DialogFragment {

		private ListAdapter listAdapter;

		private OnClickListener getOnClickListener() {
			return new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "Choice Dialog clicked");
					TagId tag = mConfig.mDefaultTagId;
					if (which < mConfig.mTags.length) {
						// Off by one because of the header text
						tag = mConfig.mTags[which - 1];
					}
					putTag(tag);

					TagTimeDialog timeDialog = new TagTimeDialog();
					timeDialog.show(getSupportFragmentManager(), mConfig.mSensorName + "time");
				}

			};
		}

		@TargetApi(11)
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Cancel any notifications for this tag.
			NotificationManager nm = (NotificationManager) getApplicationContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(mConfig.mSensorName.hashCode());

			listAdapter = getListAdapter(getActivity(), mConfig.mTags);

			// create builder
			AlertDialog.Builder builder;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// specifically set dark theme for Android 3.0+
				Log.d(TAG, "Theme dark.");
				builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
			} else {
				builder = new AlertDialog.Builder(getActivity());
			}
			builder.setAdapter(listAdapter, getOnClickListener());
			builder.setTitle(mConfig.mDialogTitle);

			// builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			TextView header = new TextView(getBaseContext());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				header.setTextColor(Color.WHITE);
			} else {
				header.setTextColor(Color.BLACK);
			}
			header.setTextSize(30);
			header.setText(R.string.dialog_will_be);
			header.setPadding(12, 3, 0, 3);
			dialog.getListView().addHeaderView(header);

			setCancelable(false);

			return dialog;
		}
	}

	private ListAdapter getListAdapter(final Context context, final TagId[] tags) {
		List<Map<String, Object>> data = getTagList(context, tags);
		SimpleAdapter adapter = new SimpleAdapter(context, data, R.layout.item_list_item,
				new String[] { "i", "t" }, new int[] { R.id.item_image, R.id.item_label });

		return adapter;
	}

	private List<Map<String, Object>> getTagList(Context context, final TagId[] tags) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < tags.length; i++) {
			TagInfo tagInfo = Tags.getTagInfo(tags[i]);
			data.add(tagInfo.asMap(context));
		}

		return data;
	}

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);

		showTagChoiceDialog();
	}

	private void putTag(TagId tag) {

		TagDB.putTag(getApplicationContext(), tag);

		sendTagData(getApplicationContext(), mConfig, tag);

		BaseTagProvider.forceUpdate(this, mConfig.mProvider.getName());
	}

	public static void sendTagData(Context context, TagWidgetConfiguration config, TagId tag) {
		String json = tag.getJson();

		// send data point with the mood to Sense
		Intent storeDataPoint = new Intent(context.getString(R.string.action_sense_new_data));
		storeDataPoint.putExtra(DataPoint.SENSOR_NAME, tag.mSensor);
		storeDataPoint.putExtra(DataPoint.SENSOR_DESCRIPTION, config.mDescription);
		storeDataPoint.putExtra(DataPoint.DISPLAY_NAME, config.mDisplayName);
		storeDataPoint.putExtra(DataPoint.DATA_TYPE, config.mDataType);
		storeDataPoint.putExtra(DataPoint.TIMESTAMP, System.currentTimeMillis());
		storeDataPoint.putExtra(DataPoint.VALUE, json);
		context.startService(storeDataPoint);
	}

	/**
	 * Shows the dialog with the list of choices.
	 */
	private void showTagChoiceDialog() {
		TagChoiceDialog dialog = new TagChoiceDialog();
		dialog.show(getSupportFragmentManager(), mConfig.mSensorName + "choice");
	}

}