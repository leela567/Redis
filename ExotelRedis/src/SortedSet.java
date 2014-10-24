import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

class SortedSet{
	
	TreeMap<Double, TreeMap<String, String>> value_treemap;
	HashMap<String, KeyTreemapData> key_treemap;
	TreeMap<KeyValueData, String> key_value_treemap;
	
	private class KeyTreemapData{
		Double value;
		KeyValueData key_value_obj;
		public KeyTreemapData(Double value, KeyValueData key_value_obj) {
			// TODO Auto-generated constructor stub
			this.value = value;
			this.key_value_obj = key_value_obj;
		}
	}
	private class KeyValueData{
		Double value;
		String key;
		public KeyValueData(Double value, String key) {
			// TODO Auto-generated constructor stub
			this.key = key;
			this.value = value;
		}
	}
	SortedSet(){
		value_treemap = new TreeMap<Double, TreeMap<String,String>>();
		key_treemap = new HashMap<String, SortedSet.KeyTreemapData>();
		key_value_treemap = new TreeMap<SortedSet.KeyValueData, String>(new Comparator<KeyValueData>() {

			@Override
			public int compare(KeyValueData o1, KeyValueData o2) {
				// TODO Auto-generated method stub
				if(o1.value < o2.value){
					return -1;
				}else if(o1.value > o2.value){
					return 1;
				}else{
					if(o1.key.compareTo(o2.key) < 0){
						return -1;
					}else if(o1.key.compareTo(o2.key) > 0){
						return 1;
					}
				}
				return 0;
			}
		});
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		Set<KeyValueData> keyvalueset = key_value_treemap.keySet();
		Iterator<KeyValueData> iterator = keyvalueset.iterator();
		StringBuilder builder = new StringBuilder();
		while(iterator.hasNext()){
			KeyValueData data = iterator.next();
			builder.append(data.key);
			builder.append(" ");
			builder.append(data.value);
			builder.append("\t");
		}
		return builder.toString();
	}
	
//	String zrange(int min_index, int max_index){
//		
//		Set<Double> valueSet = value_treemap.keySet();
//		Iterator<Double> valueIterator = valueSet.iterator();
//		int curIndex = 0;
//		int index = 0;
//		while(valueIterator.hasNext()){
//			curIndex += value_treemap.get(valueIterator.next()).size();
//			if(curIndex >= min_index)
//				break;
//			else
//				index += curIndex;
//		}
//		
//		
//		return null;
//	}
	String get(int min_index, int max_index){
		Set<KeyValueData> set = key_value_treemap.keySet();
		Iterator<KeyValueData> iterator = set.iterator();
		StringBuilder builder = new StringBuilder();
		if(max_index == -1)
			max_index = set.size();
		for(int index = 0;iterator.hasNext() && index <= max_index; index++){
			if(index < min_index){
				iterator.next();
				continue;
			}
			builder.append(min_index-index+1+") ");
			builder.append(iterator.next().key);
			builder.append("\n");
			
		}
		return builder.toString();
	}
	int getCount(){
		
		return key_value_treemap.size();
	}
	int getCount(Double min_value, Double max_value){
		
		Set<Double> valueset = value_treemap.subMap(min_value, true, max_value, true).keySet();
		Iterator<Double> iterator = valueset.iterator();
		int count = 0;
		while(iterator.hasNext()){
			count += value_treemap.get(iterator.next()).size();
		}
		return count;
	}
	void insertInValueTreeMap(Double value, String key){
		if(value_treemap.containsKey(value)){
			TreeMap<String, String> value_treemap_datamap = value_treemap.get(value);
			value_treemap_datamap.put(key, key);
			
		}else{
			TreeMap<String, String> value_treemap_datamap = new TreeMap<String, String>(new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					if(o1.compareTo(o2) < 0){
						return -1;
					}else if(o1.compareTo(o2) > 0){
						return 1;
					}
					return 0;
				}
			});
			value_treemap_datamap.put(key, key);
			value_treemap.put(value, value_treemap_datamap);
		}
	}
	void insertInValueTreeMap(Double prevValue, Double curValue, String key){
		
		if(prevValue == curValue) return;
		if(value_treemap.containsKey(prevValue)){
			TreeMap<String, String> prev_value_treemap_datamap = value_treemap.get(prevValue);
			prev_value_treemap_datamap.remove(key);
		}
		insertInValueTreeMap(curValue, key);
	}
	
	void update(Double value, String key){

		System.out.println("update");
		KeyTreemapData key_treemap_obj = key_treemap.get(key);
		insertInValueTreeMap(key_treemap_obj.value, value, key);
		key_value_treemap.remove(key_treemap_obj.key_value_obj);
		key_treemap_obj.key_value_obj.value = value;
		key_treemap_obj.value = value;
		key_value_treemap.put(key_treemap_obj.key_value_obj, key);
		
	}
	void insert(Double value, String key){
		System.out.println("insert");
		KeyValueData key_value_obj = new KeyValueData(value, key);
		KeyTreemapData key_treemap_obj = new KeyTreemapData(value, key_value_obj);
		key_treemap.put(key, key_treemap_obj);
		key_value_treemap.put(key_value_obj, key);
		insertInValueTreeMap(value, key);
	}
	void add(Double value, String key){
		if(key_treemap.containsKey(key)){
			update(value, key);
		}else{
			insert(value, key);
		}
	}
	
	
}