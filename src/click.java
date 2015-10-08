import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	
	ArrayList<AutoUpgradeData> alAutoUpgradeBuilderNOW = new ArrayList<AutoUpgradeData> ();
	ArrayList<AutoUpgradeData> alAutoUpgradeBuilderSTATIC = new ArrayList<AutoUpgradeData> ();
	
	public click() throws Exception
	{
		downloadFTP(config.configFile , "/config/config.xml");  
		setGUIandControl();
		RunEmailService();	
		
		
	//	AutoUpgrade();
		/*
		BufferedImage screencapture =cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		String name = "current.jpg";
		File outputFile = new File(name);
        ImageIO.write(screencapture, "jpg", outputFile);	
        
        System.out.println(compareImage("queen"));
        deployHero("queen");
		System.exit(0);
				
		System.out.println(gotRaided()+"GOT RAIDED");
	
				deployTroops2();
		System.exit(0);
		saveLootParent("C:\\Users\\shami_000\\Documents\\GitHub\\image\\rule\\", "current.jpg");
		
			email = "docogo.two@gmail.com";
		swap();
		
      
		upLoadFTP("config.xml","config");
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
				switch(value)
				{	
				
				case 2:
				if(inMain())
				{
					if(disconnected) GotDisconnected();	
					
					AutoUpgrade2();
					AutoUpgradeBuilder();
					
					sendPictureText();
					setUpScreen();				
					//if(clickCamp())
					if(clickBarracksForCamp()) // changed to barracks to check for full camp
					{
						guiFrame.info("camp is full");
						//click attack
						clickSafeSpot(); // get out to main screen
						clickAttack();// click attack to go to find match queue					
					}
					else // come here either misclicked OR barracks is not full so need to train
					{
						guiFrame.info("need to train troops");
						clickSafeSpot(); // get out of camp, go back to main
						clickSafeSpot();// do it twice to loose focuse of barracks from before just in case
										// clicking it when it has focus is loosing focus
						if(clickBarracks()) // click barracks, make sure we clicked
						{
							guiFrame.info("we are in barracks");
							for(int i=0; i< 2; i++) // only 4 barracks right now
							{
								trainBarbs();
								trainArchs();
							}
							
							clickSafeSpot(); // get out of barracks screen out to main
						}	
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
						swap(email, con.getEmail());			
					}
					
					break;
				
				case 1:
					clickSafeSpot(); // click safe spot to be active
					if(inMain())
					{
						AutoUpgrade2();
						AutoUpgradeBuilder();
						
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
							sendText();
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

			/*
			try{
			//only at work
			//send text to me, if service has ebeen stopped:
			if(config.work && serviceStopped && sendStopText)  //sendStopText only sends the first time service was stopped.
			{									// or else each loop it'll keep sending.
				sendText();
				guiFrame.info("SEND TEXT");
				sendStopText = false;
			}
			}
			catch(Exception e)
			{
				guiFrame.info(e.getMessage());				
			}
			*/
		}
	}
	
	public boolean zeroBuilder() throws Exception
	{

		return compareImage("zeroBuilder");
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
		
		//if screen is setup, and have builder
		if(isScreenSetup() && (!zeroBuilder()) )
		{
			guiFrame.info("Builder free and screen setup");
			al = loadUpgradeBuilder(con.getEmail());
			
			for(int i =0; (i < al.size() && !zeroBuilder()); i++)			
			{	
				clickAutoUpgrade(al.get(i));
				takeCurrentScreenshot(false);
				clickSafeSpot();
				//compare previous cropCurrent image (cropped zero builder of last time) with current zerobuilder
				// The idea is After clickAutoUpgrade(), and my image is still the same it means nothing happened, so there was no 
				// successful upgrade. If it was different THEN I should send text to show upgrade.
				
				//make sure call from zerobuilder() and here does not have any compare image, or else my cropCurrent would be overwritten
				boolean send = !SameBuilderImage();				
								
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
			downloadFTP(config.upgradeFile , "/config/upgrade.xml");
			
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
					NodeList itemList = eElement.getElementsByTagName("item");
					
					
					//loop thru all item that match our account
					for(int i = 0 ; i < itemList.getLength(); i ++)
					{						
						Node itemNode = itemList.item(i);				
						Element itemEle = (Element) itemNode;
						
						AutoUpgradeData aud = new AutoUpgradeData();
						
						aud.setName(itemEle.getElementsByTagName("name").item(0).getTextContent());
						aud.getXYArrayList().add(createXY(itemEle.getElementsByTagName("pos").item(0).getTextContent()));
						aud.getXYArrayList().add(createXY(itemEle.getElementsByTagName("upgrade").item(0).getTextContent()));
						aud.getXYArrayList().add(createXY(itemEle.getElementsByTagName("click").item(0).getTextContent()));
						
						alAutoUpgradeBuilder.add(aud);
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
		if(guiFrame.getAutoUpgrade())
		{
			try{
				alAutoUpgradeBuilderNOW.clear();
				alAutoUpgradeBuilderSTATIC.clear();
				downloadFTP(config.configFile , "/config/config.xml"); 
				
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
											
					if(id.equals("static")) 
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
					String lastEmail ="";
					boolean deleted =  false;
					boolean swapBack = false;
					
					for(int i=0; i< alAutoUpgradeBuilderNOW.size(); i++)
					{					
						String t = alAutoUpgradeBuilderNOW.get(i).getTime();
						String e = alAutoUpgradeBuilderNOW.get(i).getEmail();
						swapBack = alAutoUpgradeBuilderNOW.get(i).isSwapBack();
						AutoUpgradeData aud = alAutoUpgradeBuilderNOW.get(i);
						
						boolean active = false;
						
						DateFormat format = new SimpleDateFormat("MM/dd/yyyy h:mm a");
						Date date = format.parse(t);					
						
						//if my date is before current date, then do it!
						if(date.before(currDate))
						{
							guiFrame.info("There is upgrade to do");
							deleted = true;
							deleteFromXMLSpecific(doc, t);							
							active = isEmailActive(doc, e, ""); //empty string last param, because I don't care if same account or not							
						
						
							// The email is currently not active so we can swap
							if(!active)
							{
								swapBack = !active && swapBack ; // also need to consider swapback value
								lastEmail = e; //need to know what was the last email, so I can swap back when for loop is done.
								
								guiFrame.info("Email is not active so Swwapping");
								swap(e,con.getEmail());
								clickSafeSpot(); // click safe spot to get rid of raided screen
								Thread.sleep(3000);
								
								//we should be in the new account now.
								if(inMain()) // make sure in main.
								{
									guiFrame.info("In main from after swap");
									setUpScreen();
									clickAutoUpgrade(aud);
									AutoUpgradeBuilder();
									takeCurrentScreenshot(true);
									clickSafeSpot(); // get rid of any screen, make sure we are in main village page so we can click setting button.								
								}
								else
								{
									guiFrame.info("Swap failed, not in main");
								}
								
							}		
						}
					}
					
					//swap back to original
					if(swapBack)
					{
						guiFrame.info("Swapping back to original email");
						swap(originalEmail,con.getEmail()); // swap back
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
							
							upLoadFTP(con.configFile,"config"); 	
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
				
			}
		
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		
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
				downloadFTP(config.configFile , "/config/config.xml");
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
						con = new config(oldEmail); //just to re download config, get new autoUpgrade
						if(inMain()) // make sure in main.
						{
							setUpScreen();
							clickAutoUpgrade(aud);
							takeCurrentScreenshot(true);
							clickSafeSpot();
						}
					}
					else
					{
						guiFrame.info("Need to swap");
						swap(upgradeEmail,oldEmail); //swap to upgrade email
						clickSafeSpot(); // click safe spot to get rid of raided screen
						Thread.sleep(3000);
						
						//we should be in the new account now.
						if(inMain()) // make sure in main.
						{
							setUpScreen();
							AutoUpgradeBuilder();
							clickAutoUpgrade(aud);
							takeCurrentScreenshot(true);
							clickSafeSpot(); // get rid of any screen, make sure we are in main village page so we can click setting button.
							if(swapBack)
							{
								swap(oldEmail,upgradeEmail); // swap back
							}
							
						}
						//if not in main something happened with swap
						// upgrade failed, doing nothing should be okay.
					}
				}
				else
				{
					con = new config(oldEmail); //get new autoUpgrade
				}
			
			} // coming out of this IF I need to get new autoupgrade
		}
	}
	public void clickAutoUpgrade(AutoUpgradeData aud) throws Exception
	{
		for(int i=0 ; i < aud.getXYArrayList().size(); i++)
		{			
			Thread.sleep(2000);
			cont.mouseMove(aud.getXYArrayList().get(i).getX(), aud.getXYArrayList().get(i).getY()); 
			cont.mousePress(InputEvent.BUTTON1_MASK);	
			Thread.sleep(500);
			cont.mouseRelease(InputEvent.BUTTON1_MASK);				
		}
	}
	/*
	 * String t - is my time , which is the ID attribute for pos tag
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
				 upLoadFTP(con.configFile,"config");
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
	
	
	public void swap(String em, String oldEmail) throws Exception
	{
		downloadFTP(config.configFile , "/config/config.xml"); 
		
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
			// lets update to 1, which mean start (keep active), we'll let email service run the next time to update actual value.
			//updateReadFile("1");
			
			 Thread.sleep(5000); // wait 5 sec before taking screen shot, I want village to load up.
			 
			//take and send screenshot
			 //takeCurrentScreenshot(true);
		 }
		 
	}
	
	public void takeCurrentScreenshot(boolean send) throws Exception
	{
		BufferedImage screencapture = cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));			
		String name = "currentstatus.jpg";
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
		con = new config(guiFrame.account);
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
			String name = "currentstatus.jpg";
			File outputFile = new File(name);
	        ImageIO.write(screencapture, "jpg", outputFile);	
	        guiFrame.info("Finished saving to file");
	        	       	       
	        sendPictureTextStatus(name);
	        
	        sendPicText = false;
	        
	        clickSafeSpot();	        
			
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
		    InternetAddress[] toAddress = new InternetAddress[to.length];
		    for( int i=0; i < to.length; i++ ) { // changed from a while loop
		        toAddress[i] = new InternetAddress(to[i]);
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
		    InternetAddress[] toAddress = new InternetAddress[to.length];
		    for( int i=0; i < to.length; i++ ) { // changed from a while loop
		        toAddress[i] = new InternetAddress(to[i]);
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
	 public static void sendText() throws Exception
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
		    InternetAddress[] toAddress = new InternetAddress[to.length];
		    for( int i=0; i < to.length; i++ ) { // changed from a while loop
		        toAddress[i] = new InternetAddress(to[i]);
		    }
		    
		    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
		        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		    }
		    
		    String temp = "Bot stopped, safe to connect";

		    message.setSubject(temp);
		    String body="Services " + temp;
		   //message.setContent(body, "text/html");
		    message.setContent(body, "text/plain");
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
		catch(Exception e) // throw exception if code is 5, have email in string. valueOf will throw exception
		{
			email  = str.substring(1); // get rid of first char which is "5"
			email  = email.trim(); 
			ret = 5;
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
			downloadAndLoadConfig();
			deleteEmail("config");
			updateReadFile("1");
			return 1; // let the next email service update actual read value
		}
		else // in case for some reason, value read is not any of the numbers, lets just default to bot.
		{
			return 2;
		}
	}
	
	public void downloadAndLoadConfig()
	{
		downloadFTP(config.configFile , "/config/config.xml");
		con = new config(con.getEmail());   // new config object with same account as before, just NEW config.xml
	}
	
	/*
	 * uploads FileName to FTP dir
	 */
	public void upLoadFTP(String FileName, String dir)
	{
		File f = new File(FileName);

		FTPClient ftp = new FTPClient();

		 boolean success = false;
	     int count = 0;
	     do 
	        {   
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
					System.out.println("Error UploadFTP CLICK method");
		        	System.out.println(e.getMessage());
		        	
					guiFrame.info("Error UploadFTP CLICK method");
					guiFrame.info(e.getMessage());
		        	e.printStackTrace();
		        	success = false;					
				}
	        }while(!success && count < 10);
	}
	
	public void downloadFTP(String localFile, String remoteFile)
	{
		
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
	        	
				guiFrame.info("Error downloadFTP");
				guiFrame.info(e.getMessage());	   
				
	        	e.printStackTrace();
	        	success = false;
	        }
        }while(!success && count < 10);
  
         
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
	
	/*
	 * Really need to improve OCR
	 */
	public boolean shouldIAttack() throws Exception
	{		
		
		lootThreshold = Integer.valueOf(con.getLootThreshold());
		//IF smart loot is on.
		if(guiFrame.getSmartLoot())
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
		 
		ImageParser ip = new ImageParser(System.getProperty("user.dir")+"\\",name);
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
	    
	    ImageParser ip = new ImageParser(s,n);
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
			 Thread.sleep(1000);
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
		 e = new email(con.getEmail(), con.getPW());
		 Thread t = new Thread(e);
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
