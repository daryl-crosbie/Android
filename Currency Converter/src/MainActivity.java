package com.example.acme;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
	DecimalFormat df = new DecimalFormat("###,###,###.00");
	RelativeLayout backgroundColor;
	NumberPicker thisCurr, thatCurr;
	EditText thisCurVal;
	String thisVal;
	String[] currencies, countries;
	Market market;
	Button update;
	int pick1pos, pick2pos, color, txtStyle, txtSize;
	TextView dateLabel, date,thatCurVal, t1,t2;
	ListView ratesList;
	ArrayAdapter<String> adapter;
	boolean updated;
	Context context;
    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_main);
        
        getWindow().setSoftInputMode(
 				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        context = getApplicationContext();
        t1 = (TextView) findViewById(R.id.from);
        t2 = (TextView) findViewById(R.id.to);
        update = (Button) findViewById(R.id.updateBtn);
        update.setOnClickListener(this);
        currencies = getResources().getStringArray(R.array.currencies);
        countries = getResources().getStringArray(R.array.countries);
        date = (TextView) findViewById(R.id.currency_date);
        dateLabel = (TextView) findViewById(R.id.dateLabel);
        ratesList = (ListView) findViewById(R.id.ratesList);
        thisCurVal = (EditText)findViewById(R.id.this_currency_value);
        thatCurVal = (TextView)findViewById(R.id.that_currency_value);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        thisCurr = (NumberPicker) findViewById(R.id.this_currency);
        thatCurr = (NumberPicker) findViewById(R.id.that_currency);
        thisCurr.setDisplayedValues(getResources().getStringArray(R.array.currencies));
        thatCurr.setDisplayedValues(getResources().getStringArray(R.array.currencies));
        thisCurr.setMinValue(0);
        thisCurr.setMaxValue(currencies.length-1);
        thisCurr.setWrapSelectorWheel(true);
        thatCurr.setMinValue(0);
        thatCurr.setMaxValue(currencies.length-1);
        thatCurr.setWrapSelectorWheel(true);
        
		//Deals with rotation, saved instance state
        if(saved != null){
    		market = (Market) saved.getSerializable("market");
    		thisCurr.setValue(saved.getInt("pick1"));
    		thatCurr.setValue(saved.getInt("pick2")); 
    		thisCurVal.setText(saved.getString("amount"));
    		updated = saved.getBoolean("updated");
    		color = saved.getInt("color");
    		txtSize = saved.getInt("txtSize");
    		txtStyle = saved.getInt("txtStyle");
    		setStyling(color, txtSize, txtStyle);
    		setCountries();
		
		//Check for file containing exchange rates info
        }else if(ratesCheck()){
        	readRates();
        	loadPref();
        	thatCurr.setValue(1);
        	setCountries();
        	setStyling(color, txtSize, txtStyle);
		
		//No data, get connection and call for exchange rates
        }else{ 
        	setStyling(4, 2, 4);
        	thatCurr.setValue(1);
        	setCountries();
        	if(isNetworkAvailable()){
        		market = new Market();
        	}else{
        		Toast.makeText(getApplicationContext(), "Connection failure", Toast.LENGTH_LONG).show();
        	}	
        }
        //Number picker spin, change data, check connection for conversion
        thisCurr.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				t1.setText(countries[thisCurr.getValue()]);
				if(thisCurVal.getText().toString().isEmpty()){
					thatCurVal.setText("");
				}else if(isNetworkAvailable() || ratesCheck()){
					pick2pos = thatCurr.getValue();
					thatCurVal.setText(conversion(currencies[newVal],currencies[pick2pos]));
				}
			}
		});
        //Number picker spin, change data, check connection for conversion
        thatCurr.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				t2.setText(countries[thatCurr.getValue()]);
				if(thisCurVal.getText().toString().isEmpty()){
					thatCurVal.setText("");
				}else if(isNetworkAvailable() || ratesCheck()){
					pick1pos = thisCurr.getValue();
					thatCurVal.setText(conversion(currencies[pick1pos],currencies[newVal]));
				}
			}
		});
		
		// Check connection, apply listener to value entered field, convert exchange
        if(isNetworkAvailable() || ratesCheck()){
        	thisCurVal.addTextChangedListener(new TextWatcher(){
        		@Override
        		public void afterTextChanged(Editable s) {
        			if(s.toString().isEmpty()){
        				thatCurVal.setText(" ");
        			}else{
        				pick1pos = thisCurr.getValue();
        				pick2pos = thatCurr.getValue();
        				thatCurVal.setText(conversion(currencies[pick1pos],currencies[pick2pos]));
        			}
        		}
        		@Override
        		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        		}
        		@Override
        		public void onTextChanged(CharSequence s, int start, int before, int count) {	
        		}
        	});
        }else{
        	thatCurVal.setText("Update!");
        }
     }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	if(market != null){
    		showRates(updated);
    	}
    }
	
	//Data to be stored on rotation
	@Override
	protected void onSaveInstanceState(Bundle o) {
		super.onSaveInstanceState(o);
		o.putString("amount", thisCurVal.getText().toString());
		o.putInt("pick1", thisCurr.getValue());
		o.putInt("pick2", thatCurr.getValue());
		o.putSerializable("market", market);
		o.putBoolean("updated", updated);
		o.putInt("color", color);
		o.putInt("txtSize", txtSize);
		o.putInt("txtStyle", txtStyle);
	}
	
	public void setStyling(int c, int size, int style){
		changeBackColor(c);
		changeTxtSize(size);
		changeFontStyle(style);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		savePref();
		if(market != null){
			saveRates(market);
		}
	}
	
	// Load preferences from file 
	public void loadPref(){
		FileInputStream fis;
    	ObjectInputStream ois;
		try{
			fis = openFileInput("pref.ser");
			ois = new ObjectInputStream(fis);
			color = ois.readInt();
			txtSize = ois.readInt();
			txtStyle = ois.readInt();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error reading Preferences", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error reading Preferences", Toast.LENGTH_LONG).show();
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	// Check file for rates exists
	public boolean ratesCheck(){
		try{
			File dir = context.getFilesDir();
			File file = new File(dir, "exchange_rates.ser");
			if(file.exists()){
				return true;
			}
		}catch(Exception e){
			
		}
		return false;
	}
	
	//Read rates info from file, store in treeMap
	public void readRates(){
		FileInputStream fis;
    	ObjectInputStream ois;
		try{
			fis = openFileInput("exchange_rates.ser");
			ois = new ObjectInputStream(fis);
			String base = (String)ois.readObject();
			String date = (String)ois.readObject();
			@SuppressWarnings("unchecked")
			Map<String, Double> rateList = (TreeMap<String, Double>)ois.readObject();
			market = new Market(base, date, rateList);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "FILE Error reading", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "IO Error reading", Toast.LENGTH_LONG).show();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "CLASS Error reading", Toast.LENGTH_LONG).show();
		}
	}
	
	//Saves preferences to file 
	public void savePref(){
		try{
			FileOutputStream fos = getApplicationContext().openFileOutput("pref.ser", Context.MODE_PRIVATE);
			ObjectOutputStream ob = new ObjectOutputStream(fos);
			ob.writeInt(color);
			ob.writeInt(txtSize);
			ob.writeInt(txtStyle);
			ob.close();
			fos.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error Saving Pref", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error Saving Pref", Toast.LENGTH_LONG).show();
		}
	}
	
	public void saveRates(Market data){
		try{
			FileOutputStream fos = getApplicationContext().openFileOutput("exchange_rates.ser", Context.MODE_PRIVATE);
			ObjectOutputStream ob = new ObjectOutputStream(fos);
			ob.writeObject(data.getBase());
			ob.writeObject(data.getDate());
			ob.writeObject(data.getRates());
			ob.close();
			fos.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error Saving", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error Saving", Toast.LENGTH_LONG).show();
		}
	}
	
	//Update Market info, if no market create one
	@Override
	public void onClick(View v) {
		adapter.clear();
		if(isNetworkAvailable()){
			if(market != null){
				market.updateMarket(currencies[thisCurr.getValue()]);
			}else{
				market = new Market();
			}
			showRates(updated = true);
		}else{
			Toast.makeText(getApplicationContext(), "Connection failure", Toast.LENGTH_LONG).show();
		}
	}
	
	public void showDate(boolean updated){
		date.setText(market.getDate());
		if(updated){
			date.setTextColor(getResources().getColor(R.color.Green));
		}else{
			date.setTextColor(getResources().getColor(R.color.Red));
		}
	}
    
    public void showRates(boolean updated){
    	showDate(updated);
        for(Map.Entry<String,Double> entry : market.getRates().entrySet()){
        	String ex = String.valueOf(df.format(entry.getValue()));
        	if(ex.charAt(0) == '.'){
        		ex = 0+ex;
        	}
        	adapter.add("     "+market.getBase()+" / "+entry.getKey()+" :   1 / "+ex);
        }
        adapter.notifyDataSetChanged();
        ratesList.setAdapter(adapter);
    }
    
    public void setCountries(){
    	t1.setText(countries[thisCurr.getValue()]);
        t2.setText(countries[thatCurr.getValue()]);
    }
    
	// Convert the value of one currency to another, returns result
    public String conversion(String from, String to){
    	double value = Double.parseDouble(thisCurVal.getText().toString());
    	if(!from.equals(market.getBase())){
    		value /= market.getRates().get(from);
    	}
    	if(!to.equals(market.getBase())){
    		value *= market.getRates().get(to);
    	}
    	String val = df.format(value);
    	if(val.charAt(0) == ('.')){
    		val = 0+val;
    	}
    	return val;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	int id = item.getItemId();
		switch (id) {
		case R.id.night:
			changeBackColor(1);
			return true;
		case R.id.sunshine:
			changeBackColor(2);
			return true;
		case R.id.standard:
			changeBackColor(3);
			return true;
		case R.id.sml_txt:
			changeTxtSize(1);
			return true;
		case R.id.med_txt:
			changeTxtSize(2);
			return true;
		case R.id.lrg_txt:
			changeTxtSize(3);
			return true;
		case R.id.italic:
			changeFontStyle(1);
			return true;
		case R.id.bold:
			changeFontStyle(2);
			return true;
		case R.id.bold_italic:
			changeFontStyle(3);
			return true;
		case R.id.normal:
			changeFontStyle(4);
			return true;
	default:
			return super.onOptionsItemSelected(item);
		}
    }
    
    public void changeFontStyle(int style){
    	txtStyle = style;
    	if(style == 1){
    		t1.setTypeface(t1.getTypeface(), Typeface.ITALIC);
    		t2.setTypeface(t2.getTypeface(), Typeface.ITALIC);
    	}
    	else if(style == 2){
    		t1.setTypeface(t1.getTypeface(), Typeface.BOLD);
    		t2.setTypeface(t2.getTypeface(), Typeface.BOLD);
    	}
    	else if(style == 3){
    		t1.setTypeface(t1.getTypeface(), Typeface.BOLD_ITALIC);
    		t2.setTypeface(t2.getTypeface(), Typeface.BOLD_ITALIC);
    	}
    	else if(style == 4){
    		t1.setTypeface(t1.getTypeface(), Typeface.NORMAL);
    		t2.setTypeface(t2.getTypeface(), Typeface.NORMAL);
    	}
    }
    
    public void changeTxtSize(int size){
    	txtSize = size;
    	if(size == 1){
    		t1.setTextSize(16);
    		t2.setTextSize(16);
    	}
    	else if(size == 2){
    		t1.setTextSize(20);
    		t2.setTextSize(20);
    	}
    	else if(size == 3){
    		t1.setTextSize(24);
    		t2.setTextSize(24);
    	}
    }
    
    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                        .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }
    
    public void changeBackColor(int colorValue) {
		backgroundColor = (RelativeLayout) findViewById(R.id.background);
		color = colorValue;
	switch (colorValue) {
		case 1:
			setColors(getResources().getColor(R.color.NightBG), getResources().getColor(R.color.NightTxt),
					getResources().getColor(R.color.NightList));
			break;
		case 2:
			setColors(getResources().getColor(R.color.SunshineBG), getResources().getColor(R.color.SunshineTxt),
					getResources().getColor(R.color.SunshineList));
			break;
		case 3:
			setColors(getResources().getColor(R.color.White), getResources().getColor(R.color.Black),
					getResources().getColor(R.color.Grey));
			break;
		}
	}
	
	public void setColors(int back, int txt, int list){
		backgroundColor.setBackgroundColor(back);
		ratesList.setBackgroundColor(list);
		t1.setTextColor(txt);
		t2.setTextColor(txt);
		setNumberPickerTextColor(thisCurr, txt);
		setNumberPickerTextColor(thatCurr, txt);
		thisCurVal.setTextColor(txt);
		thatCurVal.setTextColor(txt);
		dateLabel.setTextColor(txt);
		update.setBackgroundColor(list);
	}
}
