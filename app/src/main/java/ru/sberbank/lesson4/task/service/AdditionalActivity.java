package ru.sberbank.lesson4.task.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.TextView;
import android.widget.Toast;

import static ru.sberbank.lesson4.task.service.ExampleService.MSG_SET_VALUE_FIELD;

public class AdditionalActivity extends Activity {
    private Messenger mService = null;
    private boolean mIsBound;
    private TextView mCallbackText;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ExampleService.MSG_SET_VALUE:
                    mCallbackText.append("Received from service: " + msg.getData().getString(MSG_SET_VALUE_FIELD) + " \n");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            mCallbackText.append("Attached\n");
            try {
                Message msg = Message.obtain(null,
                        ExampleService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                msg = Message.obtain(null,
                        ExampleService.MSG_SET_VALUE, 0, 0);
                Bundle data = new Bundle();
                data.putString(MSG_SET_VALUE_FIELD, getResources().getString(R.string.hello_from_activity));
                msg.setData(data);
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            Toast.makeText(AdditionalActivity.this, R.string.example_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mCallbackText.append("Disconnected\n");
            Toast.makeText(AdditionalActivity.this, R.string.example_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        bindService(new Intent(AdditionalActivity.this,
                ExampleService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        mCallbackText.append("Binding\n");
    }

    void doUnbindService() {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            ExampleService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                }
            }

            unbindService(mConnection);
            mIsBound = false;
            mCallbackText.append("Unbinding\n");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional);

        mCallbackText = (TextView)findViewById(R.id.logOutput);
        mCallbackText.append("Not attached\n");
        doBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
