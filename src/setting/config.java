/**
 * @todo
 * 
 * - Think about deploying king/queen/cc troops if I have them
 * - load config from file instead? perhaps XML?
 */

package setting;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class config
{
	ArrayList<struct> data ;
	struct s = null;
	public static boolean work = true;
	//different screen size between home and work
	String ruleFolder = System.getProperty("user.dir")+"\\"+ ((work)?"rule":"rule_home");	
	
	public final static String readFile =  System.getProperty("user.dir")+"\\read.txt";
	public final static String configFile =  System.getProperty("user.dir")+"\\config.xml";
	
	private xy xyBarrack = null;
	private xy xyCamp = null;
	private String email="";
	private String pw="";
	public int deployArcher=0;
	public int deployBarb=0;	
	public String lootThreshold="";
	public int slot =0;
	
	public ArrayList<xy> swapSlot = new ArrayList<xy>();
	
	public config()
	{
		setup();
	}
	
	public config(String account)
	{
		setup();
		loadConfig(account);
				
	}
	
	public void loadConfig(String account)
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
					email = e;
					xyBarrack = createXY(eElement.getElementsByTagName("barrack").item(0).getTextContent());
					xyCamp = createXY(eElement.getElementsByTagName("camp").item(0).getTextContent());
					pw = eElement.getElementsByTagName("password").item(0).getTextContent();
					deployArcher = Integer.valueOf(eElement.getElementsByTagName("deployArcher").item(0).getTextContent());
					deployBarb = Integer.valueOf(eElement.getElementsByTagName("deployBarb").item(0).getTextContent());
					lootThreshold = eElement.getElementsByTagName("loot").item(0).getTextContent();
					slot = Integer.valueOf(eElement.getElementsByTagName("slot").item(0).getTextContent());
				}


			}
			
			
		}
		catch(Exception e)
		{
			System.out.println("error loading config file");
			e.printStackTrace();
		}
	}

	public void setup()
	{
		swapSlot.add(new xy(508,286));  
		swapSlot.add(new xy(508,353));
		swapSlot.add(new xy(508,419));
		swapSlot.add(new xy(508,476));
		swapSlot.add(new xy(508,539));
	}
	
	public xy createXY(String s)
	{
		int a = Integer.valueOf(s.substring(0, s.indexOf(",")));
		int b = Integer.valueOf( s.substring(s.indexOf(",") + 1, s.length()));
		xy temp = new xy(a,b);
		return temp;
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
				pos.add(new xy(859,712)); //click train
				
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
				pos.add(new xy(889,663)); // click ok
				pos.add(new xy(837,507)); // click load
				pos.add(new xy(607,255)); // click textbox
				pos.add(new xy(844,255)); // click Okay
				
				s = new struct("swap",613,285,217,37, ruleFolder+"\\swap.jpg",pos);
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
		return email;
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