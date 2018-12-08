package irashindmitrii.cashboxfordriver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import irashindmitrii.cashboxfordriver.Workday;

/**
 * Created by dmitrii on 16.07.17.
 *
 * он служит для взаимодействия с базой данных
 * в частности для работы с таблицей workday
 * при повышении версии базы данных найдет таблицу и удалит ее.
 * реализует стандартные методы CRUD
 * также метод для получения всех id - для возобновления смены - нужен последний
 * метод для получения данных из таблицы для отчета
 * метод для получени всех данных для построения списка.
 *
 *
 */

public class SqliteDatabaseWorkday extends SQLiteOpenHelper {

    @SuppressWarnings("WeakerAccess") // для избавления напоминая про доступ
    public static final int DATABASE_VERSION = 3;
    @SuppressWarnings("WeakerAccess")
    public static final String DATABASE_NAME = "cashboxdriver";

    public static final String TABLE_WORKDAY = "workday";

    public static final String WORKDAY_COLUMN_ID = "_id";
    public static final String WORKDAY_COLUMN_TIMESTART = "timestart";
    public static final String WORKDAY_COLUMN_TIMEEND = "timeend";
    public static final String WORKDAY_COLUMN_CALL = "call";
    public static final String WORKDAY_COLUMN_NAME = "namedriver";
    public static final String WORKDAY_COLUMN_MILAGE1 = "milage1";
    public static final String WORKDAY_COLUMN_MILAGE2 = "milage2";
    public static final String WORKDAY_COLUMN_FILLOIL = "filloil";
    public static final String WORKDAY_COLUMN_FILLOILEXP = "filloilexp";
    public static final String WORKDAY_COLUMN_CONSUPTION = "consuption";
    public static final String WORKDAY_COLUMN_COMMENT = "comment";
    public static final String WORKDAY_COLUMN_IDPAY = "nametablepayments";

    private SQLiteDatabase db;
    private Context mCtx;


    public SqliteDatabaseWorkday (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCtx = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String	CREATE_TABLE_WORKDAY = "CREATE	TABLE " + TABLE_WORKDAY +
                " (" + WORKDAY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                WORKDAY_COLUMN_TIMESTART + " TEXT, " +
                WORKDAY_COLUMN_TIMEEND + " TEXT, " +
                WORKDAY_COLUMN_CALL + " TEXT, " +
                WORKDAY_COLUMN_NAME + " TEXT, " +
                WORKDAY_COLUMN_MILAGE1 + " INTEGER, " +
                WORKDAY_COLUMN_MILAGE2 + " INTEGER, " +
                WORKDAY_COLUMN_FILLOIL + " REAL, " +
                WORKDAY_COLUMN_FILLOILEXP + " REAL, " +
                WORKDAY_COLUMN_CONSUPTION + " REAL, " +
                WORKDAY_COLUMN_COMMENT + " TEXT, " +
                WORKDAY_COLUMN_IDPAY + " TEXT)";

        db.execSQL(CREATE_TABLE_WORKDAY);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKDAY);
        onCreate(db);
    }




    // метод для записи данных в базу данных из формы окна
    public void addWorkday(Workday workday) {

        // здесь класс проходит по геттерам и вносит информацию в таблицу.
        ContentValues values = new ContentValues();

        values.put(WORKDAY_COLUMN_TIMESTART, workday.getTimestart());
        values.put(WORKDAY_COLUMN_TIMEEND, workday.getTimeend());
        values.put(WORKDAY_COLUMN_CALL, workday.getCall());
        values.put(WORKDAY_COLUMN_NAME, workday.getName());
        values.put(WORKDAY_COLUMN_MILAGE1, workday.getMilage1());
        values.put(WORKDAY_COLUMN_MILAGE2, workday.getMilage2());
        values.put(WORKDAY_COLUMN_FILLOIL, workday.getFilloil());
        values.put(WORKDAY_COLUMN_FILLOILEXP, workday.getFilloilexp());
        values.put(WORKDAY_COLUMN_CONSUPTION, workday.getConsuption());
        values.put(WORKDAY_COLUMN_COMMENT, workday.getComment());
        values.put(WORKDAY_COLUMN_IDPAY, workday.getIdpay());

        db = this.getWritableDatabase(); // подключение к базе данных
        db.insert(TABLE_WORKDAY, null, values);
    }



    //     для внесения изменений в таблицу
    public void updateWorkday(Workday workday, int id) {
        ContentValues values = new ContentValues();
        // сбор инфорации по геттерам
        values.put(WORKDAY_COLUMN_TIMESTART, workday.getTimestart());
        values.put(WORKDAY_COLUMN_TIMEEND, workday.getTimeend());
        values.put(WORKDAY_COLUMN_CALL, workday.getCall());
        values.put(WORKDAY_COLUMN_NAME, workday.getName());
        values.put(WORKDAY_COLUMN_MILAGE1, workday.getMilage1());
        values.put(WORKDAY_COLUMN_MILAGE2, workday.getMilage2());
        values.put(WORKDAY_COLUMN_FILLOIL, workday.getFilloil());
        values.put(WORKDAY_COLUMN_FILLOILEXP, workday.getFilloilexp());
        values.put(WORKDAY_COLUMN_CONSUPTION, workday.getConsuption());
        values.put(WORKDAY_COLUMN_COMMENT, workday.getComment());
        values.put(WORKDAY_COLUMN_IDPAY, workday.getIdpay());

        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = WORKDAY_COLUMN_ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };

        db.update(TABLE_WORKDAY, values, whereClause, whereArgs);

    }


    // метод для получения данных
    public Cursor getDataforList() {
        db = this.getReadableDatabase();
        String[] columns = { WORKDAY_COLUMN_TIMESTART, WORKDAY_COLUMN_TIMEEND, WORKDAY_COLUMN_ID };
        Cursor c = db.query(TABLE_WORKDAY, columns, null, null, null, null, null);
        return c;
    }

      // метод для получения данных для отчета
    public  Cursor getDataReport(int position) {
        int curpos = position;
        db = this.getReadableDatabase();

        String selection = WORKDAY_COLUMN_ID + " = ?"; // подстановка id
        String id = String.valueOf(curpos);
        String[] selectionArgs = { id };

        Cursor c = db.query(TABLE_WORKDAY, null, selection, selectionArgs, null, null, null);
        return c;
    }


    // метод для получения данных для возобновления смены
    public Cursor getFullListID() {
        db = this.getReadableDatabase(); // подключение к базе данных
        String[] columns = { WORKDAY_COLUMN_ID };
        Cursor c = db.query(TABLE_WORKDAY, columns, null, null, null, null, null);
        return c;

    }









}

