package com.tt.httpUtils;

import org.apache.http.HttpEntity;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class AsyncClient {

	private final static int TIME_OUT=5;
	private static AsyncHttpClient MCLIENT = new AsyncHttpClient();

	static {


		MCLIENT.setTimeout(TIME_OUT * 1000);
		MCLIENT.setConnectTimeout(TIME_OUT * 1000);
		MCLIENT.setResponseTimeout(TIME_OUT * 1000);

	}

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {

		MCLIENT.removeAllHeaders();
		MCLIENT.get(url, params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {

		MCLIENT.removeAllHeaders();
		MCLIENT.post(url, params, responseHandler);
	}

	public static void post(Context context, String url, HttpEntity entity,
			String contentType, AsyncHttpResponseHandler responseHandler) {
		SetHttpHeader();
		MCLIENT.post(context, url, entity, contentType, responseHandler);
	}

	public static void put(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		MCLIENT.removeAllHeaders();
		MCLIENT.put(url, params, responseHandler);
	}

	public static void put(Context context, String url, HttpEntity entity,
			String contentType, AsyncHttpResponseHandler responseHandler) {
		SetHttpHeader();
		MCLIENT.put(context, url, entity, contentType, responseHandler);
	}

	public static void SetHttpHeader() {
		MCLIENT.addHeader("Content-Type", "application/json");
		MCLIENT.addHeader("Accept", "application/json");
	}

	public static void RemoveHttpHeader() {
		MCLIENT.removeAllHeaders();
	}

	public static void CancelAllRequest() {
		MCLIENT.cancelAllRequests(true);
		Log.i("appLog", "取消网络操作");
	}

	public static void CancelRequest(Context context) {
		MCLIENT.cancelRequests(context, true);
		Log.i("appLog", "取消网络操作");
	}
}
