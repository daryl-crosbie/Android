package com.example.acme;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class RemoteFetch {
	
	private static final String uri = "http://data.fixer.io/api/latest?access_key= FIXER API-KEY HERE";
	
	public static JSONObject getJSON(String base){
		try{
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer();
            String tmp;
            while((tmp = reader.readLine()) != null){
                json.append(tmp);
            }
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            return data;
		}catch(Exception e){
			return null;
		}
	}	
}
