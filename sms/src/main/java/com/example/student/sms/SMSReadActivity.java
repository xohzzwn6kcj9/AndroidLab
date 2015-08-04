package com.example.student.sms;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SMSReadActivity extends AppCompatActivity implements View.OnClickListener {

    Button sendBtn;
    EditText editView;
    ListView listView;

    String phoneNumber;

    ArrayList<SMSMessage> list;
    SMSReadAdapter adapter;

    MyApplication app;

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SMSMessage message = new SMSMessage();
            message.id = intent.getLongExtra(SmsTable.ID, 0L);
            message.phoneNumber = phoneNumber;
            message.date = SMSUtil.dateFormat(intent.getLongExtra(SmsTable.DATE, 0L));
            message.state = intent.getIntExtra(SmsTable.STATE, 0);
            message.content = intent.getStringExtra(SmsTable.CONTENT);

            list.add(message);
            adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        displayData(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsread);
        app = (MyApplication) getApplicationContext();

        sendBtn = (Button) findViewById(R.id.read_sendBtn);
        editView = (EditText) findViewById(R.id.read_edit);
        listView = (ListView) findViewById(R.id.read_listview);

        sendBtn.setOnClickListener(this);

        displayData(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("com.multi.ACTION_READ_ACTIVITY"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void displayData(Intent intent) {
        phoneNumber = intent.getStringExtra(SmsTable.PHONE_NUMBER);
        getSupportActionBar().setTitle(phoneNumber);

        app.displayPhoneNumber = phoneNumber;

        final SQLiteDatabase db = DbHelper.getInstance(this).getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(SmsTable.STATE, 1);
        db.update(SmsTable.TABLE_NAME, values, SmsTable.STATE + "=? and " + SmsTable.PHONE_NUMBER + "=?"
                , new String[]{"0", phoneNumber});

        Log.d("SmsReadActivity", "phoneNumber="+phoneNumber);
        final Cursor cursor = db.query(SmsTable.TABLE_NAME, null, SmsTable.PHONE_NUMBER+"=?",
                new String[]{phoneNumber}, null, null, null);
        list = new ArrayList<>();
        while(cursor.moveToNext()){
            final SMSMessage message= new SMSMessage();
            message.id = cursor.getLong(0);
            message.phoneNumber = phoneNumber;
            message.date = SMSUtil.dateFormat(cursor.getLong(2));
            message.state = cursor.getInt(3);
            message.content = cursor.getString(4);
            list.add(message);
        }
        adapter = new SMSReadAdapter(this, R.layout.item_read_left, list);
        listView.setAdapter(adapter);
        listView.setSelection(list.size()-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smsread, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** for sendBtn
     */
    @Override
    public void onClick(View v) {
        if(v == sendBtn){
            final SMSMessage message = SMSUtil.sendSMS(this, phoneNumber, editView.getText().toString(), "read");
            list.add(message);
            adapter.notifyDataSetChanged();
            adapter.add(message);
            listView.setSelection(list.size() - 1);
            editView.setText("");

        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        if(mode != null && mode.equals("read")){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else{

        }
        super.onBackPressed();
    }

}

class SMSReadAdapter extends ArrayAdapter<SMSMessage> {
    Context context;
    ArrayList<SMSMessage> list;

    public SMSReadAdapter(Context context, int resource, ArrayList<SMSMessage> list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list==null ? 0 : list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SMSMessage message = list.get(position);
        convertView = initializerView(message);

        final TextView contentView = (TextView) convertView.findViewById(R.id.read_content);
        final TextView dateView = (TextView) convertView.findViewById(R.id.read_date);
        contentView.setText(message.content);
        dateView.setText(message.date);

        return convertView;
    }
    private View initializerView(SMSMessage message){
        int resId = R.layout.item_read_left;
        if(message.state==2){
            resId=R.layout.item_read_right;
        }
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resId, null);
    }
}

