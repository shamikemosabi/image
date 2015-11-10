package setting;

import setting.config.xy;

import java.io.Serializable;
import java.util.*;

public class AutoUpgradeData implements Serializable
{

	private String email ="";
	private String time = "";
	private ArrayList<xy> xyArrayList = new ArrayList<xy>();
	private boolean swapBack = false;
	private Date swapDate = null;
	private String name ="";	
	private boolean lootFull = false;
	
	public AutoUpgradeData()
	{
		
	}
	
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String s){
		email  = s;
	}
	
	public String getTime()
	{
		return time;
	}
	public void setTime(String s){
		time  = s;
	}
	
	public ArrayList<xy> getXYArrayList()
	{
		return xyArrayList;
	}

	public boolean isSwapBack() {
		return swapBack;
	}

	public void setSwapBack(boolean swapBack) {
		this.swapBack = swapBack;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Date getSwapDate()
	{
		return swapDate;
	}
	public void setSwapDate(Date d)
	{
		this.swapDate = d;
	}
	
	public boolean getLootFull()
	{
		return lootFull;
	}
	public void setLootFull(boolean d)
	{
		this.lootFull = d;
	}
}