package kr.androidteam.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NfcEnroll extends Activity {

	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private String ID;

	public PowerManager.WakeLock mWakeLock;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_enroll);
		loadDB();
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Intent intent = new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "selectednfc");
		mWakeLock.acquire();

		if (nfcAdapter == null)
			Toast.makeText(getApplicationContext(), "NFC를 지원하지 않는 단말기입니다.",
					Toast.LENGTH_SHORT).show();

		if (!nfcAdapter.isEnabled()) {

			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setTitle("Info");
			alertbox.setMessage("NFC를 켜시겠습니까?");
			alertbox.setPositiveButton("Turn On",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								Intent intent = new Intent(
										android.provider.Settings.ACTION_NFC_SETTINGS);

								startActivity(intent);
							} else {
								Intent intent = new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								startActivity(intent);
							}
						}
					});
			alertbox.setNegativeButton("Close",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			alertbox.show();
		}

	}

	public void onClickButton(View v) {
		EditText txt = null;
		txt = (EditText) findViewById(R.id.editText1);
		String location = txt.getText().toString();

		String sql = "INSERT INTO NFC (nfc_Id,location) VALUES ('"+ID+"','"+location+"');";
		SQLiteDatabase db = openOrCreateDatabase("nfc.db",
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.execSQL(sql);
		finish();
	}

	public void loadDB() {

		SQLiteDatabase db = openOrCreateDatabase("nfc.db",
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS NFC "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,nfc_Id TEXT, location TEXT);");

	}

	public static final String CHARS = "0123456789ABCDEF";

	public static String toHexString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; ++i) {
			sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(
					CHARS.charAt(data[i] & 0x0F));
		}
		return sb.toString();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag != null) {
			byte[] tagId = tag.getId();

			ID = toHexString(tagId);

		}
	}

	@Override
	protected void onPause() {
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
		Log.i("tag", "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter != null) {
			nfcAdapter
					.enableForegroundDispatch(this, pendingIntent, null, null);

		}
		Log.i("tag", "onResume");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
