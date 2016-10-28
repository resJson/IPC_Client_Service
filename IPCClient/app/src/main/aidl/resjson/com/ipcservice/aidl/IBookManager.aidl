// IBookManager.aidl
package resjson.com.ipcservice.aidl;

// Declare any non-default types here with import statements

import resjson.com.ipcservice.aidl.Book;
import resjson.com.ipcservice.aidl.INewBookArrivedListener;

interface IBookManager {
        List<Book> getBookList();
        void addBook(in Book book);
        void rigstNewBookArrivedListener(INewBookArrivedListener lister);
        void unrigstNewBookArrivedListener(INewBookArrivedListener lister);
}
