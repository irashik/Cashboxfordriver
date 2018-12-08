package irashindmitrii.cashboxfordriver.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import irashindmitrii.cashboxfordriver.R;

/**
 * Created by dmitrii on 05.08.17.
 */

public class WorkdayViewHolder extends RecyclerView.ViewHolder {

    public TextView workday_starttime, workday_endtime;
    public ImageView workday_search;
    int position;


    public WorkdayViewHolder(View itemView) {
        super(itemView);
        workday_starttime = (TextView)itemView.findViewById(R.id.wokday_starttime);
        workday_endtime = (TextView)itemView.findViewById(R.id.wokday_endtime);
        workday_search = (ImageView)itemView.findViewById(R.id.search_workday);




    }
}
