// INewBookArrivedListener.aidl
package resjson.com.ipcservice.aidl;

// Declare any non-default types here with import statements

import resjson.com.ipcservice.aidl.Book;

interface INewBookArrivedListener {

    void onNewBookArrived(in Book book);

}
