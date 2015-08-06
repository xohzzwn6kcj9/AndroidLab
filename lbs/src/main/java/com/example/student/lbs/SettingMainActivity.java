package com.example.student.lbs;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingMainActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			getClass().getMethod("getFragmentManager");
			addResourceApi11();
		} catch (NoSuchMethodException e) { // Api < 11
			addResourceApi10();
		}
	}

	// @SuppressWarnings("deprecation")
	protected void addResourceApi10() {
		Log.d("kkang", "11111111111111111111111");
		addPreferencesFromResource(R.xml.settings);

	}

	@TargetApi(11)
	protected void addResourceApi11() {
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new MyPreferenceFragment()).commit();
	}

	@TargetApi(11)
	public static class MyPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings); 
		}
	}

}
