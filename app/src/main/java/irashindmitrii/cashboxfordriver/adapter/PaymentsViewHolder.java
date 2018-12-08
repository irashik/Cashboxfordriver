package irashindmitrii.cashboxfordriver.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import irashindmitrii.cashboxfordriver.R;

/**
 * Created by dmitrii on 05.08.17.
 */

public class PaymentsViewHolder extends RecyclerView.ViewHolder {

    public TextView payment_sum;
    public TextView payment_type;
    public TextView payment_time;
    public ImageView edit_payment;
    public ImageView delete_payment;
    int position;

    public PaymentsViewHolder(View itemView) {
        super(itemView);

        payment_sum = (TextView)itemView.findViewById(R.id.payment_sum);
        payment_type = (TextView)itemView.findViewById(R.id.payment_type);
        payment_time = (TextView)itemView.findViewById(R.id.payment_time);

        edit_payment = (ImageView)itemView.findViewById(R.id.edit_payment);
        delete_payment = (ImageView)itemView.findViewById(R.id.delete_payment);

    }
}
