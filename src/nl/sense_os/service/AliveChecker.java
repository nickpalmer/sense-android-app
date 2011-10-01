/*
 * ************************************************************************************************
 * Copyright (C) 2010 Sense Observation Systems, Rotterdam, the Netherlands. All rights reserved. *
 * ************************************************************************************************
 */
package nl.sense_os.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import nl.sense_os.service.SensePrefs.Status;

public class AliveChecker extends BroadcastReceiver {

    private static final String TAG = "Sense AliveChecker";
    private static final String ACTION = "nl.sense_os.service.CheckAlive";
    private static final int REQ_CODE = 0x0C471FE1;
    private static final int REQ_CODE_WAKEUP = 0x0C471FE2;

    @Override
    public void onReceive(Context context, Intent intent) {

        /* check if the Sense service should be alive */
        final SharedPreferences statusPrefs = context.getSharedPreferences(SensePrefs.STATUS_PREFS,
                Context.MODE_PRIVATE);
        final boolean alive = statusPrefs.getBoolean(Status.MAIN, false);

        /* if it should be alive, check if it really is still alive */
        if (true == alive) {
            // Log.v(TAG, "Sense should be alive, poke...");
            final Intent serviceIntent = new Intent(ISenseService.class.getName());
            if (null == context.startService(serviceIntent)) {
                Log.w(TAG, "Could not start Sense service!");
            }
        } else {
            // Log.v(TAG, "Sense service should NOT be alive. Doing nothing...");
        }
    }

    /**
     * Starts periodic checks on Sense Platform service's alive status.
     * 
     * @param context
     *            Context to access AlarmManager
     */
    public static void scheduleChecks(Context context) {

        Intent intent = new Intent(AliveChecker.ACTION);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // try to check pretty often when the phone is awake
        PendingIntent operation = PendingIntent.getBroadcast(context, REQ_CODE, intent, 0);
        long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        mgr.cancel(operation);
        mgr.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, operation);

        // make sure we wake up at least once an hour
        operation = PendingIntent.getBroadcast(context, REQ_CODE_WAKEUP, intent, 0);
        interval = AlarmManager.INTERVAL_HOUR;
        mgr.cancel(operation);
        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval,
                interval, operation);
    }

    /**
     * Stops the periodic checks on Sense Platform service's alive status.
     * 
     * @param context
     *            Context to access AlarmManager
     */
    public static void stopChecks(Context context) {
        Intent intent = new Intent(AliveChecker.ACTION);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.cancel(PendingIntent.getBroadcast(context, REQ_CODE, intent, 0));
        mgr.cancel(PendingIntent.getBroadcast(context, REQ_CODE_WAKEUP, intent, 0));
    }
}
