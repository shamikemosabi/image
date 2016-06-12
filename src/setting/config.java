/**
 * @todo
 * 
 * - Think about deploying king/queen/cc troops if I have them
 * - load config from file instead? perhaps XML?
 */

package setting;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;



import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.*;


import email.email;



public class config
{
	public static boolean test = false; 
	
	
	ArrayList<struct> data ;
	struct s = null;
	public static boolean work = true;
	//different screen size between home and work
	String ruleFolder = System.getProperty("user.dir")+"\\"+ ((work)?"rule":"rule_home");	
	
	public final static String readFile =  System.getProperty("user.dir")+"\\read.txt";
	public final static String configFile =  System.getProperty("user.dir")+"\\config.xml";
	public final static String upgradeFile =  System.getProperty("user.dir")+"\\upgrade.xml";
	public final static String statFile =  System.getProperty("user.dir")+"\\stat.html";
	public final static String labFile =  System.getProperty("user.dir")+"\\lab.xml";
	public final static String HashSER = System.getProperty("user.dir")+"\\hash.ser";
	
	private xy xyBarrack = null;
	private ArrayList<xy> ArrayXYCamp = null;
	private String email2="";
	private String pw="";
	public int deployArcher=0;
	public int deployBarb=0;	
	public String lootThreshold="";
	public int slot =0;	
	private boolean smartLoot = false;
	private boolean barrackBoost= false;
	private boolean autoSwap = true; // current config autoSwap
	
	private xy xyBarrackTrain = null;
	
	public ArrayList<xy> swapSlot = new ArrayList<xy>();
	
	private ArrayList<AutoUpgradeData> autoUpgradeList = new ArrayList<AutoUpgradeData>(); // don't use anymore, only in AutoUpgrade
		
	public int offsetX=0;
	public int offsetY=0;
	//used only in some local method. just to do some comparison
	public config()
	{
		//setup();
	}
	
	//only specify new account, just add account
	/*
	 * this gets called either the first time from setGUIandControl, which we NEED to new config file.
	 * 
	 * or it gets called from downloadAndLoadConfig 
	 * 
	 * which gets called from AutoUpgrade2 (we don't FTP if autoswap is false)	
	 * or it gets called from isServiceStarted2 ( we NEED to FTP )
	 */
	public config(String account, boolean forceFTP, boolean con_autoSwap)
	{
	//	setup();
		downloadFTP(config.configFile , "/config/config.xml", forceFTP, con_autoSwap); 
		loadConfig(account,"", forceFTP);
				
	}
	
	// specify new and old account , called from swap. we need to add account and delete oldAccount from active
	public config(String account, String oldAccount)
	{
		//setup();
		downloadFTP(config.configFile , "/config/config.xml", true, true); // only calls from swap, when we swap we NEED to get latest config
		loadConfig(account, oldAccount, true);			
	}
	
	
	
