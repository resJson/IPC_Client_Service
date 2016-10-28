package resjson.com.ipcservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import resjson.com.ipcservice.aidl.Book;
import resjson.com.ipcservice.aidl.IBookManager;
import resjson.com.ipcservice.aidl.INewBookArrivedListener;

/**
 *
 * Created by wl08029 on 2016/10/27.
 */

public class AidlIpcService extends Service {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<INewBookArrivedListener> mINewBookArrivedListener = new RemoteCallbackList<>();

    private AtomicBoolean isServiceDestoryed = new AtomicBoolean(false);

    private Binder mBinder = new IBookManager.Stub(){
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void rigstNewBookArrivedListener(INewBookArrivedListener lister) throws RemoteException {
            mINewBookArrivedListener.register(lister);
        }

        @Override
        public void unrigstNewBookArrivedListener(INewBookArrivedListener lister) throws RemoteException {
            mINewBookArrivedListener.unregister(lister);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "iOS"));
        new Thread(new ServiceNewBookWorker()).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class ServiceNewBookWorker implements Runnable{
        @Override
        public void run() {
            while (!isServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int mBookId = mBookList.size() + 1;
                Book mNewBook = new Book(mBookId, "new Book " + mBookId);
                mBookList.add(mNewBook);

                int N = mINewBookArrivedListener.beginBroadcast();
                for(int i = 0; i < N; i++){
                    INewBookArrivedListener listener = mINewBookArrivedListener.getBroadcastItem(i);
                    if(listener != null){
                        try {
                            listener.onNewBookArrived(mNewBook);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mINewBookArrivedListener.finishBroadcast();
            }
        }
    }

    @Override
    public void onDestroy() {
        isServiceDestoryed.set(true);
        super.onDestroy();
    }
}
