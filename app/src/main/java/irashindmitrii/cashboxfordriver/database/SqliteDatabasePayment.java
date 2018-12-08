package irashindmitrii.cashboxfordriver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import irashindmitrii.cashboxfordriver.Fragment_input_info;
import irashindmitrii.cashboxfordriver.Payment;

import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.DATABASE_NAME;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.DATABASE_VERSION;


/**
 * Created by dmitrii on 16.07.17.
 * Класс служит для взаимодействия с базой данных в частности для работы с
 * таблицами payment (они создаются для каждой смены).
 * Получает значение для создания имени таблицы (и создает таблицы)
 * При повышении версии базы - найдет и удалит все таблицы payment
 *
 *
 */

public class SqliteDatabasePayment extends SQLiteOpenHelper {

   private final String LOG_TAG = "log" + getClass().getSimpleName();
    private Context mCtx;


    /**
    * таблица workday будет одна, но там буду много строк - одна строка - одна смена
    * таблиц payments будет много с названиями соответствующие дате той смены, которые
    * будут привязываться к строчкам из таблицы workday/
     *
     * несколько методов для работы
     * 1. Метод взятие названия таблицы из времени (или из idpay смены).
     * 2. Метод создания таблицы он же onCreate
     * 3. Метод добавления записи к таблице
     * 4. Метод удаления записи из таблицы
     * 5. Метод изменения записи в таблице
     * 6. Метод получения курсора с данными для Adapter & Report class
     */

    public static final String PAYMENTS_COLUMN_ID = "_id";
    public static final String PAYMENTS_COLUMN_SUM = "sum";
    public static final String PAYMENTS_COLUMN_TYPE = "type";
    public static final String PAYMENTS_COLUMN_TIME = "time";

    private static String TABLE_PAYMENTS;
    private SQLiteDatabase db;


    public SqliteDatabasePayment(Context context) {
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCtx = context; // незнаю зачем контекст
     }

    /**
     * Логика работы следующая
     * Создается экземпляр класса
     * Создается или береться имя для таблицы
     * Создается таблица
     * Потом если окончание то закрывается таблица.
     *
     * @return
     */


     // реализация получения штампа времени (общей для обеих классов).
    @NonNull
    public static String getDataTimeforName() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmma", Locale.ENGLISH);
            String now = dateFormat.format(new Date());
            StringBuilder builder = new StringBuilder();
            builder.append("payments");
            builder.append(now);

            return builder.toString();
        }



    @Override
    public void onCreate(SQLiteDatabase db) {
        /** присвоение значения переменной
         * Логика:
         * 1. вводим данные в активити инпут и жмем начало смены
         * 2. через статический метод получаем штамп времени и записываем в setIdpay
         * 3. берем это значение из этого класса или из базы данных (но там еще не будет этой записи)
         * или получаем ее в этом классе
         * и присваеваем ее переменной TABLE_PAYMENTS
         * 4. работаем с этой таблицей.
         */


          // присвоение имени для таблицы (со временем начала смены)
           TABLE_PAYMENTS = Fragment_input_info.newnamepayment;

        if (TABLE_PAYMENTS == null) {
           throw new NullPointerException("exception");
        }

        String CREATE_TABLE_PAYMENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_PAYMENTS +
                " (" + PAYMENTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENTS_COLUMN_SUM + " REAL, " +
                PAYMENTS_COLUMN_TYPE + " TEXT, " +
                PAYMENTS_COLUMN_TIME + " TEXT);";
        db.execSQL(CREATE_TABLE_PAYMENTS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // метод для получиния курсора с именем таблиц
        Cursor findtable = this.findTable();

        // удаление таблиц
        if (findtable.moveToFirst()) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.setLength(0);
                for (String cn : findtable.getColumnNames())
                {
                    sb.append(cn + " = " + findtable.getString(findtable.getColumnIndex(cn)) + "; ");
                    String nametable = findtable.getString(findtable.getColumnIndex(cn));
                    db = this.getWritableDatabase();
                    db.execSQL("DROP TABLE " + nametable);
                }
            } while (findtable.moveToNext());
        }

        findtable.close();

    }


    public void openTable(String tablename) {
        TABLE_PAYMENTS = tablename;

    }

    // метод для записи данных в базу данных из формы окна
    public void addPayment(Payment payment) {

        ContentValues values = new ContentValues();
        values.put(PAYMENTS_COLUMN_SUM, payment.getSum());
        values.put(PAYMENTS_COLUMN_TYPE, payment.getType());
        values.put(PAYMENTS_COLUMN_TIME, payment.getTime());

        db = this.getWritableDatabase(); // подключение к базе данных
        db.insert(TABLE_PAYMENTS, null, values);
    }





// метод для изменения записи в таблице
    public void updatePayment(Payment payment, int id) {
        ContentValues values = new ContentValues();
        values.put(PAYMENTS_COLUMN_SUM, payment.getSum());
        values.put(PAYMENTS_COLUMN_TYPE, payment.getType());

        String whereClause = PAYMENTS_COLUMN_ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };

        db = this.getWritableDatabase();
        db.update(TABLE_PAYMENTS, values, whereClause, whereArgs);
    }




    // метод для удаления записи из таблицы
    public void deletePayment(int id) {
        String selection = PAYMENTS_COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db = this.getWritableDatabase();
        db.delete(TABLE_PAYMENTS, selection, selectionArgs);
    }


    // метод для получения данных (получаем все данные по таблице) для списка
    public Cursor getData() {

        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_PAYMENTS, null, null, null, null, null, null);
            return cursor;

        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "cursor is null" + e);
            Cursor cursor = null;
            return cursor;
           }


    }

    // метод для получения данных (получаем все данные по таблице) для Отчета
    public Cursor getDataReport(String tablename) {
        db = this.getReadableDatabase();
        return db.query(tablename, null, null, null, null, null, null);
    }





    // метод поиска таблиц для Upgrade
    private Cursor findTable() {
        //скрпит находит все таблицы начинающиеся на сочетание
        db = this.getReadableDatabase();
        Cursor findtable = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name like 'payments%'", null);
        return findtable;

    }

}
