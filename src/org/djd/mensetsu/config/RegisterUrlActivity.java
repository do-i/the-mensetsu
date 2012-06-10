package org.djd.mensetsu.config;

import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_KEY_CATEGORY_LIST_FILE_URL;
import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_FILE_NAME;

import org.djd.mensetsu.R;
import static  org.djd.mensetsu.global.ApplicationCommons.*;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterUrlActivity extends Activity {
	private static final String TAG = RegisterUrlActivity.class.getSimpleName();

	
	private EditText urlFieldEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_url_view);

		urlFieldEditText = (EditText) findViewById(R.id.id_category_list_url);
		String url = getCategoryListFileUrl();

		urlFieldEditText.setText(url);

	}
	/**
	 * Callback method in the event of any button is clicked.
	 * 
	 * @param view
	 */
	public void onButtonClicked(View view) {
		String url = urlFieldEditText.getText().toString();
		if (url.isEmpty()) {
			updateUrl(DEFAULT_CATEGORY_FILE_LIST_URL);
		} else if (DEFAULT_CATEGORY_FILE_LIST_URL.equals(url)) {
			Log.i(TAG, "No Change.");
		} else {
			updateUrl(url);
		}			
		
		if(null != view){
			Toast.makeText(this, R.string.msg_url_saved, Toast.LENGTH_SHORT).show();
		}		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		onButtonClicked(null);
	}	

	private void updateUrl(String url) {
		SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREFERENCE_KEY_CATEGORY_LIST_FILE_URL, url);
		editor.commit();
	}

	private String getCategoryListFileUrl() {
		SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE);
		return settings.getString(PREFERENCE_KEY_CATEGORY_LIST_FILE_URL, DEFAULT_CATEGORY_FILE_LIST_URL);
	}
}
