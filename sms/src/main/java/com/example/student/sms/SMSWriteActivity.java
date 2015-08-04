package com.example.student.sms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

public class SMSWriteActivity extends Activity implements OnClickListener {

	EditText addressEdit;
	EditText contentEdit;

	ImageButton voiceBtn;
	ImageButton sendBtn;
	Button contactsBtn;

	HashMap<String, String> sendAddressList = new HashMap<String, String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_smswrite);

		addressEdit = (EditText) findViewById(R.id.write_numberEdit);

        contentEdit = (EditText) findViewById(R.id.write_contentEdit);

        voiceBtn = (ImageButton) findViewById(R.id.write_voiceBtn);
        voiceBtn.setOnClickListener(this);
        sendBtn = (ImageButton) findViewById(R.id.write_sendBtn);
        sendBtn.setOnClickListener(this);
        contactsBtn = (Button) findViewById(R.id.write_contactsBtn);
        contactsBtn.setOnClickListener(this);

	}

	private static final int CONTACT_BTN_REQUEST = 331;
	private static final int VOICE_REQUEST = 332;
	@Override
	public void onClick(View v) {
		if(v == contactsBtn){
			Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://com.android.contacts/data/phones"));
			startActivityForResult(intent, CONTACT_BTN_REQUEST);
		}
		else if(v == voiceBtn){
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "sms message");
			startActivityForResult(intent, VOICE_REQUEST);
		}
		else if(v == sendBtn){
			String sendList[] = addressEdit.getText().toString().split(";");
			for(int i=0; i<sendList.length; i++){
				String address = sendAddressList.get(sendList[i]);
				if(address==null){
					address = sendList[i];
				}
				SMSUtil.sendSMS(this, address, contentEdit.getText().toString(), "write");
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == CONTACT_BTN_REQUEST && resultCode == RESULT_OK){
			String phoneId = Uri.parse(data.getDataString()).getLastPathSegment();
			Cursor cursor = getContentResolver().query(
					ContactsContract.Data.CONTENT_URI,
					new String[] {ContactsContract.Data._ID,
						ContactsContract.CommonDataKinds.Phone.NUMBER,
						ContactsContract.Data.DISPLAY_NAME},
						ContactsContract.Data._ID+"="+phoneId,
					null,
					null);
			cursor.moveToFirst();


			setAddressView(cursor.getString(2));

			String dbNumber = cursor.getString(1);
			String txts[] = dbNumber.split("-");
			StringBuilder sb = new StringBuilder();
			for(String txt: txts){
				sb.append(txt);
			}
			sendAddressList.put(cursor.getString(2), sb.toString());
		}
		else if(requestCode == VOICE_REQUEST && resultCode == RESULT_OK){
			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			contentEdit.setText(results.get(0));
		}
	}

	private void setAddressView(String address) {
		String addressTxt = addressEdit.getText().toString();
		if (addressTxt.trim().length() > 0) {
			if (!addressTxt.endsWith(";"))
				addressTxt = addressTxt + ";";
		}
		addressTxt += address + ";";
        addressEdit.setText(addressTxt);

		int len = addressEdit.length();
        addressEdit.setSelection(len);

	}

}
