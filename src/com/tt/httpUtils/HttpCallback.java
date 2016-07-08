package com.tt.httpUtils;

import org.json.JSONObject;

public interface  HttpCallback {
	
	public void onHttpResult(int code, JSONObject response);	
	public void onHttpResult(int code, String response);

}
