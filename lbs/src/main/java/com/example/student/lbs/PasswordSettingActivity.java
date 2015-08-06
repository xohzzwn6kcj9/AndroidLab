package com.example.student.lbs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordSettingActivity extends Activity implements OnClickListener {

    EditText oldPassView;
    EditText newPassView;
    EditText confirmPassView;
    Button bt;

    String savedPassword;

    boolean isFirst = false;

    SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Auto-generated method stub

        setContentView(R.layout.activity_passwordsetting);

        oldPassView = (EditText) findViewById(R.id.pass_et1);
        newPassView = (EditText) findViewById(R.id.pass_et2);
        confirmPassView = (EditText) findViewById(R.id.pass_et3);

        bt = (Button) findViewById(R.id.pass_bt);
        bt.setOnClickListener(this);

        pref = getSharedPreferences("myPref", MODE_PRIVATE);
        savedPassword = pref.getString("password", "");

        // 처음 비밀번호 설정이면 이전비밀번호 비 활성화
        if (savedPassword.length() < 1) {
            oldPassView.setEnabled(false);
            oldPassView.setFocusable(false);
            isFirst = true;
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == bt) {
            SharedPreferences.Editor editor = pref.edit();

            String oldPass = oldPassView.getText().toString();
            String newPass = newPassView.getText().toString();
            String confirmPass = confirmPassView.getText().toString();
            if (newPass.length() < 4 || confirmPass.length() < 4) {
                showDialog("패스워드는 4자 이상 입력해야 합니다.");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                showDialog("패스워드와 패스워드 확인이 일치하지 않습니다.");
                return;
            }
            if (!isFirst) {
                if (oldPass.length() < 4) {
                    showDialog("이전 패스워드가 입력되지 않았습니다.");
                    return;
                }

                if (!savedPassword.equals(oldPass)) {
                    showDialog("이전 패스워드가 저장된 패스워드와 일치하지 않습니다.");
                    return;
                }

            }
            //모두다 통과이면 저장

            Toast t=Toast.makeText(this, "비밀번호가 정상적으로 변경되었습니다.", Toast.LENGTH_SHORT);
            t.show();

            editor.putString("password", newPass);
            editor.commit();

            Intent intent=new Intent(this,LocationListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.emo_im_yelling);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("확인", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

}
