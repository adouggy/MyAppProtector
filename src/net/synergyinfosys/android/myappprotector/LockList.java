package net.synergyinfosys.android.myappprotector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public enum LockList {
	INSTANCE;
	
	private Context mContext;
	private SharedPreferences sharedPreferences;  
    private SharedPreferences.Editor editor;  
    
	public static final String LIST_FILE = "lock_list_file";
	
	LockList(){
	}
	
	public void setContext(Context context){
		this.mContext = context;
		
		sharedPreferences = mContext.getSharedPreferences(LIST_FILE, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}
	
	
	public Map<String, Boolean> loadList(){
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		Map<String, ?> all = sharedPreferences.getAll();
		Iterator<String> iter = all.keySet().iterator();
		while( iter.hasNext() ){
			String key = iter.next();
			Boolean value = (Boolean) all.get(key);
			map.put(key, value);
		}
		return map;
	}
	
	public void saveList( Map<String, Boolean> map){
		Iterator<String> iter = map.keySet().iterator();
		while( iter.hasNext() ){
			String key = iter.next();
			boolean value = map.get(key);
			editor.putBoolean(key, value);
		}
		editor.commit();
	}
	
	public void setList( String key, boolean value ){
		editor.putBoolean(key, value);
		editor.commit();
	}
}
