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
	private boolean autoSwap = false;
	
	//lets create temporary var, and get set methods for it.
	// this way I dont have to always manually create a new hash.ser for seralize AutoUpgradeData
	// when  I add a new variable.
	
	private int tempIntA =0; // GOLD percentage
	private int tempIntB =0; // ELIXIR percentage
	private int tempIntC =0;
	private int tempIntD =0;
	private int tempIntE =0;
	
	private String tempStringA = "";
	private String tempStringB = "";
	private String tempStringC = "";
	private String tempStringD = "";
	private String tempStringE = "";
	
	
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

	public boolean isAutoSwap() {
		return autoSwap;
	}

	public void setAutoSwap(boolean autoSwap) {
		this.autoSwap = autoSwap;
	}

	/**
	 * 
	 * @return GOLD PERCENTAGE
	 */
	public int getTempIntA() {
		return tempIntA;
	}

	/**
	 * 
	 * @param tempIntA GOLD PERCENTAGE
	 */
	public void setTempIntA(int tempIntA) {
		this.tempIntA = tempIntA;
	}

	/**
	 * 
	 * @return ELIXIR PERCENTAGE
	 */
	public int getTempIntB() {
		return tempIntB;
	}

	/**
	 * 
	 * @param tempIntB ELIXIR PERCENTAGE
	 */
	public void setTempIntB(int tempIntB) {
		this.tempIntB = tempIntB;
	}

	public int getTempIntC() {
		return tempIntC;
	}

	public void setTempIntC(int tempIntC) {
		this.tempIntC = tempIntC;
	}

	public int getTempIntD() {
		return tempIntD;
	}

	public void setTempIntD(int tempIntD) {
		this.tempIntD = tempIntD;
	}

	public int getTempIntE() {
		return tempIntE;
	}

	public void setTempIntE(int tempIntE) {
		this.tempIntE = tempIntE;
	}

	public String getTempStringA() {
		return tempStringA;
	}

	public void setTempStringA(String tempStringA) {
		this.tempStringA = tempStringA;
	}

	public String getTempStringB() {
		return tempStringB;
	}

	public void setTempStringB(String tempStringB) {
		this.tempStringB = tempStringB;
	}

	public String getTempStringC() {
		return tempStringC;
	}

	public void setTempStringC(String tempStringC) {
		this.tempStringC = tempStringC;
	}

	public String getTempStringD() {
		return tempStringD;
	}

	public void setTempStringD(String tempStringD) {
		this.tempStringD = tempStringD;
	}

	public String getTempStringE() {
		return tempStringE;
	}

	public void setTempStringE(String tempStringE) {
		this.tempStringE = tempStringE;
	}
}