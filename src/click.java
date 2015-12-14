import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Authenticator;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import email.email;
import setting.AutoUpgradeData;
import setting.config;
import setting.config.xy;
import comparator.*;




public class click
{
	boolean activateHero = false; //activate king
	Robot bot = new Robot();
	boolean serviceStopped = false;
	boolean sendStopText = true;
	boolean sendPicText= false;
	boolean disconnected =false;
	
	
	
	private gui guiFrame = null;
	private control cont = null;
	
	int lootThreshold = 150000;
	
	StopWatch s = new StopWatch();
	
	public static config con = null;
	
	private String email;
	private email e;
	private boolean master = false;
	
	ArrayList<AutoUpgradeData> alAutoUpgradeBuilderNOW = new ArrayList<AutoUpgradeData> ();
	ArrayList<AutoUpgradeData> alAutoUpgradeBuilderSTATIC = new ArrayList<AutoUpgradeData> ();
	
	//every time I swap to the new account, the new account will get swapDate updated with current date.
	static Hashtable<String, AutoUpgradeData> hashAutoUpgradeSWAP = new Hashtable <String, AutoUpgradeData> ();
	
	private updateStat updateS ;
	
	public click() throws Exception
	{
		//downloadFTP(config.configFile , "/config/config.xml", true);  
		setGUIandControl();		
		
		
		RunEmailService();
		RunUpdateStatService();
		RunUpdateWebPage();
		RunCheckForStuckPages();
		
				
	//	AutoUpgrade();
		/*
		 * 
		 * 		
		AutoUpgradeData aud = new AutoUpgradeData();

		Calendar d = Calendar.getInstance();
		Date currDate = d.getTime();
		
		String t = "10/12/2015 9:22 PM";
		aud.setEmail("docogo1@gmail.com");
		
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy h:mm a");
		Date date = format.parse(t);	
		
		aud.setSwapDate(date);
		aud.setTime("static2");
		isSwapStatic(aud, currDate);
		
		
		BufferedImage screencapture =cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		String name = "current.jpg";
		File outputFile = new File(name);
        ImageIO.write(screencapture, "jpg", outputFile);	
        
        System.out.println(compareImage("queen"));
        deployHero("queen");
		System.exit(0);
				
		System.out.println(gotRaided()+"GOT RAIDED");mt
	
				deployTroops2();
		System.exit(0);
		saveLootParent("C:\\Users\\shami_000\\Documents\\GitHub\\image\\rule\\", "current.jpg");
		
			email = "docogo.two@gmail.com";
		swap();
		
      
//		upLoadFTP("config.xml","config");
		downloadFTP("config.xml" , "/config/config.xml");
		System.exit(0);
		AutoUpgrade();
		System.exit(0);
				AutoUpgradeBuilder();
		System.exit(0);
	*/


		
		while(true)
		{	
		// if at work, we check read.txt value
			try{
				// change to case statement. 2=bot, 1=stay active dont bot, 0= stop bot.
				int value = isServiceStarted2(con.readFile);				
				//while((config.work)?isServiceStarted("C:\\Documents and Settings\\dhwang\\My Documents\\read.txt"):true)				
				
				// Always checking end of battle will allow us to not get stuck.
				// for example let's say we are in middle of deploying troops. Email service reads a 6(load config) , 5 (swap) 
				// read.txt now is not 2 anymore, loops out of deploy droops, read value is now say 5, 
				// bluestack screen is still technically can be in battle (maybe queen didn't get deployed, so will wait till time finishes)
				// So since read value is 5, we will check if inMain(), if we are then we swap, other wise keep looping.
				// WE WILL NEVER GET OUT OF THE END OF BATTLE SCREEN UNLESS READ IS 2 again.
				// BY Always checking end of battle dispite read value, we are guarrantee to not get stuck., I can now swap , config: anytime I want
				
				if(endOfBattle())
				{
					returnHomeFromBattle();
					disconnected = false;
					if(s.isStarted())
					{
						guiFrame.info("Stopping Stop Watch");
						s.reset();			
					}
				}
				
				switch(value)
				
				{	
				case 2:
				if(inMain())
				{
					if(disconnected) GotDisconnected();	
					
					
					updateSwapDate(con.getEmail(), true, false, false, false, 0,0); //update last time this email was active
					
					AutoUpgrade2(); // check for swap NOW, Static
					AutoUpgradeBuilder(); // check if free builder, if so then build					
					AutoSwapFullLoot(); // check if loot is full, if so then swap										
					sendPictureText(); //if email has status, will click to attack log and send pic back
					setUpScreen();			
					
					//has to be after screen setup
					//upgradeLab(con.getEmail()); // lab upgrade FH LOCAL
					clickSafeSpot();
					clickAttackLog(); //click attack log/ camp, save pics
					
					
					barrackBoost();
					
					clickSafeSpot();
					clickSafeSpot(); // do it twice, if we ran out of gem for barrack boost, the first clicksafespot will only get rid of the 
									// gem screen, the barrack will still be clicked
					//Let's train troops first
					guiFrame.info("Train troops");				
					if(clickBarracks()) // click barracks, make sure we clicked
					{
						guiFrame.info("we are in barracks");
						for(int i=0; i< 2; i++) // only 4 barracks right now
						{
							trainBarbs();
							trainArchs();
						}
						
						clickSafeSpot(); // get out of barracks screen out to main
						clickSafeSpot();// do it twice to loose focuse of barracks from before just in case
						// clicking it when it has focus is loosing focus						
					}	
					
					
					//if(clickCamp())
					if(clickBarracksForCamp()) // changed to barracks to check for full camp
					{
						guiFrame.info("camp is full");
						//click attack
						clickSafeSpot(); // get out to main screen
						clickAttack();// click attack to go to find match queue					
					}
					
				
					
				}
				if(inFindPage())
				{
					guiFrame.info("In find page");
					clickFind();
					//afterwards we should now be in battle screen				
				}
				if(inBattle(true))
				{	
					
					 //* disconnected works as following:
					// * When we are in battle, which means we are either finding a match or inside of battle.
					// * disconnected is set to true.
					// * The only way disconnected get sets to false is if we get into return home from battle, this mean we successfully came out of battle.
					// * 
					// * scenario: we are in battle, discconnected = true, we finish delploying troops we break out of deploytroops
					// * we haven't return home from battle yet so disconnected is still true, we end loop and check if we are inMain().
					// * IF we are inMain() this means we got d/c,
					// * if we are not in main (we are still in battle, troops are still attacking) so there was no disconnect
					// * 
					// * So the criteria for disconnect is if disconnected = true, and program sees we got into main screen with disconnected=true.
					// * which means we NEVER return home from battle.
					 
					disconnected = true;
					boolean attack = false;
					
					if(!s.isStarted())
					{
						guiFrame.info("Starting Stop Watch");
						s.start(); // start stop watch
					}
					
					
					
					while(inBattle(false) && (!(attack = shouldIAttack()))) 
					{
						guiFrame.info(String.valueOf(attack));
						clickNext();
						
					}
					if(attack)
					{
						guiFrame.info("Attacking!");
						deployTroops2();
					}
					
					
				}
				
				if(endOfBattle())
				{
					returnHomeFromBattle();
					disconnected = false;
					if(s.isStarted())
					{
						guiFrame.info("Stopping Stop Watch");
						s.reset();			
					}
				}
				
				if(gotRaided())
				{
					returnHomeFromRaided();
				}

				Thread.sleep(1000);
				clickSafeSpot(); // lets click the screen, in case, getting kicked for too long, or any connection problems it will restart the game.
				guiFrame.info("end loop");
				break;
				
				case 5:
					if(inMain())
					{
						//email is from read file
						swap(email, con.getEmail(), true);				
					}
					
					break;
				
				case 1:
					clickSafeSpot(); // click safe spot to be active
					if(inMain())
					{
						updateSwapDate(con.getEmail(), true, false, false, false, 0 ,0); //update last time this email was active
						AutoUpgrade2();
						AutoUpgradeBuilder();
						upgradeLab(con.getEmail());
						AutoSwapFullLoot();
						clickAttackLog();
						
					}
					Thread.sleep(30000);
					guiFrame.info("STAY ACTIVE");
					break;
					
				case 0:
					try{
						//only at work
						//send text to me, if service has ebeen stopped:
						if(con.work && serviceStopped && sendStopText)  //sendStopText only sends the first time service was stopped.
						{									// or else each loop it'll keep sending.
															// might not need these variables anymore (now we change to switch), but lets jus leave it.
						    
							sendText("Bot stopped, safe to connect" , "Services stopped");
							guiFrame.info("SEND TEXT");
							sendStopText = false;
						}
						}
						catch(Exception e)
						{
							guiFrame.info(e.getMessage());				
						}
					break;
					
				default:
						System.out.println("default? don't need to do anything");
			
			}
			}
			catch(Exception e)
			{
			
				guiFrame.info(e.getMessage());
				
				if(s.isStarted())
				{
					guiFrame.info("Error, stopping stop watch");
					s.reset();
				}
			}
		}
	}
	
	
	public void barrackBoost() throws Exception 
	{
		guiFrame.info("Barrack boost value: " + con.isBarrackBost());
		
		if(con.isBarrackBost())
		{
			con.setName("barrackBoost");
		    cont.mouseMove(con.getPos().get(0).getX(), con.getPos().get(0).getY()); // click first one, which is barracks
			cont.mousePress(InputEvent.BUTTON1_MASK);
			cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			Thread.sleep(1000);
			
			
			//we should have clicked the barracks
			//lets compare image and see if we need to boost
			 
			boolean needToBoost = compareImage("barrackBoost");
			if(needToBoost)
			{
				for(int i= 1; i  < con.getPos().size(); i++) // i = 1, dont need to click barrack again
				{
					cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
					cont.mousePress(InputEvent.BUTTON1_MASK);
					cont.mouseRelease(InputEvent.BUTTON1_MASK);			
					Thread.sleep(1000);
				}
			}
			
		}
		
	}
	public void seralize(Hashtable o, String s) 
	{						
		
		try{
			 File f =  new File(s);
			 OutputStream file = new FileOutputStream(f );
		     OutputStream buffer = new BufferedOutputStream( file );
		     ObjectOutput output = new ObjectOutputStream( buffer );
		      	      
		      output.writeObject(o);
		      output.close();
		      file.close();
		      buffer.close();		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			guiFrame.info("Error Seraliz Hashtable");
			guiFrame.info(e.getMessage());
		}
		
	}
	
	public Hashtable deSeralize(Hashtable l, String s) 
	{			
		try{			
			File fileName =  new File(s);
			InputStream file = new FileInputStream(fileName);
			InputStream buffer = new BufferedInputStream( file );
			ObjectInput input = new ObjectInputStream ( buffer );  
	    		  	    
			l =  (Hashtable)input.readObject(); 		
	    
			file.close();
			buffer.close();
			input.close();	    
		}
		catch(FileNotFoundException fnfe) // returns a brand new Data, if data.ser does not exist, first time
		{
			l = new Hashtable();
			return l;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			guiFrame.info("Error deSeralize Hashtable");
			guiFrame.info(e.getMessage());
		}
		
		return l;
		  
	}
	
	
	/*
	 * reads lab.xml
	 * 
	 * account is our email
	 * loads up all item's XY . each item is troop to upgrade
	 * 
	 * goes thru all of them and try to upgrade.
	 * 
	 */
	public void upgradeLab(String account)
	{
		downloadFTP(config.labFile , "/config/lab.xml", false);
		Document doc  = createDocFromXML(config.labFile);
		ArrayList<AutoUpgradeData> al = new ArrayList<AutoUpgradeData>();
		
		NodeList nList = doc.getElementsByTagName("email");			
		for (int temp = 0; temp < nList.getLength(); temp++) 
		{			
			Node nNode = nList.item(temp);				
			Element eElement = (Element) nNode;
			String e  = eElement.getAttribute("id");
			
			if(e.equals(account)) // if this is same account as what was input
			{
				// position of lab and research
				NodeList lab = eElement.getElementsByTagName("lab");	
				NodeList itemList = eElement.getElementsByTagName("items");
				
				loadXYnodeList(lab,"pos", al, true);
				loadXYnodeList(itemList, "item", al, true);

			}
		}
		
		//al now contains all XY records
		try{
			for(int i = 0; i < al.size(); i ++)
			{
				clickAutoUpgrade(al.get(i), 500);
			}
		}
		catch(Exception e)
		{
			guiFrame.info("Error in UpgradeLab");
			e.printStackTrace();
		}
		
	}
	
