package org.djd.mensetsu;

import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_FILE_NAME;
import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_KEY_FONT_SIZE;
import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_KEY_WEB_FONT_SIZE;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

public class Preferences extends PreferenceActivity {
	
	
	private static final String TAG = Preferences.class.getSimpleName();

   @Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		addPreferencesFromResource(R.xml.preference);
		
		
		Preference fontSizeListPreference = findPreference("fontSize");
		fontSizeListPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE);
				Editor editor = settings.edit();
				
				// remove float after WebView is doing great.
				editor.putFloat(PREFERENCE_KEY_FONT_SIZE, Float.parseFloat(newValue.toString()));
				editor.putInt(PREFERENCE_KEY_WEB_FONT_SIZE, Integer.parseInt(newValue.toString()));
				editor.apply();	
				return true;
			}
		});
	}
	
}
