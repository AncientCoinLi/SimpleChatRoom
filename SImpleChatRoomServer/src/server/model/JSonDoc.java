package server.model;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSonDoc {
	
	protected JSONObject obj;
	
	public JSonDoc(){
		obj=new JSONObject();
	}
	
	public JSonDoc(JSONObject obj){
		this.obj = obj;
	}
	
	@SuppressWarnings("unchecked")
	public void append(String key,String val){
		if(val==null){
			obj.put(key, null);
		} else {
			obj.put(key, new String(val));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void append(String key,JSonDoc doc){
		obj.put(key, doc.obj);
	}
	
	@SuppressWarnings("unchecked")
	public void append(String key,boolean val){
		obj.put(key, new Boolean(val));
	}
	
	@SuppressWarnings("unchecked")
	public void append(String key,ArrayList<?> val){
		JSONArray list = new JSONArray();
		for(Object o : val){
			if(o instanceof JSonDoc){
				list.add(((JSonDoc)o).obj);
			} else {
				list.add(o);
			}
		}
		obj.put(key,list);
	}
	
	@SuppressWarnings("unchecked")
	public void append(String key,long val){
		obj.put(key, new Long(val));
	}
	
	@SuppressWarnings("unchecked")
	public void append(String key,int val){
		obj.put(key, new Integer(val));
	}
	
	public String toJson(){
		return obj.toJSONString();
	}
	
	public static JSonDoc parse(String json) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj  = (JSONObject) parser.parse(json);
			return new JSonDoc(obj);
		} catch (ParseException e) {
			return new JSonDoc();
		} catch (ClassCastException e){
			return new JSonDoc();
		}
	}
	
	public boolean containsKey(String key){
		return obj.containsKey(key);
	}
	
	public String getString(String key){
		return (String) obj.get(key);
	}
	
	private ArrayList<Object> getList(JSONArray o){
		ArrayList<Object> list = new ArrayList<Object>();
		for(Object l : (JSONArray)o){
			if(l instanceof JSONObject){
				list.add(new JSonDoc((JSONObject) l));
			} else if(l instanceof JSONArray){
				list.add(getList((JSONArray) l));
			} else {
				list.add(l);
			}
		}
		return list;
	}
	
	public Object get(String key){
		Object o = obj.get(key);
		if(o instanceof JSONObject){
			return (Object) new JSonDoc((JSONObject) o);
		} else if(o instanceof JSONArray){
			return getList((JSONArray)o);
		} else {
			return o;
		}
		
	}
	
	public int getInteger(String key){
		return (int) obj.get(key);
	}
	
	public long getLong(String key){
		return (long) obj.get(key);
	}
	
	public boolean getBoolean(String key){
		return (boolean) obj.get(key);
	}
}
