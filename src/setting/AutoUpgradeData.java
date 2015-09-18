package setting;

import setting.config.xy;
import java.util.*;

public class AutoUpgradeData
{

	private String email ="";
	private String time = "";
	private ArrayList<xy> xyArrayList = new ArrayList<xy>();
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
	
}