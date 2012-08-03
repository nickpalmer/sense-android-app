package nl.sense_os.app.appwidget;

import nl.sense_os.app.badges.Badges.BadgeProvider;
import nl.sense_os.app.tags.Tags.TagId;
import nl.sense_os.app.tags.Tags.TagProvider;

/**
 * This class represents the configuration for a tag widget.
 *
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public abstract class TagWidgetConfiguration implements BadgeProvider, TagProvider
{
	public final String mSensorName;
	public final TagId[] mTags;
	public final TagId mDefaultTagId;
	public final String mDescription;
	public final String mDisplayName;
	public final String mDataType;
	public final int mDialogTitle;
	public final Class<? extends BaseTagReceiver> mReceiver;
	public final Class<? extends BaseTagProvider> mProvider;

	public TagWidgetConfiguration(
					Class<? extends BaseTagProvider> providerClass,
					Class<? extends BaseTagReceiver> receiverClass,
					String sensorName,
					String dataType,
					String displayName,
					String description,
					int dialogTitle,
					TagId[] tags,
					TagId defaultTag
					) {
		mSensorName = sensorName;
		mTags = tags;
		mDefaultTagId = defaultTag;
		mDescription = description;
		mDisplayName = displayName;
		mDataType = dataType;
		mDialogTitle = dialogTitle;
		mReceiver = receiverClass;
		mProvider = providerClass;
	}
}