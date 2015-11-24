package comparator;
import java.util.Comparator;

import setting.AutoUpgradeData;

public class LootPrecentageComparator implements Comparator<AutoUpgradeData> {
    @Override
    public int compare(AutoUpgradeData o1, AutoUpgradeData o2) {
    	
    	int loot1 = o1.getTempIntA() + o1.getTempIntB();
    	int loot2 = o2.getTempIntA() + o2.getTempIntB();
    	
    	if(loot1 == loot2) //very unlikely percentage are exact
    	{
    		return 0;
    	}
    	else if(loot1 < loot2)
    	{
    		return -1;
    	}
    	else
    	{
    		return 1;
    	}    		
    }
}
