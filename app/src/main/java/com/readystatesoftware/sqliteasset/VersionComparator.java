package com.readystatesoftware.sqliteasset;

import android.util.Log;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper.SQLiteAssetException;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VersionComparator implements Comparator<String> {
	private static final String TAG = SQLiteAssetHelper.class.getSimpleName();
	private Pattern pattern = Pattern.compile(".*_upgrade_([0-9]+)-([0-9]+).*");

	VersionComparator() {
	}

	public int compare(String file0, String file1) {
		Matcher m0 = this.pattern.matcher(file0);
		Matcher m1 = this.pattern.matcher(file1);
		if (!m0.matches()) {
			Log.w(TAG, "could not parse upgrade script file: " + file0);
			throw new SQLiteAssetException("Invalid upgrade script file");
		} else if (m1.matches()) {
			int v0_from = Integer.valueOf(m0.group(1)).intValue();
			int v1_from = Integer.valueOf(m1.group(1)).intValue();
			int v0_to = Integer.valueOf(m0.group(2)).intValue();
			int v1_to = Integer.valueOf(m1.group(2)).intValue();
			if (v0_from == v1_from) {
				if (v0_to == v1_to) {
					return 0;
				}
				if (v0_to >= v1_to) {
					return 1;
				}
				return -1;
			} else if (v0_from >= v1_from) {
				return 1;
			} else {
				return -1;
			}
		} else {
			Log.w(TAG, "could not parse upgrade script file: " + file1);
			throw new SQLiteAssetException("Invalid upgrade script file");
		}
	}
}
