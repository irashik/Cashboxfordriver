package irashindmitrii.cashboxfordriver;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;

import irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment;
import irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday;

import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.TABLE_WORKDAY;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_CALL;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_COMMENT;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_CONSUPTION;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_FILLOIL;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_FILLOILEXP;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_ID;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_IDPAY;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_MILAGE1;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_MILAGE2;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_NAME;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_TIMEEND;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_TIMESTART;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dmitrii on 30.05.17.
 *
 * Этот класс реализует:
 * по нажатии на кнопку начала смены
 * 1. создание записи в таблице workday (создание таблицы)
 * 2. создание таблицы payment c именем которое содержит время начала смены
 *
 * Кнопка записать - записывает в базу данных информацию из полей (берет и полей
 * сначала). (либо обновляет поля)
 *
 * Кнопка окончания смены - записывает в строку таблицы workday данные
 * 2. потом закрывает таблицу payments
 * 3. открывает активити reports (там будут отображаться данные из одноименных полей +
 * + будет выборка из таблицы payments (cм. Report.xml)
 *
 * После нажатия кнопки окончания смены - очистка полей.
 * Отдельная реализация при возобновлении смены - загрузка данных из базы в поля.
 *
 */


public class Fragment_input_info extends Fragment implements View.OnClickListener {

    private Button btn_save,  btn_timestart, btn_timeend;
    private EditText input_numbercall, input_name, input_mileage1, input_mileage2,
                         input_petrol, input_petrol_exp, input_expense, input_comment;
    private TextView timestart, timeend;
    private SqliteDatabaseWorkday mDatabaseWorkday;
    private SqliteDatabasePayment mDatabasePayment;
    Workday newsetWorkday;
    Workday workday;
    public static String newnamepayment;
    private Fragment_report fragment_report;
    private View rootv;
    private SQLiteDatabase db;
    public static boolean sOPENGANG = false;
    public static int curgangid = 0;
    private OnBackClickListener mOnBackClickListener;
    private static boolean restore = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());

        // проверка передачи аргументов редактирования
        if (getArguments() != null) {
            restore = getArguments().getBoolean("restore");
           }
          // инициализация обратного вызова // честно, я не уверен нужен ли здесь trycatch
        try {
            mOnBackClickListener = (OnBackClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must improment");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // если view уже создан то метод его не создает заново
        if (rootv != null) {

        } else {

            rootv = inflater.inflate(R.layout.input_info, null);
            btn_save = (Button) rootv.findViewById(R.id.btn_input_save);
            btn_timestart = (Button) rootv.findViewById(R.id.input_timestart);
            btn_timeend = (Button) rootv.findViewById(R.id.input_timeend);

            input_numbercall = (EditText) rootv.findViewById(R.id.input_numbercall);
            input_name = (EditText) rootv.findViewById(R.id.input_name);
            input_mileage1 = (EditText) rootv.findViewById(R.id.input_mileage1);
            input_mileage2 = (EditText) rootv.findViewById(R.id.input_mileage2);
            input_petrol = (EditText) rootv.findViewById(R.id.input_filloil);
            input_petrol_exp = (EditText) rootv.findViewById(R.id.input_filloil_expense);
            input_expense = (EditText) rootv.findViewById(R.id.input_expense);
            input_comment = (EditText) rootv.findViewById(R.id.input_comments);

            timestart = (TextView) rootv.findViewById(R.id.timestart);
            timeend = (TextView) rootv.findViewById(R.id.timeend);

            // Создание экземпляра класса сеттеров
            newsetWorkday = new Workday();

            btn_timestart.setOnClickListener(this);
            btn_timeend.setOnClickListener(this);
            btn_save.setOnClickListener(this);
            btn_timeend.setEnabled(false);


            // ситуация если передан аргумент возобновления смены
                if (restore) {

                    btn_timeend.setEnabled(true);
                    btn_timestart.setEnabled(false);
                    mDatabaseWorkday = new SqliteDatabaseWorkday(getActivity());

                    // получения всех id таблицы и взятие последнего.
                    Cursor lastid = mDatabaseWorkday.getFullListID();

                    lastid.moveToLast();
                    int lastintid = lastid.getInt(lastid.getColumnIndex(WORKDAY_COLUMN_ID));
                    lastid.close();
                    curgangid = lastintid;

                    Cursor query = mDatabaseWorkday.getDataReport(curgangid);

                    query.moveToFirst();
                    int id = query.getInt(query.getColumnIndex(WORKDAY_COLUMN_ID));
                    String qtimestart = query.getString(query.getColumnIndex(WORKDAY_COLUMN_TIMESTART));
                    String qtimeend = query.getString(query.getColumnIndex(WORKDAY_COLUMN_TIMEEND));
                    String call = query.getString(query.getColumnIndex(WORKDAY_COLUMN_CALL));
                    String name = query.getString(query.getColumnIndex(WORKDAY_COLUMN_NAME));

                    int milage1 = query.getInt(query.getColumnIndex(WORKDAY_COLUMN_MILAGE1));
                    int milage2 = query.getInt(query.getColumnIndex(WORKDAY_COLUMN_MILAGE2));

                    float filloil = query.getFloat(query.getColumnIndex(WORKDAY_COLUMN_FILLOIL));
                    float filloilexp = query.getFloat(query.getColumnIndex(WORKDAY_COLUMN_FILLOILEXP));
                    float consuption = query.getFloat(query.getColumnIndex(WORKDAY_COLUMN_CONSUPTION));

                    String comment = query.getString(query.getColumnIndex(WORKDAY_COLUMN_COMMENT));
                    String idpay = query.getString(query.getColumnIndex(WORKDAY_COLUMN_IDPAY));

                    query.close();

                    workday = new Workday(id,
                            qtimestart, qtimeend, call,
                            name, milage1, milage2, filloil,
                            filloilexp, consuption, comment, idpay);

                    workday.setId(id);
                    workday.setTimestart(qtimestart);
                    workday.setTimeend(qtimeend);
                    workday.setCall(call);
                    workday.setName(name);
                    workday.setMilage1(milage1);
                    workday.setMilage2(milage2);
                    workday.setFilloil(filloil);
                    workday.setFilloilexp(filloilexp);
                    workday.setConsuption(consuption);
                    workday.setComment(comment);
                    workday.setIdpay(idpay);

                    // заполнение полей по полученным данным
                    timestart.setText(workday.getTimestart());
                    timeend.setText(workday.getTimeend());

                    input_numbercall.setText(workday.getCall());
                    input_name.setText(workday.getName());

                    input_mileage1.setText(String.valueOf(workday.getMilage1()));
                    input_mileage2.setText(String.valueOf(workday.getMilage2()));
                    input_petrol.setText(String.valueOf(workday.getFilloil()));
                    input_petrol_exp.setText(String.valueOf(workday.getFilloilexp()));
                    input_expense.setText(String.valueOf(workday.getConsuption()));

                    input_comment.setText(workday.getComment());

                    mDatabasePayment = new SqliteDatabasePayment(getActivity());
                    // нужно открыть нужную таблицу платежей
                    mDatabasePayment.openTable(workday.getIdpay());
                    db = mDatabasePayment.getWritableDatabase();

                    // присвоение переменной открытой смены значение true
                    sOPENGANG = true;
                    // для дальнейшей работы
                    newsetWorkday.setId(workday.getId());
                    newsetWorkday.setIdpay(workday.getIdpay());
                    curgangid = workday.getId();
                    restore = false;
                }
        }
        return rootv;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
                // кнопка начала новой смены
                case R.id.input_timestart:

                 /* по нажатию берется текущее время
                * создается запись в базе данных
                * создается таблица payment
                * отражается это время в активити
                * кнопка становиться неактивной до нажатия button timeend
                */

                // фиксация даты и времени и установка в текстовом поле
                     newnamepayment = mDatabasePayment.getDataTimeforName();

                    String now = DateFormat.getDateTimeInstance(
                            DateFormat.SHORT, DateFormat.SHORT, Locale.ENGLISH)
                            .format(new Date());

                    timestart.setText(now);
                    newsetWorkday.setIdpay(newnamepayment);

                /* получение подлкючения к базе данных (таблица Payments)
                *  происходит при открытии класса Fragment_main
                *  этот фрагмент создает экземпляр класса SqliteDatabasePayment
                *  a он в свою очередь берет беременную
                *  newnamepayment для метода onCreate
                **/

                    // запись данных из полей (установка сеттеров).
                    newsetWorkday.setTimestart(timestart.getText().toString());
                    newsetWorkday.setCall(input_numbercall.getText().toString());
                    newsetWorkday.setName(input_name.getText().toString());

                    // тут добавить исключение или смену на 0
                    try {
                        newsetWorkday.setMilage1(Integer.parseInt(input_mileage1.getText().toString()));
                    } catch (NumberFormatException ex) {
                        newsetWorkday.setMilage1(0);
                        input_mileage1.setText(String.valueOf(newsetWorkday.getMilage1()));


                    }
                    try {
                        newsetWorkday.setMilage2(Integer.parseInt(input_mileage2.getText().toString()));
                    } catch (NumberFormatException ex) {
                        newsetWorkday.setMilage2(0);
                        input_mileage2.setText(String.valueOf(newsetWorkday.getMilage2()));
                    }
                    try {
                        String str = input_petrol.getText().toString();
                        String summ = String.format(Locale.ENGLISH, "%.2f",
                                Float.valueOf(str));
                        newsetWorkday.setFilloil(Float.parseFloat(summ));

                    } catch (NumberFormatException ex) {
                        newsetWorkday.setFilloil(0.00f);
                        input_petrol.setText(String.valueOf(newsetWorkday.getFilloil()));
                    }
                    try {
                        String str = input_petrol_exp.getText().toString();
                        String summ = String.format(Locale.ENGLISH, "%.2f",
                                Float.valueOf(str));
                        newsetWorkday.setFilloilexp(Float.parseFloat(summ));

                    } catch (NumberFormatException ex) {
                        newsetWorkday.setFilloilexp(0.00f);
                        input_petrol_exp.setText(String.valueOf(newsetWorkday.getFilloilexp()));
                    }
                    try {
                        String str = input_expense.getText().toString();
                        String summ = String.format(Locale.ENGLISH, "%.2f",
                                Float.valueOf(str));
                        newsetWorkday.setConsuption(Float.parseFloat(summ));

                    } catch (NumberFormatException ex) {
                        newsetWorkday.setConsuption(0.00f);
                        input_expense.setText(String.valueOf(newsetWorkday.getConsuption()));
                    }
                    newsetWorkday.setComment(input_comment.getText().toString());

                    mDatabaseWorkday = new SqliteDatabaseWorkday(getActivity());
                    // запись полученных значений в базу данных
                    mDatabaseWorkday.addWorkday(newsetWorkday);

                    // сделать кнопку начала новой смены неактивной
                    btn_timestart.setEnabled(false);


                    // думаю тут и создать таблицу платежей, чтобы с ней работать
                    mDatabasePayment = new SqliteDatabasePayment(getActivity());
                    db = mDatabasePayment.getWritableDatabase();
                    mDatabasePayment.onCreate(db);

                    // присвоение переменной открытой смены значение true
                    sOPENGANG = true;
                    // получение записанного значение id
                    int id = 0;
                    String sql = "SELECT * FROM " + TABLE_WORKDAY +
                            " WHERE " + WORKDAY_COLUMN_IDPAY + " = ?";
                    String[] selectionArgs = {String.valueOf(newsetWorkday.getIdpay())};
                    Cursor getid = db.rawQuery(sql, selectionArgs);

                    if (getid.moveToFirst()) {
                        id = getid.getInt(0);
                    }
                    getid.close();
                    // устанавливаем сеттер
                    newsetWorkday.setId(id);
                    curgangid = id;
                    btn_timeend.setEnabled(true);
                    break;

            // кнопка сохранения изменений
            case R.id.btn_input_save:

                 // проверка условия (редактирование или обычная смена (перезапись))
                 // обработка нажатие при стандартном состоянии смены (сохранение)
                 /* по нажатию на кнопку передаются данные в класс  SqliteDatabaseWorkday
                  * и записываются в базу данных (или обновляютс)
                  * запись данных из полей (установка сеттеров).
                  */
                    newsetWorkday.setCall(input_numbercall.getText().toString());
                    newsetWorkday.setName(input_name.getText().toString());

                          /* Обертываю в try catch для того чтобы исключить запись (попытку записи)
                        в цифровое поле базы - пустого значения или символов кроме цифровых
                        заменяю на 0 в противном случае.
                        */
                  try {
                        newsetWorkday.setMilage1(Integer.parseInt(input_mileage1.getText().toString()));
                    } catch (NumberFormatException ex) {
                        newsetWorkday.setMilage1(0);
                        input_mileage1.setText(String.valueOf(newsetWorkday.getMilage1()));
                    }
                    try {
                        newsetWorkday.setMilage2(Integer.parseInt(input_mileage2.getText().toString()));
                    } catch (NumberFormatException ex) {
                        newsetWorkday.setMilage2(0);
                        input_mileage2.setText(String.valueOf(newsetWorkday.getMilage2()));
                    }
                    // попытка вставить пустое значение
                    try {

                        String str = input_petrol.getText().toString();
                        String summ = String.format(Locale.ENGLISH, "%.2f",
                                Float.valueOf(str));
                        newsetWorkday.setFilloil(Float.parseFloat(summ));

                    } catch (NumberFormatException ex) {
                        newsetWorkday.setFilloil(0.00f);
                        input_petrol.setText(String.valueOf(newsetWorkday.getFilloil()));
                    }
                    try {
                        String str = input_petrol_exp.getText().toString();
                        String summ = String.format(Locale.ENGLISH, "%.2f",
                                Float.valueOf(str));
                        newsetWorkday.setFilloilexp(Float.parseFloat(summ));

                    } catch (NumberFormatException ex) {
                        newsetWorkday.setFilloilexp(0.00f);
                        input_petrol_exp.setText(String.valueOf(newsetWorkday.getFilloilexp()));
                    }
                    try {
                        String str = input_expense.getText().toString();
                        String summ = String.format(Locale.ENGLISH, "%.2f",
                                Float.valueOf(str));
                        newsetWorkday.setConsuption(Float.parseFloat(summ));

                    } catch (NumberFormatException ex) {
                        newsetWorkday.setConsuption(0.00f);
                        input_expense.setText(String.valueOf(newsetWorkday.getConsuption()));
                    }
                    newsetWorkday.setComment(input_comment.getText().toString());
                    // метод для добавления данных в таблицу из класс SqliteDatabaseWorkday
                    mDatabaseWorkday = new SqliteDatabaseWorkday(getActivity());
                    // записываем все это делочерез этот метод по определенному id
                    mDatabaseWorkday.updateWorkday(newsetWorkday, newsetWorkday.getId());
                    break;


            // кнопка окончания смены
            case R.id.input_timeend:
                /*
                 по нажатию проставляется текущее время
                 записывается оно в базу данных и обновляются другие поля
                 закрывается таблица payment
                 отображается время в текстовом поле
                 вызывается фрагмент report class
                 происходит очистка полей
                 кнопка timestart становиться активной
                 */

                // фиксация даты и времени и установка в текстовом поле
                String now2 = DateFormat.getDateTimeInstance(
                        DateFormat.SHORT, DateFormat.SHORT, Locale.ENGLISH)
                        .format(new Date());

                timeend.setText(now2);
                //закрытие таблицы payments
                mDatabasePayment.close();

                // тут при закрытии смены следую добавить исключения для корректного и полного
                // заполнения полей.
                newsetWorkday.setTimestart(timestart.getText().toString());
                newsetWorkday.setTimeend(timeend.getText().toString());

                newsetWorkday.setCall(input_numbercall.getText().toString());
                newsetWorkday.setName(input_name.getText().toString());

                /* Обертываю в try catch для того чтобы исключить запись (попытку записи)
                в цифровое поле базы - пустого значения или символов кроме цифровых
                заменяю на 0 в противном случае.
                */
                try {
                    newsetWorkday.setMilage1(Integer.parseInt(input_mileage1.getText().toString()));
                } catch (NumberFormatException ex) {
                    newsetWorkday.setMilage1(0);
                    input_mileage1.setText(String.valueOf(newsetWorkday.getMilage1()));
                }
                try {
                    newsetWorkday.setMilage2(Integer.parseInt(input_mileage2.getText().toString()));
                } catch (NumberFormatException ex) {
                    newsetWorkday.setMilage2(0);
                    input_mileage2.setText(String.valueOf(newsetWorkday.getMilage2()));
                }
                // попытка вставить пустое значение
                try {

                    String str = input_petrol.getText().toString();
                    String summ = String.format(Locale.ENGLISH, "%.2f",
                            Float.valueOf(str));
                    newsetWorkday.setFilloil(Float.parseFloat(summ));

                } catch (NumberFormatException ex) {
                    newsetWorkday.setFilloil(0.00f);
                    input_petrol.setText(String.valueOf(newsetWorkday.getFilloil()));
                }
                try {
                    String str = input_petrol_exp.getText().toString();
                    String summ = String.format(Locale.ENGLISH, "%.2f",
                            Float.valueOf(str));
                    newsetWorkday.setFilloilexp(Float.parseFloat(summ));

                } catch (NumberFormatException ex) {
                    newsetWorkday.setFilloilexp(0.00f);
                    input_petrol_exp.setText(String.valueOf(newsetWorkday.getFilloilexp()));
                }
                try {
                    String str = input_expense.getText().toString();
                    String summ = String.format(Locale.ENGLISH, "%.2f",
                            Float.valueOf(str));
                    newsetWorkday.setConsuption(Float.parseFloat(summ));

                } catch (NumberFormatException ex) {
                    newsetWorkday.setConsuption(0.00f);
                    input_expense.setText(String.valueOf(newsetWorkday.getConsuption()));
                }
                newsetWorkday.setComment(input_comment.getText().toString());

                mDatabaseWorkday.updateWorkday(newsetWorkday, newsetWorkday.getId());
                mDatabaseWorkday.close();

                // сделать кнопку новой смены активной
                btn_timestart.setEnabled(true);
                btn_timeend.setEnabled(false);

                // очистить все поля edittext & textView
                timestart.setText("");
                timeend.setText("");
                input_numbercall.getText().clear();
                input_name.getText().clear();
                input_mileage1.getText().clear();
                input_mileage2.getText().clear();
                input_petrol.getText().clear();
                input_petrol_exp.getText().clear();
                input_expense.getText().clear();
                input_comment.getText().clear();


                fragment_report = new Fragment_report();
                Bundle bundle = new Bundle();
                bundle.putBoolean("openreport", true);
                bundle.putInt("curgangid", newsetWorkday.getId());
                fragment_report.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment_report)
                        .commit();
                sOPENGANG = false;
                restore = false;
                curgangid = 0;
                try {
                    mOnBackClickListener.onBackClick();
                } catch (NullPointerException e) {

                }
                rootv = null;
                break;
        }
    }



public interface OnBackClickListener {
    void onBackClick();
}



}
