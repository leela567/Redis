class ExotelRedisFactory{
	
	static ExotelRedisFactory instance;
	private ExotelRedis cur_instance;
	private ExotelRedisFactory()
	{
		cur_instance = new ExotelRedis();
	}
	public static ExotelRedisFactory getInstance()
	{
		if(instance == null){
			instance = new ExotelRedisFactory();
		}
		return instance;
	}
	public ExotelRedis getCurRedisInstance()
	{
		if(cur_instance == null)
			cur_instance = new ExotelRedis();
		return cur_instance;
	}
}