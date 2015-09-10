import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;



public class control{
	
	private Robot bot = null;
	private gui guiFrame=null;
	
	public control(gui g, Robot b)
	{
		bot = b;
		guiFrame = g;
	}
	
	
	/* 
	 * a way to pause mid program
	 */
	private void checkPause()
	{
		while(!guiFrame.getStarted())
		{
			
		}
	}
	
	
	public void mouseMove(int x, int y)
	{
		checkPause();
		 bot.mouseMove(x,y);
	}
	
	public void mousePress(int i)
	{
		checkPause();
		 bot.mousePress(i);
	}
	
	public void mouseRelease(int i)
	{
		checkPause();
		bot.mouseRelease(i);
	}
	
	public void keyPress(int i)
	{
		checkPause();
		 bot.keyPress(i);
	}
	
	public void keyRelease(int i)
	{
		checkPause();
		bot.keyRelease(i);
	}
	
	public BufferedImage createScreenCapture(Rectangle rec)
	{
		checkPause();
		return bot.createScreenCapture(rec);
	}
	
	public void mouseWheel(int i)
	{
		checkPause();
		 bot.mouseWheel(i);
	}
}