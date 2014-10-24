import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

class ExotelRedis{
	
	private static String STRING_TYPE = String.class.getName();
	private static String SORTED_SET_TYPE = SortedSet.class.getName();
	private class RedisData{
		Object data;
		long created_time;
		String dataType;
		public RedisData() {
			// TODO Auto-generated constructor stub
			this.created_time = -1;
			this.dataType = STRING_TYPE;
		}
	}
	
	private class list{
		HashMap<String, SortedMapData> member_map;
		//List<>
	}
	private class SortedMapData{
		int freq;
		String value;
	}
	
	HashMap<String, RedisData> hashMap = new HashMap<String, ExotelRedis.RedisData>();
	
	String insert(String key, String value)
	{
		
		
		RedisData curValue = hashMap.get(key);
		if(curValue != null){
			return "(integer) 0";
		}
		curValue = new RedisData();
	
		curValue.data = value;
		curValue.created_time = -1;
		curValue.dataType = STRING_TYPE;
		hashMap.put(key, curValue);
		return "(integer) 1";
	}
	String insert(String key, String value, long ttl)
	{
		RedisData prevValue = hashMap.get(key);
		RedisData curValue = null;
		
		if(prevValue != null){
			if(!prevValue.dataType.equals(STRING_TYPE)){
				hashMap.remove(key);
				curValue = new RedisData();
			}else{
				curValue = prevValue;
			}
		}
		else{
			curValue = new RedisData();
		}
		curValue.data = (Object)value;
		insertInMap(key, curValue);
		if(ttl != -1){
			curValue.created_time = Calendar.getInstance().getTimeInMillis() + ttl;
		}else{
			curValue.created_time = -1;
		}

		return "OK";
	}
	private void insertInMap(String key, RedisData value)
	{
		if(key == null || value == null){
			System.out.println("Error");
			return;
		}
		hashMap.put(key, value);
	}
	String insert(String key, int frequency, String value)
	{
		RedisData curValue = hashMap.get(key);
		if(curValue == null)
		{
			curValue = new RedisData();
			curValue.data = new TreeMap<SortedMapData, String>(new Comparator<SortedMapData>() {

				@Override
				public int compare(SortedMapData o1, SortedMapData o2) {
					// TODO Auto-generated method stub
					if(o1.freq > o2.freq)
						return 1;
					else if(o1.freq < o2.freq)
						return -1;
					else 
						return 0;
				}
			});
		}
		return "OK";
	}
	
	String insert(String key, String member_key, Double member_value){
		RedisData curValue = hashMap.get(key);
		
		if(curValue != null && curValue.dataType.equals(STRING_TYPE)){
			return "(error) WRONGTYPE Operation against a key holding the wrong kind of value";
		}
		if(curValue == null){
			curValue = new RedisData();
			curValue.dataType = SORTED_SET_TYPE;
			SortedSet value_set = new SortedSet();
			value_set.add(member_value, member_key);
			curValue.data = value_set;
			hashMap.put(key, curValue);
		}else{
			((SortedSet)curValue.data).add(member_value, member_key); 
		}
		return "OK";
	}
	
	int getCount(String key){
		if(hashMap.containsKey(key)){
				RedisData curValue = hashMap.get(key);
				if(curValue.dataType.equals(SORTED_SET_TYPE)){
					return ((SortedSet)curValue.data).getCount();
				}
		}
		return 0;
	}
	int getCount(String key, Double min_value, Double max_value){

		if(hashMap.containsKey(key)){
			RedisData curValue = hashMap.get(key);
			if(curValue.dataType.equals(SORTED_SET_TYPE)){
				return ((SortedSet)curValue.data).getCount(min_value, max_value);
				
			}else{
				return 0;
			}
		}
		return 0;
	}
	String getDummy(String key){
		if(hashMap.containsKey(key)){
			return ((SortedSet)hashMap.get(key).data).toString();
		}
		return null;
	}
	String get(String key)
	{
		RedisData curValue = hashMap.get(key);
		if(curValue == null)
			return "(nil)";
		else{
			if(curValue.data.getClass().getName().equals(STRING_TYPE)){
				if(curValue.created_time == -1)
					return (String)curValue.data;
				else{
					long curretTime = Calendar.getInstance().getTimeInMillis();
					if(curValue.created_time - curretTime > 0)
						return (String)curValue.data;
					else
						return "(nil)";
				}
			}
			else
				return "Error";
		}
		
	}
	
	String get(String key, int min_index, int max_index){
		if(hashMap.containsKey(key)){
			RedisData curValue = hashMap.get(key);
			if(curValue.dataType.equals(SORTED_SET_TYPE)){
				return ((SortedSet)curValue.data).get(min_index, max_index);
			}else{
				return "(error) WRONGTYPE Operation against a key holding the wrong kind of value";
			}
		}else{
			return "(empty list or set)";
		}
	}
	//String get(String key, )
}