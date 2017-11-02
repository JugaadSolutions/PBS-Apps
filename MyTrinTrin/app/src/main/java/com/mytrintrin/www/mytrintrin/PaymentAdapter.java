package com.mytrintrin.www.mytrintrin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gwr on 9/4/17.
 */

public class PaymentAdapter extends ArrayAdapter<PaymentObject> {


    public PaymentAdapter(Context context, ArrayList<PaymentObject> payments) {
        super(context, 0, payments);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView==null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.payment_item, parent, false);
        }

        PaymentObject paymentObject = getItem(position);

        TextView paymentdate = (TextView) listItemView.findViewById(R.id.paymentdate);
        paymentdate.setText(""+paymentObject.getmPaymentDate());

        TextView paymentinvoice = (TextView) listItemView.findViewById(R.id.invoiceno);
        paymentinvoice.setText(""+paymentObject.getmPaymentInvoiceNo());

        TextView paymentmode = (TextView) listItemView.findViewById(R.id.payment);
        paymentmode.setText(""+paymentObject.getmPaymentMode());

        TextView paymentcredit = (TextView) listItemView.findViewById(R.id.paidamount);
        paymentcredit.setText(""+paymentObject.getmPaymentCredit());

        TextView paymentcreditordebit = (TextView) listItemView.findViewById(R.id.creditordebit_tv);
        paymentcreditordebit.setText(""+paymentObject.getmCreditorDebit());

        return listItemView;
    }
}
