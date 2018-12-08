package irashindmitrii.cashboxfordriver.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import irashindmitrii.cashboxfordriver.Payment;
import irashindmitrii.cashboxfordriver.R;
import irashindmitrii.cashboxfordriver.database.SqliteDatabasePayment;

/**
 * Created by dmitrii on 16.07.17.
 *
 * Реализует адаптер для построения списка платежей
 * реализует методы обратного вызова для кнопок  удаления и редактирования
 *
 *
 */

public class PaymentsAdapter extends CursorRecyclerViewAdapter<PaymentsViewHolder> {

    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;
    private SqliteDatabasePayment mDatabasePayment;
    public static int sCurpos_payment;

    public interface OnItemClickListener {
        void OnItemClickEdit(int position);
        void OnItemClickDel();
    }


    public PaymentsAdapter(Context context, Cursor cursor,
                           OnItemClickListener listener) {
        super(context, cursor);
        this.inflater = LayoutInflater.from(context);
        mDatabasePayment = new SqliteDatabasePayment(context);
        this.mOnItemClickListener = listener;
    }


    @Override
    public PaymentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_cashbox, parent, false);
        PaymentsViewHolder vh = new PaymentsViewHolder(view);
        view.setTag(vh.position);
        return vh;
    }


    @Override
    public void onBindViewHolder(final PaymentsViewHolder holder, Cursor cursor) {

        Payment singlePayment = Payment.fromCursor(cursor);

        if (holder != null) {
            holder.payment_sum.setText(String.valueOf(singlePayment.getSum()));
            holder.payment_type.setText(singlePayment.getType());
            holder.payment_time.setText(singlePayment.getTime());
            holder.position = singlePayment.getId();
        }
        // обработчик кнопки редактирование
        holder.edit_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // открытие фрагмента для редактирования
                try {
                    mOnItemClickListener.OnItemClickEdit(holder.position);
                } catch (NullPointerException e) {
                    Log.e("Exception", e.toString());
                }
                sCurpos_payment = holder.position;
            }
        });


        // обработчик кнопки удаления записи
        holder.delete_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {

                mDatabasePayment.deletePayment(holder.position);
                // обертка для callback method
                try {
                    mOnItemClickListener.OnItemClickDel();
                } catch (NullPointerException e) {
                    Log.e("Exception", e.toString());
                }
            }
        });
    }
}


