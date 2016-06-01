import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;



import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.*;


public class control{
	
	private Robot bot = null;
	private gui guiFrame=null;
	
	private int offsetX = 0;
	private int offsetY = 0;
	
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
	
	
	   public interface User32 extends StdCallLibrary {
		      User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
		               W32APIOptions.DEFAULT_OPTIONS);

		      HWND FindWindow(String lpClassName, String lpWindowName);

		      int GetWindowRect(HWND handle, int[] rect);
		   }
	
	  public void setRect(String windowName)  {
		HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
				
		int[] rect = {0, 0, 0, 0};
		int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
		
		offsetX = rect[0];
		offsetY = rect[1];
		}
	  
	  public int getOffSetX()
	  { 
		  return this.offsetX;
	  }
	  public int getOffSetY()
	  { 
		  return this.offsetY;
	  }
}