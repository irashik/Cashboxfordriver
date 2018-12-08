package irashindmitrii.cashboxfordriver.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import irashindmitrii.cashboxfordriver.R;
import irashindmitrii.cashboxfordriver.Workday;
import irashindmitrii.cashboxfordriver.database.SqliteDatabaseWorkday;


/**
 * Created by dmitrii on 16.07.17.
 *
 * нужен чтобы работать с элементами списка смен
 * обрабатывает нажатия на кнопки просмотр
 * реализует методы обратного вызова для кнопки просмотр
 *
 *
 */

public class WorkdayAdapter extends CursorRecyclerViewAdapter<WorkdayViewHolder> {

    private LayoutInflater inflater;
    SqliteDatabaseWorkday mDatabaseWorkday;
    public static int currentposition;
    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void OnItemClicked(int position);

    }


    public WorkdayAdapter(Context context, Cursor cursor,  OnItemClickListener listener) {
        super(context, cursor);
        this.inflater = LayoutInflater.from(context);
        mDatabaseWorkday = new SqliteDatabaseWorkday(context);
        this.mOnItemClickListener = listener;

    }


    @Override
    public WorkdayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_workday, parent, false);
        WorkdayViewHolder vh = new WorkdayViewHolder(view);
        view.setTag(vh.position);
        return vh;
    }



    @Override
    public void onBindViewHolder(final WorkdayViewHolder holder, Cursor cursor) {

        Workday singleWorkday = Workday.fromCursor(cursor);
        if (holder != null) {
            holder.workday_starttime.setText(singleWorkday.getTimestart());
            holder.workday_endtime.setText(singleWorkday.getTimeend());
            holder.position = singleWorkday.getId();
        }

         // обработчик нажатия кнопки поиск (лупа) - просмотр смены через репорт
        holder.workday_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /*
                 * в этом методе происходит открытие фрагмента репорт
                 * для просмотра информации
                 */
                currentposition = holder.position;
                // открытие фрагмента Report метод Callback - реализация из примера.
                try {
                      mOnItemClickListener.OnItemClicked(currentposition);
                } catch (NullPointerException e) {
                    Log.e("Exception", e.toString());
                }
            }
        });
    }
}

