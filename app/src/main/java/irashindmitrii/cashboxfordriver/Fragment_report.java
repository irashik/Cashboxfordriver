package irashindmitrii.cashboxfordriver;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment;
import irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday;

import static irashindmitrii.cashboxfordriver.Fragment_input_info.sOPENGANG;
import static irashindmitrii.cashboxfordriver.Fragment_input_info.curgangid;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_CALL;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_COMMENT;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_CONSUPTION;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_FILLOIL;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_FILLOILEXP;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_IDPAY;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_MILAGE1;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_MILAGE2;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_NAME;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_TIMEEND;
import static irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday.WORKDAY_COLUMN_TIMESTART;

import java.util.Locale;


/**
 * Created by dmitrii on 30.05.17.
 *
 * Этот клас отображает сводную инфорацию по смене.
 * При открытии фрагмента класс загружает информацию из базы данных.
 * Обновление информации происходит при вызове из другого класса (из Fragment_workday)
 * либо из Fragment_input_info при нажатии на кнопку завершение смены.
 *
 * в планах. Здесь есть задумка реализовать (кнопку) сделать возможным отправлять этот отчет
 * на сервер... для печати и (или) хранения и дальнейшей обработки.
 *
 */

public class Fragment_report extends Fragment {

    TextView rs_call, rs_name, rs_data1, rs_data2, rs_proceeds_sum, rs_proceeds_cash,
    rs_proceeds_noncash, rs_proceeds_ticket, rs_proceeds_noticket, rs_proceeds_yandexcash,
            rs_proceeds_yandexnoncash, rs_proceeds_yandexbonus, rs_mileage1, rs_mileage2, rs_mileage,
    rs_petroil, rs_petroil_expense, rs_petroil_uexpense, rs_expense, rs_comments, rs_petrol_kmexpense;

    private SqliteDatabaseWorkday mDatabaseWorkday;
    private SqliteDatabasePayment mDatabasePayment;
    private String currenttable;
    private float filloilEXP = 0;
    private View view;
    private boolean openreport = false;

    private int curpos;

    private int cursoradapter;
    private int curgang;

    /**
     * Логика класса
     * При открытии происходит инициализация полей.
     * Получение курсора с данными и поля с id данные о котором нужны
     * (данные будут браться из базы данных)
     * Заполнение полей
     *
     * Соотсветственно для базы данных смен и базы данных платежей
     * Для платежей производятся операции суммирования
     * Для некоторых полей производтяся операции вычисления
     *
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());

        if (getArguments() != null) {
            openreport = getArguments().getBoolean("openreport");
            cursoradapter = getArguments().getInt("getid");
            curgang =  getArguments().getInt("curgangid");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // если перепенная открытия смены из input_info
        // или переменная переданная при закрытии смены true
        // то загружаем данные
        if (sOPENGANG | openreport ) {

            view = inflater.inflate(R.layout.report, null);
            //инициилазация полей
            rs_call = (TextView) view.findViewById(R.id.rs_call);
            rs_name = (TextView) view.findViewById(R.id.rs_name);
            rs_data1 = (TextView) view.findViewById(R.id.rs_data1);
            rs_data2 = (TextView) view.findViewById(R.id.rs_data2);

            rs_proceeds_sum = (TextView) view.findViewById(R.id.rs_proceeds_sum);
            rs_proceeds_cash = (TextView) view.findViewById(R.id.rs_proceeds_cash);
            rs_proceeds_noncash = (TextView) view.findViewById(R.id.rs_proceeds_noncash);
            rs_proceeds_ticket = (TextView) view.findViewById(R.id.rs_proceeds_ticket);
            rs_proceeds_noticket = (TextView) view.findViewById(R.id.rs_proceeds_noticket);
            rs_proceeds_yandexcash = (TextView) view.findViewById(R.id.rs_proceeds_yandexcash);
            rs_proceeds_yandexnoncash = (TextView) view.findViewById(R.id.rs_proceeds_yandexnoncash);
            rs_proceeds_yandexbonus = (TextView) view.findViewById(R.id.rs_proceeds_yandexbonus);

            rs_mileage1 = (TextView) view.findViewById(R.id.rs_mileage1);
            rs_mileage2 = (TextView) view.findViewById(R.id.rs_mileage2);
            rs_mileage = (TextView) view.findViewById(R.id.rs_mileage);
            rs_petroil = (TextView) view.findViewById(R.id.rs_petroil);
            rs_petroil_expense = (TextView) view.findViewById(R.id.rs_petroil_expense);
            rs_petroil_uexpense = (TextView) view.findViewById(R.id.rs_petroil_uexpense);
            rs_petrol_kmexpense = (TextView) view.findViewById(R.id.rs_petroil_kmexpense);

            rs_expense = (TextView) view.findViewById(R.id.rs_expense);
            rs_comments = (TextView) view.findViewById(R.id.rs_comments);

            // создание экземпляра класса Workday
            mDatabaseWorkday = new SqliteDatabaseWorkday(getActivity());



            /* проверка curpos ошибок быть не должно, но на всякий случай try catch
            * cursoradapter из адаптера
            * curgang  откуда? из input_info после окончания смены
            * curgangid from input_info
            */

            try {
                 curpos = checkID(cursoradapter, curgangid, curgang);
            } catch (Exception e) {
                  Log.e("Exception", e.toString());
              }




            // подключение к базе данных и получение курсора с данными
            Cursor query = mDatabaseWorkday.getDataReport(curpos);