	/*
	 * This method will be called at the beginning of the program, it will 
	 * create a new AutoUpgradeData that holds all the email account that we MAY swap to.
	 * it will have a swapDate field which is the last swap date. 
	 */
	public Hashtable<String, AutoUpgradeData> loadAutoUpgradeSWAP()
	{	
		Hashtable<String, AutoUpgradeData> hs = new Hashtable <String, AutoUpgradeData> ();
				
		try{
			File fXmlFile = new File(this.configFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		
			NodeList nList = doc.getElementsByTagName("email");			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				
				Node nNode = nList.item(temp);				
				Element eElement = (Element) nNode;
				String e  = eElement.getAttribute("id");

				AutoUpgradeData aud = new AutoUpgradeData();
				aud.setEmail(e);
				
				//get current date
				Calendar d = Calendar.getInstance();
				Date currDate = d.getTime();
				aud.setSwapDate(currDate);		
							
				// set auto swap, I need this to be in AutoUpgradeData object because I need to know the value of OTHER account's value. Not just current.
				// I need to know the value before I swap to the new config.
				aud.setAutoSwap(Boolean.valueOf(eElement.getElementsByTagName("autoSwap").item(0).getTextContent()));
				
				hs.put(e, aud);
			}
			
		}
		catch(Exception e)
		{
			System.out.println("loadAutoUpgradeSWAP error");
			e.printStackTrace();
		}
		
		return hs;
	}
	public void loadConfig(String account, String oldAccount, boolean forceFTP)
	{
		try{
			File fXmlFile = new File(this.configFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("email");			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				
				Node nNode = nList.item(temp);				
				Element eElement = (Element) nNode;
				String e  = eElement.getAttribute("id");
				
				if(e.equals(account)) // if this is same account as what was input
				{
					email2 = e;
					xyBarrack = createXY(eElement.getElementsByTagName("barrack").item(0).getTextContent());
					ArrayXYCamp = createXYfromPipes(eElement.getElementsByTagName("camp").item(0).getTextContent());
					pw = eElement.getElementsByTagName("password").item(0).getTextContent();
					deployArcher = Integer.valueOf(eElement.getElementsByTagName("deployArcher").item(0).getTextContent());
					deployBarb = Integer.valueOf(eElement.getElementsByTagName("deployBarb").item(0).getTextContent());
					lootThreshold = eElement.getElementsByTagName("loot").item(0).getTextContent();
					slot = Integer.valueOf(eElement.getElementsByTagName("slot").item(0).getTextContent());
					xyBarrackTrain =  createXY(eElement.getElementsByTagName("barrackTrain").item(0).getTextContent());										
					email.alDestEmail = createDestinationEmailArray(eElement.getElementsByTagName("destEmail").item(0).getTextContent());					
					smartLoot = Boolean.valueOf(eElement.getElementsByTagName("smartLoot").item(0).getTextContent());
					barrackBoost = Boolean.valueOf(eElement.getElementsByTagName("barrackBoost").item(0).getTextContent());
					autoSwap = Boolean.valueOf(eElement.getElementsByTagName("autoSwap").item(0).getTextContent());
					
				}
			}
			
	 
			setUpSwapSlots(doc);
			// create AutoUpgrade object
			createAutoUpgrade(doc);	
			
			
			addActiveEmail(doc , account, forceFTP);
			if(!oldAccount.isEmpty())
			{
				deleteActiveEmail(doc, oldAccount, forceFTP);
			}
			
			
		}
		catch(Exception e)
		{
			System.out.println("error loading config file");
			e.printStackTrace();
		}
	}
	
	
	/*
	 * take string of email, phone numbers, separate them by ',' and load them in arraylist.
	 */
	public ArrayList<String> createDestinationEmailArray(String e)
	{		
		String[] t = e.split("\\,");		
		ArrayList<String> temp = new ArrayList<String>(Arrays.asList(t));				
		return temp;
	}
	
	
	public void setUpSwapSlots(Document doc)
	{	
		NodeList nList = doc.getElementsByTagName("swapSlots");
		for (int temp = 0; temp < nList.getLength(); temp++) 
		{

			Node nNode = nList.item(temp);				
			Element eElement = (Element) nNode;
			
			swapSlot.add(createXY(eElement.getTextContent().trim()));										
		}
					
	}
	
	
	public void deleteActiveEmail(Document doc, String oldAccount, boolean forceFTP)
	{
		boolean exist = false;
		Node active = doc.getElementsByTagName("active").item(0); //should only be  1 active
		NodeList nList = doc.getElementsByTagName("activeEmail");
		for (int temp = 0; temp < nList.getLength(); temp++) 
		{
			Node nNode = nList.item(temp);				
			Element eElement = (Element) nNode;
			String email  = eElement.getTextContent();
			//String email = eElement.getAttribute("id");
			if(email.equals(oldAccount))
			{
				exist = true;		
				active.removeChild(nNode);			
			}
			
		}
		
		// if exist then it means we deleted node, need to FTP 
		if(exist)
		{
			try{
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(this.configFile));
				transformer.transform(source, result);
				
				 upLoadFTP(this.configFile,"config", forceFTP);
			}
			catch(Exception e)
			{
				System.out.println("error in writing into XML file, method deleteActiveEmail");			
				System.out.println(e.getMessage());				
				e.printStackTrace();
			}
		}
	}
	
	
	// if email does not already exist then add it.
	public void addActiveEmail(Document doc, String account, boolean forceFTP)
	{
		boolean exist = false;
		NodeList nList = doc.getElementsByTagName("activeEmail");
		for (int temp = 0; temp < nList.getLength(); temp++) 
		{
			Node nNode = nList.item(temp);				
			Element eElement = (Element) nNode;
			String email  = eElement.getTextContent();
			//String email = eElement.getAttribute("id");
			if(email.equals(account))
			{
				exist = true;
				break;
			}
			
		}
		
		//could not find email, we have to add it.
		if(!exist)
		{
			
			Node active = doc.getElementsByTagName("active").item(0); //should only be  1 active
			// append a new node
			Element age = doc.createElement("activeEmail");
			age.appendChild(doc.createTextNode(account));
			active.appendChild(age);
			
			try{
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(this.configFile));
				transformer.transform(source, result);
				
				 upLoadFTP(this.configFile,"config", forceFTP);
			}
			catch(Exception e)
			{
				System.out.println("error in writing into XML file, method addActiveEmail");				
				System.out.println(e.getMessage());				
				e.printStackTrace();
			}
				
				
		}
	}

	public void createAutoUpgrade(Document doc)
	{	
		autoUpgradeList.clear();
		
		NodeList nList = doc.getElementsByTagName("pos");
		for (int temp = 0; temp < nList.getLength(); temp++) 
		{
			AutoUpgradeData aud = new AutoUpgradeData();
			
			Node nNode = nList.item(temp);				
			Element eElement = (Element) nNode;
			
			String time = eElement.getAttribute("id");
			String email = eElement.getElementsByTagName("upgradeEmail").item(0).getTextContent();
			boolean swapBack= Boolean.valueOf(eElement.getElementsByTagName("swapBack").item(0).getTextContent());
			aud.setTime(time);
			aud.setEmail(email);
			aud.setSwapBack(swapBack);
						
			NodeList xyList = eElement.getElementsByTagName("xy");
			
			for (int i = 0; i < xyList.getLength(); i++) 
			{
				Node n = xyList.item(i);
				Element xye = (Element) n;
				
				xy newXY = createXY(xye.getTextContent());
				aud.getXYArrayList().add(newXY);
						
			}
			
			autoUpgradeList.add(aud);
		}

	}
	
	// update email's alDestEmail
	public ArrayList<String> getDestEmailList()
	{
		return email.alDestEmail;
	}
	public ArrayList<AutoUpgradeData> getAutoUpgradeList()
	{
		return autoUpgradeList;
	}
	public void setup()
	{
		//downloadFTP(config.configFile , "/config/config.xml");  
		
		/*
		swapSlot.add(new xy(508,286));  
		swapSlot.add(new xy(508,353));
		swapSlot.add(new xy(508,419));
		swapSlot.add(new xy(508,476));
		swapSlot.add(new xy(508,539));	
		*/	
		
	}
	
	public xy createXY(String s)
	{
		int a = Integer.valueOf(s.substring(0, s.indexOf(",")));
		int b = Integer.valueOf( s.substring(s.indexOf(",") + 1, s.length()));
		xy temp = new xy(a,b);
		return temp;
	}
	
	/**
	 * 
	 * @param s - string of coordinate, seprated by pipes |
	 * 
	 * @return arraylist of xy containing all the coordinates
	 */
	public ArrayList<xy> createXYfromPipes(String s)
	{
		ArrayList<xy> temp = new ArrayList<xy>();
		
		 String[] splitArray = s.split("\\|");
		 
		 for(int i=0; i < splitArray.length; i ++)
		 {
			 String str = splitArray[i];
			 int a = Integer.valueOf(str.substring(0, str.indexOf(",")));
			 int b = Integer.valueOf(str.substring(str.indexOf(",") + 1, str.length()));
			 xy tempXY = new xy(a,b);
			 
			 temp.add(tempXY);
		 }	

		return temp;
	}	
	
	public void upLoadFTP(String FileName, String dir , boolean forceFTP)
	{
		
		if(!test && (forceFTP || this.isAutoSwap())){			
			File f = new File(FileName);
	
			FTPClient ftp = new FTPClient();
	
			 boolean success = false;
		     int count = 0;
		     do 
		        {   
					try{	
						count++;
						ftp.connect("doms.freewha.com");
						System.out.println(ftp.login("www.mturkpl.us","freewebsucks11"));		
								
						System.out.println(ftp.getReplyString());
						ftp.enterLocalPassiveMode();
						ftp.changeWorkingDirectory(dir);				
						
						final InputStream is = new FileInputStream(f.getPath());
						success = ftp.storeFile(f.getName(), is);
					
						is.close();
						
						ftp.disconnect();	
						
					}
					catch(Exception e)
					{
						System.out.println("Error UploadFTP CONFIG method");
			        	System.out.println(e.getMessage());
								        	
			        	e.printStackTrace();
			        	success = false;					
					}
		        }while(!success && count < 10);
	     
		}
	}
	
	/**
	 * The idea is to separate out multiple config files.
	 * I suspect because I am now calling click.downloadAndLoadConfig every loop
	 * there is a higher chance of conflict if multiple machine is running and are accessing config.xml.
	 * 
	 * This is bad for my client account with autoSwap = false.
	 * 
	 * So the idea is to check if autoSwap is true or false.
	 * If it is true, then this means the account is one of my docogo accounts and I'm running AutoUpgrade with this.
	 * in this case I will continue reading the MAIN config.xml
	 * 
	 * If autoSwap is false, for my clients, then I want to read a different config file which will be in
	 * "config/acct/ACOUNTNAME/config.xml.
	 * Hopefully there won't be conflcit anymore.
	 * 
	 * 
	 * 
	 * The only problem right now is that, The INITIAL call from setGUIandControl() I wont know if autoSwap is true or false
	 * because I get autoSwap from config.xml.
	 * 
	 * So in this case we can just check autoSwap anyways, because it initialzes as true, which means this account is mine
	 * which means it has to use MAIN config.xml, and if it's a client's account I need it to look at the main config.xml only
	 * the first time (I have not setup autoSwap yet).  
	 * 
	 * 
	 * 
	 * @return
	 */
	public String getFTPConfigPath()
	{
		String ret="";
		
		if(this.isAutoSwap()) // main CONFIG.XML
		{
			
		}
		else
		{
			
		}
		return "";
	}
	
	
	
	/**
	 * 
	 *  Need a way to NOT FTP for autoswap = false. (client's account)
	 * 
	 * @param localFile
	 * @param remoteFile
	 * @param forceFTP - force ftp
	 */
	
	public void downloadFTP(String localFile, String remoteFile, boolean forceFTP, boolean con_autoSwap)
	{
		if(!test && (forceFTP || con_autoSwap)){
	        String server = "doms.freewha.com";
	        String user = "www.mturkpl.us";
	        String pass = "freewebsucks11";
	 
	        FTPClient ftpClient = new FTPClient();
	        
	        // if retrieveFile fails, it will retry again.
	        boolean success = false;
	        int count = 0;
	        do 
	        {        
		        try {
		        	count++;		 
		        	
		            ftpClient.connect(server);
		            ftpClient.login(user, pass);
		            ftpClient.enterLocalPassiveMode();
		            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		
		            File downloadFile1 = new File(localFile);
		            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
		             success = ftpClient.retrieveFile(remoteFile, outputStream1);
		            outputStream1.close();
		            ftpClient.disconnect();	  
		            
		            
		        }
		        catch(Exception e)
		        {
		        	System.out.println("Error downloadFTP");
		        	System.out.println(e.getMessage());
		        	e.printStackTrace();
		        	success = false;
		        }
	        }while(!success && count < 10);
		}
         
	}
	
	public void setName(String name)
	{
		
		ArrayList<xy> pos = new ArrayList();
		if(work)
		{
			setScreenOffset();
			if(name.equals("main"))
			{				
				pos.add(new xy(257,729)); // click attack button
				//change match pic, to builders head
				//s = new struct("main",520,21, 37, 38, ruleFolder+"\\main.jpg",pos);
				s = new struct("main",467,91, 31, 21, ruleFolder+"\\main.jpg",pos);
			}
			else if(name.equals("camp_full"))
			{
				//use barracks jpg different coordinate, match on All Camps Full!
				s = new struct("camp_full",596,246, 92, 17, ruleFolder+"\\barracks.jpg",pos); 
			}
			else if(name.equals("camp"))
			{
				//pos.add(new xy(359,466)); //click camp	
				// xyCamp is now an arraylist of XY objects
				for(int i=0; i < ArrayXYCamp.size(); i ++)
				{
					pos.add(ArrayXYCamp.get(i));
				}				
				pos.add(new xy(660, 739)); // click info
				
				/*
				 * Uses Army Camp text image crop, but have to be careful and see if lvl 7 army camp moves the texts
				 */
				s = new struct("camp",	558, 155, 167, 37, ruleFolder+"\\camp.jpg",pos);
			}
			else if(name.equals("find")) // matching queue
			{
				//pos.add(new xy(337, 636)); // click find a match
				pos.add(new xy(430, 590)); // click find a match
				//pos.add(new xy(725, 541)); // press okay to subtract shield
				
				//match on the townhall image 
				//s = new struct("find",	202, 283, 140, 133, ruleFolder+"\\find.jpg",pos);
				s = new struct("find",	401, 342, 63, 49, ruleFolder+"\\find.jpg",pos);
			}
			else if(name.equals("init")) // main village, with screen setup (zoomed out and scrolledup)
			{
				//s = new struct("init",	1009, 63, 111, 64, ruleFolder+"\\init.jpg",pos);
				s = new struct("init",	796, 108, 33, 32, ruleFolder+"\\init.jpg",pos);
			}
			else if(name.equals("barracks"))
			{
				//pos.add(new xy(505,297)); //click barracks 
				pos.add(xyBarrack); 
				pos.add(xyBarrackTrain); //click train
				
				//match on Troop capacity after training:
				s = new struct("barracks",	566, 339, 147, 17, ruleFolder+"\\barracks.jpg",pos);				
			}
			else if(name.equals("battle")){
				
				//pos.add(new xy(1296,673)); //click next
				pos.add(new xy(924,633)); //click next
				//s = new struct("battle",33, 695, 128, 28, ruleFolder+"\\battle.jpg",pos);	//match on end battle
				s = new struct("battle",216, 654, 91, 18, ruleFolder+"\\battle.jpg",pos);	//match on end battle
			}
			else if(name.equals("end")){
				//pos.add(new xy(714,743)); //click return home			
				//s = new struct("end",661,719, 111, 48, ruleFolder+"\\end.jpg",pos); //return home
				
				pos.add(new xy(623,636)); //click return home
				s = new struct("end",558,633, 129, 22, ruleFolder+"\\end.jpg",pos); //return home
			}
			else if(name.equals("gold")){				
				s = new struct("gold",239,135, 80, 18, ruleFolder+"\\gold.jpg",pos);			      			       
			}
			else if(name.equals("elixer")){				
				s = new struct("elixer",239,163, 84, 19, ruleFolder+"\\elixer.jpg",pos);
			}
			
			else if(name.equals("safe"))
			{
			//	pos.add(new xy(1031,87)); 
				pos.add(new xy(1023,95)); 
				s = new struct("safe",0,0, 0, 0, ruleFolder+"\\safe.jpg",pos); 
			}
			else if(name.equals("attack"))
			{
				//pos.add(new xy(246,337));
				pos.add(new xy(304,337));
				pos.add(new xy(696,175));
				pos.add(new xy(710,619));
				pos.add(new xy(352,492));
				s = new struct("attack",0,0, 0, 0, ruleFolder+"\\attack.jpg",pos); 
			}
			else if(name.equals("barbs"))
			{
				//pos.add(new xy(433,446)); // position of barb in camp
				//pos.add(new xy(1191,418)); // next button
				//pos.add(new xy(229,812)); // position in battle screen
				
				pos.add(new xy(356,433)); // position of barb in camp
				pos.add(new xy(956,411)); // next button
				pos.add(new xy(257,733)); // position in battle screen
				//s = new struct("barbs",221,698,55, 68, ruleFolder+"\\barbs.jpg",pos); 
				s = new struct("barbs",228,724,53, 33, ruleFolder+"\\barbs.jpg",pos);
			}
			else if(name.equals("archs"))
			{
				//pos.add(new xy(570,447)); // position of archs in camp
				//pos.add(new xy(1191,418)); // next button
				//pos.add(new xy(318,810)); // position in battle screen
				//s = new struct("archs",317,701, 54, 59, ruleFolder+"\\archs.jpg",pos); 
				
				pos.add(new xy(466,422)); // position of archs in camp
				pos.add(new xy(956,411)); // next button
				pos.add(new xy(323,731)); // position in battle screen
				s = new struct("archs",300,727, 51, 32, ruleFolder+"\\archs.jpg",pos); 
			}
			
			//Changed logic, Just click slot 3,4 and 5. deploy King, queen, maybe CC troops as well.
			else if(name.equals("king"))
			{				
				
				//pos.add(new xy(414,807));
				pos.add(new xy(404,737));
				pos.add(new xy(476,737));
				pos.add(new xy(559,737));
				
				s = new struct("king",280,562, 43, 39, ruleFolder+"\\king.jpg",pos); 
				
			}
			else if(name.equals("inactive"))
			{
				//pos.add(new xy(391,658));
				//s = new struct("inactive",24,31, 30, 25, ruleFolder+"\\inactive.jpg",pos);
				s = new struct("inactive",211,89, 21, 17, ruleFolder+"\\inactive.jpg",pos); 
			}
			else if(name.equals("raided"))
			{
				pos.add(new xy(89,797)); // click return home
				s = new struct("raided",44,600, 86, 52, ruleFolder+"\\raided.jpg",pos); 
			}
			else if(name.equals("de")) // used to see if DE exist
			{				
				s = new struct("de",39,166, 21, 21, ruleFolder+"\\de.jpg",pos); 
			}
			else if(name.equals("de_amt")) // used to get DE amount
			{				
				s = new struct("de",62,160, 75, 27, ruleFolder+"\\de.jpg",pos); 
			}
			else if(name.equals("queen"))
			{
				//pos.add(new xy(408,706));
				//pos.add(new xy(414,807));
				pos.add(new xy(302,584));
				//s = new struct("queen",383,776, 64, 58, ruleFolder+"\\queen.jpg",pos);  //new queen position																
				s = new struct("queen",278,568, 46, 39, ruleFolder+"\\queen.jpg",pos);  //new queen position
			}
			else  if(name.equals("status"))
			{
				pos.add(new xy(237,213));
				pos.add(new xy(576,192));
				s = new struct("status",0,0,0,0, ruleFolder+"\\status.jpg",pos);
			}
			else if(name.equals("swap"))
			{
				//pos.add(new xy(1383,694)); //click setting
				//pos.add(new xy(738,114)); // click setting tab
				//pos.add(new xy(745,537)); // click disconnect
				//pos.add(swapSlot.get(slot-1)); // click where your account is
				////pos.add(new xy(889,663)); // click ok, Okay pos will change when we add more account, lets make it last swapSlots
				//pos.add(swapSlot.get(swapSlot.size()-1));
				//pos.add(new xy(869,562)); // click load
				//pos.add(new xy(549,275)); // click textbox
				//pos.add(new xy(853,269)); // click Okay
				
				pos.add(new xy(1013,648)); //click setting
				pos.add(new xy(629,190)); // click setting tab
				pos.add(new xy(623,487)); // click disconnect
				pos.add(swapSlot.get(slot-1)); // click where your account is
				//pos.add(new xy(889,663)); // click ok, Okay pos will change when we add more account, lets make it last swapSlots
				pos.add(swapSlot.get(swapSlot.size()-1)); //no more ok button but still here for position
				pos.add(new xy(708,498)); // click load
				pos.add(new xy(501,264)); // click textbox
				pos.add(new xy(711,261)); // click Okay
				
				//s = new struct("swap",602,302,237,47, ruleFolder+"\\swap.jpg",pos); //load Image
				s = new struct("swap",539,328,170,36, ruleFolder+"\\swap.jpg",pos); //load Image
			}
			if(name.equals("zeroBuilder"))
			{						
				//change match pic, to 0 builders
				s = new struct("zeroBuilder",517,91, 16, 14, ruleFolder+"\\init.jpg",pos);
			}	
			if(name.equals("zeroBuilder4"))
			{						
				//change match pic, to 0 builders
				s = new struct("zeroBuilder4",518,91, 16, 14, ruleFolder+"\\zeroBuilder4.jpg",pos);
			}	
			if(name.equals("maxElixir"))
			{
				s = new struct("maxElixir",1174,102, 17, 16, ruleFolder+"\\maxElixir.jpg",pos);
			}
			if(name.equals("maxGold"))
			{
				s = new struct("maxGold",1174,33, 17, 16, ruleFolder+"\\maxGold.jpg",pos);
			}
			if(name.equals("fullGold"))
			{				
				s = new struct("fullGold",863,106, 133, 4, ruleFolder+"\\maxGold.jpg",pos);
			}
			if(name.equals("fullElixir"))
			{				
				s = new struct("fullElixir",859,157, 137,4, ruleFolder+"\\maxElixir.jpg",pos);
			}
			if(name.equals("barrackBoost"))
			{
				// this only works on MAX barracks, currently lvl 10. (there is no upgrade button)  so only 4 button when not boosted
				pos.add(xyBarrack);
				pos.add(new xy(788,735)); // boost all button
				pos.add(new xy(721,495)); // pay with gem
				
				// match on middle of the Boost All image (2 clocks)
				s = new struct("barrackBoost",754,724, 58, 18, ruleFolder+"\\barrackBoost.jpg",pos);
			}
			if(name.equals("try"))
			{				
				pos.add(new xy(829,569)); // Try these apps button
				
				//match on bluestack icon middle top
				s = new struct("try",564,90, 122, 101, ruleFolder+"\\try.jpg",pos);
			}
			if(name.equals("search"))
			{				
				//pos.add(new xy(80,745)); // Return home button
				pos.add(new xy(255,732)); // Return home button
				//match on return home, the women's head
				//s = new struct("search",62,774, 50, 39, ruleFolder+"\\search.jpg",pos);
				s = new struct("search",236,705, 42, 34, ruleFolder+"\\search.jpg",pos);
			}	
			if(name.equals("bluestack"))
			{				
				//pos.add(new xy(309,192)); // click last open app (should be clash of clan)
				pos.add(new xy(209,172)); // click last open app (should be clash of clan)
				
				//match on search button
				//s = new struct("bluestack",54,139, 87, 87, ruleFolder+"\\bluestack.jpg",pos);
				s = new struct("bluestack",37,126, 74, 79, ruleFolder+"\\bluestack.jpg",pos);
			}
			if(name.equals("loadVillage"))
			{
				pos.add(new xy(538,492)); // click cancel
				//s = new struct("swap",580,269,284,55, ruleFolder+"\\swap.jpg",pos); //load Image				
				s = new struct("swap",539,328,170,36, ruleFolder+"\\swap.jpg",pos); //load Image
			}
			if(name.equals("confirm"))
			{
				pos.add(new xy(782,102)); // click cancel
				s = new struct("confirm",537,82,163,28, ruleFolder+"\\confirm.jpg",pos); //matchon Are you sure?
			}
		}

	}
	
	
	public int getDeployArcher()
	{
		return deployArcher;
	}
	public int getDeployBarb()
	{
		return deployBarb;
	}
	public String getLootThreshold()
	{
		return lootThreshold;
	}
	public String getEmail()
	{
		return email2;
	}
	public boolean getSmartLoot()
	{
		return smartLoot;
	}
	public String getPW()
	{
		return pw;
	}
	public String getName()
	{
		return s.name;
	}
	public int getX()
	{
		// I have to subtract rule by default is 140, 27
		return s.x + this.offsetX - 142;
	}
	public int getY()
	{
		return s.y + this.offsetY - 27;
	}
	
	public int getXNOTOFF()
	{
		
		return s.x;
	}
	public int getYNOTOFF()
	{
		return s.y ;
	}
	
	public int getW()
	{
		return s.w;
	}
	public int getH()
	{
		return s.h;
	}
	public String getFile()
	{
		return s.file;
	}
	public ArrayList<xy> getPos()
	{
		return s.pos;
	}
	public boolean isAutoSwap() {
		return autoSwap;
	}

	public void setAutoSwap(boolean autoSwap) {
		this.autoSwap = autoSwap;
	}
	
	 public interface User32 extends StdCallLibrary {
	      User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
	               W32APIOptions.DEFAULT_OPTIONS);

	      HWND FindWindow(String lpClassName, String lpWindowName);

	      int GetWindowRect(HWND handle, int[] rect);
	   }

 public void setScreenOffset()  {
	HWND hwnd = User32.INSTANCE.FindWindow(null, "Bluestacks App Player");
			
	int[] rect = {0, 0, 0, 0};
	int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
	
	offsetX = rect[0];
	offsetY = rect[1];
	}
 
 
	
	
	/*
	 * x, y - x,y coordinate 
	 * w, h - width , height  These are the area to check
	 * 
	 * file - file name
	 * 
	 * pos = arraylist, that holds position that SHOULD be click in order. 
	 */
	public class struct
	{
		String name;
		int x;
		int y;
		int w;
		int h;
		String file;
		ArrayList<xy> pos = new ArrayList();
		
		public struct(String n, int x1, int y1, int w1, int h1, String f, ArrayList<xy> a)
		{
			name = n;
			x = x1;
			y = y1;
			w = w1;
			h = h1;
			file = f;
			pos = a;
		}
	}
	
	public class xy
	{
		int x;
		int y;
		public xy(int x1, int y1)
		{
			x = x1;
			y = y1;
		}
		
		public int getX()
		{
			return x + offsetX - 142; 
		}
		public int getY()
		{
			return y  + offsetY - 27;
		}
	}

	public boolean isBarrackBost() {
		return barrackBoost;
	}

	public void setBarrackBost(boolean barrackBost) {
		this.barrackBoost = barrackBost;
	}
}