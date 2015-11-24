package comparator;
import java.util.Comparator;

import setting.AutoUpgradeData;

public class DateComparator implements Comparator<AutoUpgradeData> {
    @Override
    public int compare(AutoUpgradeData o1, AutoUpgradeData o2) {
        return o1.getSwapDate().compareTo(o2.getSwapDate());
    }
}