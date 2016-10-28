package resjson.com.ipcservice.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by wl08029 on 2016/10/27.
 */

public class Book implements Parcelable {
    public String getmBookName() {
        return mBookName;
    }

    public void setmBookName(String mBookName) {
        this.mBookName = mBookName;
    }

    public int getmBookId() {
        return mBookId;
    }

    public void setmBookId(int mBookId) {
        this.mBookId = mBookId;
    }

    private String mBookName;
    private int mBookId;

    protected Book(Parcel in) {
        mBookName = in.readString();
        mBookId = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[0];
        }
    };

    @Override
    public String toString() {
        return mBookName + "--" + mBookId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Book(int mBookId, String mBookName){
        this.mBookId = mBookId;
        this.mBookName = mBookName;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mBookName);
        parcel.writeInt(mBookId);
    }
}
