import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;
import net.sourceforge.javaocr.scanner.PixelImage;

public class ImageScanner {
	
	public String print(String name) throws Exception
	{
		 OCRScanner scanner = new OCRScanner();
	        TrainingImageLoader loader = new TrainingImageLoader();
	        HashMap<Character, ArrayList<TrainingImage>> trainingImageMap = new HashMap<Character, ArrayList<TrainingImage>>();
	        loader.load("rule.bmp", new CharacterRange('0', '9'), trainingImageMap);
	        scanner.addTrainingImages(trainingImageMap);

	        Image image = ImageIO.read(new File(name));
	        
	        PixelImage pixelImage = new PixelImage(image);
	        pixelImage.toGrayScale(true);
	        pixelImage.filter();

	       
	       String gold = scanner.scan(image, 0, 0, 0, 0, null);
	        
	       return gold;
	        
	      
		
	}
	public void print(String name, int x, int y, int x2, int y2) throws Exception
	{
		 OCRScanner scanner = new OCRScanner();
	        TrainingImageLoader loader = new TrainingImageLoader();
	        HashMap<Character, ArrayList<TrainingImage>> trainingImageMap = new HashMap<Character, ArrayList<TrainingImage>>();
	        loader.load("rule.bmp", new CharacterRange('0', '9'), trainingImageMap);
	        scanner.addTrainingImages(trainingImageMap);

	        Image image = ImageIO.read(new File(name));
	        
	        PixelImage pixelImage = new PixelImage(image);
	        pixelImage.toGrayScale(true);
	        pixelImage.filter();

	       // String text = scanner.scan(image, 0, 0, 0, 0, null);
	        //String text = scanner.scan(image, 55, 117, 135, 138, null);
	       
	       String gold = scanner.scan(image, x, y, x2, y2, null);
	        String elixer = scanner.scan(image, x, y, x2, y2, null);
	        
	       System.out.println("Gold 1: " + gold);
	        System.out.println("Elixer 1: " + elixer);
	        
	        
	        
	        //System.out.println(text);
	        
	        //elixers
	        
	         image = ImageIO.read(new File(name));
	        
	         pixelImage = new PixelImage(image);
	        pixelImage.toGrayScale(true);
	        pixelImage.filter();

	        

	        gold = scanner.scan(image, x, y, x2, y2, null);
	        elixer = scanner.scan(image, x, y, x2, y2, null);
	        
	        System.out.println("GOLD 2: "+gold);
	        
	        System.out.println();
	       System.out.println("ELIXER 2: "+elixer);
		
	}
	
	
	public void print2(String name) throws Exception
	{
		OCRScanner scanner = new OCRScanner();
        TrainingImageLoader loader = new TrainingImageLoader();
        HashMap<Character, ArrayList<TrainingImage>> trainingImageMap = new HashMap<Character, ArrayList<TrainingImage>>();
        loader.load("rule2.bmp", new CharacterRange('0', '9'), trainingImageMap);
        scanner.addTrainingImages(trainingImageMap);

        Image image = ImageIO.read(new File(name));
        
        PixelImage pixelImage = new PixelImage(image);
        pixelImage.toGrayScale(true);
        pixelImage.filter();

       // String text = scanner.scan(image, 0, 0, 0, 0, null);
        //String text = scanner.scan(image, 55, 117, 135, 138, null);
       
       String gold = scanner.scan(image, 64, 87, 151, 111, null);
        String elixer = scanner.scan(image, 59, 125, 155, 149, null);
       System.out.println(gold);
        System.out.println( elixer);
        
        

        
         image = ImageIO.read(new File(name));
        
         pixelImage = new PixelImage(image);
        pixelImage.toGrayScale(true);
        pixelImage.filter();

        gold = scanner.scan(image, 64, 87, 170, 111, null);
         elixer = scanner.scan(image, 64, 125, 151, 147, null);
         System.out.println(gold);
        System.out.println(elixer);
		
	}
    public static void main(String[] args) throws Exception {
        OCRScanner scanner = new OCRScanner();
        TrainingImageLoader loader = new TrainingImageLoader();
        HashMap<Character, ArrayList<TrainingImage>> trainingImageMap = new HashMap<Character, ArrayList<TrainingImage>>();
        loader.load("rule.bmp", new CharacterRange('0', '9'), trainingImageMap);
        scanner.addTrainingImages(trainingImageMap);

        Image image = ImageIO.read(new File("invert-click.bmp"));
        
        PixelImage pixelImage = new PixelImage(image);
        pixelImage.toGrayScale(true);
        pixelImage.filter();

       // String text = scanner.scan(image, 0, 0, 0, 0, null);
        //String text = scanner.scan(image, 55, 117, 135, 138, null);
       
       String gold = scanner.scan(image, 64, 87, 153, 111, null);
        String elixer = scanner.scan(image, 59, 125, 155, 149, null);
        
       System.out.println("Gold 1: " + gold);
        System.out.println("Elixer 1: " + elixer);
        
        
        
        System.out.println();
        
        //elixers
        
         image = ImageIO.read(new File("invert-click.bmp"));
        
         pixelImage = new PixelImage(image);
        pixelImage.toGrayScale(true);
        pixelImage.filter();

        

        gold = scanner.scan(image, 64, 87,180, 111, null);
        elixer = scanner.scan(image, 59, 125, 180, 147, null);
        
        System.out.println("GOLD 2: "+gold);
        
        System.out.println();
       System.out.println("ELIXER 2: "+elixer);
		

    }
    
}