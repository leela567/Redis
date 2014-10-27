import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

class ExotelRedis implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String STRING_TYPE = String.class.getName();
	private static String SORTED_SET_TYPE = SortedSet.class.getName();
	private static String STRING_BIT_TYPE = "BIT_TYPE";
	private class RedisData{
		Object data;
		long created_time;
		String dataType;
		byte[] byteArray;
		public RedisData() {
			// TODO Auto-generated constructor stub
			this.created_time = -1;
			this.dataType = STRING_TYPE;
		}
	}
	
	
	HashMap<String, RedisData> hashMap = new HashMap<String, ExotelRedis.RedisData>();
	
	public String toString() {
		Set<String> keySet = hashMap.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		StringBuilder builder = new StringBuilder();
		while(keyIterator.hasNext()){
			String curKey = keyIterator.next();
			RedisData curData = hashMap.get(curKey);
			builder.append(curKey);
			builder.append(" ");
			if(curData.dataType.equals(STRING_TYPE)){
				builder.append(STRING_TYPE);
				builder.append(" ");
				builder.append((String)curData.data);
			}else if(curData.dataType.equals(STRING_BIT_TYPE)){
				builder.append(STRING_BIT_TYPE);
				builder.append(" ");
				builder.append((String)curData.data);
			}else if(curData.dataType.equals(SORTED_SET_TYPE)){
				builder.append(SORTED_SET_TYPE);
				builder.append(" ");
				builder.append(((SortedSet)curData.data).toString());
			}
			builder.append("\n");
			
		}
		return builder.toString();
	}
	
	String insert(String key, String value)
	{
		
		
		RedisData curValue = hashMap.get(key);
		if(curValue != null){
			return ":0";
		}
		curValue = new RedisData();
	
		curValue.data = value;
		curValue.created_time = -1;
		curValue.dataType = STRING_TYPE;
		hashMap.put(key, curValue);
		return ":1";
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

		return "+OK";
	}
	private void insertInMap(String key, RedisData value)
	{
		if(key == null || value == null){
			System.out.println("Error");
			return;
		}
		hashMap.put(key, value);
	}
	
	String insert(String key, String member_key, Double member_value){
		RedisData curValue = hashMap.get(key);
		
		if(curValue != null && curValue.dataType.equals(STRING_TYPE)){
			return "-(error) WRONGTYPE Operation against a key holding the wrong kind of value";
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
		return "+OK";
	}
	
	void addbit(RedisData curValue, int byte_pos, int bit, long bit_offset){
		
		byte[] new_byte_val = new byte[byte_pos+1];
		byte curByte = new_byte_val[byte_pos];
		int curBit_pos = (int)(7 - (bit_offset & 0x7));
		//System.out.println("byte_pos "+byte_pos + " curBit_pos"+curBit_pos+" curByte "+curByte);
		int prev_bit = curByte & (1 << curBit_pos);
		prev_bit = (prev_bit >> curBit_pos);
		curByte = (byte) (curByte & ~(1 << curBit_pos));
		curByte |= (bit << curBit_pos);
		new_byte_val[byte_pos] = curByte;

		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < new_byte_val.length; i++){
			builder.append(DatatypeConverter.printByte(new_byte_val[i]));
			//System.out.println(i+" "+DatatypeConverter.printByte(new_byte_val[i]));
		}
		curValue.data = builder.toString();
		curValue.dataType = STRING_BIT_TYPE;
		curValue.byteArray = new_byte_val;
		//System.out.println("curValue "+curValue.data);
		System.out.println(":"+prev_bit);
		
	}

	void getbit(String key, long bit_offset){
		if(hashMap.containsKey(key)){
			RedisData curValue = hashMap.get(key);
			if(curValue.dataType.equals(SORTED_SET_TYPE)){
				System.err.println("error) WRONGTYPE Operation against a key holding the wrong kind of value");
				return;
			}
			byte[] byte_arr = null;
			if(curValue.dataType.equals(STRING_TYPE)){
				try {
					byte_arr = ((String)curValue.data).getBytes("UTF8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(curValue.dataType.equals(STRING_BIT_TYPE)){
				
				byte_arr = curValue.byteArray;
				//System.out.println(STRING_BIT_TYPE+" byte_arr.lenght "+byte_arr.length);
			}
			int byte_pos = (int) (bit_offset >> 3);
			if(byte_arr.length < byte_pos){
				//System.out.println("byte_arr.length < byte_pos");
				System.out.println(":0");
			}else{
				byte curByte = byte_arr[byte_pos];
				int curBit_pos = (int) (7 - (bit_offset & 0x7));
				int prev_bit = curByte & (1 << curBit_pos);
				prev_bit = (prev_bit >> curBit_pos);
				/*for(int i = 0; i < byte_arr.length; i++){
					System.out.println(byte_arr[i]);
				}*/
				//prev_bit = (prev_bit >> curBit_pos);
				//System.out.println(curValue.data);
				//System.out.println("not byte_arr.length < byte_pos");
				System.out.println(":"+prev_bit);
			}
			
		}else{
			System.out.println(":0");
		}
	}
	void setbit(String key, long bit_position, int bit){
		RedisData curValue = null;
		if(!(bit == 0 || bit == 1)){
			System.err.println("-(error) ERR bit is not an integer or out of range");
			return;
		}
		if(bit_position < 0 || bit_position > 4.096e+9){
			System.err.println("-(error) value is out of range");
			return;
		}
		int byte_pos = (int)(bit_position >> 3);
		if(hashMap.containsKey(key)){
			curValue = hashMap.get(key);
			if(!curValue.dataType.equals(SORTED_SET_TYPE)){
				
				byte[] byte_val = null;
				if(curValue.dataType.equals(STRING_BIT_TYPE)){
					byte_val = curValue.byteArray;
				}else{
				try {
						byte_val = ((String)curValue.data).getBytes("UTF8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						System.err.println("-Err");
						return;
						//e.printStackTrace();
					}
				}
				if(byte_val.length < byte_pos+1){
					
					addbit(curValue, byte_pos, bit, bit_position);
					

				}else{
					
					byte curByte = byte_val[byte_pos];
					int curBit_pos = (int) (7 - (bit_position & 0x7));
					int prev_bit = curByte & (1 << curBit_pos);
					prev_bit = (prev_bit >> curBit_pos);
					curByte = (byte) (curByte & ~(1 << curBit_pos));
					curByte |= (bit << curBit_pos);
					byte_val[byte_pos] = curByte;
					StringBuilder builder = new StringBuilder();
					//System.out.println("New");
					for(int i = 0; i < byte_val.length; i++){
						builder.append(DatatypeConverter.printByte(byte_val[i]));
						//System.out.println(i+" "+DatatypeConverter.printByte(byte_val[i]));
					}
					curValue.data = builder.toString();
					System.out.println(":"+prev_bit);
				}
				
			}else{
				System.err.println("-(error) WRONGTYPE Operation against a key holding the wrong kind of value");
			}
		}else{
			curValue = new RedisData();
			addbit(curValue, byte_pos, bit, bit_position);
			hashMap.put(key, curValue);
		}
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
			return "+(nil)";
		else{
			if(curValue.dataType.equals(STRING_BIT_TYPE) || curValue.dataType.equals(STRING_TYPE)){
				if(curValue.created_time == -1)
					return (String)curValue.data;
				else{
					long curretTime = Calendar.getInstance().getTimeInMillis();
					if(curValue.created_time - curretTime > 0)
						return "+"+(String)curValue.data;
					else
						return "+(nil)";
				}
			}
			else
				return "-Error";
		}
		
	}
	
	String get(String key, int min_index, int max_index){
		if(hashMap.containsKey(key)){
			RedisData curValue = hashMap.get(key);
			if(curValue.dataType.equals(SORTED_SET_TYPE)){
				return ((SortedSet)curValue.data).get(min_index, max_index);
			}else{
				return "-(error) WRONGTYPE Operation against a key holding the wrong kind of value";
			}
		}else{
			return "-(empty list or set)";
		}
	}
	//String get(String key, )
}