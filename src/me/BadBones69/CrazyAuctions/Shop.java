package me.badbones69.crazyauctions;

public enum Shop{
	
	SELL("Sell"), BID("Bid");
	
	String Name;
	
	/**
	 * @param name Name of the Shop Type.
	 */
	private Shop(String name){
		this.Name=name;
	}
	
	/**
	 * @return Returns the type name as a string.
	 */
	public String getName(){
		return Name;
	}
	
	/**
	 * @param name Name of the Type you want.
	 * @return Returns the Type as a Enum.
	 */
	public static Shop getFromName(String name){
		for(Shop type : Shop.values()){
			if(type.getName().equalsIgnoreCase(name)){
				return type;
			}
		}
		return null;
	}
}