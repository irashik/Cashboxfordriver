package irashindmitrii.cashboxfordriver;

import android.database.Cursor;

import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_ID;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_TIMEEND;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_TIMESTART;

/**
 * Created by dmitrii on 16.07.17.
 */

public class Workday {

    private int id;
    private String timestart;
    private String timeend;
    private String call;
    private String name;
    private int milage1;
    private int milage2;
    private float filloil;
    private float filloilexp;
    private float consuption;
    private String comment;
    private String idpay;

     // метод для полной выборки для отчета Report
    public Workday(int id, String timestart, String timeend, String call,
                   String name, int milage1, int milage2, float filloil,
                   float filloilexp,
                   float consuption, String comment, String idpay) {

        this.id = id;
        this.timestart = timestart;
        this.timeend = timeend;
        this.call = call;
        this.name = name;
        this.milage1 = milage1;
        this.milage2 = milage2;
        this.filloil = filloil;
        this.filloilexp = filloilexp;
        this.consuption = consuption;
        this.comment = comment;
        this.idpay = idpay;

    }


    // метод для полной выборки для setter
    public Workday() {

    }

    public int getId() { return id;  }
    public void setId(int id) { this.id = id;  }

    public String getTimestart() { return timestart; }
    public void setTimestart(String timestart) { this.timestart = timestart; }

    public String getTimeend() { return timeend; }
    public void setTimeend(String timeend) { this.timeend = timeend; }

    public String getCall() { return call; }
    public void setCall(String call) { this.call = call; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMilage1() { return milage1; }
    public void setMilage1(int milage1) { this.milage1 = milage1; }

    public int getMilage2() { return milage2; }
    public void setMilage2(int milage2) { this.milage2 = milage2; }

    public float getFilloil() { return filloil; }
    public void setFilloil(float filloil) { this.filloil = filloil; }

    public float getFilloilexp() { return filloilexp; }
    public void setFilloilexp(float filloilexp) { this.filloilexp = filloilexp; }

    public float getConsuption() { return consuption; }
    public void setConsuption(float consuption) { this.consuption = consuption; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getIdpay() { return idpay; }
    public void setIdpay(String idpay) { this.idpay = idpay; }


    // метод нужен для класса CursorRecyclerViewAdapter
    public static Workday fromCursor(Cursor cur) {

        Workday workday = new Workday();
        workday.setId(cur.getInt(cur.getColumnIndex(WORKDAY_COLUMN_ID)));
        workday.setTimestart(cur.getString(cur.getColumnIndex(WORKDAY_COLUMN_TIMESTART)));
        workday.setTimeend(cur.getString(cur.getColumnIndex(WORKDAY_COLUMN_TIMEEND)));

        return workday;
    }


}
