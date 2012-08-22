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
import java.util.Map;

import nl.sense_os.app.TwoLineListActivity;
import nl.sense_os.app.badges.Badges.BadgeInfo;
import nl.vu.lifetag.R;

/**
 * This activity shows a list of badges earned by the user.
 *
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public class BadgesActivity extends TwoLineListActivity {

	public BadgesActivity() {
		super(R.string.label_badges, R.string.label_no_badges);
	}

	protected List<Map<String, Object>> getDataList() {
		List<BadgeInfo> badges = BadgeDB.getAllEarnedBadges(this);

		List<Map<String, Object>> badgeInfoList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < badges.size(); i++) {
			BadgeInfo badge = badges.get(i);
			badgeInfoList.add(badge.asMap(this));
		}

		return badgeInfoList;
	}

}
