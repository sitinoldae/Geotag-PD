package com.example.plndpt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MySMSBroadCastReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {

    //   Bundle bundle = intent.getExtras();
//        SmsMessage[] smsm = null;
//        String sms_str ="";

//        if (bundle != null)
//        {
//            // Get the SMS message
//            Object[] pdus = (Object[]) bundle.get("pdus");
//            smsm = new SmsMessage[pdus.length];
//            for (int i=0; i<smsm.length; i++){
//                smsm[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
//
//                sms_str += "\r\nMessage: ";
//                sms_str += smsm[i].getMessageBody().toString();
//                sms_str+= "\r\n";
//
//                String Sender = smsm[i].getOriginatingAddress();
//                //Check here sender is yours
//                Intent smsIntent = new Intent("otp");
//                smsIntent.putExtra("message",sms_str);
//
//                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
//
//            }
//        }

    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody().split(":")[1];

                    message = message.substring(0, message.length() - 1);
                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    Intent myIntent = new Intent("otp");
                    myIntent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                    // Show Alert

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

//        final Bundle bundle = intent.getExtras();
//
//        try {
//
//            if (bundle != null) {
//
//                final Object[] pdusObj = (Object[]) bundle.get("ID-SMSPDM");
//
//                for (int i = 0; i < pdusObj.length; i++) {
//
//                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
//                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
//
//                    String senderNum = phoneNumber;
//                    String message = currentMessage.getDisplayMessageBody().replaceAll("\\D", "");
//
//                    //message = message.substring(0, message.length()-1);
//                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
//
//                    Intent myIntent = new Intent("otp");
//                    myIntent.putExtra("message", message);
//                    myIntent.putExtra("number", senderNum);
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
//                    // Show Alert
//
//                } // end for loop
//            } // bundle is null
//
//        } catch (Exception e) {
//            Log.e("SmsReceiver", "Exception smsReceiver" + e);
//
//        }
//    }
}


