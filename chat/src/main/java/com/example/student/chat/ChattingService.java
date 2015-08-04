package com.example.student.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ChattingService extends Service {
    public ChattingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
