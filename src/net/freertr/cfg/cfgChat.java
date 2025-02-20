package net.freertr.cfg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.freertr.line.lineScript;
import net.freertr.tab.tabGen;
import net.freertr.user.userFilter;
import net.freertr.user.userHelping;
import net.freertr.util.cmds;

/**
 * chat script configuration
 *
 * @author matecsaba
 */
public class cfgChat implements Comparator<cfgChat>, cfgGeneric {

    /**
     * name of script
     */
    public String name;

    /**
     * the script
     */
    public lineScript script;

    public int compare(cfgChat o1, cfgChat o2) {
        return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
    }

    public String toString() {
        return "chat script " + name;
    }

    /**
     * defaults text
     */
    public final static String[] defaultL = {};

    /**
     * defaults filter
     */
    public static tabGen<userFilter> defaultF;

    /**
     * create new profile
     *
     * @param nam name of interface
     */
    public cfgChat(String nam) {
        name = nam.trim();
        script = new lineScript();
        script.scrName = name;
    }

    public void getHelp(userHelping l) {
        script.getHelp(l);
    }

    public List<String> getShRun(int filter) {
        List<String> l = new ArrayList<String>();
        l.add("chat-script " + name);
        l.addAll(script.getCfg(cmds.tabulator, filter));
        l.add(cmds.tabulator + cmds.finish);
        l.add(cmds.comment);
        if ((filter & 1) == 0) {
            return l;
        }
        return userFilter.filterText(l, defaultF);
    }

    public void doCfgStr(cmds cmd) {
        String s = cmd.word();
        boolean neg = s.equals("no");
        if (!neg) {
            cmd = cmd.copyBytes(true);
        }
        if (script.doCfg(cmd, neg)) {
            cmd.badCmd();
        }
    }

    public String getPrompt() {
        return "chat";
    }

}
