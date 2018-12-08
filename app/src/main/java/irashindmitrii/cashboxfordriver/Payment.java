package irashindmitrii.cashboxfordriver;

import android.database.Cursor;

import static irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment.PAYMENTS_COLUMN_ID;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment.PAYMENTS_COLUMN_TIME;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment.PAYMENTS_COLUMN_SUM;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment.PAYMENTS_COLUMN_TYPE;


/**
 * Created by dmitrii on 16.07.17.
 */

public class Payment {

    private int id;
    private float sum;
    private String type;
    private String time;


    public Payment (int id, float sum, String type, String time) {
        this.id = id;
        this.sum = sum;
        this.type = type;
        this.time = time;
    }

     // для сеттеров
    public Payment () {

    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public float getSum() {   return sum;    }
    public void setSum(float sum) {    this.sum = sum;   }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public static Payment fromCursor(Cursor cur) {
        Payment payment = new Payment();
        payment.setId(cur.getInt(cur.getColumnIndex(PAYMENTS_COLUMN_ID)));
        payment.setSum(cur.getFloat(cur.getColumnIndex(PAYMENTS_COLUMN_SUM)));
        payment.setType(cur.getString(cur.getColumnIndex(PAYMENTS_COLUMN_TYPE)));
        payment.setTime(cur.getString(cur.getColumnIndex(PAYMENTS_COLUMN_TIME)));

        return payment;

    }
}
