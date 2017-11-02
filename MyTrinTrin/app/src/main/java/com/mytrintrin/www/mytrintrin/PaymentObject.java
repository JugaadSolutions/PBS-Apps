package com.mytrintrin.www.mytrintrin;

/**
 * Created by gwr on 9/4/17.
 */

public class PaymentObject {

    private String mPaymentDate;

    private String mPaymentInvoiceNo;

    private String mPaymentMode;

    private int mPaymentCredit ;

    private String mCreditorDebit;

    public PaymentObject(String paymentdate, String paymentinvoiceno, String paymentmode, int paymentcredit, String creditordebit)

    {
        mPaymentDate = paymentdate;
        mPaymentInvoiceNo = paymentinvoiceno;
        mPaymentMode = paymentmode;
        mPaymentCredit = paymentcredit;
        mCreditorDebit = creditordebit;
    }


    public String getmPaymentMode() {
        return mPaymentMode;
    }

    public String getmPaymentInvoiceNo() {
        return mPaymentInvoiceNo;
    }

    public String getmPaymentDate() {
        return mPaymentDate;
    }

    public int getmPaymentCredit() {
        return mPaymentCredit;
    }

    public String getmCreditorDebit() {
        return mCreditorDebit;
    }
}
