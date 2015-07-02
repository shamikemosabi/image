import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;


import javax.imageio.ImageIO;
import javax.swing.*;


import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

import setting.config;


public class ImageParser
{
	
	protected BufferedImage img1 = null;
	protected BufferedImage img2 = null;
	
	protected String gold ="";
	protected String elixer="";
	
	boolean work= true;
	//String dir = (work)?"C:\\Dropbox\\Workspace\\image\\loot\\":"C:\\Users\\CGS\\bot\\image\\loot\\";
	String dir = System.getProperty("user.dir")+"\\loot\\";
	public static void main(String[] args)
	{
		try
		{
				new ImageParser();		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public String work(BufferedImage b) throws Exception
	{
		int x1 = 0;
		int x2 = 0;
		int y= 6;  // this value can change
		String ret= "";
		
		boolean out=false;
		boolean NotFirst= false;
		boolean flag = false;   // false we are searching for black, TRUE we are searching fro white pixel
		Raster r=  b.getData();
		for(int x=0; x< b.getWidth(); x++)
		{
			int i =r.getSample(r.getMinX() + x, r.getMinY() + y, 0);
 			//System.out.println(i+"      " + x);
			//r.getPixel(r.getMinX() + x, r.getMinY() + y, 0);
			
			if((flag)? i >119 : i<70 ) // can play around with the numbers +-
			{
				x1 = x2;
				x2 = x;
				out = true && NotFirst;		
				flag = !flag;  //reverse
				NotFirst=true;	
			}
			
			if(out) // only gets here after having our two x1, x2 points
			{
				File outputFile = new File(dir+"test"+x+".jpg");
	            ImageIO.write(b.getSubimage(x1-2, 0, x2-x1+4, 22), "jpg", outputFile);
	            NotFirst =false;
	            out = false;
	            
	            //read the individual subimage
 	            ImageScanner sc = new ImageScanner();
 	            String s = sc.print(dir+"test"+x+".jpg").trim();
 	            
 	            //check if s is empty, if it is lets just fill it wiht any value
 	            if(s.equals("")) s = "1";
	         // System.out.println( "EQUALS " + s.substring(s.length()-1));
	           ret = ret+s.substring(s.length()-1);
	        //  System.out.println( "-----------------------------------------------------------------");
	           outputFile.delete();
			}
		}
		
		System.out.println(ret);
		return ret;
		
	}
	public void saveLootParent(String s, String n)throws Exception
	{
		
	
		//save current screen into current.jpg
		//String name = n;
		File outputFile = new File(s+n);
		
      
		
		//ImageScanner scan = new ImageScanner();
		config con = new config();
		con.setName("gold");
		Convert c = new Convert();
      
        c.invertImage(s+n, s+"crop_gold"+n, con.getX(), con.getY(), con.getW(), con.getH());
        // Now I will have the current screen image, inverted and cropped
      
        
        String gold="";       
       // gold = scan.print(s+"crop_gold"+n).trim();
     
        
        this.img1 = imageToBufferedImage(loadJPG(s+"crop_gold"+n));
        
        gold= gold.replaceAll(" ", "");
        String elixer="";        
        con.setName("elixer");
        c.invertImage(s+n, s+"crop_elixer"+n, con.getX(), con.getY(), con.getW(), con.getH());        
       // elixer = scan.print(s+"crop_elixer"+n).trim();
       
        elixer= elixer.replaceAll(" ", "");
        
        this.img2 = imageToBufferedImage(loadJPG(s+"crop_elixer"+n));

        String de="";
		//con.setName("de_amt");			
	   // c.invertImage(s+n, s+"crop_de"+n, con.getX(), con.getY(), con.getW(), con.getH());        
	  //  de = scan.print(s+"crop_de"+n).trim();
	 
	  //  de= de.replaceAll(" ", "");		
	                    
      
        
	 //   Integer comb = Integer.valueOf(gold) + Integer.valueOf(elixer);	
        
        //saveLoot(gold, elixer, de);
        //Thread.sleep(2000);      
	}
	
	
	
	
	public void saveLoot(String g, String e, String d) throws Exception
	{
		File f = new File("current.jpg");
		File f1 = new File(System.getProperty("user.dir")+"\\loot\\"+g+"_"+e+"_"+d+".jpg");		
		f.renameTo(f1);
	}
	
	public ImageParser() throws Exception
	{
		//String s= "C:\\Users\\CGS\\bot\\image\\loot\\" ;
		String s= dir ;
		
		String g = "074593_00004_133.bmp";
		
		// takes file, update img1/img2 to bufferimage for gold/elixer
		saveLootParent(s,g);
		
		work(this.img1);
		
		//System.out.println("Elixer -----");
		work(this.img2);
	}
	
	public ImageParser(String d, String n) throws Exception
	{	
		// takes file, update img1/img2 to bufferimage for gold/elixer
		saveLootParent(d,n);		
		this.gold = work(this.img1);		
	//	System.out.println("Elixer -----");
		this.elixer = work(this.img2);
	}
	
/*
	public ImageParser(String file1, String file2) {
		this(loadJPG(file1), loadJPG(file2));
	}
	*/
 
	// constructor 2. use awt images.
	public ImageParser(Image img1, Image img2) {
		this(imageToBufferedImage(img1), imageToBufferedImage(img2));
	}
 
	// constructor 3. use buffered images. all roads lead to the same place. this place.
	public ImageParser(BufferedImage img1, BufferedImage img2) {
		this.img1 = img1;
		this.img2 = img2;
		
	}
	
	protected static Image loadJPG(String filename) {
		FileInputStream in = null;
		try { 
			in = new FileInputStream(filename);
		} catch (java.io.FileNotFoundException io) { 
			System.out.println("File Not Found"); 
		}
		JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
		BufferedImage bi = null;
		try { 
			bi = decoder.decodeAsBufferedImage(); 
			in.close(); 
		} catch (java.io.IOException io) {
			System.out.println("IOException");
		}
		return bi;
	}
	
	
	protected static BufferedImage imageToBufferedImage(Image img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, null, null);
		return bi;
	}
}