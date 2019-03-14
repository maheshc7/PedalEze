package com.smazee.product.pedaleze.model.rest.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by N.Mahesh on 09/03/2019.
 */

public class MessengerErrorResponse {

    public static Map<String,String> errorMessageMap = null;
    public static String GetErrorMessage(String errorCode) {
        if (errorMessageMap == null) {
            //lazy initialization
            errorMessageMap = new HashMap<String,String>();
            errorMessageMap.put("100", "Success");
            errorMessageMap.put("101", "Duplicate email id");
            errorMessageMap.put("102", "Unable to process");
            errorMessageMap.put("103", "email id not registered");
            errorMessageMap.put("164", "Invalid registration id");
            errorMessageMap.put("254", "Missing ipv4 or ipv6");
            //please add more
        }
        String errorMessage = errorMessageMap.get(errorCode);
        return (errorMessage == null) ? errorCode : errorMessage;
    }

    public MessengerErrorResponse() {

    }



        private String appendErrorMessage(String errorMessageString, String errorCode) {
            if (errorCode != null) {
                errorMessageString = (errorMessageString == "") ? errorMessageString : (errorMessageString += ",");
                errorMessageString += GetErrorMessage(errorCode);
            }
            return errorMessageString;
        }



}
