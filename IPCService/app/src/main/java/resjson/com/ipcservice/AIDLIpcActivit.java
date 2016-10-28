package resjson.com.ipcservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import resjson.com.ipcservice.aidl.Book;
import resjson.com.ipcservice.aidl.IBookManager;

/**
 * aidl IPC通信
 * Created by wl08029 on 2016/10/26.
 */

public class AIDLIpcActivit extends Activity {

    public TextView tv_aidl_ipc;
    public TextView tv_aidl_add_ipc;

    private IBookManager bookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aidl_ipc);

        Intent intent = new Intent(AIDLIpcActivit.this, AidlIpcService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        tv_aidl_ipc = (TextView) findViewById(R.id.tv_aidl_ipc);
        tv_aidl_ipc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(AIDLIpcActivit.this, getShowText(bookManager.getBookList()),Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        tv_aidl_add_ipc = (TextView) findViewById(R.id.tv_aidl_add_ipc);
        tv_aidl_add_ipc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book book = new Book(3, "PHP");
                try {
                    bookManager.addBook(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bookManager = IBookManager.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

}