	public Document createDocFromXML(String s)
	{	
		Document doc = null;
		try{
			File fXmlFile = new File(s);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		}
		catch(Exception e)
		{
			guiFrame.info("error in CreatDocFromXML");			
		}
		return doc;
	}
	
	/*
	 * will swap to next account when loot is full
	 * it will mark lootFull in AutoUpgradeData in hashAutoUpgradeSWAP to true if it's full so we know it's full an dodn't need to bot in it.
	 * 
	 * since getSetLootFUll will be call everytime we succesfully swap we have a real time value of it (dont need to full reset)
	 * only problem I see is if program thinks its full, but I manually upgrade something, program will still think it's full UNTIL it swaps to it.
	 * So only downside is if I never swap to it, I will always think it's full.
	 * 
	 * It will then select next email that has lootFUll= false.
	 * if all lootFull is true then don't do anything just keep botting at current email
	 * 
	 * 
	 */
	public void AutoSwapFullLoot() throws Exception
	{
		
		boolean swap = getSetLootFull(con.getEmail());
		String swapEmail="";		
		if(swap && guiFrame.getAutoUpgrade()) // loot is full!, only do it for auto upgrade for now maybe?
		{
			swapEmail = getNextRandomEmail();
			//also have to check if email is not active
			if(!swapEmail.isEmpty()) // if it is empty we dont do anything, it means all email loot is full
			{
				sendText("Loot full!", con.getEmail() + "'s loot is full, swaping to " + swapEmail);
				swap(swapEmail, con.getEmail(), true);
			}
			else
			{
				guiFrame.info("Every email account's loot is full!");
			}
			
		}
	}
	
	public String getNextRandomEmail()
	{
		String ret="";
		downloadFTP(config.configFile , "/config/config.xml", false);  //download latest, I need latest active accounts
		Document doc  = createDocFromXML(config.configFile);
		
		Object[] values = hashAutoUpgradeSWAP.values().toArray(); //convert hash to array
		ArrayList<Object> n = new ArrayList<Object>(Arrays.asList(values)); // convert array to arraylist
		
		// convert arraylist of objects into arraylist of AutoUpgradeData
		ArrayList<AutoUpgradeData> n1  = new ArrayList<AutoUpgradeData>();
		for (Object object : n) {				
			n1.add((AutoUpgradeData) object);
		}		
		
		Collections.sort(n1, new LootPrecentageComparator()); // sort by least loot first
		//Collections.shuffle(n); //shuffle list

		for(int i=0; i < n.size(); i++)
		{
			AutoUpgradeData aud = (AutoUpgradeData) n.get(i);
			boolean full = aud.getLootFull();
			boolean exist = isEmailActive(doc, aud.getEmail() , "");
			
			if((!full) && (!exist) && aud.isAutoSwap())  // not full and not active and is autoswap
			{
				return aud.getEmail(); //return the first non full loot
			}
		}
		
		// if ret is empty string, it means every email has loot full
		return ret;
		
	}
	
	/*	
	 * check to see if loot is full. 
	 * if this gets called after autoupgrade2(static or NOW), we will have almost a real time knowledge of if loot is full or not.
	 * 
	 * should get call after every successful swap.
	 * 
	 * 
	 */
	public boolean getSetLootFull(String e)
	{
		boolean ret = false;
		AutoUpgradeData aud = null; 

		try{
			
			if(compareImage("maxElixir") && compareImage("maxGold"))
			{
				guiFrame.info("loot is full for email account " + e);
				ret = true;				
				aud = updateSwapDate(e, false, true, ret, false, 0, 0);										
			}
			else
			{
				ret = false;
				aud = updateSwapDate(e, false, true, ret ,false, 0, 0);	
			}
					
		}
		catch(Exception ex)
		{
			guiFrame.info("Error in setLootFull");
			guiFrame.info(ex.getMessage());
		}
		return ret && aud.isAutoSwap(); // if autoswap is false, don't swap (for client)
		
	}
	
	/**
	 * Takes current screen shot,
	 * use ImageParser to calculate % of gold/elixir.
	 * Once we have the %, we call updateSwapDate, this will update % of gold and elixir into hash.ser
	 * 
	 * @param e - email 
	 * 
	 * @throws Exception
	 */
	public void setCalculateLootPercentage(String e) throws Exception
	{
		takeCurrentScreenshot(false, "lootValue.jpg");
		
		ImageParser ip = new ImageParser(System.getProperty("user.dir")+"\\","lootValue.jpg", "fullGold");
		int gold = ip.value;
		
		ImageParser ip2 = new ImageParser(System.getProperty("user.dir")+"\\","lootValue.jpg", "fullElixir");
		int elixir = ip2.value;
		

		updateSwapDate(e, false, false, false, true, gold, elixir);
		
	}
	
	public boolean zeroBuilder() throws Exception
	{

		return (compareImage("zeroBuilder") || compareImage("zeroBuilder4"));
	}
	
	/* 
	 * Auto upgrade when builder is available.
	 * Current session will check for free builder.
	 * 
	 * Other session will have to swap first then will check for free builder
	 */
	public void AutoUpgradeBuilder() throws Exception
	{
		ArrayList<AutoUpgradeData>  al;
		
		setCalculateLootPercentage(con.getEmail());
		
		//if screen is setup, and have builder
		if(isScreenSetup() && (!zeroBuilder()) )
		{
			guiFrame.info("Builder free and screen setup");
			al = loadUpgradeBuilder(con.getEmail());
			
			for(int i =0; (i < al.size() && ( al.get(i).getName().equals("wall") || !zeroBuilder())); i++)	// if walls dont need to check builder		
			{	
				clickAutoUpgrade(al.get(i),500);
				takeCurrentScreenshot(false,"currentstatus.jpg");
				clickSafeSpot();
				//compare previous cropCurrent image (cropped zero builder of last time) with current zerobuilder
				// The idea is After clickAutoUpgrade(), and my image is still the same it means nothing happened, so there was no 
				// successful upgrade. If`it was different THEN I should send text to show upgrade.
				
				//make sure call from zerobuilder() and here does not have any compare image, or else my cropCurrent would be overwritten
				
				boolean send = false;
				if(!al.get(i).getName().equals("wall"))
				{
					send = !SameBuilderImage();	
				}	
								
				 if(send)
		        {
		        	sendPictureText("currentstatus.jpg");
		        }
	
			}
		}
		
	}
	
