package email;


import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;


import javax.imageio.ImageIO;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.activation.*;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import setting.config;


public class email implements Runnable{

 public static void main(String[] args) {
	 
	 
	 
	 email gmail = new email();
	 
	while(true) 
	{
	 gmail.read();
	 
	 
	 try {
		    Thread.sleep(60000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
 }
 
 
 public void run()
 {	
		while(true) 
		{ 
			 try 
			 {
				 read();
				 Thread.sleep(60000);                 //1000 milliseconds is one second.
			 } 
			 
			 catch(InterruptedException ex) 
			 {
				    Thread.currentThread().interrupt();
			 }
		}
 }

 public void read() {
//  Properties props = new Properties();
  
  Properties props = System.getProperties();
	 String host = "smtp.gmail.com";
	    String username = "mturkbot@gmail.com";
	    String password = "mturkbotpassword";
    

  
  try {
	  
	  props.put("mail.smtp.starttls.enable", "true"); // added this line
	  props.put("mail.smtp.host", host);
	  props.put("mail.smtp.user", username);
	  props.put("mail.smtp.password", password);
	  props.put("mail.smtp.port", "587");
	  props.put("mail.smtp.auth", "true");	
	  
  // props.load(new FileInputStream(new File("C:\\smtp.properties")));
   Session session = Session.getDefaultInstance(props, new GMailAuthenticator(username, password));

   Store store = session.getStore("imaps");
   store.connect("smtp.gmail.com", username,password);

   Folder inbox = store.getFolder("inbox");
   inbox.open(Folder.READ_WRITE);
   int messageCount = inbox.getMessageCount();

   System.out.println("Total Messages:- " + messageCount);

   Message[] messages = inbox.getMessages();
   
   String last ="";
   System.out.println("------------------------------");
   for (int i = 0; i < messages.length; i++) {
       System.out.println(messages[i].getSubject());      
      
     // System.out.println(messages[i].getContentType());
      // System.out.println(messages[i].getContent());
      // Part p = (Part)messages[i].getContent();
      // Multipart mp = (Multipart)p.getContent();
               
      String msg="";
      String body="";
      
      body = getText((Part)messages[i]); //(String)messages[i].getContent();//;
      /*
      if(messages[i].getContentType().startsWith("TEXT/PLAIN"))
      {
    	  body = (String)messages[i].getContent();
      }
      else
      {    	       
    	      	 
    	 
      }
      */
      
      body = body.toLowerCase().trim();
      
      //System.out.println(body);
      /*
      if (messages[i].getSubject()!= null)
      {
    	  msg = messages[i].getSubject().toLowerCase().trim();    	  
      }
      if(msg.equals("start") || msg.equals("stop") || msg.equals("bot"))
      {    	
    	  last = msg;
      }
      */
      
      if(body.contains("start"))
      {
    	  last = "start";
      }
      else if(body.contains("stop"))
      {
    	  last = "stop";
      }
      else if(body.contains("bot"))
      {
    	  last = "bot";
      }
      else if(body.contains("status"))
      {
    	  last="status";
      }
      else if(body.contains("connect"))
      {
    	  last = "connect";
    	  messages[i].setFlag(Flags.Flag.DELETED, true); // flag for delete
      }
      else if(body.contains("screen"))
      {
    	  last = "screen";
    	  messages[i].setFlag(Flags.Flag.DELETED, true); // flag for delete
      }
      
      System.out.println(last);
    	  

   }
   
   
   
   
   // let's check to see if we have a difference
   File inputFile = new File(config.readFile);
   BufferedReader br = new BufferedReader(new FileReader(inputFile));
   String blah2 = br.readLine().trim();
   br.close();
   Boolean cont = false;
   
   String blah="";
	  if (last.equals("start"))
	  {
		  blah ="1";    		  
	  }
	  else if (last.equals("bot"))
	  {
		  blah = "2";
	  }
	  else if (last.equals("status"))
	  {
		  blah = "4";
	  }
	  else if (last.equals("connect"))
	  {
		  blah = blah2; //set it to whatever it was before. Won't trigger overwrite of file so won't send text.		  
		  ClickRestart();
		  //specify my own send text
		  sendText(session,"Clash of Clans restarting...");
	  }
	  else if(last.equals("screen"))
	  {
		  blah = blah2;
		  String fileName = SaveScreenShot();
		  sendPictureText(fileName);
		  
	  }
	  else
	  {
		  blah = "0";
	  }
	 
	  cont = blah.equals(blah2) ? false: true ;
   
   //write
	  if(cont){
		  File f = new File(config.readFile);
		  PrintWriter fw = new PrintWriter(f);
		  		  
		  fw.print(blah);
		  fw.close();
		  
		  if((blah2.equals("2")||blah2.equals("4")) && (blah.equals("4")  || blah.equals("2")))
		  {
			  
		  }
		  else{
			  sendText(session,blah);
		  }
	  }
	  
	  
   inbox.close(true);
   store.close();

  } catch (Exception e) {
   //e.printStackTrace();
  }
 }
 /*
 private String getText(Part p) throws Exception {
	 String s ="";
	 if (p.isMimeType("text/*")) {
		 s = (String)p.getContent();
		 //textIsHtml = p.isMimeType("text/html");	
	}
	return s;
 }*/
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
	
	
public String SaveScreenShot() throws Exception
	{
		Robot bot = new Robot();
		BufferedImage screencapture = bot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));				

		String name = "currentscreen.jpg";
		File outputFile = new File(name);
        ImageIO.write(screencapture, "jpg", outputFile);	
        
        return name;
        	       	       
	}
 public void ClickRestart() throws Exception
 {
	 Robot bot = new Robot();
	 bot.mouseMove(335,183); //where clash icon is on bluestack
	 bot.mousePress(InputEvent.BUTTON1_MASK);			 
	 bot.mouseRelease(InputEvent.BUTTON1_MASK);
 }
 public static void sendText(Session ses, String s) throws Exception
 {
	 String host = "smtp.gmail.com";
	    String from = "mturkbot@gmail.com";
	    String pass = "mturkbotpassword";
	    Properties props = System.getProperties();
	    
	    /*
	    props.put("mail.smtp.starttls.enable", "true"); // added this line
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.user", from);
	    props.put("mail.smtp.password", pass);
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");	
	    */
	    
	    //String[] to = {"shamikemosabi@gmail.com"}; // added this line
	    String[] to = {"6462841208@tmomail.net"}; // added this line
	    
	    MimeMessage message = new MimeMessage(ses);
	    message.setFrom(new InternetAddress("mturkbot@gmail.com"));
	    InternetAddress[] toAddress = new InternetAddress[to.length];
	    for( int i=0; i < to.length; i++ ) { // changed from a while loop
	        toAddress[i] = new InternetAddress(to[i]);
	    }
	    
	    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
	        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
	    }
	    
	    String temp = s ;
	    if(s.equals("1")){
	    	temp ="Started";
	    }
	    else if(s.equals("2")){
	    	temp = "BOT Started";
	    }
	    else if(s.equals("0"))
	    {
	    	temp ="Stoped";
	    }

	    message.setSubject(temp);
	    String body="Services " + temp;
	   //message.setContent(body, "text/html");
	    message.setContent(body, "text/plain");
	    Transport transport = ses.getTransport("smtp");
	    transport.connect(host, from, pass);
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
 }

 public void sendPictureText(String file) throws Exception
	{		
		 String host = "smtp.gmail.com";
		    String from = "mturkbot@gmail.com";
		    String pass = "mturkbotpassword";
		    Properties props = System.getProperties();

		    //String[] to = {"shamikemosabi@gmail.com"}; // added this line
		    String[] to = {"6462841208@tmomail.net"}; // added this line
		    		    
		    Session session = Session.getDefaultInstance(props, new GMailAuthenticator(from, pass));
		    MimeMessage message = new MimeMessage(session);
		    
	        // creates multi-part
	        Multipart multipart = new MimeMultipart();	      	       
	        MimeBodyPart attachPart = new MimeBodyPart();
	        attachPart.attachFile(file);
			    
	        multipart.addBodyPart(attachPart);
	     	        
		    message.setFrom(new InternetAddress("mturkbot@gmail.com"));
		    InternetAddress[] toAddress = new InternetAddress[to.length];
		    for( int i=0; i < to.length; i++ ) { // changed from a while loop
		        toAddress[i] = new InternetAddress(to[i]);
		    }
		    
		    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
		        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		    }
		    
		    message.setContent(multipart);
		    Transport transport = session.getTransport("smtp");
	
		    transport.connect(host, from, pass);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
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