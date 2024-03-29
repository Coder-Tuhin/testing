/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connection.HTTPConnection;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import utils.GlobalClass;

public class HttpReq extends AsyncTask<HttpReqPkg, String, String> {

	private int resCode;
	private String resMsg;

	private PerformHTTP.ResponseListener listener;
	private boolean showProgressDialog = true;


	public HttpReq() {
		resCode = 0;
		resMsg = "na";
	}

	public void setOnResultsListener(PerformHTTP.ResponseListener listener) {
		this.listener = listener;
	}

	public void setProgressDialog(boolean bool){
		this.showProgressDialog = bool;
	}


	@Override
	protected void onPreExecute() {
		try {
			if(showProgressDialog) {
				GlobalClass.showProgressDialog("Requesting...");
			}
			disableConnectionReuseIfNecessary();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String doInBackground(HttpReqPkg... params) {

		URL url;
		BufferedReader reader = null;

		String username = params[0].getUsername();
		String password = params[0].getPassword();
		String authStringEnc = null;

		if (username != null && password != null) {
			String authString = username + ":" + password;

			byte[] authEncBytes;
			authEncBytes = Base64.encode(authString.getBytes(), Base64.DEFAULT);
			authStringEnc = new String(authEncBytes);
		}

		String uri = params[0].getUri();

		if (params[0].getMethod().equals("GET")) {
			uri += "?" + params[0].getEncodedParams();
		}

		try {
			StringBuilder sb;
			// create the HttpURLConnection
			url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			if (authStringEnc != null) {
				connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			}

			// Http POST
			if (params[0].getMethod().equals("POST")) {
				//Set the POST req method
				connection.setRequestMethod("POST");

				// enable writing output to this url
				connection.setDoOutput(true);
			}

			// give it 15 seconds to respond
			connection.setConnectTimeout(10 * 1000);
			connection.setReadTimeout(10000);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.connect();

			if (params[0].getMethod().equals("POST")) {
				OutputStreamWriter writer = new OutputStreamWriter(
						connection.getOutputStream());
				try {

					writer.write(params[0].getEncodedParams());
					writer.flush();
					writer.close();
				}catch (IOException e){
					e.printStackTrace();
				}finally {
					if (writer != null){
						writer.close();
					}
				}

			}


			// read the output from the server
			InputStream in;
			resCode = connection.getResponseCode();
			resMsg = connection.getResponseMessage();
			if (resCode != HttpURLConnection.HTTP_OK)
				in = connection.getErrorStream();
			else
				in = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			sb = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			sb.append(resCode).append(" : ").append(resMsg);
			return sb.toString();
		} catch (IOException e) {
			listener.onFailure(Integer.toString(resCode) + " : " + resMsg);
			e.printStackTrace();
		} finally {
			// close the reader; this can throw an exception too, so
			// wrap it in another try/catch block.

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		try {
			GlobalClass.dismissdialog();
			if (listener != null && result != null)
				listener.onSuccess(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disableConnectionReuseIfNecessary() {
		// Work around pre-Froyo bugs in HTTP connection reuse.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

}
