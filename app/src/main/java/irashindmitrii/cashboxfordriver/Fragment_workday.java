package irashindmitrii.cashboxfordriver;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

import irashindmitrii.cashboxfordriver.adapter.WorkdayAdapter;
import irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday;
import irashindmitrii.cashboxfordriver.adapter.WorkdayAdapter.OnItemClickListener;


/**
 * Created by dmitrii on 30.05.17.
 *
 * нужен для создания списка смен:
 * берет данные в базе данных посредоством загрузчика
 * реализует callback методя для просмотра смены по нажатию на холдер
 *
 *
 *
 */

public class Fragment_workday extends Fragment
        implements LoaderCallbacks<Cursor>, OnItemClickListener {

    private SqliteDatabaseWorkday mDatabaseWorkday;
    private RecyclerView workdayView;
    private WorkdayAdapter workdayAdapter;
    private Fragment_input_info fragment_input_info;
    private Fragment_report fragment_report;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Fabric.with(getActivity(), new Crashlytics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.workday, null);
        workdayView = (RecyclerView) v.findViewById(R.id.workday_list);
        // создание экземпляра класса с базой данных.
        mDatabaseWorkday = new SqliteDatabaseWorkday(getActivity());
        // открытие ее для чтения
        mDatabaseWorkday.getReadableDatabase();

       // создание экземпляра класса адаптера
        workdayAdapter = new WorkdayAdapter(getActivity(), null, this);
        workdayView.setAdapter(workdayAdapter);

        // нужен для реализации RecycledView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        workdayView.setLayoutManager(linearLayoutManager);
        workdayView.setHasFixedSize(true);
        // инициализация загрузчика
        getLoaderManager().initLoader(0, null, this);
        return v;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabaseWorkday != null) {
            mDatabaseWorkday.close();
        }
    }




    // методы LoaderCallbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getActivity(), mDatabaseWorkday);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        workdayAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        workdayAdapter.swapCursor(null);
    }


    // вложенные класс Курсор лоадера
static class MyCursorLoader extends CursorLoader {
        private SqliteDatabaseWorkday mDatabaseWorkday;

        public MyCursorLoader(Context context, SqliteDatabaseWorkday db) {
            super(context);
            this.mDatabaseWorkday = db;
        }

        @Override
        public Cursor loadInBackground() {
           Cursor cursor = mDatabaseWorkday.getDataforList();
           return cursor;

        }
}


    // метод обратного вызова для просмотра отчета по смене
    @Override
    public void OnItemClicked(int id) {
        try {
            fragment_report = new Fragment_report();
            Bundle bundle = new Bundle();
            bundle.putInt("getid", id);
            bundle.putBoolean("openreport", true);
            fragment_report.setArguments(bundle);
            // смена фрагмента на репорт
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment_report)
                    .commit();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}