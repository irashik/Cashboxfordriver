package irashindmitrii.cashboxfordriver;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;

import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

import irashindmitrii.cashboxfordriver.adapter.PaymentsAdapter;
import irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment;
import irashindmitrii.cashboxfordriver.adapter.PaymentsAdapter.OnItemClickListener;

/**
 * Created by dmitrii on 26.08.17.
 * Данные класс реализует создание списка listview во фрагменте
 * Плюс передает взаимодейтсвие через адаптер реализуя возможность удалять,
 * редактировать записи
 *
 *
 *
 */

public class Fragment_payment extends Fragment
        implements LoaderCallbacks<Cursor>, OnItemClickListener {


    private SqliteDatabasePayment mDatabasePayment; // почему избыточный когда private и почему при смене на public не дает ошибку.
    private PaymentsAdapter paymentsAdapter;
    private RecyclerView paymentView;
    private Fragment_main fragment_main;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(getActivity(), new Crashlytics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.payment, null);
        paymentView = (RecyclerView) v.findViewById(R.id.payment_list);

        mDatabasePayment = new SqliteDatabasePayment(getActivity());
        mDatabasePayment.getReadableDatabase();

        paymentsAdapter = new PaymentsAdapter(getActivity(), null, this);
        paymentView.setAdapter(paymentsAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        paymentView.setLayoutManager(linearLayoutManager);
        paymentView.setHasFixedSize(true);
        getLoaderManager().initLoader(0, null, this);

        return v;
    }

    // при уничтожении фрагмента вызывается onDestroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDatabasePayment != null) {
            mDatabasePayment.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

       getLoaderManager().getLoader(0).forceLoad();
        // тут нужно написать что проихойдет при восстановлении фрагмента
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getActivity(), mDatabasePayment);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
          paymentsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        paymentsAdapter.swapCursor(null);
    }

    // вложенные класс Курсор лоадера
    static class MyCursorLoader extends CursorLoader {
        SqliteDatabasePayment mDatabasePayment;
        private String LOG_TAG = "log";

        public MyCursorLoader (Context context, SqliteDatabasePayment db) {
            super(context);
            this.mDatabasePayment = db;
        }

        @Override
        public Cursor loadInBackground() {
            // получение курсора с данными через метод getData
            Cursor cursor = mDatabasePayment.getData();
            return cursor;
        }

    }




    // обработчик нажатия в адаптере для редактирования платежа
    @Override
    public void OnItemClickEdit(int id) {
       // нужно вызвать фрагмент для редактирования
        // думаю как то поставит пометку  при открытии фрагмента что это редактироввание
        fragment_main = new Fragment_main();
        Bundle bundle = new Bundle();

        // передаю истину редактирования и id
        bundle.putBoolean("redaction", true);
        bundle.putInt("editid", id);

        fragment_main.setArguments(bundle);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment_main)
                .commit();

    }

    // обработчик нажатия в адаптере для удаления и перезагрузки
    @Override
    public void OnItemClickDel() {
        // нужно для рестарта загрузчика
        getLoaderManager().getLoader(0).forceLoad();
    }

}




