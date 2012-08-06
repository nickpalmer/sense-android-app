package nl.sense_os.app.appwidget;

import java.util.Timer;
import java.util.TimerTask;

import nl.sense_os.service.commonsense.SensorRegistrator;
import android.app.AlarmManager;
import android.content.Context;
import android.util.Log;

/**
 * Verifies the existence of the required sensors for the life tag project. Should be called before
 * attempting to send any data points are sent to CommonSense.
 */
public class TagSensorRegistrator extends SensorRegistrator {

	private static final String TAG = "TagSensorRegistrator";

	private final String mName;
	private String mDisplayName;
	private String mDescription;
	private String mDataType;

	/**
	 * dummy value, used for estimating data structure for the tag data
	 */
	private final String mValue = "{\"id\":1,\"desc\":\"string\"}";

	private final Timer sensorVerifyTimer = new Timer();
	private TimerTask sensorVerifyTask;
	private long sensorVerifyTimeout = 15000;

	public TagSensorRegistrator(Context context, String name, String displayName,
			String description, String dataType) {
		super(context);
		mName = name;
		mDisplayName = displayName;
		mDescription = description;
		mDataType = dataType;

		// verify existence of this tag sensor
		// (run in separate thread to avoid NetworkOnMainThreadException)
		new Thread() {
			public void run() {
				verifySensorExistence();
			};
		}.start();
	}

	@Override
	public boolean verifySensorIds(String deviceType, String deviceUuid) {
		// verify existence of this tag sensor
		return checkSensor(mName, mDisplayName, mDataType, mDescription, mValue, null, null);
	}

	/**
	 * Makes sure that there is a sensor registered at CommonSense that can handle our life tag
	 * data. If verification fails, e.g. due to connection problems, it schedules a retry after some
	 * delay.
	 */
	private synchronized void verifySensorExistence() {

		if (null != sensorVerifyTask) {
			sensorVerifyTask.cancel();
		}

		if (verifySensorIds(null, null)) {
			Log.v(TAG, "Sensor existence verified");
		} else {
			Log.w(TAG, "Failed to verify the sensor existence! Retry in "
					+ (sensorVerifyTimeout / 1000) + " seconds");
			sensorVerifyTask = new TimerTask() {

				@Override
				public void run() {
					verifySensorExistence();
				}
			};
			sensorVerifyTimer.schedule(sensorVerifyTask, sensorVerifyTimeout);
			if (sensorVerifyTimeout < AlarmManager.INTERVAL_FIFTEEN_MINUTES) {
				sensorVerifyTimeout *= 2;
			}
		}
	}
}
