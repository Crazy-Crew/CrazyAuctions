package me.badbones69.crazyauctions.api.enums;

public enum GuiType {
	
	
	BIDDING(27, "Settings.Bidding-On-Item"),
	BUYING(9, "Settings.Buying-Item"),
	EXPIRED_LIST(54, "Settings.Cancelled/Expired-Items", true),
	CURRENT_LIST(54, "Settings.Players-Current-Items"),
	CATEGORIES(54, "Settings.Categories"),
	SHOP(54, "Settings.GUIName", true),
	SEARCH(54, "Settings.Search", true),
	;
	
	
	private final int size;
	private final String title;
	private final boolean paginable;
	
	
	private GuiType(int size, String title) {
		this(size, title, false);
	}
	
	
	private GuiType(int size, String title, boolean paginable) {
		this.size = size;
		this.title = title;
		this.paginable = paginable;
	}
	
	
	public int getSize() {
		return this.size;
	}
	
	
	public String getTitlePath() {
		return this.title;
	}
	
	
	public boolean isPaginable() {
		return this.paginable;
	}
	

}
