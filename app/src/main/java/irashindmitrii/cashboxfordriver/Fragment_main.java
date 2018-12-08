package irashindmitrii.cashboxfordriver;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dmitrii on 30.05.17.
 *
 * Этот фрагмент в программе наверное будет самый нагруженный обращениями.
 * Тут продумать реализацию работы sqlite - transaction
 * так чтобы данные гарантированно записывались в базу данных.
 * Логика тут простая.
 * Пользователь вводит сумму, выбирает флаг группы и щелкает записать
 * Код берет данные из полей и записывает их в таблицу payments
 * и выводит toast окошко
 * и очищает поля
 * Сделать фокус мигающего курсора в текстовом поле при открытии
 */


public class Fragment_main extends Fragment implements OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    private Button button;
    public RadioGroup mRadioGroup;
    private EditText editText;
    private String strEditText;
    private TextView selectitem;
    private SqliteDatabasePayment mDatabasePayment;
    private SQLiteDatabase db;
    private View view;
    private boolean redaction = false;
    private int editid;
    private Fragment_payment fragment_payment;
    private Context mcontext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());

        if (getArguments() != null) {
            redaction = getArguments().getBoolean("redaction");
            editid = getArguments().getInt("editid");
        }

        // реализация занесение в базу данных данных
        mDatabasePayment = new SqliteDatabasePayment(getActivity());
        db = mDatabasePayment.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, null);
        button =  (Button) view.findViewById(R.id.save_btn);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(this);

        selectitem = (TextView) view.findViewById(R.id.selected);
        editText = (EditText) view.findViewById(R.id.input_summ);
        editText.requestFocus();
        // присвоение обработчика кнопке
        button.setOnClickListener(this);
        /* меняем текст кнопки + фон */
        if (redaction) {
           LinearLayout ll = (LinearLayout) view.findViewById(R.id.frg_main);
           ll.setBackgroundResource(R.color.redaction_main);
           button.setText("Редактирование");
        }
        return view;
    }


    // слушатель для радиобуттон
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        CompoundButton cb = (CompoundButton) group.findViewById(checkedId);
        if (cb == null && cb.isChecked()) {
            RadioButton b = (RadioButton) getView().findViewById(R.id.radio_button_first);
            b.setChecked(true);
            if (selectitem == null) {
                selectitem.setText(cb.getText());
            }
        }

        switch (group.getCheckedRadioButtonId()) {
            case R.id.radio_button_first:
                selectitem.setText(cb.getText());
                break;
            case R.id.radio_button_second:
                selectitem.setText(cb.getText());
                break;
            case R.id.radio_button_third:
                selectitem.setText(cb.getText());
                break;
            case R.id.radio_button_four:
                selectitem.setText(cb.getText());
                break;
            case R.id.radio_button_five:
                selectitem.setText(cb.getText());
                break;
            case R.id.radio_button_six:
                selectitem.setText(cb.getText());
                break;
            case R.id.radio_button_seven:
                selectitem.setText(cb.getText());
                break;
        }

    }

    // слушатель для кнопки
    @Override
    public void onClick(View v) {

        strEditText = editText.getText().toString();
        if (TextUtils.isEmpty(strEditText)) {
            Toast.makeText(getActivity(), "Заполните сумму!",
                    Toast.LENGTH_SHORT).show();
        } else {
            // если для редактирования
            if (redaction) {
               // задание сеттеров через Payment.class и запись в базу
                Payment editpayment = new Payment();
                editpayment.setSum(Float.parseFloat(editText.getText().toString()));
                editpayment.setType(selectitem.getText().toString());
                // добавление данных в базу данных
                mDatabasePayment.updatePayment(editpayment, editid);

                // взятие из RadioGroup & EditText значений  & вывод сообщение тоаст
                Toast.makeText(getActivity(), "Изменено! " +
                                editText.getText() + "руб. " +
                                selectitem.getText(),
                            Toast.LENGTH_SHORT).show();

                // очистка полей после записи
                editText.getText().clear();
                // перевод РадиоБуттон на позицию наличные.
                RadioButton b = (RadioButton) getView().findViewById(R.id.radio_button_first);
                b.setChecked(true);

                // потом закрываем соединение
                 /*
                 обнуляем параметры если надо
                 открываем fragment_payment
                 уничножаем фрагмент (может это лучше)

                  */
                mDatabasePayment.close();
                editid = 0;
                redaction = false;

                fragment_payment = new Fragment_payment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment_payment)
                        .commit();
                // если сработает
                 onDestroy();

            } else {
                // обычный режим ввода платежей.
                // взятие времени записи суммы
                String now = DateFormat.getTimeInstance(
                        DateFormat.SHORT, Locale.ENGLISH
                ).format(new Date());

                // задание сеттеров через Payment.class и запись в базу
                Payment payment = new Payment();

                // задание паттерна для округления до сотых

                String str = editText.getText().toString();
                String summ = String.format(Locale.ENGLISH, "%.2f", Float.valueOf(str));

                payment.setSum(Float.parseFloat(summ));
                payment.setType(selectitem.getText().toString());
                payment.setTime(now);

                // добавление данных в базу данных
                mDatabasePayment.addPayment(payment);


                // взятие из RadioGroup & EditText значений  & вывод сообщение тоаст
                Toast.makeText(getActivity(), "Записано! " +
                                summ + "руб. " +
                                selectitem.getText() + " " +
                                now,
                        Toast.LENGTH_SHORT).show();


                // очистка полей после записи
                editText.getText().clear();
                // перевод РадиоБуттон на позицию наличные.
                RadioButton b = (RadioButton) getView().findViewById(R.id.radio_button_first);
                b.setChecked(true);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // установка курсора в поле после восстановления
        editText.requestFocus();
        // отображение клавиатуры автомачически
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(mcontext.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

    }

    @Override
    public void onStart() {
        super.onStart();
        // fавтоматическое отображение клавиатуры и установка курсора
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(mcontext.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDatabasePayment.close();
    }


}
