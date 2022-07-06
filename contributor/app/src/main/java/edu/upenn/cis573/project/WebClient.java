package edu.upenn.cis573.project;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class WebClient implements Runnable {

    private String host;
    private int port;
    protected String response;
    protected String request;

    public WebClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public WebClient() {

    }

    /**
     * Make an HTTP request to the RESTful API at the object's host:port
     * The request will be of the form http://[host]:[port]/[resource]?
     * followed by key=value& for each of the entries in the queryParams map.
     * @return the JSON object returned by the API if successful, null if unsuccessful
     */
    public String makeRequest(String resource, Map<String, Object> queryParams) {

        request = "http://" + host + ":" + port + resource + "?";

        for (String key : queryParams.keySet()) {
            request += key + "=" + queryParams.get(key) + "&";

        }
        Log.v("webclient", request);

        /*
        Web traffic must be done in a background thread in Android.
         */
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(this);

            // this waits for up to 1 second
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(1, TimeUnit.SECONDS);

            Log.v("webclient", response);
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }



    @Override
    public void run() {
        try {
            URL url = new URL(request);
            url.openConnection();
            Scanner in = new Scanner(url.openStream());
            response = "";
            while (in.hasNext()) {
                String line = in.nextLine();
                response += line;
            }

            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            response = e.toString();
        }
    }


}
