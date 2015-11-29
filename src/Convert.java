import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;

class Convert
{
    public static void main(String[] args)
    {
    	try
    	{
    		//invertImage("click.jpg","fuck.jpg",64, 87, 89, 24);
    		//invertImage("click.jpg","fuck.jpg",64, 87, 99, 24); // LONG
            invertImage("click.jpg","fuck.jpg", 59, 123, 90, 24);    //59, 125, 155, 149,
    		
    		 ImageScanner scan = new ImageScanner();
	            //scan.print(outName);
	            scan.print("fuck.jpg");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    public static void invertImage(String imageName, String outName, int x1, int y1, int w, int h) throws Exception {
        BufferedImage inputFile = null;
        BufferedImage cropImage = null;
        try {
            inputFile = ImageIO.read(new File(imageName));
            
             cropImage = inputFile.getSubimage(x1, y1, w, h);
             
     		String ranStr = RandomStringUtils.randomAlphanumeric(8);
            File out = new File("crop"+ranStr+".jpg");
            ImageIO.write(cropImage, "jpg", out);
            
            out.delete();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int x = 0; x < cropImage.getWidth(); x++) {
            for (int y = 0; y < cropImage.getHeight(); y++) {
                int rgba = cropImage.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                                255 - col.getGreen(),
                                255 - col.getBlue());
                cropImage.setRGB(x, y, col.getRGB());
            }
        }
        Thread.sleep(500);
        try {
            File outputFile = new File(outName);
            ImageIO.write(cropImage, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }
    
    
    public static void invertImage(String imageName, String outName) {
        BufferedImage inputFile = null;        
        try {
            inputFile = ImageIO.read(new File(imageName));
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        for (int x = 0; x < inputFile.getWidth(); x++) {
            for (int y = 0; y < inputFile.getHeight(); y++) {
                int rgba = inputFile.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                                255 - col.getGreen(),
                                255 - col.getBlue());
                inputFile.setRGB(x, y, col.getRGB());
            }
        }

        try {
            File outputFile = new File(outName);
            ImageIO.write(inputFile, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
}