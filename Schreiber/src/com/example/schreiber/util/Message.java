package com.example.schreiber.util;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.widget.Toast;

public class Message {
	public static void messsage(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}

}