	public boolean SameBuilderImage() throws Exception
	{
		boolean ret = false;
		File f = new File("cropBuilderPrev.jpg");
		f.delete();
		
		File f2 = new File("cropcurrent.jpg");
		
		f2.renameTo(f);
		
		zeroBuilder();
		
	    ImageCompare ic = new ImageCompare("cropBuilderPrev.jpg", "cropcurrent.jpg");
	    ret =  ic.setupAndCompare(ic);
	    
	    return ret;
	}
	
	
	public ArrayList<AutoUpgradeData> loadUpgradeBuilder(String account)
	{
		ArrayList<AutoUpgradeData> alAutoUpgradeBuilder = new ArrayList<AutoUpgradeData> ();
		try{					
			downloadFTP(config.upgradeFile , "/config/upgrade.xml", false);
			
			File fXmlFile = new File(config.upgradeFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("email");			
			for (int temp = 0; temp < nList.getLength(); temp++) 
			{
				
				Node nNode = nList.item(temp);				
				Element eElement = (Element) nNode;
				String e  = eElement.getAttribute("id");
				
				if(e.equals(account)) // if this is same account as what was input
				{
					// load elixir and gold separately.
					
					NodeList elixirList = eElement.getElementsByTagName("elixir");				// only 1 element	
					NodeList goldList = eElement.getElementsByTagName("gold");					// only 1 element
					
					
					AutoUpgradeData aud = hashAutoUpgradeSWAP.get(account);
					
					int gold = aud.getTempIntA();
					int elixir = aud.getTempIntB();
					
					boolean threshold = (gold > 90 || elixir > 90);
					
					
					// if there are nore elixir, always build elixir first
					if(elixir > gold)
					{
						loadXYnodeList(elixirList, "item",alAutoUpgradeBuilder, threshold);
						loadXYnodeList(goldList, "item",alAutoUpgradeBuilder, threshold);  // in case I dont have elixir, I can still upgrade gold
					}
					// there are more gold, and difference exceeds 20%
					else if((gold - elixir) > 20) 
					{
						loadXYnodeList(goldList, "item",alAutoUpgradeBuilder, threshold);
						loadXYnodeList(elixirList, "item",alAutoUpgradeBuilder, threshold);
					}
					else // gold is more but the difference is less the 20% , lets do random
					{
						Random r  = new Random();
						int i = r.nextInt(2);
						if(i%2==0)
						{
							loadXYnodeList(goldList, "item",alAutoUpgradeBuilder, threshold);
							loadXYnodeList(elixirList, "item",alAutoUpgradeBuilder, threshold);
						}
						else
						{
							loadXYnodeList(elixirList, "item",alAutoUpgradeBuilder, threshold);
							loadXYnodeList(goldList, "item",alAutoUpgradeBuilder, threshold);							
						}
					}
					
				}
			}									
		}
		catch(Exception e)
		{
			e.printStackTrace();
			guiFrame.info("Error in loadUpgradeBuilder");
		}
		
		return alAutoUpgradeBuilder;
	}
	
	/*
	 * n1 takes NodeList that should contain <xy> records
	 * 
	 * this method will loop thru the list and add AutoUpgradeData into alAutoUpgradeBuilder
	 * 
	 * 
	 * @param - added third parameter, to check to see if loot percentage value exceeds a certain value. 
	 * if it does then we add walls., if not then don't bother with walls, we just waste time trying to upgrade it.
	 */
	public void loadXYnodeList(NodeList nl, String tagName, ArrayList<AutoUpgradeData> al, boolean threshold)
	{
		for(int a = 0 ; a < nl.getLength(); a ++) //should only be 1 length
		{			
			Node nNode = nl.item(a);				
			Element eElement = (Element) nNode;
			
			NodeList itemList = eElement.getElementsByTagName(tagName); // get all item
			for(int i = 0 ; i < itemList.getLength(); i ++)
			{						
				Node itemNode = itemList.item(i);				
				Element itemEle = (Element) itemNode;
				
				AutoUpgradeData aud = new AutoUpgradeData();

				NodeList xyList = itemEle.getElementsByTagName("xy");	
				String name = itemEle.getElementsByTagName("name").item(0).getTextContent();
				
				if( !name.equals("wall") ||  threshold)
				{
					for (int j = 0; j < xyList.getLength(); j++) 		
					{		
						Node n = xyList.item(j);		
						Element xye = (Element) n;		
								
						xy newXY = createXY(xye.getTextContent());		
						aud.getXYArrayList().add(newXY);		
						aud.setName(name);
										
					}							
					al.add(aud);
				}
			}
		}
	}
	
	public xy createXY(String s)
	{
		int a = Integer.valueOf(s.substring(0, s.indexOf(",")));
		int b = Integer.valueOf( s.substring(s.indexOf(",") + 1, s.length()));
		
		xy temp = con.new xy(a,b);		
		
		return temp;
		
	}
	
	/*
	 * New auto upgrade method,
	 * - the idea is to call this everytime which will FTP everytime. might be a lot of FTP but I'm doing this now because there will now be no more
	 *   concurrent machine running auto upgrade. Also with this new way, I don't have to call config: to load config file, I can just FTP new config file
	 *   no more loading manually.
	 *   
	 * - This will work by reading ALL pos in upgrade. The sorting order now does not matter, because I will now loop thru all of them and if the time is
	 * 	  less then current time, then I will do it. So if I just want to do a quick pos click, I can just add a pos with old date and it will work.
	 * - if I want to do a future pos click ( maybe to remove gem, or to overwrite a build order from upgrade.xml) I can set the date and it will do it when
	 *   it gets to it
	 * 
	 * - and lastly there will now be a "static" id pos, which is pos that I don't want to delete. These are basically used so I can swap to them 
	 *   scheduled maybe every 2 - 3 hrs. This way I don't have to constantly upgrade config.xml to see when a builder is free. 
	 *   every 2-3 hrs it will swap to another account, if it sees a free builder it will build. if it doesn't it just doesn't do anything and swaps to the 
	 *   next account
	 * - not sure how I want to do this yet, Maybe something outside of this that keeps track of timing, and when it hits every 2- 3 hrs it will set TRUE
	 *   and it will try to STATIC swap.     
	 *     
	 */
	
	public void AutoUpgrade2() throws Exception
	{

			try{
				alAutoUpgradeBuilderNOW.clear();
				alAutoUpgradeBuilderSTATIC.clear();
				downloadAndLoadConfig(false); // get latest config, also update config object (it will have latest config)
				
				File fXmlFile = new File(config.configFile);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();		
				
				NodeList nList = doc.getElementsByTagName("pos");
				for (int temp = 0; temp < nList.getLength(); temp++) // each POS
				{
					Node nNode = nList.item(temp);				
					Element eElement = (Element) nNode;				
					String id = eElement.getAttribute("id");
					
					AutoUpgradeData aud = new AutoUpgradeData();
									
					String email = eElement.getElementsByTagName("upgradeEmail").item(0).getTextContent();
					boolean swapBack= Boolean.valueOf(eElement.getElementsByTagName("swapBack").item(0).getTextContent());
					
					aud.setEmail(email);
					aud.setSwapBack(swapBack);
					aud.setTime(id);
					
					NodeList xyList = eElement.getElementsByTagName("xy");
					
					for (int i = 0; i < xyList.getLength(); i++) 
					{
						Node n = xyList.item(i);
						Element xye = (Element) n;
						
						xy newXY = createXY(xye.getTextContent());
						aud.getXYArrayList().add(newXY);
								
					}
											
					if(id.startsWith("static")) 
					{
						alAutoUpgradeBuilderSTATIC.add(aud);
						
					}
					else  // if not static assume id is time
					{
						alAutoUpgradeBuilderNOW.add(aud);
					}
					
				}
				// I have loaded everything
				
				//current time
				DateFormat dateFormat = new SimpleDateFormat("MM/dd h:mm a");
				Calendar d = Calendar.getInstance();
				Date currDate = d.getTime();
				
				
				
				if(alAutoUpgradeBuilderNOW.size() > 0)
				{
					String originalEmail = con.getEmail();					
					boolean deleted =  false;
					boolean swapBack = false;
					boolean defSwapBack  =false; // this is to know if we actually swapped or not, scenario where if swapBack is true (from config)
												// the account exist is active, we need to know if it actually swapped and not soley rely on swaBack.
					
					for(int i=0; i< alAutoUpgradeBuilderNOW.size(); i++)
					{					
						defSwapBack = false; //default to false so it's for current for loop record
						String t = alAutoUpgradeBuilderNOW.get(i).getTime();
						String e = alAutoUpgradeBuilderNOW.get(i).getEmail();
						swapBack = alAutoUpgradeBuilderNOW.get(i).isSwapBack();
						AutoUpgradeData aud = alAutoUpgradeBuilderNOW.get(i);
						
						boolean active = false;
						boolean exist = true;
						
						DateFormat format = new SimpleDateFormat("MM/dd/yyyy h:mm a");
						Date date = format.parse(t);					
						
						//if my date is before current date, then do it!
						if(date.before(currDate))
						{
							guiFrame.info("There is upgrade to do");
						
							// only delete of autoupgrade, if it's not autoupgrade it'll still try to swap
							if(guiFrame.getAutoUpgrade())
							{
								deleted = true;
								deleteFromXMLSpecific(doc, t);
							}
							exist = isEmailActive(doc, e, con.getEmail()); // exist is false if does not exist in xml, or swapping email and current email is the same 							
													
							// The email is not active or email swapping to is the same as old email
							if(!exist)
							{
								if(con.getEmail().equals(e)) // if same account then dont need to swap
								{
									defSwapBack = false; //dont have to swapback original account
									guiFrame.info("same account don't need to swap");
									
									if(inMain()) // make sure in main.
									{
										setUpScreen();
										clickAutoUpgrade(aud, 2000);
										takeCurrentScreenshot(true,"currentstatus.jpg");
										clickSafeSpot();
									}
								}
								else if(guiFrame.getAutoUpgrade())
								{									
									defSwapBack = true;
									guiFrame.info("Email is not active so Swapping");
									swap(e,con.getEmail(),false);
									clickSafeSpot(); // click safe spot to get rid of raided screen
									Thread.sleep(3000);
									
									//we should be in the new account now.
									if(inMain()) // make sure in main.
									{
										guiFrame.info("In main from after swap");
										setUpScreen();
										clickAutoUpgrade(aud, 2000);
										AutoUpgradeBuilder();
										takeCurrentScreenshot(true,"currentstatus.jpg");
										clickSafeSpot(); // get rid of any screen, make sure we are in main village page so we can click setting button.		
										getSetLootFull(con.getEmail()); //update lootFull
									}
									else
									{
										guiFrame.info("Swap failed, not in main");										
									}
									
								}				
								
							}		
						}
					}
					
					//swap back to original, IF current email is not original email AND swapBack from xml is true
					if(guiFrame.getAutoUpgrade() && defSwapBack && swapBack && (!con.getEmail().equals(originalEmail)))
					{
						guiFrame.info("Swapping back to original email");
						swap(originalEmail,con.getEmail(),false); // swap back
					}
					
					if(deleted) // if we deleted from above then write back and FTP. 
					{
						//After looping all, lets take whatever is left of doc and write it to config and FTP it back
						try{
							// write the content into xml file
							guiFrame.info("Writing document to config.xml");
							TransformerFactory transformerFactory = TransformerFactory.newInstance();
							Transformer transformer = transformerFactory.newTransformer();
							DOMSource source = new DOMSource(doc);
							StreamResult result = new StreamResult(new File(con.configFile));
							transformer.transform(source, result);
							
							upLoadFTP(con.configFile,"config", false); 	
						}
						catch(Exception e)
						{
							System.out.println("error in deleteFromXMLSpecific");
							guiFrame.info("error in deleteFromXMLSpecific");
							guiFrame.info(e.getMessage());
							e.printStackTrace();
						}
					}
					
				}
				
				//static, only for auto upgrade option
				// config has to have autoSwap true. (client account will have autoSwap = false)
				if(guiFrame.getAutoUpgrade() && con.isAutoSwap() && alAutoUpgradeBuilderSTATIC.size() > 0)
				{	
					String orignalEmail = con.getEmail();
					for(int i= 0 ;  i< alAutoUpgradeBuilderSTATIC.size(); i++)
					{
						AutoUpgradeData  aud = alAutoUpgradeBuilderSTATIC.get(i);
						String email = aud.getEmail(); //email to swap to
						boolean swap = isSwapStatic(aud, currDate);
				
						boolean exist = false;
						if(swap)
						{
							guiFrame.info("Static exceeded will try to swap to " + email);
							exist = isEmailActive(doc, email, con.getEmail()); // don't compare with current email, simply see if upgrade to email exist
							
							if(!exist) //if doesn't exist
							{								
								
								if(email.equals(con.getEmail())) // same accont don't swap, all we do is click autoupgrade
								{
									guiFrame.info("Static In main same account");
									clickSafeSpot(); // go back to main									
									setUpScreen();
									clickAutoUpgrade(aud, 2000);
								}								
								else
								{
									guiFrame.info("Static email is not active swapping to " + email + "from " + con.getEmail());
									String localEmail = con.getEmail();
									boolean swapStatus = swap(email,localEmail,false);
									clickSafeSpot(); // click safe spot to get rid of raided screen
									Thread.sleep(3000);
									
									//we should be in the new account now.
									if(inMain() && swapStatus) // make sure in main and swap success
									{
										guiFrame.info("Static In main from after swap");
										setUpScreen();
										clickAutoUpgrade(aud, 2000);
										AutoUpgradeBuilder();									
										clickSafeSpot(); // get rid of any screen, make sure we are in main village page so we can click setting button.		
										getSetLootFull(email); //update lootFull
									}
									else
									{
										guiFrame.info("Swap failed, not in main");
										sendText("Swap failed","swapping from " + localEmail + " to " + email + " variable swapStatus = " + swapStatus);
									}
								
								}
							}

						}

					} //for
					
					if(!orignalEmail.equals(con.getEmail())) //check to se if current email matches orginal, if not then swap back to original
					{
						guiFrame.info("Swapping back to original email " + orignalEmail+ " From " +con.getEmail() );
						swap(orignalEmail,con.getEmail(),false); // swap back
					}
				}
				
			}
		
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		
		
	}
	
	
	/*
	 * take aud.getTime, find the number after "static" 
	 * that is the number of hours we will swapped next.
	 * 
	 * So for this current email, get last swap date, if difference of last swap date and curren date
	 * is greater then number after static then we swap
	 */
	public boolean isSwapStatic(AutoUpgradeData aud, Date d)
	{
		boolean ret = false;
		
		String email = aud.getEmail();
		
		String s = aud.getTime();
		String temp = s.substring(6);//remove the word static
		long longStatic = Long.valueOf(temp); // this is the value that it has to exceed to swap
		
		
		// get when this email was last swapped
		AutoUpgradeData swapAUD = hashAutoUpgradeSWAP.get(email);
		Date swapDate = swapAUD.getSwapDate();
	//	Date swapDate = aud.getSwapDate();
		
		guiFrame.info(email + " last swap time : " + swapDate);
		
		// get differecne between curr date and swap date
		long diff = d.getTime() - swapDate.getTime();		
		long diffHours = diff / (60 * 60 * 1000);
		
		guiFrame.info("long : " + diff);
		guiFrame.info("Hours : " + diffHours);

		//the difference has to equal or exceed static number
		if(diffHours >= longStatic)
		{
			ret = true;
			
			guiFrame.info("difference exceeds, swapping static");
		}			
						
		return ret;
	}
	
	public void deleteFromXMLSpecific(Document d, String id)
	{
		try{		
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        String expression = "//upgrade/pos[@id='"+ id + "']";
        
		Node node = (Node) xpath.compile(expression).evaluate(d, XPathConstants.NODE);
		node.getParentNode().removeChild(node);
		guiFrame.info("removed node");
		}
		catch(Exception e)
		{
			guiFrame.info("error in deleteFromXMLSpecific");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		
	}
	
	/**
	 *  check auto upgrade list if there is anything to upgrade, if there is 
	 *  we will swap to that account upgrade and swap back to originaly account and continue botting.
	 *  
	 *  @deprecated
	 */
	public void AutoUpgrade() throws Exception
	{	
		if(guiFrame.getAutoUpgrade() && (!con.getAutoUpgradeList().isEmpty()))
		{		
			clickSafeSpot();
			boolean exist  = false;
			//current time
			DateFormat dateFormat = new SimpleDateFormat("MM/dd h:mm a");
			Calendar d = Calendar.getInstance();
			Date currDate = d.getTime();
			
			// get the first one, assume to be sorted.
			String t = con.getAutoUpgradeList().get(0).getTime();
			DateFormat format = new SimpleDateFormat("MM/dd/yyyy h:mm a");
			Date date = format.parse(t);
	
			//current time - 15 min
			d.setTime(currDate);
			d.add(Calendar.MINUTE, -15); // 15 mins
			Date currDateSubtracted = d.getTime();
			
			String upgradeEmail = con.getAutoUpgradeList().get(0).getEmail().trim();
			
			AutoUpgradeData aud = con.getAutoUpgradeList().get(0);
			boolean swapBack = con.getAutoUpgradeList().get(0).isSwapBack();
	
			String oldEmail = con.getEmail();
			// if date is before date, but also 15 mins prior to current time, then lets try to upgrade
			// I don't want to keep upgrading old config file.
			
			if(date.before(currDate) && date.after(currDateSubtracted) )
			{
				guiFrame.info("have auto upgrade");
				
				//first thing i have to do is download config file. Just to make sure I have the most updated file
				//downloadFTP(config.configFile , "/config/config.xml");
				// second thing I want to do is to see if it still exist in config file. if it does I want to get rid of it
				// and go ahead and swap - > upgrade.
				// if it doesn't exist then don't do anything because it's been removed!, OR most likely been done by another bot
				// It's important to note this is running concurrently on multiple machines, So I'm hoping the download of config file
				// modifiying config and upload config back to FTP is quick.
				exist = workOnConfigFile(t , upgradeEmail, oldEmail);
				
				if(exist)
				{	
					guiFrame.info("found in XML file");
					if(oldEmail.equals(upgradeEmail)) // same account dont need to swap
					{
						guiFrame.info("same account don't need to swap");
					//	con = new config(oldEmail, true); //just to re download config, get new autoUpgrade
						if(inMain()) // make sure in main.
						{
							setUpScreen();
							clickAutoUpgrade(aud, 2000);
							takeCurrentScreenshot(true,"currentstatus.jpg");
							clickSafeSpot();
						}
					}
					else
					{
						guiFrame.info("Need to swap");
						swap(upgradeEmail,oldEmail,false); //swap to upgrade email
						clickSafeSpot(); // click safe spot to get rid of raided screen
						Thread.sleep(3000);
						
						//we should be in the new account now.
						if(inMain()) // make sure in main.
						{
							setUpScreen();
							AutoUpgradeBuilder();
							clickAutoUpgrade(aud, 2000);
							takeCurrentScreenshot(true,"currentstatus.jpg");
							clickSafeSpot(); // get rid of any screen, make sure we are in main village page so we can click setting button.
							if(swapBack)
							{
								swap(oldEmail,upgradeEmail,false); // swap back
							}
							
						}
						//if not in main something happened with swap
						// upgrade failed, doing nothing should be okay.
					}
				}
				else
				{
				//	con = new config(oldEmail, true); //get new autoUpgrade
				}
			
			} // coming out of this IF I need to get new autoupgrade
		}
	}
	public void clickAutoUpgrade(AutoUpgradeData aud, int wait) throws Exception
	{
		for(int i=0 ; i < aud.getXYArrayList().size(); i++)
		{			
			Thread.sleep(wait);
			cont.mouseMove(aud.getXYArrayList().get(i).getX(), aud.getXYArrayList().get(i).getY()); 
			cont.mousePress(InputEvent.BUTTON1_MASK);	
			Thread.sleep(400);
			cont.mouseRelease(InputEvent.BUTTON1_MASK);				
		}
	}
	/**
	 * String t - is my time , which is the ID attribute for pos tag
	 * @deprecated
	 */
	public boolean workOnConfigFile(String t, String upgradeEmail, String oldEmail)
	{
		boolean exist = false;
		try{
			File fXmlFile = new File(con.configFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();			

			boolean active  = isEmailActive(doc, upgradeEmail, oldEmail);
			
			// returns true if deleted, false if can't find
			// upgrade email has to be not active, if it's active it means another machine is running it. don't want to interrput it.
			 exist  = (!active) && deleteFromXML(doc, t);
			 
			 
			 if(exist)
			 {
				// upLoadFTP(con.configFile,"config");
			 }
		
		}
		catch(Exception e)
		{
			System.out.println("error in method workOnConfigFile");
			guiFrame.info(e.getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return exist;
	}
	
	public boolean isEmailActive(Document d, String upgradeEmail, String oldEmail)
	{
		
		boolean exist = false;

		NodeList nList = d.getElementsByTagName("activeEmail");
		for (int temp = 0; temp < nList.getLength(); temp++) 
		{
			Node nNode = nList.item(temp);				
			Element eElement = (Element) nNode;
			String email  = eElement.getTextContent();
			if(email.equals(upgradeEmail))
			{
				exist = true;		
			}
			
		}
		
		// if old email is same as upgradeEmail, that means we are going to upgrade on same account
		// which means it's going to be active but I'm on same account so I can upgrade so set to false
		if(upgradeEmail.equals(oldEmail)){
			exist = false;
		}
		
		return exist;
	}
	
	public boolean deleteFromXML(Document d , String t)
	{
		
		Node upgrade = d.getElementsByTagName("upgrade").item(0);
		NodeList list = upgrade.getChildNodes();
		Node node = list.item(1); // should be first pos
		
		System.out.println(node.getNodeName());
		Element eElement = (Element) node;
		String time = eElement.getAttribute("id");
		
		if ("pos".equals(node.getNodeName()) && time.equals(t))  // make sure first child is pos, and attribute id is same as time
		{
			upgrade.removeChild(node);
			
			try{
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(d);
				StreamResult result = new StreamResult(new File(con.configFile));
				transformer.transform(source, result);
			}
			catch(Exception e)
			{
				System.out.println("error in deleteFromXML");
				guiFrame.info("error in deleteFromXML");
				guiFrame.info(e.getMessage());
				e.printStackTrace();
			}
			
		 }
		else
		{
			return false;
		}
		
		return true;
				
	}
	
	/**
	 * takes e and see if exist in AutoUpgradeSwap
	 * it should exist, and we update swapDate with current date.
	 * it will keep track of when the last time an email swapped was.
	 * 
	 * modify method to be general update UpgradeSwap. Specify boolean in parameter to know what to update, date? or loot? or both.. etc...
	 * 
	 * 
	 * @param
	 * 	 e - email 
	 * @param
	 * 	 s - boolean to update swap date
	 * @param
	 *   l - boolean to update loot
	 *   @param
	 *   l2 - actual value to update loot
	 *   @param
	 *   llootValue - boolean to update  gold / elixir percentage
	 *   @param
	 *   gold - actual vaule of gold perecentage
	 *   @param
	 *   elixir - actual value of elixir percentage
	 *   
	 *   
	 * @ return
	 * 	-  returns AutoUpgradeData object, this way coming out of method I can have access to more information  
	 */
	public AutoUpgradeData updateSwapDate(String e, boolean s, boolean l, boolean l2, boolean llootValue, int gold, int elixir )
	{		
		downloadFTP(config.HashSER , "/config/hash.ser", false);			
		hashAutoUpgradeSWAP = deSeralize(new Hashtable(),config.HashSER);
		
		AutoUpgradeData aud = hashAutoUpgradeSWAP.get(e);
		if(s)
		{
			Calendar d = Calendar.getInstance();
			Date currDate = d.getTime();						
			aud.setSwapDate(currDate);
		}
		
		if(l)
		{
			aud.setLootFull(l2);			
		}
		
		if(llootValue)
		{
			aud.setTempIntA(gold);
			aud.setTempIntB(elixir);
		}
			
		
		
		hashAutoUpgradeSWAP.put(e, aud);	
		
		seralize(hashAutoUpgradeSWAP, config.HashSER);
		upLoadFTP(config.HashSER,"config", false);		
		
		return aud;
		
	}
	
	/*
	 * Swapping between 2 accounts
	 * 
	 * - sometimes swap will fail because of Blue stack download app screen, or any screen that randomly pops up.
	*	or can't connect to google play service, so when trying to swithc between account doesn't work
	
	* - determine swap is successful if we reach the load village page.
	*
	 */
	public boolean swap(String em, String oldEmail, boolean send) throws Exception
	{
		//downloadFTP(config.configFile , "/config/config.xml");  do i need this? when I make the new config object I download config already
		
		updateSwapDate(em, true, false, false, false, 0, 0);
		
		guiFrame.info("Swapping from " + oldEmail + " to " + em);
		
		config newCon = new config(em, oldEmail); // new account's setting, most importantly I need the slot position
		
		newCon.setName("swap");
		cont.mouseMove(newCon.getPos().get(0).getX(), newCon.getPos().get(0).getY()); // move to setting button.
		cont.mousePress(InputEvent.BUTTON1_MASK);	
		 Thread.sleep(1000);
		 cont.mouseRelease(InputEvent.BUTTON1_MASK);	
		 Thread.sleep(1000);
		 
		 int temp = 0;
		 boolean ret  =false ;
		 while(temp < 10)
		 {
			Thread.sleep(500);
			cont.mouseMove(newCon.getPos().get(1).getX(), newCon.getPos().get(1).getY()); // move to disconnect
			cont.mousePress(InputEvent.BUTTON1_MASK);	
			 Thread.sleep(1000);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);	
			Thread.sleep(500);
			cont.mousePress(InputEvent.BUTTON1_MASK);	
			 Thread.sleep(1000);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);
			 
			 Thread.sleep(7000);
			 
			 cont.mouseMove(newCon.getPos().get(2).getX(), newCon.getPos().get(2).getY()); // move to account
			cont.mousePress(InputEvent.BUTTON1_MASK);	
			Thread.sleep(1000);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);
			 
			 // move to OK
			 cont.mouseMove(newCon.getPos().get(3).getX(), newCon.getPos().get(3).getY()); 
			cont.mousePress(InputEvent.BUTTON1_MASK);	
			Thread.sleep(1000);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);
			 
			 
			 Thread.sleep(7000); //wait 7 seconds for load village to show up.
			 
			 ret = compareImage("swap"); //check if load village screen.
			
			 if(ret)
			 {
				 break; // break out of while.
			 }
			 
			 temp++; // I want something to break the while loop. Don't want infinite loops
		 }
		 
		 if(ret)
		 {
			 // we are at load village screen.
			 //click load
			 cont.mouseMove(newCon.getPos().get(4).getX(), newCon.getPos().get(4).getY()); 
			 cont.mousePress(InputEvent.BUTTON1_MASK);	
			 Thread.sleep(1000);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);	
			 Thread.sleep(2000);
			 
			 //click textbox
			 cont.mouseMove(newCon.getPos().get(5).getX(), newCon.getPos().get(5).getY()); 
			 cont.mousePress(InputEvent.BUTTON1_MASK);	
			 Thread.sleep(1000);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);	
			 Thread.sleep(1000);
			 
			//Type the word CONFIRM
			 cont.keyPress( KeyEvent.VK_SHIFT );
			 cont.keyPress(KeyEvent.VK_C);
			 cont.keyRelease(KeyEvent.VK_C);
			 Thread.sleep(500);
			 cont.keyPress(KeyEvent.VK_O);
			 cont.keyRelease(KeyEvent.VK_O);
			 Thread.sleep(500);
			 cont.keyPress(KeyEvent.VK_N);
			 cont.keyRelease(KeyEvent.VK_N);
			 Thread.sleep(500);
			 cont.keyPress(KeyEvent.VK_F);
			 cont.keyRelease(KeyEvent.VK_F);
			 Thread.sleep(500);
			 cont.keyPress(KeyEvent.VK_I);
			 cont.keyRelease(KeyEvent.VK_I);
			 Thread.sleep(500);
			 cont.keyPress(KeyEvent.VK_R);
			 cont.keyRelease(KeyEvent.VK_R);
			 Thread.sleep(500);
			 cont.keyPress(KeyEvent.VK_M);
			 cont.keyRelease(KeyEvent.VK_M);
			 cont.keyRelease( KeyEvent.VK_SHIFT );
			 
			 Thread.sleep(1000);
			 
			 //click okay
			 cont.mouseMove(newCon.getPos().get(6).getX(), newCon.getPos().get(6).getY()); 
			 cont.mousePress(InputEvent.BUTTON1_MASK);	
			 Thread.sleep(1000);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);	
			
			 
			 //delete swap email
			 //this HAS to be the old con NOT THE NEW!! old account has the swap email.
			 deleteEmail("swap");
			 
			 
			 // We assume village will load successfully.
			 //In this case we need to set the the new config
			 con = newCon;
			 
