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
	public final static String statFile =  System.getProperty("user.dir")+"\\stat.txt";
	public final static String labFile =  System.getProperty("user.dir")+"\\lab.xml";
	public final static String HashSER = System.getProperty("user.dir")+"\\hash.ser";
	
	private xy xyBarrack = null;
	private xy xyCamp = null;
	private String email2="";
	private String pw="";
	public int deployArcher=0;
	public int deployBarb=0;	
	public String lootThreshold="";
	public int slot =0;	
	private boolean smartLoot = false;
	
	private xy xyBarrackTrain = null;
	
	public ArrayList<xy> swapSlot = new ArrayList<xy>();
	
	private ArrayList<AutoUpgradeData> autoUpgradeList = new ArrayList<AutoUpgradeData>(); // don't use anymore, only in AutoUpgrade
		
	
	
	//used only in some local method. just to do some comparison
	public config()
	{
		setup();
	}
	
	//only specify new account, just add account
	public config(String account)
	{
		setup();
		loadConfig(account,"");
				
	}
	
	// specify new and old account , called from swap. we need to add account and delete oldAccount from active
	public config(String account, String oldAccount)
	{
		setup();
		loadConfig(account, oldAccount);			
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
							
				// set auto swap
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
	public void loadConfig(String account, String oldAccount)
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
					xyCamp = createXY(eElement.getElementsByTagName("camp").item(0).getTextContent());
					pw = eElement.getElementsByTagName("password").item(0).getTextContent();
					deployArcher = Integer.valueOf(eElement.getElementsByTagName("deployArcher").item(0).getTextContent());
					deployBarb = Integer.valueOf(eElement.getElementsByTagName("deployBarb").item(0).getTextContent());
					lootThreshold = eElement.getElementsByTagName("loot").item(0).getTextContent();
					slot = Integer.valueOf(eElement.getElementsByTagName("slot").item(0).getTextContent());
					xyBarrackTrain =  createXY(eElement.getElementsByTagName("barrackTrain").item(0).getTextContent());										
					email.alDestEmail = createDestinationEmailArray(eElement.getElementsByTagName("destEmail").item(0).getTextContent());					
					smartLoot = Boolean.valueOf(eElement.getElementsByTagName("smartLoot").item(0).getTextContent());
					 
				}
			}
			
	 
			setUpSwapSlots(doc);
			// create AutoUpgrade object
			createAutoUpgrade(doc);	
			
			
			addActiveEmail(doc , account);
			if(!oldAccount.isEmpty())
			{
				deleteActiveEmail(doc, oldAccount);
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
	
	
	public void deleteActiveEmail(Document doc, String oldAccount)
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
				
				 upLoadFTP(this.configFile,"config");
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
	public void addActiveEmail(Document doc, String account)
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
				
				 upLoadFTP(this.configFile,"config");
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
		downloadFTP(config.configFile , "/config/config.xml");  
		
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
	
	public void upLoadFTP(String FileName, String dir)
	{
		if(!test){
			File f = new File(FileName);
	
			FTPClient ftp = new FTPClient();
	
			 boolean success = false;
		     int count = 0;
		   //  do 
		    //    {   
					try{		
						ftp.connect("doms.freewha.com");
						System.out.println(ftp.login("www.mturkpl.us","freewebsucks11"));		
						System.out.println(ftp.getReplyString());
						ftp.enterLocalPassiveMode();
						ftp.changeWorkingDirectory(dir);				
						
						final InputStream is = new FileInputStream(f.getPath());
						success = ftp.storeFile(f.getName(), is);
					
						is.close();
						
						ftp.disconnect();	
						count++;
					}
					catch(Exception e)
					{
						System.out.println("Error UploadFTP CONFIG method");
			        	System.out.println(e.getMessage());
								        	
			        	e.printStackTrace();
			        	success = false;					
					}
		   //     }while(!success && count < 10);
	     
		}
	}
	
	public void downloadFTP(String localFile, String remoteFile)
	{
		if(!test){
	        String server = "doms.freewha.com";
	        String user = "www.mturkpl.us";
	        String pass = "freewebsucks11";
	 
	        FTPClient ftpClient = new FTPClient();
	        
	        // if retrieveFile fails, it will retry again.
	        boolean success = false;
	        int count = 0;
	     //   do 
	      //  {        
		        try {
		 
		            ftpClient.connect(server);
		            ftpClient.login(user, pass);
		            ftpClient.enterLocalPassiveMode();
		            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		
		            File downloadFile1 = new File(localFile);
		            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
		             success = ftpClient.retrieveFile(remoteFile, outputStream1);
		            outputStream1.close();
		            ftpClient.disconnect();	  
		            
		            count++;
		        }
		        catch(Exception e)
		        {
		        	System.out.println("Error downloadFTP");
		        	System.out.println(e.getMessage());
		        	e.printStackTrace();
		        	success = false;
		        }
	      //  }while(!success && count < 10);
		}
         
	}
	
	public void setName(String name)
	{
		
		ArrayList<xy> pos = new ArrayList();
		if(work)
		{
			if(name.equals("main"))
			{				
				pos.add(new xy(89,743)); // click attack button
				//change match pic, to builders head
				s = new struct("main",520,21, 37, 38, ruleFolder+"\\main.jpg",pos);
			}
			else if(name.equals("camp_full"))
			{
				//use barracks jpg different coordinate, match on All Camps Full!
				s = new struct("camp_full",692,174, 122, 25, ruleFolder+"\\barracks.jpg",pos); 
			}
			else if(name.equals("camp"))
			{
				//pos.add(new xy(359,466)); //click camp				
				pos.add(xyCamp);
				pos.add(new xy(660, 739)); // click info
				
				/*
				 * Uses Army Camp text image crop, but have to be careful and see if lvl 7 army camp moves the texts
				 */
				s = new struct("camp",	558, 155, 167, 37, ruleFolder+"\\camp.jpg",pos);
			}
			else if(name.equals("find")) // matching queue
			{
				pos.add(new xy(285, 634)); // click find a match
				pos.add(new xy(772, 470)); // in case shield is on, click okay
				s = new struct("find",	204, 610, 141, 66, ruleFolder+"\\find.jpg",pos);
			}
			else if(name.equals("init")) // main village, with screen setup (zoomed out and scrolledup)
			{
				s = new struct("init",	1009, 63, 111, 64, ruleFolder+"\\init.jpg",pos);				
			}
			else if(name.equals("barracks"))
			{
				//pos.add(new xy(505,297)); //click barracks 
				pos.add(xyBarrack); 
				pos.add(xyBarrackTrain); //click train
				
				//match on Troop capacity after training:
				s = new struct("barracks",	562, 298, 203, 21, ruleFolder+"\\barracks.jpg",pos);				
			}
			else if(name.equals("battle")){
				
				pos.add(new xy(1316,612)); //click next
				s = new struct("battle",31, 648, 121, 28, ruleFolder+"\\battle.jpg",pos);	//match on end battle
			}
			else if(name.equals("end")){
				pos.add(new xy(718,686)); //click return home			
				s = new struct("end",666,669, 105, 48, ruleFolder+"\\end.jpg",pos);
			}
			else if(name.equals("gold")){				
				s = new struct("gold",64,87, 110, 24, ruleFolder+"\\gold.jpg",pos);			      			       
			}
			else if(name.equals("elixer")){				
				s = new struct("elixer",59,125, 110, 24, ruleFolder+"\\elixer.jpg",pos);
			}
			
			else if(name.equals("safe"))
			{
				pos.add(new xy(1396,42)); 
				s = new struct("safe",0,0, 0, 0, ruleFolder+"\\safe.jpg",pos); 
			}
			else if(name.equals("attack"))
			{
				//pos.add(new xy(246,337));
				pos.add(new xy(246,337));
				pos.add(new xy(828,105));
				pos.add(new xy(890,890));
				pos.add(new xy(201,602));
				s = new struct("attack",0,0, 0, 0, ruleFolder+"\\attack.jpg",pos); 
			}
			else if(name.equals("barbs"))
			{
				pos.add(new xy(450,400)); // position of barb in camp
				pos.add(new xy(1159,398)); // next button
				pos.add(new xy(254,764)); // position in battle screen
				s = new struct("barbs",221,698,55, 68, ruleFolder+"\\barbs.jpg",pos); 
			}
			else if(name.equals("archs"))
			{
				pos.add(new xy(590,408)); // position of archs in camp
				pos.add(new xy(1159,398)); // next button
				pos.add(new xy(344,750)); // position in battle screen
				s = new struct("archs",317,701, 54, 59, ruleFolder+"\\archs.jpg",pos); 
			}
			else if(name.equals("king"))
			{				
				
				pos.add(new xy(431,746));
				s = new struct("king",400,726, 70, 43, ruleFolder+"\\king.jpg",pos);  //new queen position
				
			}
			else if(name.equals("inactive"))
			{
				//pos.add(new xy(391,658));
				s = new struct("inactive",25,29, 23, 18, ruleFolder+"\\inactive.jpg",pos); 
			}
			else if(name.equals("raided"))
			{
				pos.add(new xy(89,743)); // click return home
				s = new struct("raided",49,712, 68, 47, ruleFolder+"\\raided.jpg",pos); 
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
				pos.add(new xy(431,746));
				s = new struct("queen",400,726, 70, 43, ruleFolder+"\\queen.jpg",pos);  //new queen position
			}
			else  if(name.equals("status"))
			{
				pos.add(new xy(63,187));
				pos.add(new xy(666,106));
				s = new struct("status",0,0,0,0, ruleFolder+"\\status.jpg",pos);
			}
			else if(name.equals("swap"))
			{
				pos.add(new xy(1379,646)); //click setting
				pos.add(new xy(723,489)); // click disconnect
				pos.add(swapSlot.get(slot-1)); // click where your account is
				//pos.add(new xy(889,663)); // click ok, Okay pos will change when we add more account, lets make it last swapSlots
				pos.add(swapSlot.get(swapSlot.size()-1));
				pos.add(new xy(837,507)); // click load
				pos.add(new xy(607,255)); // click textbox
				pos.add(new xy(844,255)); // click Okay
				
				s = new struct("swap",613,285,217,37, ruleFolder+"\\swap.jpg",pos);
			}
			if(name.equals("zeroBuilder"))
			{						
				//change match pic, to 0 builders
				s = new struct("zeroBuilder",581,29, 22, 23, ruleFolder+"\\init.jpg",pos);
			}	
			if(name.equals("zeroBuilder4"))
			{						
				//change match pic, to 0 builders
				s = new struct("zeroBuilder4",581,29, 22, 23, ruleFolder+"\\zeroBuilder4.jpg",pos);
			}	
			if(name.equals("maxElixir"))
			{
				s = new struct("maxElixir",1196,99, 17, 16, ruleFolder+"\\maxElixir.jpg",pos);
			}
			if(name.equals("maxGold"))
			{
				s = new struct("maxGold",1196,33, 17, 16, ruleFolder+"\\maxGold.jpg",pos);
			}
			
		}
		else
		{
			
			if(name.equals("main")) // main village
			{
				pos.add(new xy(81,655));
				s = new struct("main",462,20, 32, 35, ruleFolder+"\\main.jpg",pos);
			}
			else if(name.equals("camp")) // when click army camp
			{
				pos.add(new xy(332,416)); //click camp
				pos.add(new xy(581, 668)); // click info	
				s = new struct("camp",	495, 137, 268, 29, ruleFolder+"\\camp.jpg",pos); 
			}
			else if(name.equals("camp_full")) // full army camp, 200/200
			{
				//pos.add(new xy(332,416)); //click camp
				//pos.add(new xy(581, 668)); // click info
				pos.add(new xy(455,265)); //click barracks
				pos.add(new xy(764,633)); //click train
				s = new struct("camp_full",	352, 553, 158, 31, ruleFolder+"\\camp_full.jpg",pos);
			}
			else if(name.equals("find")) // matching queue
			{
				pos.add(new xy(260, 560)); // click find a match
				pos.add(new xy(772, 470)); // in case shield is on, click okay
				s = new struct("find",	185, 541, 121, 61, ruleFolder+"\\find.jpg",pos);
			}
			else if(name.equals("init")) // main village, with screen setup (zoomed out and scrolledup)
			{
				s = new struct("init",	880, 28, 100, 50, ruleFolder+"\\init.jpg",pos);				
			}
			else if(name.equals("barracks"))
			{
				pos.add(new xy(455,265)); //click barracks
				pos.add(new xy(764,633)); //click train
				//pos.add(new xy(745,673)); //click train for boosted barracks
				s = new struct("init",	439, 564, 426, 22, ruleFolder+"\\barracks.jpg",pos);	
				
			}
			else if(name.equals("safe"))
			{
				pos.add(new xy(1245,42)); 
				s = new struct("safe",0,0, 0, 0, ruleFolder+"\\safe.jpg",pos); 
			}
			else if(name.equals("barbs"))
			{
				pos.add(new xy(404,356)); // position of barb in camp
				pos.add(new xy(1025,375)); // next button
				pos.add(new xy(225,672)); // barbs position in battle
				
				s = new struct("barbs",197,621, 49, 56, ruleFolder+"\\barbs.jpg",pos); 
			}
			else if(name.equals("archs"))
			{
				pos.add(new xy(523,355)); // position of archs in camp
				pos.add(new xy(1025,375)); // next button
				pos.add(new xy(306,658)); // archer position in battle
				s = new struct("archs",282,619, 50, 61, ruleFolder+"\\archs.jpg",pos); 
			}
			else if(name.equals("gold")){				
				s = new struct("gold",57,77, 89, 22, ruleFolder+"\\gold.jpg",pos);			      			       
			}
			else if(name.equals("elixer")){				
				s = new struct("elixer",55,110, 90, 25, ruleFolder+"\\elixer.jpg",pos);
			}
			else if(name.equals("battle")){
				
				pos.add(new xy(1160,547)); //click next
				//mathc on "end Battle" button
				s = new struct("battle",35, 557, 97, 24, ruleFolder+"\\battle.jpg",pos);	
			}
			else if(name.equals("end")){
				pos.add(new xy(657,623)); //click return home			
				s = new struct("end",589,590, 93, 49, ruleFolder+"\\end.jpg",pos);
			}
			else if(name.equals("attack"))
			{				
				pos.add(new xy(219,304));
				pos.add(new xy(714,90)); //363
				pos.add(new xy(823,766)); //363
				pos.add(new xy(194,570)); //363
				s = new struct("attack",0,0, 0, 0, ruleFolder+"\\attack.jpg",pos); 
			}
			else if(name.equals("king"))
			{
				pos.add(new xy(391,658));
				s = new struct("king",366,628, 44, 57, ruleFolder+"\\king.jpg",pos); 
			}
			else if(name.equals("inactive"))
			{
				//pos.add(new xy(391,658));
				s = new struct("inactive",24,25, 19, 16, ruleFolder+"\\inactive.jpg",pos); 
			}
			
			else if(name.equals("raided"))
			{
				pos.add(new xy(70,671)); // click return home
				s = new struct("raided",29,636, 86, 76, ruleFolder+"\\raided.jpg",pos); 
			}

			
			else if(name.equals("de")) // used to see if DE exist
			{				
				s = new struct("de",33,148, 21, 21, ruleFolder+"\\de.jpg",pos); 
			}
			else if(name.equals("de_amt")) // used to get DE amount
			{				
				s = new struct("de",54,142, 77, 25, ruleFolder+"\\de.jpg",pos); 
			}
			else if(name.equals("status"))
			{
				pos.add(new xy(48,578)); // click inbox
				pos.add(new xy(577,90)); // click attack log
				s = new struct("status",0,0, 0, 0, ruleFolder+"\\status.jpg",pos); 
			}
			else if(name.equals("queen"))
			{
				pos.add(new xy(391,658));
				s = new struct("queen",358,626, 44, 57, ruleFolder+"\\queen.jpg",pos); 
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
		return s.x;
	}
	public int getY()
	{
		return s.y;
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
			return x;
		}
		public int getY()
		{
			return y;
		}
	}
}