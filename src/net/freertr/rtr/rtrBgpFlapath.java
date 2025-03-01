package net.freertr.rtr;

import java.util.Comparator;
import net.freertr.cfg.cfgAll;
import net.freertr.util.bits;
import net.freertr.util.cmds;

/**
 * bgp4 flap statistic
 *
 * @author matecsaba
 */
public class rtrBgpFlapath implements Comparator<rtrBgpFlapath> {

    /**
     * create instance
     */
    public rtrBgpFlapath() {
    }

    /**
     * path
     */
    public String path;

    /**
     * counter
     */
    public int count;

    /**
     * last
     */
    public long last;

    public int compare(rtrBgpFlapath o1, rtrBgpFlapath o2) {
        return o1.path.compareTo(o2.path);
    }

    public String toString() {
        return count + " " + bits.timePast(last) + " " + path;
    }

    /**
     * dump entry
     *
     * @param rev reverse path
     * @return dumped
     */
    public String dump(boolean rev) {
        String p;
        if (rev) {
            p = "";
            cmds c = new cmds("pth", path);
            for (;;) {
                String a = c.word();
                if (a.length() < 1) {
                    break;
                }
                p = a + " " + p;
            }
        } else {
            p = path;
        }
        return count + "|" + bits.timePast(last) + "|" + bits.time2str(cfgAll.timeZoneName, last + cfgAll.timeServerOffset, 3) + "|" + p;
    }

}
