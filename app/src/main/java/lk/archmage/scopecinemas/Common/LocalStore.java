package lk.archmage.scopecinemas.Common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocalStore {

    private static SQLiteDatabase sdb;

    private static void createConnection(Context ct){

        sdb = ct.openOrCreateDatabase("ticketsData", Context.MODE_PRIVATE, null);
        sdb.execSQL("CREATE TABLE IF NOT EXISTS ticket_data (id int, imagePath varchar(255), theaterName varchar(55)," +
                " theaterCity varchar(45), movieName varchar(45), date varchar(20), time varchar(20)," +
                " seatsName varchar(100), ticketsNo varchar(100), price varchar(45)," +
                " seatCount varchar(20), fullSeat varchar(20), halfSeat varchar(20), " +
                "userId varchar(20), userName varchar(50), userMobile varchar(10), " +
                "userEmail varchar(40), category varchar(20), symbol varchar(10), couponId varchar(20), qrLink varchar(40), status varchar(10))");

        sdb.execSQL("CREATE TABLE IF NOT EXISTS myTheater_data (id integer primary key AUTOINCREMENT, theaterId varchar(10))");
    }

    public static void insert_Update_Delete(Context ct, String query){

        if(sdb == null){
            createConnection(ct);
        }

        sdb.execSQL(query);
    }

    public static Cursor search(Context ct, String query){

        if(sdb == null){
            createConnection(ct);
        }

        Cursor c = sdb.rawQuery(query, null);
        return c;
    }
}