			//set email service with new account and pw
			 e.setEmail(con.getEmail());
			 e.setPW(con.getPW());
			 
			//might have to update read.txt here.
			updateReadFile("2");
			
			 Thread.sleep(10000); // wait 10 sec before taking screen shot, I want village to load up.
			 
			//take and send screenshot
			 if(send)					
			 {		
				 takeCurrentScreenshot(true,"currentstatus.jpg");		
			 }
		 }
		 else // was not able to reach load village screen. Something happened can't swap successfully
		 {
			 // we need to update active email back to what it was.
			 con = new config(oldEmail, em );			 
		 }
		 
		 return ret;
		 
	}
	
	public void takeCurrentScreenshot(boolean send , String n) throws Exception
	{
		BufferedImage screencapture = cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));			
		String name = n;
		File outputFile = new File(name);
        ImageIO.write(screencapture, "jpg", outputFile);	
        if(send)
        {
        	sendPictureText(name);
        }
	}
	
	public void GotDisconnected()
	{
		guiFrame.info("Got Disconnected"); 
		disconnected = false;
		if(s.isStarted())
		{
			guiFrame.info("Stopping Stop Watch");
			s.reset();			
		}
	}
	public void setGUIandControl()
	{
		guiFrame = new gui();
		cont = new control(guiFrame, bot);		
		con = new config(guiFrame.account, true, true);
		//hashAutoUpgradeSWAP = con.loadAutoUpgradeSWAP();
		hashAutoUpgradeSWAP = updateSwapDateFromSER(con.loadAutoUpgradeSWAP());
		//updateSwapDate(con.getEmail(), true, false, false); //update last time this email was active
		
		//Let's upload our latest hash, we may have added new account
		seralize(hashAutoUpgradeSWAP, config.HashSER);
		upLoadFTP(config.HashSER,"config", false);		
	}
	
	
	/**
	 * Takes hs, which is from config.loadAutoUpgradeSWAP(), which is from all the account listed in config.xml
	 * It will read hash.ser, which we also get from FTP, this will contain the updated Hashtable data.
	 * we will deseralize it into another hashtable, update hash from FTP into my hs.
	 * @param hs
	 * @return  hs, updated hs with updated value from hashable from hash.ser
	 */
	public Hashtable<String, AutoUpgradeData> updateSwapDateFromSER(Hashtable<String, AutoUpgradeData> hs)
	{
		
		downloadFTP(config.HashSER , "/config/hash.ser", false);			
		Hashtable <String, AutoUpgradeData> b = deSeralize(new Hashtable(),config.HashSER);
		
		//reason why I'm looping and updating, instead of just using hs from hash.ser is because what if i add new account to config.xml?		
		Object[] values = b.values().toArray(); //convert hash to array
		ArrayList<Object> n = new ArrayList<Object>(Arrays.asList(values)); // convert array to arraylist
		
		// convert arraylist of objects into arraylist of AutoUpgradeData
		ArrayList<AutoUpgradeData> n1  = new ArrayList<AutoUpgradeData>();
		for (Object object : n) {				
			n1.add((AutoUpgradeData) object);
		}		
		
		for(int i =0 ; i< n1.size(); i++)
		{
			//values I want to update with
			String key = n1.get(i).getEmail();
			Date d = n1.get(i).getSwapDate();
			
			AutoUpgradeData aud = hs.get(key);
			aud.setSwapDate(d);
			
			aud.setTempIntA(n1.get(i).getTempIntA());
			aud.setTempIntB(n1.get(i).getTempIntB());
			
			hs.put(key, aud);
		}
		
		
		return hs;						
	}
	
	/*
	 * this will click camp, save screen shot,
	 * click to attack log, save screen shot.
	 * 
	 * Then service will upload these imgage files to site
	 */
	public void clickAttackLog() throws Exception
	{
		clickCamp(); // lets click camp first, get image of camp		
		con.setName("status");
		for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);	
			 Thread.sleep(1000);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(2000);
		 }
		BufferedImage screencapture = cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		
		String name = "currentStatusnWP.jpg";
		File outputFile = new File(name);
        ImageIO.write(screencapture, "jpg", outputFile);	
        clickSafeSpot();
	}
	
	
	
	public void sendPictureText() throws Exception
	{
		if(sendPicText)
		{
			clickCamp(); // lets click camp first, get image of camp
			guiFrame.info("Started picture sending");			
			con.setName("status");
			
			for(int i=0; i< con.getPos().size(); i++)
			 {
				 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
				 cont.mousePress(InputEvent.BUTTON1_MASK);	
				 Thread.sleep(1000);
				 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
				 Thread.sleep(2000);
			 }
			
			BufferedImage screencapture = cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			guiFrame.info("Took screen shot");
			
			//save current screen into current.jpg
			String name = "currentStatusnWP.jpg";
			File outputFile = new File(name);
	        ImageIO.write(screencapture, "jpg", outputFile);	
	        guiFrame.info("Finished saving to file");
	        	       	       
	        sendPictureTextStatus(name);
	        
	        sendPicText = false;
	        
	        clickSafeSpot();
	        
	        master = false; //reset master flag
			
		}
	}
	// DE is inaccurate
	public boolean calculateLoot(String g, String e, String d) throws Exception
	{
		String gold = g;
		String elixer = e;
		
		
		// if ends with 1 or 3 , let's take it out		

			gold   = (g.endsWith("1")||g.endsWith("3"))? g.substring(0,g.length()-1):g;			
			if (Integer.valueOf(gold) > new Integer(60000))
			{
				guiFrame.info("value of gold is " + gold + " returning true");
				return true;
			}				
		

			elixer = (e.endsWith("1")||e.endsWith("3"))? e.substring(0,e.length()-1):e;
			if (Integer.valueOf(elixer) > new Integer(60000))
			{
				guiFrame.info("value of elixer is " + elixer + " returning true");
				return true;
			}
		
		
		// if starts with 0, then the 0 was mis read as a 2 digit value		
		gold   = (gold.startsWith("0"))? "1"+gold : gold;
		elixer = (elixer.startsWith("0"))? "1"+elixer : elixer;
		

		if (Integer.valueOf(gold) > new Integer(60000))
		{
			guiFrame.info("value of gold is " + gold + " returning true");
			return true;
		}	
		if (Integer.valueOf(elixer) > new Integer(60000))
		{
			guiFrame.info("value of elixer is " + elixer + " returning true");
			return true;
		}
		
		
		System.out.println(gold);
		System.out.println(elixer);
	
		return false;
	}
	
	/*
	 * use mouse drag to deploy
	 * 
	 * - This works by taking 4 coordinates specify on config for "attack"
	 * first point has slope 12,-9, moves towards 1 o clock
	 * second point has slope of 12,9, moves toward 4 o clock
	 * point three has slope of 12,-9 same as first, position shoudl be middle of screen and way down
	 * point 4 will be close to where point 1 was, but slope will be 12,9
	 * 
	 * I chose these points so there's only 2 sets of slope
	 */
	
	public void deployTroops2()throws Exception
	{			
		
		
		Random rand=  new Random(); 
				
		
		//scroll up , need focuse first
		clickSafeSpot();		 
		 for(int i =0; i<12; i++)
		 {
			 cont.mouseWheel(-100);
			 Thread.sleep(100);
		 }
		
		 config conLocal  = new config();
		 conLocal.setName("attack");
		 // x and y is the safe start. I KNOW for sure that it can deploy.
		int x0 = conLocal.getPos().get(0).getX(); 
		int y0 =conLocal.getPos().get(0).getY(); // keep track of y
			
		
		// x1,y1 - cooridnate of getPos(i)
		int x1;
		int y1;
		
		//x,y - cooridinates that constantly get changed
		int x=x0;
		int y=y0;
		
		
		int xSlope=12;
		int ySlope=9;
		
		int breakOut = 0;
		
		//con.mouseMove(x,y);
		//con.mousePress(InputEvent.BUTTON1_MASK);
		//Thread.sleep(500);			
		
			while ((!endOfBattle())) //not end of battle
			{
				if(breakOut > 1) break; // make sure we break out of loop eventually.
					
				if ((outOfBarbs() && outOfArchs())) // out of troops
				{
					guiFrame.info("breaking");
					break; // break while
					
				}
				else
				{
					for(int temp = 0; temp< conLocal.getPos().size(); temp++) // should loop each pos , should only be 4
					{
						x1 = conLocal.getPos().get(temp).getX();  // current base point.
						y1= conLocal.getPos().get(temp).getY();  // don't want to mod x1/y1, because I use them to recalculate what to plot next
						
						// x/y is  used to the actual move, it gets changed every loop			
						x = x1;
						y = y1;
						
						//need another loop to switch between barbs/archs
						//for...
						for(int b=0 ; b<2; b++) // 2 for barbs and arch
						{
							
							config c2 = new config();
							 // click barbs							 				
							c2.setName((b==0)?"barbs":"archs");
							 cont.mouseMove(c2.getPos().get(2).getX(),c2.getPos().get(2).getY()); //click barbs/archs , index 2 is position of arch/barb in battle screen
							 cont.mousePress(InputEvent.BUTTON1_MASK);	
							 cont.mouseRelease(InputEvent.BUTTON1_MASK);
							 Thread.sleep(500);
							 
							 
							//Have to mouse move to safe spot and mouse press
							cont.mouseMove(x0,y0);
							cont.mousePress(InputEvent.BUTTON1_MASK);
							Thread.sleep(500);
							
							//them mouse move to x1,y1 
							cont.mouseMove(x1,y1);
							
							
							for(int a= 0; a<  ((con.work)? ((b%2==0)?con.getDeployBarb():con.getDeployArcher()): ((b%2==0)?28:22)) ; a++) //25:20 for home cause it lags.  
							{				
								int local = rand.nextInt(30);
								int local2 = rand.nextInt(10); // gives it a little realistic feel 
								
								//move any where on that line
								//CHANGE IT TO MOUSE MOVE ONLY LATER
								cont.mouseMove(x,y);					
								//con.mousePress(InputEvent.BUTTON1_MASK);
								//con.mouseRelease(InputEvent.BUTTON1_MASK);
									
								//calculate next click
								// x1,y1 is base coordinate, so just calculate number and add it to that
								// slope changes for temp(0) and temp(2), need to multiply -1
								x = x1 + ( xSlope *local) + local2;
								y = y1 + ( ((temp%2==0)?(-1*ySlope):ySlope) * local) + local2;
								
								//guiFrame.info(xSlope+","+ String.valueOf(((temp%2==0)?(-1*ySlope):ySlope)));
								
								// sleep randomly for realistic feel
								Thread.sleep(150+(local*4));			
							//}
							}	
							cont.mouseRelease(InputEvent.BUTTON1_MASK); // release mouse here
							//switch between barbs/archs			
						}	
						
						
						if(activateHero && temp==3) // first temp= 1 won't activate because activateHero will be false;
						{
							activateHero();
							activateHero = false;
						}

							deployHero("king") ;
							deployHero("queen") ;													
						
					}
				}
				guiFrame.info("break out value " + breakOut);
				breakOut++;
				
		}
			
			guiFrame.info("broke out of while");
			
		
	}		

	
	public void sendPictureText(String file) throws Exception
	{
		guiFrame.info("Started sendPictureText");
		 String host = "smtp.gmail.com";
		    String from = con.getEmail(); 
		    String pass = con.getPW();
		    Properties props = System.getProperties();
		    
		  		    
		    props.put("mail.smtp.starttls.enable", "true"); // added this line
		    props.put("mail.smtp.host", host);
		    props.put("mail.smtp.user", from);
		    props.put("mail.smtp.password", pass);
		    props.put("mail.smtp.port", "587");
		    props.put("mail.smtp.auth", "true");	
		    
		    
		    //String[] to = {"shamikemosabi@gmail.com"}; // added this line
		    String[] to = {"6462841208@tmomail.net"}; // added this line
		    
 		    //Session session = Session.getDefaultInstance(props);
			   Session session = Session.getDefaultInstance(props, new GMailAuthenticator(from, pass));
		    MimeMessage message = new MimeMessage(session);
		    
		    // creates message part
	      //  MimeBodyPart messageBodyPart = new MimeBodyPart();
	     //   messageBodyPart.setContent("hi", "text/html");
	 
	        // creates multi-part
	        Multipart multipart = new MimeMultipart();
	      //  multipart.addBodyPart(messageBodyPart);
	        
	        MimeBodyPart attachPart = new MimeBodyPart();
	        attachPart.attachFile(file);
	        
	       // MimeBodyPart attachPart2 = new MimeBodyPart();
	       // attachPart2.attachFile("currentcamp.jpg");
	        
		    
	        multipart.addBodyPart(attachPart);
	       // multipart.addBodyPart(attachPart2);
	        
		    message.setFrom(new InternetAddress(con.getEmail())); 
		    
		    InternetAddress[] toAddress = new InternetAddress[con.getDestEmailList().size()];
		    for( int i=0; i < con.getDestEmailList().size(); i++ ) { // changed from a while loop
		        toAddress[i] = new InternetAddress(con.getDestEmailList().get(i));
		    }
		    
		    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
		        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		    }
		    
		   // String temp = "Screen shot";

		  //  message.setSubject(temp);
		 //   String body=temp;
		   //message.setContent(body, "text/html");
		    message.setContent(multipart);
		    Transport transport = session.getTransport("smtp");
		    guiFrame.info("Connect");
		    transport.connect(host, from, pass);
		    guiFrame.info("Send message");
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		    guiFrame.info("Message Sent");
	}
	
	
	public void sendPictureTextStatus(String file) throws Exception
	{
		guiFrame.info("Started sendPictureText");
		 String host = "smtp.gmail.com";
		    String from = con.getEmail(); 
		    String pass = con.getPW();
		    Properties props = System.getProperties();
		    
		  		    
		    props.put("mail.smtp.starttls.enable", "true"); // added this line
		    props.put("mail.smtp.host", host);
		    props.put("mail.smtp.user", from);
		    props.put("mail.smtp.password", pass);
		    props.put("mail.smtp.port", "587");
		    props.put("mail.smtp.auth", "true");	
		    
		    
		    //String[] to = {"shamikemosabi@gmail.com"}; // added this line
		    String[] to = {"6462841208@tmomail.net"}; // added this line
		    
 		    //Session session = Session.getDefaultInstance(props);
			   Session session = Session.getDefaultInstance(props, new GMailAuthenticator(from, pass));
		    MimeMessage message = new MimeMessage(session);
		    
		    // creates message part
	      //  MimeBodyPart messageBodyPart = new MimeBodyPart();
	     //   messageBodyPart.setContent("hi", "text/html");
	 
	        // creates multi-part
	        Multipart multipart = new MimeMultipart();
	      //  multipart.addBodyPart(messageBodyPart);
	        
	        MimeBodyPart attachPart = new MimeBodyPart();
	        attachPart.attachFile(file);
	        
	        MimeBodyPart attachPart2 = new MimeBodyPart();
	        attachPart2.attachFile("currentcamp.jpg");
	        
		    
	        multipart.addBodyPart(attachPart);
	        multipart.addBodyPart(attachPart2);
	        
		    message.setFrom(new InternetAddress(con.getEmail()));
		    
		    if(master)
		    {
			    InternetAddress[] toAddress = new InternetAddress[to.length];
			    
			    for( int i=0; i < to.length; i++ ) { // changed from a while loop
			        toAddress[i] = new InternetAddress(to[i]);
			    }
			    
			    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
			        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			    }
		    }
		    else // send to everyone
		    {
			    InternetAddress[] toAddress = new InternetAddress[con.getDestEmailList().size()];
			    
			    for( int i=0; i < con.getDestEmailList().size(); i++ ) { // changed from a while loop
			        toAddress[i] = new InternetAddress(con.getDestEmailList().get(i));
			    }
			    
			    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
			        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			    }
		    }
		    

		   // String temp = "Screen shot";

		  //  message.setSubject(temp);
		 //   String body=temp;
		   //message.setContent(body, "text/html");
		    message.setContent(multipart);
		    Transport transport = session.getTransport("smtp");
		    guiFrame.info("Connect");
		    transport.connect(host, from, pass);
		    guiFrame.info("Send message");
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		    guiFrame.info("Message Sent");
	}
	 public void sendText(String sub, String bod) throws Exception
	 {
		 
		 String host = "smtp.gmail.com";
		    String from = con.getEmail();
		    String pass = con.getPW();
		    Properties props = System.getProperties();
		  		    
		    props.put("mail.smtp.starttls.enable", "true"); // added this line
		    props.put("mail.smtp.host", host);
		    props.put("mail.smtp.user", from);
		    props.put("mail.smtp.password", pass);
		    props.put("mail.smtp.port", "587");
		    props.put("mail.smtp.auth", "true");	
		    
		    
		    //String[] to = {"shamikemosabi@gmail.com"}; // added this line
		    String[] to = {"6462841208@tmomail.net"}; // added this line
		    
		   
		    Session session = Session.getDefaultInstance(props, new GMailAuthenticator(from, pass));
		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(new InternetAddress(con.getEmail()));
		    
		    
		    if(master)
		    {
			    InternetAddress[] toAddress = new InternetAddress[to.length];
			    
			    for( int i=0; i < to.length; i++ ) { // changed from a while loop
			        toAddress[i] = new InternetAddress(to[i]);
			    }
			    
			    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
			        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			    }
		    }
		    else // send to everyone
		    {
			    InternetAddress[] toAddress = new InternetAddress[con.getDestEmailList().size()];
			    
			    for( int i=0; i < con.getDestEmailList().size(); i++ ) { // changed from a while loop
			        toAddress[i] = new InternetAddress(con.getDestEmailList().get(i));
			    }
			    
			    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
			        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			    }
		    }
		    
		    message.setSubject(sub);
		   //message.setContent(body, "text/html");
		    message.setContent(bod, "text/plain");
		    Transport transport = session.getTransport("smtp");
		    transport.connect(host, from, pass);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
	     
	 }
	 
	
		
	public boolean isBaseInactive() throws Exception
	{			
		con.setName("inactive");
		
		boolean ret =compareImage("inactive");
		guiFrame.info("base is inactive: " + ret);
		return ret;

	}
	
	public void activateHero() throws Exception
	{
		if(activateHero)
		{
			guiFrame.info("activating Hero");			
			con.setName("king");
			
			 cont.mouseMove(con.getPos().get(0).getX(), con.getPos().get(0).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);	
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		
	}
	public void deployHero(String h) throws Exception
	{		
		con.setName(h);
		Random rand = new Random();
		boolean ret  = compareImage(h);			
		
		int i = rand.nextInt(2);
		
		if(ret) // we have king must deploy OR activateHero is true, so we activate ability
		{
			 cont.mouseMove(con.getPos().get(0).getX(), con.getPos().get(0).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);	
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);
			 Thread.sleep(500);
			 
			 config c2 = new config();
			 c2.setName("attack");
			 cont.mouseMove(c2.getPos().get(i).getX(), c2.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);	
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);
			 
			 guiFrame.info("deployed hero");
			 
			 activateHero = true;			
		}	
		
	}
	
	public void clickSafeSpot() throws Exception
	{		
		con.setName("safe");
		
		for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);			 
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(1000);
		 }
		guiFrame.info("clicked safe spot");		
	}
	
	public boolean outOfArchs() throws Exception
	{				
		con.setName("archs");
		
		boolean ret =compareImage("archs");
		guiFrame.info("out of archers: " + ret);
		return ret;
	}
	
	public boolean outOfBarbs() throws Exception
	{		
		con.setName("barbs");
		
		boolean ret =compareImage("barbs");
		guiFrame.info("out of barbs: " + ret);
		return ret; 
	}
	
	public boolean gotRaided() throws Exception
	{		
		con.setName("raided");
		
		boolean ret =compareImage("raided"); 		
		return ret;
		
	}
	public void returnHomeFromRaided() throws Exception
	{				
		con.setName("raided");
		
		for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);			 
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(1000);
		 }
		guiFrame.info("Got raided returning to main screen");
		
		Thread.sleep(4000); //takes a few seconds to return home.
		
	}
	
	
	public boolean endOfBattle() throws Exception
	{		
		con.setName("end");
		
		boolean ret =compareImage("end"); 
		guiFrame.info("End of battle " + ret);
		return ret;
		
	}
	public void returnHomeFromBattle() throws Exception
	{		
		con.setName("end");
		
		for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);			 
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(1000);
		 }
		guiFrame.info("returning to main");
		
		Thread.sleep(4000); //takes a few seconds to return home.
		
	}

	/*
	 * read from a file, if 2 then we bot, 0 then we don't, 1 is only for AHK click (stay active BUT don't try to bot)
	 * - 4 is now status, take screen shot of inbox attack log
	 */
	public boolean isServiceStarted(String s) throws Exception
	{
		//guiFrame.info("Checking service started or not");
		File f = new File(s); //file to read
		BufferedReader br = new BufferedReader(new FileReader(f));
		int ret = Integer.valueOf(br.readLine());
		br.close();
		
		if(ret == 2 || ret == 4) 
		{
			serviceStopped = false;
			sendStopText = true; // flip to true
			
			
			/*
			 * if 4, we have to set flag to get screen shot later once we are in inMain, But here we also have to delete the status email
			 * make sure to change email service to not send email when changing from 2 -> 4, don't want redunant email saying bot started.
			 */
			
			guiFrame.info("Read file value is " + ret);
			if(ret == 4)
			{
				deleteEmail("status");
				sendPicText = true;
				
			}
			return true;
		}
		else // 1 or 0 we stop service
		{
			serviceStopped = true;
			//guiFrame.info("stopping service");
			return false;
		}
	}
	
	
	public void updateReadFile(String s) throws Exception
	{
		
		 File f = new File(config.readFile);
		  PrintWriter fw = new PrintWriter(f);
		  		  
		  fw.print(s);
		  fw.close();
		
	}
	
	/*
	 * 
	 * return int instead of boolean
	 * 
	 */
	
	   /*
	    *  1 - start
	    *  2 - bot
	    *  4 - status
	    *  5 - swap
	    *  6 - config
	    */
	
	public int isServiceStarted2(String s) throws Exception
	{
		//guiFrame.info("Checking service started or not");
		File f = new File(s); //file to read
		BufferedReader br = new BufferedReader(new FileReader(f));
		int ret = 0;
		String str = br.readLine();
		try{
			ret = Integer.valueOf(str);
		}
		catch(Exception e) // throw exception if code have characters
		{
			if(str.startsWith("5"))
			{
				email  = str.substring(1); // get rid of first char which is "5"
				email  = email.trim(); 
				ret = 5;
			}
			else if(str.startsWith("4")) // master status
			{
				deleteEmail("status");
				sendPicText = true;
				master = true;
				ret = 2;
			}

		}
		br.close();
		
		if(ret == 2 || ret == 4) 
		{
			serviceStopped = false;
			sendStopText = true; // flip to true
			
			
			/*
			 * if 4, we have to set flag to get screen shot later once we are in inMain, But here we also have to delete the status email
			 * make sure to change email service to not send email when changing from 2 -> 4, don't want redunant email saying bot started.
			 */
			
			guiFrame.info("Read file value is " + ret);
			if(ret == 4)
			{
				deleteEmail("status");
				sendPicText = true;			
				
			}
			return 2; // return 2, 4 is just so we know we should take screen shot later, but still return 2 to bot
		}
		else if(ret==5)
		{
			return 5;
		}
		else if(ret==1)// 1 or 0 we stop service
		{
			return 1;
		}
		else if(ret==0)
		{
			serviceStopped = true;
			//guiFrame.info("stopping service");
			return 0;
		}
		else if(ret==6) // download new config.xml file and load new config object
		{
			downloadAndLoadConfig(true);
			
			// also download other xml files
			downloadFTP(config.labFile , "/config/lab.xml", true);
			downloadFTP(config.upgradeFile , "/config/upgrade.xml", true);	
			
			deleteEmail("config");
			//updateReadFile("1"); // why not update to 2??
			updateReadFile("2");
			return 1; // let the next email service update actual read value
		}
		else // in case for some reason, value read is not any of the numbers, lets just default to bot.
		{
			return 2;
		}
	}
	
	/**
	 * Take current screen shot, take current status page (this will be saved every loop)
	 * FTP both image to website../acct/con.getEmail()/ directory
	 * 
	 * User will access the page via html page in the directory which will display the 2 jpg
	 * @throws Exception
	 */
	public void updateWebPage() throws Exception 
	{
		takeCurrentScreenshot(false, "currentScreenWP.jpg");		
		upLoadFTP(new String[]{"currentScreenWP.jpg","currentStatusnWP.jpg", "currentcamp.jpg"},"config/acct/"+con.getEmail(), FTP.BINARY_FILE_TYPE);
		
	}
	
	
	/**
	 * separate thread that checks for known stuck pages
	 * tries to click out of them
	 * 
	 * @throws Exception
	 */
	public void checkForStuckPages() throws Exception
	{
		boolean temp ;
		
		String [] array = {"bluestack", "try", "search"};
		
		for(int i = 0 ; i< array.length; i++)
		{
			guiFrame.info("comparing " + array[i]);
			config newConfig = new config();
			newConfig.setName(array[i]);			
			temp= compareImage(array[i], false, newConfig);			

						
			if(temp)
			{
				guiFrame.info("Match on " + array[i]);
				
				//loop 5 times
				for(int j = 0 ; j < 5 ; j++)
				{					
					temp= compareImage(array[i], false, newConfig);
					
					guiFrame.info("loop " +j+ " match is " + temp );
					//sleep 5 seconds
					Thread.sleep(5000);
					
					//if temp is false, doesn't match we break
					if(!temp)
					{
						break;
					}
				}
				
				
				// if temp is still true then it means we are stuck
				// we need to click stuff to get out

				for(int ii=0; ii < newConfig.getPos().size(); ii++)
				 {
					 cont.mouseMove(newConfig.getPos().get(ii).getX(), newConfig.getPos().get(ii).getY());
					 cont.mousePress(InputEvent.BUTTON1_MASK);			 
					 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
					 Thread.sleep(1000);
				 }
				
			}
			
		}
		
		
	}
	
	
	
	public void updateStat() throws Exception 
	{
		if(guiFrame.getAutoUpgrade())
		{							
			// hashAutoUpgradeSWAP, really only have 3 values, email, swapDate, and lootFull
			Object[] values = hashAutoUpgradeSWAP.values().toArray(); //convert hash to array
			ArrayList<Object> n = new ArrayList<Object>(Arrays.asList(values)); // convert array to arraylist
			
			// convert arraylist of objects into arraylist of AutoUpgradeData
			ArrayList<AutoUpgradeData> n1  = new ArrayList<AutoUpgradeData>();
			for (Object object : n) {				
				n1.add((AutoUpgradeData) object);
			}			
			
			File f = new File(config.statFile);
			PrintWriter fw = new PrintWriter(f);					
			
		    Collections.sort(n1, new DateComparator()); //sort by swap date
			
			for(int i=0; i < n1.size(); i ++)
			{
				AutoUpgradeData aud =  n1.get(i);
				fw.println("Account : " + aud.getEmail());
				fw.println("Last Swap Date : " + aud.getSwapDate());
				fw.println("Gold % : " + aud.getTempIntA());
				fw.println("Elixir % : " + aud.getTempIntB());				
				fw.println("Loot Full : " + aud.getLootFull());
				fw.println();
			}
			
			//output config's variables
			
			fw.println("Current Config Value:");
			fw.println("Email : " + con.getEmail());
			fw.println("deployArcher : " + con.getDeployArcher());
			fw.println("deployBarb : " + con.getDeployBarb());
			fw.println("Loot Threshold : " + con.getLootThreshold());
			fw.println("Smart Loot : " + con.getSmartLoot());
			fw.println();
			
			//output system variables:
			fw.println("System Variable");
			fw.println("Manual smart loot : " + guiFrame.getSmartLoot());
			fw.println();
			
			
			fw.close();					
					
			upLoadFTP(config.statFile,"config", false);
			
		}

	}
	
	public void downloadAndLoadConfig(boolean forceFTP)
	{		
		con = new config(con.getEmail(), forceFTP, con.isAutoSwap());   // new config object with same account as before, just NEW config.xml	
	}
	
	
	
	/*overload method, takes string array of multiple file names.
	 * 
	 * 
	 * always try to FTP. I want to see web page stuff even for clients.
	 * 
	 */
	public void upLoadFTP(String[] FileNames, String dir, int file_type)
	{
		
		if(!config.test) {
	
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
						
						ftp.setFileType(file_type);
						
						for(int i= 0; i < FileNames.length; i ++)
						{
							File f = new File(FileNames[i]);
							final InputStream is = new FileInputStream(f.getPath());
							success = ftp.storeFile(f.getName(), is);
							is.close();
						}
						
						ftp.disconnect();	
						
						
					}
					catch(Exception e)
					{
						System.out.println("Error UploadFTP CLICK method");
			        	System.out.println(e.getMessage());
			        	
						guiFrame.info("Error UploadFTP CLICK method");
						guiFrame.info(e.getMessage());
			        	e.printStackTrace();
			        	success = false;					
					}
		        }while(!success && count < 10);
	     
		}
	}
	
	
	/*
	 * uploads FileName to FTP dir
	 */
	public void upLoadFTP(String FileName, String dir, boolean forceFTP)
	{
		
		if(!config.test && (forceFTP || con.isAutoSwap())){				
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
						ftp.setFileType(FTP.BINARY_FILE_TYPE);
						
						final InputStream is = new FileInputStream(f.getPath());
						success = ftp.storeFile(f.getName(), is);
					
						is.close();
						
						ftp.disconnect();	
						
						
					}
					catch(Exception e)
					{
						System.out.println("Error UploadFTP CLICK method");
			        	System.out.println(e.getMessage());
			        	
						guiFrame.info("Error UploadFTP CLICK method");
						guiFrame.info(e.getMessage());
			        	e.printStackTrace();
			        	success = false;					
					}
		        }while(!success && count < 10);
	     
		}
	}
	
	public void downloadFTP(String localFile, String remoteFile, boolean forceFTP)
	{
		if(!config.test && (forceFTP || con.isAutoSwap())){			
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
		        	
					guiFrame.info("Error downloadFTP");
					guiFrame.info(e.getMessage());	   
					
		        	e.printStackTrace();
		        	success = false;
		        }
	        }while(!success && count < 10);
  
         
		}
	}
	
	
	public void deleteEmail(String str) throws Exception
	{
		guiFrame.info("Started delete " + str + "email");
		 String host = "smtp.gmail.com";
		    String from = con.getEmail();
		    String pass = con.getPW();
		    Properties props = System.getProperties();
		   
		  
			  props.put("mail.smtp.starttls.enable", "true"); // added this line
			    props.put("mail.smtp.host", host);
			    props.put("mail.smtp.user", from);
			    props.put("mail.smtp.password", pass);
			    props.put("mail.smtp.port", "587");
			    props.put("mail.smtp.auth", "true");	
		   Session session = Session.getDefaultInstance(props, new GMailAuthenticator(from, pass));
		 
		   Store store = session.getStore("imaps");
 		   store.connect("smtp.gmail.com", from,pass);
		 
		   Folder inbox = store.getFolder("inbox");
		   inbox.open(Folder.READ_WRITE);

		   Message[] messages = inbox.getMessages();
		    
		   for (int i = 0; i < messages.length; i++) {

		      String body="";
		       
		      body = getText((Part)messages[i]).toLowerCase(); //(String)messages[i].getContent();//;		    
		      if(body.contains(str)) messages[i].setFlag(Flags.Flag.DELETED, true);
		   }
		   
		   inbox.close(true);
		   
		   guiFrame.info("Completed delete " + str +" email");
	  
		   
	}
	
	private String getText(Part p) throws
	    MessagingException, IOException {
		   if (p.isMimeType("text/*")) {
		   String s = (String)p.getContent();
		   //textIsHtml = p.isMimeType("text/html");
		   return s;
		   }
	    
		   if (p.isMimeType("multipart/alternative")) {
			   // prefer html text over plain text
			   Multipart mp = (Multipart)p.getContent();
			   String text = null;
			   for (int i = 0; i < mp.getCount(); i++) {
			    Part bp = mp.getBodyPart(i);
			    if (bp.isMimeType("text/plain")) {
			        if (text == null)
			            text = getText(bp);
			        continue;
			    } else if (bp.isMimeType("text/html")) {
			        String s = getText(bp);
			        if (s != null)
			            return s;
			    } else {
			        return getText(bp);
			    }
			   }
			   return text;
		   } else if (p.isMimeType("multipart/*")) {
			   	Multipart mp = (Multipart)p.getContent();
				   for (int i = 0; i < mp.getCount(); i++) {
					    String s = getText(mp.getBodyPart(i));
					    if (s != null)
					        return s;
				   }
		   }
		
		return null;
}
	
	public void clickNext() throws Exception
	{		
		con.setName("battle");
		
		for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);			 
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(1000);
		 }
		guiFrame.info("NEXT!");
	}
	
	
	public boolean isSmartLoot()
	{
		return con.getSmartLoot() && (!con.isBarrackBost()); // if boosting barracks don't smart loot
	}
	/*
	 * Really need to improve OCR
	 */
	public boolean shouldIAttack() throws Exception
	{		
		
		lootThreshold = Integer.valueOf(con.getLootThreshold());
		//IF smart loot is on.
		if(isSmartLoot())
		{
			setLoot();	
		}
		
		if( isBaseInactive()&& saveLootParent() )
		{
			guiFrame.info("Base is inactive, loot is good lets attack!");
			return true;
		}
		else //lets only attack inactive base (for now?)
		{
			return false;
		}		
	}
	
	
	/* 
	 * set lootThreshold base on timer.
	 */
	public void setLoot()	
	{
		guiFrame.info("Setting Loot");
		if(getTime()< 120000) // 2 mins
		{
			lootThreshold = 350000;
		}
		else if(getTime() < 240000)//4 min
		{
			lootThreshold = 300000;
		}
		else if(getTime() < 300000)//5 min
		{
			lootThreshold = 250000;
		}
		else if(getTime() < 420000)//7 min
		{
			lootThreshold = 200000;
		}
		else if(getTime() < 540000)//9 min
		{
			lootThreshold = 150000;
		}	
		guiFrame.info("Time: "+getTime()/60000+"mins loot threshold: "+lootThreshold);			
	}
	public long getTime()
	{
		s.suspend();
		long i = s.getTime();
		s.resume();
		return i;
	}
	/*
	 * read gold and elixers,
	 * save the image file, so I can see later if it read correctly or not
	 * 
	 * file name = gold+_+elixer.jpg, in loot folder
	 */
	public boolean saveLootParent()throws Exception
	{
		guiFrame.info("Calculate loot value");
		
		boolean ret = false;
		BufferedImage screencapture = cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		
		//save current screen into current.jpg
		String name = "current.jpg";
		File outputFile = new File(name);
		ImageIO.write(screencapture, "jpg", outputFile);
      			
		 String gold=""; 
		 String elixer="";  
		 String de = "";
		 
		/*
		 * REVAMP CALCULATE LOOT
		 * 
		 * 
		ImageScanner scan = new ImageScanner();
		config con = new config();
		con.setName("gold");
		Convert c = new Convert();
      
        c.invertImage(name, "crop"+name, con.getX(), con.getY(), con.getW(), con.getH());
        // Now I will have the current screen image, inverted and cropped
      
             
        gold = scan.print("crop"+name).trim();
        guiFrame.info(gold);
        
        gold= gold.replaceAll(" ", "");
              
        con.setName("elixer");
        c.invertImage(name, "crop"+name, con.getX(), con.getY(), con.getW(), con.getH());        
        elixer = scan.print("crop"+name).trim();
        guiFrame.info(elixer);
        elixer= elixer.replaceAll(" ", "");
        
        
        	String de = getDarkElixerValue(c,scan,name);
        	System.out.println("DE:" +de);
        */
             
        /*
         * I have value of gold and elixers here
         * Need to add further logic to make values more accurate         
         * - seems like the last digits that should be sremoved is only 1 or 3
         * - Both value should be around the same length
         * - use DE count, if high DE count (3 digit higher) gold/exlier should be high
         *   for cases where I read really low gold/elixer but it's actually 6 digits.
         *   DE is not that accurate eiither....
         * 
         */
      
		//ret =  calculateLoot(gold, elixer, de);
        
        
        
        //Integer comb = Integer.valueOf(gold) + Integer.valueOf(elixer);
		 
		ImageParser ip = new ImageParser(System.getProperty("user.dir")+"\\",name, "");
		gold  =  ip.gold;
		elixer = ip.elixer;
		
		Integer comb = Integer.valueOf(gold) + Integer.valueOf(elixer);
		
		ret = comb > lootThreshold;
		
		if(guiFrame.getDebugMode())
		{
			saveLoot(gold, elixer, de, ret);
		}
        
        guiFrame.info("COMBINED LOOT : "+String.valueOf(comb )+" "+ret);
        
        return ret;
        
        //Thread.sleep(2000);      
	}
	
	
	public String getDarkElixerValue(Convert c, ImageScanner scan, String name) throws Exception
	{			
		String de= "0";
		con.setName("de");
		
		boolean ret = compareImage("de");
		guiFrame.info("DE exist" + ret);
		
		// if we have DE need to calculate how much
		if(ret)
		{
			con.setName("de_amt");			
		    c.invertImage(name, "crop"+name, con.getX(), con.getY(), con.getW(), con.getH());        
		    de = scan.print("crop"+name).trim();
		    guiFrame.info(de);
		    de= de.replaceAll(" ", "");			
		}
		return de;
	}
	

	public void saveLootParent(String s, String n)throws Exception
	{
		
	
		//save current screen into current.jpg
		//String name = n;
		File outputFile = new File(s+n);
		boolean ret;
		String gold=""; 
		String elixer="";    
		String de="";
		/*
		ImageScanner scan = new ImageScanner();
		config con = new config();
		con.setName("gold");
		Convert c = new Convert();
      
        c.invertImage(s+n, s+"crop_gold"+n, con.getX(), con.getY(), con.getW(), con.getH());
        // Now I will have the current screen image, inverted and cropped
      
              
        gold = scan.print(s+"crop_gold"+n).trim();
        System.out.println(gold);
        
        gold= gold.replaceAll(" ", "");
            
        con.setName("elixer");
        c.invertImage(s+n, s+"crop_elixer"+n, con.getX(), con.getY(), con.getW(), con.getH());        
        elixer = scan.print(s+"crop_elixer"+n).trim();
        System.out.println(elixer);
        elixer= elixer.replaceAll(" ", "");

        
		con.setName("de_amt");			
	    c.invertImage(s+n, s+"crop_de"+n, con.getX(), con.getY(), con.getW(), con.getH());        
	    de = scan.print(s+"crop_de"+n).trim();
	    System.out.println(de);
	    de= de.replaceAll(" ", "");		
	                    
	    */
	  //  calculateLoot(gold, elixer, de);
	    
	    ImageParser ip = new ImageParser(s,n,"");
		gold  = ip.gold;
		elixer = ip.elixer;
		
		Integer comb = Integer.valueOf(gold) + Integer.valueOf(elixer);
		
		ret = comb > 180000;
        saveLoot(gold, elixer, de, ret);
        
        System.out.println(String.valueOf(comb )+ ret);
	    
	    
	    
	   // Integer comb = Integer.valueOf(gold) + Integer.valueOf(elixer);	
        
      //  saveLoot(gold, elixer);
        //Thread.sleep(2000);      
	}
	
	public void saveLoot(String g, String e, String d, boolean r) throws Exception
	{
		File f = new File("current.jpg");
		File f1 = new File(System.getProperty("user.dir")+"\\loot\\"+g+"_"+e+"_"+d+ ((r)?"ATTACK":"") +".jpg");		
		f.renameTo(f1);
	}
	
	/*
	 *  Passing parameter init, if set to true this means that it's the first check
	 *  of in battle, don't need to sleep ( this is because we don't need to wait for village to load (cloud screen))
	 *  only sleep when we are in while loop of inBattle.
	 */
	public boolean inBattle(boolean init) throws Exception
	{		
		guiFrame.info("Checking In battle");
		con.setName("battle");
		if(!init)
		{
			Thread.sleep((con.work)?3000:8000); //wait 3 seconds for finding, longer for home....
		}
		guiFrame.info("AFter In battle");
		return compareImage("battle"); 
	}
	public void clickFind() throws Exception
	{		
		con.setName("find");
		
		for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);			 
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(1000);
		 }
		
		guiFrame.info("clicked Find");
	}
	public void trainBarbs() throws Exception
	{
		guiFrame.info("training barbs");		
		con.setName("barbs");
				
		cont.mouseMove(con.getPos().get(0).getX(), con.getPos().get(0).getY());
		cont.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(3000); // 3 seconds
		cont.mouseRelease(InputEvent.BUTTON1_MASK);		
		Thread.sleep(1000);
		
		
		cont.mouseMove(con.getPos().get(1).getX(), con.getPos().get(1).getY()); // click next
		cont.mousePress(InputEvent.BUTTON1_MASK);
		cont.mouseRelease(InputEvent.BUTTON1_MASK);
		Thread.sleep(1000);
	}
	
	public void trainArchs() throws Exception
	{
		guiFrame.info("training archers");		
		con.setName("archs");
				
		cont.mouseMove(con.getPos().get(0).getX(), con.getPos().get(0).getY());
		cont.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(3000); // 3 seconds
		cont.mouseRelease(InputEvent.BUTTON1_MASK);		
		Thread.sleep(1000);
				
		cont.mouseMove(con.getPos().get(1).getX(), con.getPos().get(1).getY()); // click next
		cont.mousePress(InputEvent.BUTTON1_MASK);
		cont.mouseRelease(InputEvent.BUTTON1_MASK);
		Thread.sleep(1000);
	}
	
	public boolean clickBarracks() throws Exception
	{		 		
		 con.setName("barracks");
		 
		 for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(1000);
		 }		 
		 
		return (compareImage("barracks")||compareImage("camp_full")); //just need to amke sure 
	}
	
	/**
	 *  Difference between this and clickBarracks are the following.
	 *  
	 *  this one will click Barracks, BUT it will check to see if camp is full
	 */
	public boolean clickBarracksForCamp() throws Exception
	{		 		
		 con.setName("barracks");
		 
		 for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);			 
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(1000);
		 }		 
		 
		return compareImage("camp_full");		
	}
	public void clickAttack() throws Exception
	{		
		guiFrame.info("click attack");		 
		 con.setName("main");
		 
		 //click twice, once to get out of camp screen
		 cont.mouseMove(con.getPos().get(0).getX(), con.getPos().get(0).getY());  
		 cont.mousePress(InputEvent.BUTTON1_MASK);
		 cont.mouseRelease(InputEvent.BUTTON1_MASK);
		 Thread.sleep(1000);
		 guiFrame.info("click attack end");
	}
	
	public void setUpScreen() throws Exception
	{		
		int breakout = 0; 
		while(!isScreenSetup())
		{
			if(breakout > 10) break;	// make sure we break out eventually, don't want any infinite loops 	
			 guiFrame.info("Setting up screen");			 
			 
			 //lets click to get focus
			// con.mouseMove(555, 217);  
			 //con.mousePress(InputEvent.BUTTON1_MASK);
			 //con.mouseRelease(InputEvent.BUTTON1_MASK);
			 clickSafeSpot();
			 
			 //zoom out
			 for(int j=0; j<8; j++)
			 {
				 bot.keyPress(KeyEvent.VK_DOWN);
				 bot.keyRelease(KeyEvent.VK_DOWN);
				 Thread.sleep(500);
			 }
			 
			 //move screen down			 
			 cont.mouseMove(1033, 337);
			 cont.mousePress(InputEvent.BUTTON1_MASK);
			 
			 for(int i =0; i<8; i++)
			 {
				 cont.mouseWheel(-100);
				 Thread.sleep(100);
			 }	
			 
			breakout++;
		}
		clickSafeSpot(); // to prevent any buildings from being selected
		Thread.sleep(1000);
		guiFrame.info("Coming out of set up screen");
		 
	}
	
	
	/*
	 * Get current screen shot
	 * 
	 * crop it , size , file, etc.. depend on s
	 * 
	 * then compare with rule image, also cropped
	 * 
	 * return if image is same
	 */
	public boolean compareImage(String s) throws Exception
	{
		boolean ret = false;		 
		BufferedImage screencapture = cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		
		//save current screen into current.jpg
		String name = "current.jpg";
		File outputFile = new File(name);
       ImageIO.write(screencapture, "jpg", outputFile);
              
       con.setName(s);
       Convert c = new Convert();
       
       c.invertImage(name, "crop"+name, con.getX(), con.getY(), con.getW(), con.getH());
       // Now I will have the current screen image, inverted and cropped
       
       
       //do the same for our //rule/main.jpg
       c.invertImage(con.getFile(), "crop"+con.getName()+".jpg", con.getX(), con.getY(), con.getW(), con.getH());
       
       ImageCompare ic = new ImageCompare("crop"+name, "crop"+con.getName()+".jpg");
       ret =  ic.setupAndCompare(ic);
      // guiFrame.info(ret);
       
		return ret;
	}
	
	public boolean compareImage(String s , boolean save) throws Exception
	{
		boolean ret = false;		 
		BufferedImage screencapture = cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		
		//save current screen into current.jpg
		String name = "current"+s+".jpg";
		File outputFile = new File(name);
	    ImageIO.write(screencapture, "jpg", outputFile);
	
       
       con.setName(s);
       Convert c = new Convert();
       
       c.invertImage(name, "crop"+name, con.getX(), con.getY(), con.getW(), con.getH());
       // Now I will have the current screen image, inverted and cropped
       
       
       //do the same for our //rule/main.jpg
       c.invertImage(con.getFile(), "crop"+con.getName()+".jpg", con.getX(), con.getY(), con.getW(), con.getH());
       
       ImageCompare ic = new ImageCompare("crop"+name, "crop"+con.getName()+".jpg");
       ret =  ic.setupAndCompare(ic);
      // guiFrame.info(ret);
       
		return ret;
	}
	
	/**
	 * 
	 * @param s - set name string, like barracks, etc... the image to compares
	 * @param save - nothing so far not sure why I added this
	 * @param configure - specify the config instead of using our main config "con", this is because we now have threads calling compareImage
	 * @return - true if the image matches
	 * 
	 * @throws Exception
	 */
	public boolean compareImage(String s , boolean save, config configure) throws Exception
	{
		boolean ret = false;		 
		BufferedImage screencapture = cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		
		//save current screen into current.jpg
		String name = "current"+s+".jpg";
		File outputFile = new File(name);
	    ImageIO.write(screencapture, "jpg", outputFile);
	
       
	    configure.setName(s);
       Convert c = new Convert();
       
       c.invertImage(name, "crop"+name, configure.getX(), configure.getY(), configure.getW(), configure.getH());
       // Now I will have the current screen image, inverted and cropped
       
       
       //do the same for our //rule/main.jpg
       c.invertImage(configure.getFile(), "crop"+configure.getName()+".jpg", configure.getX(), configure.getY(), configure.getW(), configure.getH());
       
       ImageCompare ic = new ImageCompare("crop"+name, "crop"+configure.getName()+".jpg");
       ret =  ic.setupAndCompare(ic);
      // guiFrame.info(ret);
       
		return ret;
	}
	
	/*
	 * make sure screen is zoomed out and scrolled up.
	 * This is important for main village screen because we need to know where army camps, training camps are located
	 */
	public boolean isScreenSetup() throws Exception
	{
		return compareImage("init");
	}
	/*
	 * Check to see if current screen is main village.
	 */
	public boolean inMain() throws Exception
	{
		return compareImage("main");
	}
	
	
	public boolean clickCamp() throws Exception
	{
		guiFrame.info("click camp");		 	
		 con.setName("camp");
		 		 
		 for(int i=0; i< con.getPos().size(); i++)
		 {
			 cont.mouseMove(con.getPos().get(i).getX(), con.getPos().get(i).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);			 
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);			
			 Thread.sleep(200);
		 }
		 
		 clickSafeSpot();
		 return compareImage("camp", true); // return false if not in camp		
	}
	public boolean isCampFull() throws Exception
	{		
		 return compareImage("camp_full");
	}
	public boolean inFindPage() throws Exception
	{
		return compareImage("find");
	}
	
	public void RunEmailService()
	{
		 e = new email(con.getEmail(), con.getPW(), con.getDestEmailList());		 		
		 Thread t = new Thread(e);
		 t.start();
	}
	
	public void RunUpdateStatService()
	{
		 updateS = new updateStat();		 		
		 Thread t = new Thread(updateS);
		 t.start();
	}
	public void RunUpdateWebPage()
	{
		UpdateWebPage up = new UpdateWebPage();		 		
		 Thread t = new Thread(up);
		 t.start();
	}	
	
	public void RunCheckForStuckPages()
	{
		StuckPage up = new StuckPage();		 		
		 Thread t = new Thread(up);
		 t.start();
	}		
	
	
	public static void main(String[] args)
	{
		try{
			new click();
			/*
		    Robot bot = new Robot();
		    
			for(int i=0; i < 20 ; i++)

			    
				//bot.keyPress(KeyEvent.VK_DOWN);
				//bot.keyRelease(KeyEvent.VK_DOWN);
				
				
			    //con.mouseMove(1964, 17);    
			  //  con.mousePress(InputEvent.BUTTON1_MASK);
			  //  con.mouseRelease(InputEvent.BUTTON1_MASK);
			    
			    
			    BufferedImage screencapture = con.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			    		    
			   String name = "click.jpg";
			   String outName ="invert-click.jpg";
	            File outputFile = new File(name);
	            ImageIO.write(screencapture, "jpg", outputFile);
	            
	            Convert c= new Convert();
	            c.invertImage(name,outName, 64, 87, 89, 24);
	            	           
	            
	            ImageScanner scan = new ImageScanner();
	            //scan.print(outName);
	            
	            scan.print(outName);
	            
	            guiFrame.info();
	            guiFrame.info();
	            
	            
	            //elixers
	            c.invertImage(name,outName, 59, 125, 90, 24);    //59, 125, 155, 149,
	            scan.print(outName);
	            
			   // Thread.sleep(3000);      
			*/
		}
		
		
		
			
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/*
	 * inner class thread to udpate stat.txt every min
	 */
	 class updateStat implements Runnable{
			
			public void run()
			{
				while(true) 
				{ 				
					try{
						updateStat();
						Thread.sleep(60000); 
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	 
	 
	 class UpdateWebPage implements Runnable{
			
			public void run()
			{
				while(true) 
				{ 				
					try{
						updateWebPage();
						Thread.sleep(60000); 
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}	
	 
	 class StuckPage implements Runnable{
			
			public void run()
			{
				while(true) 
				{ 				
					try{
						checkForStuckPages();
						Thread.sleep(60000); 
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}	
	 
	
}





class GMailAuthenticator extends Authenticator {
     String user;
     String pw;
     public GMailAuthenticator (String username, String password)
     {
        super();
        this.user = username;
        this.pw = password;
     }
    public PasswordAuthentication getPasswordAuthentication()
    {
       return new PasswordAuthentication(user, pw);
    }
}
