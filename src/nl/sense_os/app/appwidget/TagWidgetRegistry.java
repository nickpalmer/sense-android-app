package nl.sense_os.app.appwidget;

import android.util.Log;
import nl.sense_os.app.badges.Badges;
import nl.sense_os.app.tags.Tags;

/**
 * This class ensures that all TagWidgets have been loaded by
 * the class loader.
 * <br/>
 * It is needed to ensure that all tag widgets are known to
 * the Badges and Tags classes.
 *
 * If you add a widget please make sure it is also added
 * to this registry.
 *
 * @author nick &lt;nick@sluggardy.net&gt;
 *
 */
public class TagWidgetRegistry {

	private static final String TAG = "TagWidgetRegistry";
	private static boolean sRegistered = false;

	public static synchronized void registerWidgets() {
		if (!sRegistered) {
			register(LifeTagWidget.sConfig);
		}
		sRegistered = true;
	}

	/**
	 * Helper method which registers a tag configuration.
	 * @param configuration
	 */
	private static void register(TagWidgetConfiguration configuration) {
		Log.d(TAG, "Registering: " + configuration.mSensorName);
		Badges.addBadgeProvider(configuration);
		Tags.addTagProvider(configuration);
	}

}
