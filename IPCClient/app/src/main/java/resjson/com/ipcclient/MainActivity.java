package resjson.com.ipcclient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import resjson.com.ipcservice.aidl.Book;
import resjson.com.ipcservice.aidl.IBookManager;
import resjson.com.ipcservice.aidl.INewBookArrivedListener;

public class MainActivity extends Activity {

    private IBookManager bookManager;
    private TextView tv_aidl_ipc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(this);

        Intent intent = new Intent("forServiceAidl");
        intent.setPackage("resjson.com.ipcservice");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        tv_aidl_ipc = (TextView) findViewById(R.id.tv_aidl_ipc);
        tv_aidl_ipc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book book = new Book(3, "PHP");
                try {
                    bookManager.addBook(book);
                    Toast.makeText(MainActivity.this, getShowText(bookManager.getBookList()),Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    showNotificationView((Book)msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private NotificationManager mNotificationManager;
    private Notification.Builder mBuilder;
    private Notification notify;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotificationView(Book obj) {
        mBuilder.setContentText(obj.getmBookName())
                .setTicker(obj.getmBookName())
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher);
        notify = mBuilder.build();
        mNotificationManager.notify(1, notify);
    }

    private INewBookArrivedListener mINewBookArrivedListener = new INewBookArrivedListener.Stub(){
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            mHandler.obtainMessage(1, book).sendToTarget();
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bookManager = IBookManager.Stub.asInterface(iBinder);
            try {
                bookManager.rigstNewBookArrivedListener(mINewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private String getShowText(List<Book> list) {
        String str = "";
        for(int i = 0; i< list.size(); i++){
            if(i == list.size() - 1){
                str += list.get(i).toString();
            }else{
                str += list.get(i).toString() + "\n";
            }
        }
        return str;
    }

    @Override
    protected void onDestroy() {
        if(bookManager != null && bookManager.asBinder().isBinderAlive()){
            try {
                bookManager.unrigstNewBookArrivedListener(mINewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
