package irashindmitrii.cashboxfordriver;

/**
 * Это самый первый класс программы
 * он реализиует создание нижнего меню и добавляет верхнее меню
 * инициализирует фрагменты и обеспечивает переключение основных фрагментов
 * Наверное больше ему и ничего не надо делать. :-)
 * установлены зависимости для отправки исключениий на сервер
 * также реализует два Callback метода
 *
 */

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//todo полностью удали из программы данный интерфейс.
//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

import static irashindmitrii.cashboxfordriver.Fragment_input_info.sOPENGANG;


public class MainActivity extends AppCompatActivity implements Fragment_input_info.OnBackClickListener {


    private Fragment_input_info fragment_input_info;
    private Fragment_main fragment_main;
    private Fragment_report fragment_report;
    private Fragment_payment fragment_payment;
    private Fragment_workday fragment_workday;
    private DialogFragment myDialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        myDialogFragment = new MyDialogFragment();

        // обработчик нажатий на кнопки меню
        BottomNavigationView bottomNav = (BottomNavigationView)
                findViewById(R.id.navigation);
        bottomNav.setSelectedItemId(R.id.navigation_input);

        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                /*
                                тут проверяется условие, если смена открыта
                                то будет создаваться заново (для обновления полей)
                                если смена закрыта то будет возвращать преждний фрагмент
                                 */
                                if (sOPENGANG) {
                                    if (fragment_main != null) {
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, fragment_main)
                                                .commit();
                                    } else {
                                        fragment_main = new Fragment_main();
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, fragment_main)
                                                .commit();
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Сначала начните смену!!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;


                            case R.id.navigation_input:

                                if (fragment_input_info != null) {
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, fragment_input_info)
                                            .commit();
                                } else {
                                    fragment_input_info = new Fragment_input_info();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, fragment_input_info)
                                            .commit();
                                }
                                break;


                            case R.id.navigation_report:
                                    fragment_report = new Fragment_report();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, fragment_report)
                                            .commit();
                                break;


                            case R.id.navigation_payments:
                                if (sOPENGANG) {
                                        fragment_payment = new Fragment_payment();
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, fragment_payment)
                                                .commit();


                                } else {
                                    Toast.makeText(MainActivity.this, "Сначала начните смену!!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        return true;
                    }
                });

  // говорю о том что если вначале пусто то открываем Фрагмент_маин
        if (savedInstanceState == null) {
            // при первом запуске программы
            // добавляем в контейнер при помощи метода add()
            fragment_input_info = new Fragment_input_info();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment_input_info)
                    .commit();

        }
    }

// добавляю меню.
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
           case R.id.help:
                Intent intent3 = new Intent(this, HelpActivity.class);
                startActivity(intent3);
                return true;
            case R.id.menu_workday:
                fragment_workday = new Fragment_workday();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment_workday)
                        .commit();
                return true;
            case R.id.restore:
                fragment_input_info = new Fragment_input_info();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment_input_info)
                        .commit();

                Bundle bundle = new Bundle();
                bundle.putBoolean("restore", true);
                fragment_input_info.setArguments(bundle);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //  используется в нажатии клавиши окончание смены.
    @Override
    public void onBackClick() {
        fragment_input_info = new Fragment_input_info();
    }


@Override
    public void onBackPressed() {
        myDialogFragment.show(getFragmentManager(), "dialog");

}





}