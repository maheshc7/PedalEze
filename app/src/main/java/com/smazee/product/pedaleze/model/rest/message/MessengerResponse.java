package com.smazee.product.pedaleze.model.rest.message;

import com.google.gson.annotations.SerializedName;

/**
 * Created by N.Mahesh on 09/03/2019.
 */

public class MessengerResponse {
    public String getResponse() {
        return responseStatus;
    }

    private  String responseStatus;

    private  String message;

   public boolean isSuccess() {
       return (responseStatus.equals("success"));
   }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return ((responseStatus != null) ? "Response is " + responseStatus : "") +
                ((message != null) ? "Message is " + message : "");
    }
}
