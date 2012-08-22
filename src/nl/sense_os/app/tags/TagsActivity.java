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
package nl.sense_os.app.tags;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import nl.sense_os.app.TwoLineListActivity;
import nl.sense_os.app.tags.Tags.TagInfo;
import nl.vu.lifetag.R;
import android.content.Intent;
import android.text.format.DateFormat;
import android.widget.TextView;

/**
 * This activity shows a list of tags made by the user.
 *
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public class TagsActivity extends TwoLineListActivity {

	public static final String EXTRA_DATE_YEAR = "y";
	public static final String EXTRA_DATE_MONTH = "m";
	public static final String EXTRA_DATE_DAY = "d";

	private int mYear;
	private int mMonth;
	private int mDay;

	public TagsActivity() {
		super(R.string.label_tags, R.string.label_no_tags);
	}

	@Override
	public void onStart() {
		Intent launch = getIntent();
		mYear = launch.getIntExtra(EXTRA_DATE_YEAR, -1);
		mMonth = launch.getIntExtra(EXTRA_DATE_MONTH, -1);
		mDay = launch.getIntExtra(EXTRA_DATE_DAY, -1);

		if (mYear > 0) {
			TextView title = (TextView) findViewById(R.id.sense_banner);
			Calendar c = Calendar.getInstance();
			c.set(mYear, mMonth, mDay);
			title.setText(R.string.label_tags);
			title.append(": " + DateFormat.getDateFormat(this).format(c.getTime()));
		}

		super.onStart();
	}

	/**
	 * Return the list of tags.
	 */
	@Override
	protected List<Map<String, Object>> getDataList() {
		List<TagInfo> tags = TagDB.getAllTags(this, mYear, mMonth, mDay);

		List<Map<String, Object>> tagInfoList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < tags.size(); i++) {
			TagInfo tag = tags.get(i);
			tagInfoList.add(tag.asMap(this));
		}

		return tagInfoList;
	}

}
