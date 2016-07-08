package com.tt.httpUtils;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class AsyncHttp {

	private Context mcontext;

	public AsyncHttp(Context context) {

		this.mcontext = context;
	}

	public void GetHttpClient(String url, final HttpCallback callback) {

		AsyncClient.get(url, null, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				callback.onHttpResult(statusCode, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String responseString) {
				// TODO Auto-generated method stub
				callback.onHttpResult(statusCode, responseString);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				callback.onHttpResult(statusCode, errorResponse);
				Log.i("appLog", "statusCode=" + statusCode);
				Log.i("appLog", "headers=" + headers);
				Log.i("appLog", "throwable=" + throwable.toString());
				Log.i("appLog", "errorResponse=" + errorResponse);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				callback.onHttpResult(statusCode, responseString);
				Log.i("appLog", "statusCode=" + statusCode);
				Log.i("appLog", "headers=" + headers);
				Log.i("appLog", "throwable=" + throwable.toString());
				Log.i("appLog", "responseString=" + responseString);
			}
		});
	}

	public void PostHttpClient(String url, RequestParams params,
			final HttpCallback callback) {
		try {

			AsyncClient.post(url, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					callback.onHttpResult(statusCode, response);
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String responseString) {
					// TODO Auto-generated method stub
					callback.onHttpResult(statusCode, responseString);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					// TODO Auto-generated method stub
					callback.onHttpResult(statusCode, errorResponse);
					Log.i("appLog", "statusCode=" + statusCode);
					Log.i("appLog", "headers=" + headers);
					Log.i("appLog", "throwable=" + throwable.toString());
					Log.i("appLog", "errorResponse=" + errorResponse);

				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					// TODO Auto-generated method stub
					callback.onHttpResult(statusCode, responseString);
					Log.i("appLog", "statusCode=" + statusCode);
					Log.i("appLog", "headers=" + headers);
					Log.i("appLog", "throwable=" + throwable.toString());
					Log.i("appLog", "responseString=" + responseString);

				}
			});
		} catch (Exception ex) {

		}
	}

	public void PostHttpClient(String url, JSONObject object,
			final HttpCallback callback) {

		try {
			StringEntity entity = new StringEntity(object.toString(), "utf-8");
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			AsyncClient.post(mcontext, url, entity,
					"application/json;charset=utf-8",
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							callback.onHttpResult(statusCode, response);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								String responseString) {
							// TODO Auto-generated method stub
							callback.onHttpResult(statusCode,
									responseString);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							// TODO Auto-generated method stub
							callback.onHttpResult(statusCode,
									errorResponse);
							Log.i("appLog", "statusCode=" + statusCode);
							Log.i("appLog", "headers=" + headers);
							Log.i("appLog", "throwable=" + throwable.toString());
							Log.i("appLog", "errorResponse=" + errorResponse);

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							// TODO Auto-generated method stub
							callback.onHttpResult(statusCode,
									responseString);
							Log.i("appLog", "statusCode=" + statusCode);
							Log.i("appLog", "headers=" + headers);
							Log.i("appLog", "throwable=" + throwable.toString());
							Log.i("appLog", "responseString=" + responseString);
						}
					});

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void PutHttpClient(String url, RequestParams params,
			final HttpCallback callback) {
		try {

			AsyncClient.put(url, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					callback.onHttpResult(statusCode, response);
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String responseString) {
					// TODO Auto-generated method stub
					callback.onHttpResult(statusCode, responseString);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					// TODO Auto-generated method stub
					callback.onHttpResult(statusCode, errorResponse);
					Log.i("appLog", "statusCode=" + statusCode);
					Log.i("appLog", "headers=" + headers);
					Log.i("appLog", "throwable=" + throwable.toString());
					Log.i("appLog", "errorResponse=" + errorResponse);

				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					// TODO Auto-generated method stub
					callback.onHttpResult(statusCode, responseString);
					Log.i("appLog", "statusCode=" + statusCode);
					Log.i("appLog", "headers=" + headers);
					Log.i("appLog", "throwable=" + throwable.toString());
					Log.i("appLog", "responseString=" + responseString);

				}
			});
		} catch (Exception ex) {

		}
	}

	public void PutHttpClient(String url, JSONObject object,
			final HttpCallback callback) {

		try {
			StringEntity entity = new StringEntity(object.toString(), "utf-8");
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			AsyncClient.put(mcontext, url, entity,
					"application/json;charset=utf-8",
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							Log.i("PushActivity", "statusCode=" + statusCode);
							Log.i("PushActivity", "headers=" + headers);
							Log.i("PushActivity",
									"response=" + response.toString());
							callback.onHttpResult(statusCode, response);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								String responseString) {
							// TODO Auto-generated method stub
							Log.i("PushActivity", "statusCode=" + statusCode);
							Log.i("PushActivity", "headers=" + headers);
							Log.i("PushActivity", "response=" + responseString);
							callback.onHttpResult(statusCode,
									responseString);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							// TODO Auto-generated method stub
							callback.onHttpResult(statusCode,
									errorResponse);
							Log.i("PushActivity", "statusCode=" + statusCode);
							Log.i("PushActivity", "headers=" + headers);
							Log.i("PushActivity",
									"throwable=" + throwable.toString());
							Log.i("PushActivity", "errorResponse="
									+ errorResponse);

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							// TODO Auto-generated method stub
							callback.onHttpResult(statusCode,
									responseString);
							Log.i("PushActivity", "statusCode=" + statusCode);
							Log.i("PushActivity", "headers=" + headers);
							Log.i("PushActivity",
									"throwable=" + throwable.toString());
							Log.i("PushActivity", "responseString="
									+ responseString);
						}
					});

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void CancelAllRequest() {
		try {
			AsyncClient.CancelAllRequest();
		} catch (Exception ex) {

		}
	}

	public void CancelRequest() {
		try {
			AsyncClient.CancelRequest(this.mcontext);
		} catch (Exception ex) {

		}
	}
}
