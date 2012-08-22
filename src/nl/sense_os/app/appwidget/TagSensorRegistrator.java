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
