package ru.mobilap.network;

import android.util.Log;
import android.net.Uri;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class DownloadFilesTask extends AsyncTask<URL, Integer, String> {

    public interface ResultListener {
        public abstract void onResultString(final int code, final String body);
    }

    private final String TAG = DownloadFilesTask.class.getName();
    private ResultListener _listener = null;
    private int responseCode = 0;

    public void setResultListener(final ResultListener l) {
        _listener = l;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    protected String doInBackground(URL... urls) {
        int count = urls.length;
        if(count <= 0) return null;
        try {
            URL url = urls[0];
            HttpURLConnection c = (HttpURLConnection) url.openConnection();

            responseCode = c.getResponseCode();
            String responseMessage = c.getResponseMessage();

            if (responseCode >= 400) {
                String resp = responseCode + " " + responseMessage;
                Log.w(TAG, resp);
                String err = convertStreamToString(c.getErrorStream());
                Log.e(TAG, "Downloading error: " + err);
                return err;
            } else {
                String body = convertStreamToString(c.getInputStream());
                return body;
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(Integer[] progress) {
        //Log.w(TAG, "Downloading progress: " + progress[0]);
    }

    protected void onPostExecute(String result) {
        //Log.w(TAG, "Downloaded " + result + " bytes");
        super.onPostExecute(result);
        if(_listener != null) {
            _listener.onResultString(responseCode, result);
        }
    }
}
