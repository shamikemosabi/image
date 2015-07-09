import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import org.apache.commons.lang3.time.StopWatch;

import email.email;
import setting.config;



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
	public click() throws Exception
	{
	
		
		RunEmailService();		
		setGUIandControl();
		
		/*
		BufferedImage screencapture =cont.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		String name = "current.jpg";
		File outputFile = new File(name);
        ImageIO.write(screencapture, "jpg", outputFile);	
        
        System.out.println(compareImage("queen"));
        deployHero("queen");
		System.exit(0);
				
		saveLootParent("C:\\Users\\shami_000\\Documents\\GitHub\\image\\rule\\", "current.jpg");
        */

		
		while(true)
		{	
		// if at work, we check read.txt value
			try{
				// change to case statement. 2=bot, 1=stay active dont bot, 0= stop bot.
				int value = isServiceStarted2(config.readFile);				
				//while((config.work)?isServiceStarted("C:\\Documents and Settings\\dhwang\\My Documents\\read.txt"):true)	
				switch(value)
				{	
				
				case 2:
				if(inMain())
				{
					if(disconnected) GotDisconnected();					
					
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
				
				
				case 1:
					clickSafeSpot(); // click safe spot to be active	
					Thread.sleep(30000);
					guiFrame.info("STAY ACTIVE");
					break;
					
				case 0:
					try{
						//only at work
						//send text to me, if service has ebeen stopped:
						if(config.work && serviceStopped && sendStopText)  //sendStopText only sends the first time service was stopped.
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
	}
	
	public void sendPictureText() throws Exception
	{
		if(sendPicText)
		{
			clickCamp(); // lets click camp first, get image of camp
			guiFrame.info("Started picture sending");
			config con = new config();
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
	        	       	       
	        sendPictureText(name);
	        
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
		config con = new config();
		con.setName("attack");
		
		Random rand=  new Random(); 
				
		
		//scroll up , need focuse first
		clickSafeSpot();		 
		 for(int i =0; i<12; i++)
		 {
			 cont.mouseWheel(-100);
			 Thread.sleep(100);
		 }
		
		// x and y is the safe start. I KNOW for sure that it can deploy.
		int x0 = con.getPos().get(0).getX(); 
		int y0 =con.getPos().get(0).getY(); // keep track of y
		
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
					for(int temp = 0; temp< con.getPos().size(); temp++) // should loop each pos , should only be 4
					{
						x1 = con.getPos().get(temp).getX();  // current base point.
						y1= con.getPos().get(temp).getY();  // don't want to mod x1/y1, because I use them to recalculate what to plot next
						
						// x/y is  used to do the actual move, it gets changed every loop			
						x = x1;
						y = y1;
						
						//need another loop to switch between barbs/archs
						//for...
						for(int b=0 ; b<2; b++) // 2 for barbs and arch
						{
							
							 // click barbs
							 config c2 = new config();				 
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
							
							
							for(int a= 0; a<  ((config.work)? ((b%2==0)?18:14): ((b%2==0)?28:22)) ; a++) //25:20 for home cause it lags.  
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
		    String from = "mturkbot@gmail.com";
		    String pass = "mturkbotpassword";
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
	        
		    message.setFrom(new InternetAddress("mturkbot@gmail.com"));
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
		    String from = "mturkbot@gmail.com";
		    String pass = "mturkbotpassword";
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
		    message.setFrom(new InternetAddress("mturkbot@gmail.com"));
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
		config con = new config();
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
			config con = new config();
			con.setName("king");
			
			 cont.mouseMove(con.getPos().get(0).getX(), con.getPos().get(0).getY());
			 cont.mousePress(InputEvent.BUTTON1_MASK);	
			 cont.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		
	}
	public void deployHero(String h) throws Exception
	{		
		config con = new config();
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
		config con = new config();
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
		config con = new config();
		con.setName("archs");
		
		boolean ret =compareImage("archs");
		guiFrame.info("out of archers: " + ret);
		return ret;
	}
	
	public boolean outOfBarbs() throws Exception
	{		
		config con = new config();
		con.setName("barbs");
		
		boolean ret =compareImage("barbs");
		guiFrame.info("out of barbs: " + ret);
		return ret; 
	}
	
	public boolean gotRaided() throws Exception
	{		
		config con = new config();
		con.setName("raided");
		
		boolean ret =compareImage("raided"); 		
		return ret;
		
	}
	public void returnHomeFromRaided() throws Exception
	{				
		config con = new config();
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
		config con = new config();
		con.setName("end");
		
		boolean ret =compareImage("end"); 
		guiFrame.info("End of battle " + ret);
		return ret;
		
	}
	public void returnHomeFromBattle() throws Exception
	{		
		config con = new config();
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
				deleteStatusEmail();
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
	
	/*
	 * 
	 * return int instead of boolean
	 */
	
	public int isServiceStarted2(String s) throws Exception
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
				deleteStatusEmail();
				sendPicText = true;
				
			}
			return 2; // return 2, 4 is just so we know we should take screen shot later, but still return 2 to bot
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
		else // in case for some reason, value read is not any of the numbers, lets just default to bot.
		{
			return 2;
		}
	}
	
	public void deleteStatusEmail() throws Exception
	{
		guiFrame.info("Started delete status email");
		 String host = "smtp.gmail.com";
		    String from = "mturkbot@gmail.com";
		    String pass = "mturkbotpassword";
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
		      if(body.contains("status")) messages[i].setFlag(Flags.Flag.DELETED, true);
		   }
		   
		   inbox.close(true);
		   
		   guiFrame.info("Completed delete status email");
	  
		   
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
		config con = new config();
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
		config con = new config();
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
		config con = new config();
		con.setName("battle");
		if(!init)
		{
			Thread.sleep((config.work)?3000:8000); //wait 3 seconds for finding, longer for home....
		}
		guiFrame.info("AFter In battle");
		return compareImage("battle"); 
	}
	public void clickFind() throws Exception
	{		
		config con = new config();
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
		config con = new config();
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
		config con = new config();
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
		 config con = new config();
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
		 config con = new config();
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
		 config con = new config();
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
			if(breakout > 5) break;	// make sure we break out eventually, don't want any infinite loops 	
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
       
       config con = new config();
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
	
       
       config con = new config();
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
		 config con = new config();
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
		 email e = new email();
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
