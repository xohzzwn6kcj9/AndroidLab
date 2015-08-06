package com.example.student.lbs;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

public class SMSReceiverSettingActivity extends ListActivity implements OnClickListener{

    ArrayList<SMSReceiverData> listData;

    SMSReceiverAdapter ap;

    Button addBtn;
    Button saveBtn;
    Button removeBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smssetting);

        listData = new ArrayList<SMSReceiverData>();
        readDB();

        addBtn=(Button)findViewById(R.id.activity_smslist_add);
        saveBtn=(Button)findViewById(R.id.activity_smslist_save);
        removeBtn=(Button)findViewById(R.id.activity_smslist_remove);

        addBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        removeBtn.setOnClickListener(this);

        ap = new SMSReceiverAdapter(this, R.layout.smslist_item, listData);
        setListAdapter(ap);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (listData.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.emo_im_yelling);
            builder.setTitle("알림");
            builder.setMessage("SMS 수신자를 지정하셔야 합니다.").setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            dialog.cancel();
                            Intent intent = new Intent(Intent.ACTION_PICK, Uri
                                    .parse("content://com.android.contacts/data/phones"));
                            startActivityForResult(intent, 0);
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    private void readDB() {

        DBAdapter db = new DBAdapter(this, DBAdapter.SQL_CREATE_SMS, "sms");

        db.open();

        Cursor cursor = db.selectTable(
                new String[] { "_id", "name", "ischeck" }, null, null, null,
                null, null);

        if (cursor.moveToFirst()) {

            do {
                SMSReceiverData data = new SMSReceiverData();
                data.id = cursor.getInt(0);
                data.name = cursor.getString(1);
                if (cursor.getInt(2) == 0) {
                    data.isChecked = false;
                } else
                    data.isChecked = true;

                listData.add(data);

            } while (cursor.moveToNext());
        }
        db.close();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(v==addBtn){
            Intent intent = new Intent(Intent.ACTION_PICK, Uri
                    .parse("content://com.android.contacts/data/phones"));
            startActivityForResult(intent, 0);
        }else if(v==saveBtn){
            for (int i = 0; i < listData.size(); i++) {
                int isChecked = 0;

                Log.d("kkang", listData.get(i).name);

                if (listData.get(i).isChecked) {
                    isChecked = 1;

                }

                DBAdapter db = new DBAdapter(this, DBAdapter.SQL_CREATE_SMS,
                        "sms");

                db.open();

                ContentValues values = new ContentValues();
                values.put("ischeck", isChecked);

                db.updateTable(values, "_id", listData.get(i).id);

                db.close();
            }
            finish();
        }else if(v==removeBtn){
            DBAdapter db = new DBAdapter(this,DBAdapter.SQL_CREATE_SMS, "sms");

            db.open();

            //로컬 변수로 size 를 받지 않으면 나중에 Adapter 에서 remove 되어 size 가 동적으로 변경되어 정상적 갯수가 삭제되지 못하는 문제 발생
            //여러건 삭제시 또한 list 의 데이터가 계속 삭제됨으로 index 값을 삭제한 경우에는 --  으로 주어야 한다.
            int size=listData.size();
            int count=0;
            for (int i = 0; i < size; i++) {

                if (listData.get(i).isChecked) {

                    db.deleteTable("_id", listData.get(i).id);

                    ap.remove(listData.get(i));
                    i--;
                    if(listData.size()==0)
                        break;

                }
                count++;
                if(count==size)
                    break;

                Log.d("kkang","for end:"+i);

            }
            ap.notifyDataSetChanged();

            db.close();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            // 전화번호부 선택
            if (resultCode == RESULT_OK) {

                String phoneId = Uri.parse(data.getDataString())
                        .getLastPathSegment();
                Cursor cursor = getContentResolver().query(
                        Data.CONTENT_URI,
                        new String[] { Data._ID, Phone.NUMBER,
                                Data.DISPLAY_NAME },
                        Data._ID + "=" + phoneId, null, null);

                cursor.moveToFirst();

                String name = cursor.getString(2);
                String phone_number = cursor.getString(1);
                // DB 저장
                DBAdapter db = new DBAdapter(this, DBAdapter.SQL_CREATE_SMS,
                        "sms");

                db.open();
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("phone_number", phone_number);
                int dbId = (int) db.insertTable(values);
                db.close();

                SMSReceiverData rdata = new SMSReceiverData();
                rdata.id = dbId;
                rdata.name = name;
                rdata.isChecked = true;

                // Adpater 의 데이터 추가하면 화면에 적용되는 것 뿐아니라 arraylist 에도 자동으로 데이터 들어가게
                // 된다.
                // 그럼으로 listData 에 따로 추가해주지 않아도 된다.
                ap.add(rdata);
                ap.notifyDataSetChanged();


            }
        }
    }

}

class SMSReceiverAdapter extends ArrayAdapter<SMSReceiverData> {
    Context cxt;
    int resId;
    ArrayList<SMSReceiverData> list;

    SMSReceiverAdapter(Context cxt, int resId, ArrayList<SMSReceiverData> list) {
        super(cxt, resId, list);
        this.cxt = cxt;
        this.resId = resId;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        SMSReceiverWrapper wrapper;

        if (convertView == null) {

            // xml 로 레이아웃을 만들어 놓고 임의의 위치에서 xml 을 로딩하여 View 객체를 생성하고자 할때

            LayoutInflater vi = (LayoutInflater) cxt
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 우리가 사용하고자 하는 view 를 하나 초기화
            convertView = vi.inflate(resId, null);

            wrapper = new SMSReceiverWrapper(convertView);
            convertView.setTag(wrapper);

        }

        final SMSReceiverData o = list.get(position);

        if (o != null) {
            wrapper = (SMSReceiverWrapper) convertView.getTag();
            TextView nameView = wrapper.getNameView();
            CheckBox checkView = wrapper.getChechView();
            checkView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub

                    o.isChecked = isChecked;

                }
            });

            nameView.setText(o.name);
            checkView.setChecked(o.isChecked);

            if (o.isChecked)
                checkView.setChecked(true);
            else {
                checkView.setChecked(false);
            }
        }

        return convertView;
    }

}


class SMSReceiverWrapper {
    View base;
    TextView nameView;
    CheckBox chechView;

    SMSReceiverWrapper(View base) {
        this.base = base;
    }

    public TextView getNameView() {
        if (nameView == null)
            nameView = (TextView) base.findViewById(R.id.sms_name);
        return nameView;
    }

    public CheckBox getChechView() {
        if (chechView == null)
            chechView = (CheckBox) base.findViewById(R.id.sms_check);
        return chechView;
    }

}

class SMSReceiverData {
    int id;
    boolean isChecked;
    String name;
}
