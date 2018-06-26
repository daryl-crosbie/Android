package com.example.acme;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;
import android.os.Handler;
import android.widget.Toast;
import android.app.Activity;

public class Market extends Activity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Handler handler;
	private String base;
	private String date;
	private Map<String, Double> rateList = new TreeMap<String, Double>();
	
	public Market(){
		handler = new Handler();
		updateMarket("EUR");
	}
	
	public Market(String base, String date, Map<String,Double> rateList){
		handler = new Handler();
		this.base = base;
		this.date = date;
		this.rateList = rateList;
	}
	
	public void updateMarket(final String base){
		new Thread() {
            @Override
            public void run() {
		final JSONObject json = RemoteFetch.getJSON(base);
			if(json == null) {
				handler.post(new Runnable() {
                    @Override
                    public void run() {
                    	Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                    }
                });
	        } else {
	        	 handler.post(new Runnable() {
                     @Override
                     public void run() {
                    	 renderCurrency(json);
                     }
                 });
             }
          }
       }.start();
	}
	
	public void renderCurrency(JSONObject json){
		try{
			this.base = json.getString("base");
			this.date = json.getString("date");
			JSONObject rates = json.getJSONObject("rates");
			if(!base.equals("EUR")){
				rateList.put("EUR",rates.getDouble("EUR"));
			}
			if(!base.equals("USD")){
				rateList.put("USD",rates.getDouble("USD"));
			}
			if(!base.equals("GBP")){
				rateList.put("GBP",rates.getDouble("GBP"));
			}
			if(!base.equals("AUD")){
				rateList.put("AUD",rates.getDouble("AUD"));
			}
			if(!base.equals("CAD")){
				rateList.put("CAD",rates.getDouble("CAD"));
			}
			if(!base.equals("HKD")){
				rateList.put("HKD",rates.getDouble("HKD"));
			}
			if(!base.equals("THB")){
				rateList.put("THB",rates.getDouble("THB"));
			}
			if(!base.equals("JPY")){
				rateList.put("JPY",rates.getDouble("JPY"));
			}
			if(!base.equals("KRW")){
				rateList.put("KRW",rates.getDouble("KRW"));
			}
			if(!base.equals("SGD")){
				rateList.put("SGD",rates.getDouble("SGD"));
			}
			if(!base.equals("MYR")){
				rateList.put("MYR",rates.getDouble("MYR"));
			}
			if(!base.equals("PHP")){
				rateList.put("PHP",rates.getDouble("PHP"));
			}
			if(!base.equals("VND")){
				rateList.put("VND",rates.getDouble("VND"));
			}
			if(!base.equals("AED")){
				rateList.put("AED",rates.getDouble("AED"));
			}
			if(!base.equals("CNY")){
				rateList.put("CNY",rates.getDouble("CNY"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public Map<String, Double> getRates(){
		return rateList;
	}
	public String getBase(){
		return base;
	}
	public String getDate(){
		return date;
	}
	
}
