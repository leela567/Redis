import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ExotelRedisRunner{
	/*private static String GET = "GET";
	private static String SET = "SET";
	private static String ZADD = "ZADD";*/
	public static void main(String[] args)
	{
		ExotelRedisFactory factory = ExotelRedisFactory.getInstance();
		if(factory == null) { System.out.println("Not able to create instance"); return; }
		
		Scanner sc = new Scanner(System.in);
		while(sc.hasNext())
		{
			String input = sc.nextLine();
			StringTokenizer inputTokenizer = new StringTokenizer(input);
			String command = null, key = null, value = null;
			long ttl = -1;
			
			if(inputTokenizer.hasMoreTokens()){
				command = inputTokenizer.nextToken();
			}else{
				System.err.println("Error: Less number of arguments");
			}
			
			if(command.equalsIgnoreCase("get")){
				if(inputTokenizer.countTokens() == 1){
					key = inputTokenizer.nextToken();
					//System.out.println("command: "+command+" key: "+key);
					System.out.println("\""+factory.getCurRedisInstance().get(key)+"\"");
				}else{
					System.err.println("(error) wrong number of arguments ("+inputTokenizer.countTokens()+" for 1)");
				}
				
			}else if(command.equalsIgnoreCase("set") || command.equalsIgnoreCase("setnx")){
				if(inputTokenizer.hasMoreTokens()){
					key = inputTokenizer.nextToken();
				}
				if(inputTokenizer.countTokens() > 0)
				{
					value = inputTokenizer.nextToken();
					if(value.charAt(0) == '"'){
						StringBuilder stringBuilder = new StringBuilder(value.substring(1));
						while(inputTokenizer.hasMoreTokens()){
							stringBuilder.append(" ");
							stringBuilder.append(inputTokenizer.nextToken());
						}
						value = stringBuilder.substring(0, stringBuilder.length()-1);
					}else if(inputTokenizer.countTokens() > 0)
					{
						System.err.println("(error) ERR Syntax error");
						continue;
					}
				}else{
					value = "";
				}
				//System.out.println("command: "+command+" key: "+key+" value: "+value);
				if(command.equalsIgnoreCase("setnx")){
					System.out.println(factory.getCurRedisInstance().insert(key, value));
				}else{
					System.out.println(factory.getCurRedisInstance().insert(key, value, -1));
				}
			}else if(command.equalsIgnoreCase("setex") || command.equals("psetex")){
				if(inputTokenizer.countTokens() == 3){
					if(inputTokenizer.hasMoreTokens()){
						key = inputTokenizer.nextToken();
					}
					try{
						ttl = Long.parseLong(inputTokenizer.nextToken());
						if(command.equalsIgnoreCase("setex"))
							ttl = 1000*ttl;
					}catch(NumberFormatException e){
						System.err.println("(error) ERR value is not an integer or out of range");
						continue;
					}
					value = inputTokenizer.nextToken();
					if(value.charAt(0) == '"'){
						StringBuilder stringBuilder = new StringBuilder(value.substring(1));
						while(inputTokenizer.hasMoreTokens()){
							stringBuilder.append(" ");
							stringBuilder.append(inputTokenizer.nextToken());
						}
						value = stringBuilder.substring(0, stringBuilder.length()-1);
					}
					//System.out.println("command: "+command+" key: "+key+" ttl: "+ttl+" value: "+value);
					System.out.println(factory.getCurRedisInstance().insert(key, value, ttl));
				}else{
					System.err.println("(error) wrong number of arguments ("+inputTokenizer.countTokens()+" for 3)");
				}
				
				
			}else if(command.equalsIgnoreCase("zadd")){
				
				Double member_value = 0.0;
				String member_key = null;
				if(inputTokenizer.countTokens() >= 3){
					key = inputTokenizer.nextToken();
					try{
						member_value = Double.parseDouble(inputTokenizer.nextToken());
					}catch(NumberFormatException e){
						System.err.println("(error) ERR value is not a valid float");
						continue;
					}
					member_key = inputTokenizer.nextToken();
					if(inputTokenizer.countTokens() != 0 && member_key.charAt(0) != '"'){
						System.err.println("(error) ERR syntax error");
					}else if(inputTokenizer.countTokens() > 0){
						StringBuilder builder = new StringBuilder(member_key.subSequence(1, member_key.length()-1));
						while(inputTokenizer.hasMoreTokens()){
							builder.append(" ");
							builder.append(inputTokenizer.nextToken());
						}
						member_key = builder.substring(0, builder.length()-1);
					}else if(member_key.charAt(0) == '"'){
						member_key = member_key.substring(1, member_key.length()-1);
					}
					//System.out.println("Command: "+command+" key: "+key+" member_value "+member_value+" member_key "+member_key);
					
					System.out.println(factory.getCurRedisInstance().insert(key, member_key, member_value));
				}else{
					System.err.println("(error) ERR wrong number of arguments for 'zadd' command");
				}
			}
			else if(command.equalsIgnoreCase("zget")){
				
				key = inputTokenizer.nextToken();
				System.out.println(factory.getCurRedisInstance().getDummy(key));
			}
			else if(command.equalsIgnoreCase("zcount") || command.equalsIgnoreCase("zrange")){
				if(inputTokenizer.countTokens() == 3){
					key = inputTokenizer.nextToken();
					if(command.equalsIgnoreCase("zrange")){
						int min_index = 0, max_index = 0;
						try{
							min_index = Integer.parseInt(inputTokenizer.nextToken());
							max_index = Integer.parseInt(inputTokenizer.nextToken());
						}catch(NumberFormatException e){
							System.err.println("(error) ERR min or max is not a float");
							continue;
						}
						System.out.println(factory.getCurRedisInstance().get(key, min_index, max_index));
					}
					else{
						Double min_value = 0.0, max_value = 0.0;
						try{
							 min_value = Double.parseDouble(inputTokenizer.nextToken());
							 max_value = Double.parseDouble(inputTokenizer.nextToken());
						}catch(NumberFormatException e){
							System.err.println("(error) ERR min or max is not a float");
							continue;
						}
						System.out.println(factory.getCurRedisInstance().getCount(key, min_value, max_value));
					}
					
				}else{
					System.err.println("error) wrong number of arguments ("+inputTokenizer.countTokens()+" for 3)");
				}
			}else if(command.equalsIgnoreCase("zcard")){
				if(inputTokenizer.countTokens() == 1){
					key = inputTokenizer.nextToken();
					System.out.println(factory.getCurRedisInstance().getCount(key));
				}
			}else if(command.equalsIgnoreCase("getbit")){
				if(inputTokenizer.countTokens() == 2){
					key = inputTokenizer.nextToken();
					long bit_offset;
					try{
						bit_offset = Long.parseLong(inputTokenizer.nextToken());
					}catch(NumberFormatException e){
						System.err.println("(error) ERR bit offset is not an integer or out of range");
						continue;
					}
					factory.getCurRedisInstance().getbit(key, bit_offset);
				}else{
					System.err.println("error) wrong number of arguments ("+inputTokenizer.countTokens()+" for 2)");
				}
				
			}else if(command.equalsIgnoreCase("setbit")){
				
				if(inputTokenizer.countTokens() == 3){
					key = inputTokenizer.nextToken();
					long bit_position;
					int bit;
					try{
						bit_position = Long.parseLong(inputTokenizer.nextToken());
					}catch(NumberFormatException e){
						System.err.println("(error) ERR bit offset is not an integer or out of range");
						continue;
					}
					try{
						bit = Integer.parseInt(inputTokenizer.nextToken());
					}catch(NumberFormatException e){
						System.err.println("(error) ERR bit is not an integer or out of range");
						continue;
					}
					factory.getCurRedisInstance().setbit(key, bit_position, bit);
				}else{
					System.err.println("(error) wrong number of arguments ("+inputTokenizer.countTokens()+" for 3)");
				}
			}else if(command.equalsIgnoreCase("save")){
				String saveStr = factory.getCurRedisInstance().toString();
				try {
					String filename = "exotelredis"+Calendar.getInstance().getTimeInMillis()+".RDB";
					File file = new File(filename);
					FileOutputStream fos = new FileOutputStream(file);
					Writer out = new OutputStreamWriter(fos, "UTF8");
					out.write(saveStr);
					out.close();
					System.out.println(filename+" saved");
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else if(command.equalsIgnoreCase("exit")){
				break;
			}else{
				System.err.println("(error) I'm sorry, I don't recognize that command.");
			}
		}
			
	}
}