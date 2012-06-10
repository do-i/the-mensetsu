package org.djd.mensetsu;

import static org.djd.mensetsu.global.ApplicationCommons.INTENT_EXTRA_KEY_CONTINUE;

import org.djd.mensetsu.config.ConfigActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class TheMensetsuActivity extends Activity {
	private static final String TAG = TheMensetsuActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entrance_view);
	}

	/**
	 * Callback method in the event of any button is clicked.
	 * 
	 * @param view
	 */
	public void onButtonClicked(View view) {
		Intent intent = null;
		switch (view.getId()) {
		case R.id.btn_start_id:
			intent = new Intent(this, QuizWebActivity.class);
			intent.putExtra(INTENT_EXTRA_KEY_CONTINUE, false);
			break;
		case R.id.btn_config_id:
			intent = new Intent(this, ConfigActivity.class);
			break;
		case R.id.btn_continue_id:
			intent = new Intent(this, QuizWebActivity.class);
			intent.putExtra(INTENT_EXTRA_KEY_CONTINUE, true);
			
			break;
		}

		if (null != intent) {
			startActivity(intent);
		} else {
			Toast.makeText(this, R.string.msg_sys_no_case_defined_error, Toast.LENGTH_SHORT).show();
			Log.e(TAG, getString(R.string.msg_sys_no_case_defined_error));

		}
	}
}