            // запись данных из полей (установка сеттеров).
            if (query != null) {
                // как то тут кривовато
                query.moveToFirst();
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

                // установка get & set
                Workday workday = new Workday();

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

                // присваивания значения переменной для получения таблицы
                currenttable = workday.getIdpay();

                // запись значение через get workday
                rs_call.setText(workday.getCall());
                rs_name.setText(workday.getName());
                rs_data1.setText(workday.getTimestart());
                rs_data2.setText(workday.getTimeend());

                rs_mileage1.setText(String.valueOf(workday.getMilage1()));
                rs_mileage2.setText(String.valueOf(workday.getMilage2()));

                // разница между mileage2 & mileage1
                int difference = milage2 - milage1;
                rs_mileage.setText(String.valueOf(difference));
                // присвоение значения переменной для дальнейшего вычисления
                filloilEXP = workday.getFilloilexp();
                rs_petroil.setText(String.valueOf(workday.getFilloil()));
                rs_petroil_expense.setText(String.valueOf(workday.getFilloilexp()));

                rs_expense.setText(String.valueOf(workday.getConsuption()));
                rs_comments.setText(workday.getComment());

            }

            // создание экземпряра данных базы payment
            mDatabasePayment = new SqliteDatabasePayment(getActivity());
            Cursor querypayment = mDatabasePayment.getDataReport(currenttable);


            // инициилизация переменных для сумм
            float SUMcash = 0;
            float SUMtick = 0;
            float SUMnotik = 0;
            float SUMcard = 0;
            float SUMycash = 0;
            float SUMycard = 0;
            float SUMybon = 0;

            if (querypayment != null) {
                if (querypayment.moveToFirst()) {
                    do {
                        String type = querypayment.getString(querypayment.getColumnIndex(mDatabasePayment.PAYMENTS_COLUMN_TYPE));
                        float sum = querypayment.getFloat(querypayment.getColumnIndex(mDatabasePayment.PAYMENTS_COLUMN_SUM));

                  /*      тут внимательно потому что по этип названиям сортируется таблица
                        <string name="cash">Наличные</string>
                        <string name="ticket">Талон</string>
                        <string name="noticket">Без талона</string>
                        <string name="by_card">По карте</string>
                        <string name="by_yandexcash">Яндекс наличные</string>
                        <string name="by_yandexnoncash">Яндекс по карте</string>
                        <string name="by_yandexbonus">Яндекс бонус</string>
                    потом полученную сумму записываем в поля textView
                     */

                        switch (type) {
                            case "Наличные":
                                SUMcash = SUMcash + sum;
                                break;
                            case "Талон":
                                SUMtick = SUMtick + sum;
                                break;
                            case "Без талона":
                                SUMnotik = SUMnotik + sum;
                                break;
                            case "По карте":
                                SUMcard = SUMcard + sum;
                                break;
                            case "Яндекc наличные":
                                SUMycash = SUMycash + sum;
                                break;
                            case "Яндекс по карте":
                                SUMycard = SUMycard + sum;
                                break;
                            case "Яндекс бонус":
                                SUMybon = SUMybon + sum;
                                break;
                        }
                    } while (querypayment.moveToNext());
                }

                querypayment.close();
            }

            // общая выручка
            rs_proceeds_sum.setText(String.valueOf(SUMcash + SUMcard + SUMnotik + SUMtick + SUMybon + SUMycard + SUMycash));
            // общая выручка наличными
            rs_proceeds_cash.setText(String.valueOf(SUMcash));
            // общая выручка по картам
            rs_proceeds_noncash.setText(String.valueOf(SUMcard));
            // общая выручка по талонам
            rs_proceeds_ticket.setText(String.valueOf(SUMtick));
            // общая выручка без талонов
            rs_proceeds_noticket.setText(String.valueOf(SUMnotik));
            // общая выручка яндекс наличные
            rs_proceeds_yandexcash.setText(String.valueOf(SUMycash));
            // общая выручка яндекс безнал
            rs_proceeds_yandexnoncash.setText(String.valueOf(SUMycard));
            // общая выручка яндекс бонус
            rs_proceeds_yandexbonus.setText(String.valueOf(SUMybon));


            // удельный расход бензина на руб.
            // нужно  расход бензина в рублях поделить на общую сумму выручки
            float specific_val = filloilEXP / (SUMcash + SUMcard + SUMnotik + SUMtick + SUMybon + SUMycard + SUMycash);
            String summ = String.format(Locale.ENGLISH, "%.3f", specific_val);
            rs_petroil_uexpense.setText(summ);

            // удельные расход бензина на км
            float specific_val2 = Float.parseFloat(rs_petroil.getText().toString()) / ((Integer.parseInt(rs_mileage.getText().toString()))/100);
            String summ2 = String.format(Locale.ENGLISH, "%.3f", specific_val2);
            rs_petrol_kmexpense.setText(summ2);

              // после загрузки информации по значению этой переменной
            // она снова становиться false
            openreport = false;
            return view;

        } else {

             // иначе возвращаем пустой вид.
             view = inflater.inflate(R.layout.report, null);
             return view;

        }
    }




    private static int checkID(int curposadapter, int curgangid, int curgang) throws Exception {
        int curpos;

        if (curgangid != 0 && curposadapter != 0 && curgang !=0) {
             throw new Exception("myException id not idintifed");
        }
        else if (curposadapter != 0) {
            curpos = curposadapter;
        }
        else if (curgangid != 0) {
            curpos = curgangid;
        }
        else if (curgang != 0) {
            curpos = curgang;
        }
        else {
            throw new Exception("myException checkID: id=0");
        }
        return curpos;
    }

}