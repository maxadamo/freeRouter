package net.freertr.cfg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.freertr.addr.addrIP;
import net.freertr.tab.tabGen;
import net.freertr.tab.tabListing;
import net.freertr.tab.tabListingEntry;
import net.freertr.tab.tabPlcmapN;
import net.freertr.user.userFilter;
import net.freertr.user.userHelping;
import net.freertr.util.bits;
import net.freertr.util.cmds;

/**
 * policy map configuration
 *
 * @author matecsaba
 */
public class cfgPlymp implements Comparator<cfgPlymp>, cfgGeneric {

    /**
     * name of policymap
     */
    public String name;

    /**
     * current sequence number
     */
    public int seq;

    /**
     * list of policy
     */
    public tabListing<tabPlcmapN, addrIP> plcmap;

    /**
     * defaults text
     */
    public final static String[] defaultL = {
        "policy-map .*! sequence .* description ",
        "policy-map .*! sequence .* no match access-group",
        "policy-map .*! sequence .* no match frag",
        "policy-map .*! sequence .* match flag all",
        "policy-map .*! sequence .* match length all",
        "policy-map .*! sequence .* match ttl all",
        "policy-map .*! sequence .* match ethtyp all",
        "policy-map .*! sequence .* match sgt all",
        "policy-map .*! sequence .* match cos all",
        "policy-map .*! sequence .* match exp all",
        "policy-map .*! sequence .* match tos all",
        "policy-map .*! sequence .* match dscp all",
        "policy-map .*! sequence .* match precedence all",
        "policy-map .*! sequence .* match qosgroup all",
        "policy-map .*! sequence .* match flow all",
        "policy-map .*! sequence .* set sgt leave",
        "policy-map .*! sequence .* set cos leave",
        "policy-map .*! sequence .* set exp leave",
        "policy-map .*! sequence .* set tos leave",
        "policy-map .*! sequence .* set ttl leave",
        "policy-map .*! sequence .* set dscp leave",
        "policy-map .*! sequence .* set precedence leave",
        "policy-map .*! sequence .* set qosgroup leave",
        "policy-map .*! sequence .* set flow leave",
        "policy-map .*! sequence .* access-rate 0",
        "policy-map .*! sequence .* exceed-rate 0",
        "policy-map .*! sequence .* time-interval 0",
        "policy-map .*! sequence .* queue-limit 0",
        "policy-map .*! sequence .* no random-detect",
        "policy-map .*! sequence .* no service-policy",
        "policy-map .*! sequence .* no log"
    };

    /**
     * defaults filter
     */
    public static tabGen<userFilter> defaultF;

    /**
     * create new policy map
     */
    public cfgPlymp() {
        plcmap = new tabListing<tabPlcmapN, addrIP>();
        seq = plcmap.nextseq();
    }

    /**
     * get current entry
     *
     * @return current entry
     */
    public synchronized tabPlcmapN getCurr() {
        tabPlcmapN ntry = new tabPlcmapN();
        ntry.sequence = seq;
        ntry = plcmap.find(ntry);
        if (ntry != null) {
            return ntry;
        }
        ntry = new tabPlcmapN();
        ntry.sequence = seq;
        ntry.action = tabListingEntry.actionType.actPermit;
        plcmap.add(ntry);
        return ntry;
    }

    public int compare(cfgPlymp o1, cfgPlymp o2) {
        return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
    }

    public String getPrompt() {
        return "plcmp";
    }

    public String toString() {
        return name;
    }

    public void getHelp(userHelping l) {
        l.add(null, "1 2   sequence              sequence number of an entry");
        l.add(null, "2 1,.   <num>               sequence number");
        l.add(null, "1 2,. reindex               reindex route map");
        l.add(null, "2 3,.   [num]               initial number to start with");
        l.add(null, "3 4,.     [num]             increment number");
        l.add(null, "1 2   action                set action to do");
        l.add(null, "2 .     drop                drop every packet");
        l.add(null, "2 .     transit             forward every packet");
        l.add(null, "2 .     police              police to rate");
        l.add(null, "2 .     pps                 police to packet rate");
        l.add(null, "2 .     shape               shape to rate");
        l.add(null, "2 .     bandwidth           bandwidth to rate");
        l.add(null, "2 .     priority            priority to rate");
        l.add(null, "1 2,. description           description of this policy map");
        l.add(null, "2 2,.   [text]              text describing this policy map");
        l.add(null, "1 2   rename                rename this policy map");
        l.add(null, "2 .     <str>               set new name");
        l.add(null, "1 2   match                 match values from packet");
        l.add(null, "2 3     length              match length value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     ttl                 match ttl value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     ethtyp              match ethertype value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     access-group        match by access list");
        l.add(null, "3 .       <name:acl>        name of access list");
        l.add(null, "2 3     precedence          match precedence value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     dscp                match dscp value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     tos                 match tos value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 .     frag                fragmented datagrams");
        l.add(null, "2 3     flag                tcp flags");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     sgt                 match sgt value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     cos                 match cos value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     exp                 match exp value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     qosgroup            match qos value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "2 3     flow                match flow value");
        l.add(null, "3 .       <num>             value to match");
        l.add(null, "3 .       all               any value");
        l.add(null, "1 2   set                   set values in packet");
        l.add(null, "2 3     precedence          set precedence value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "2 3     dscp                set dscp value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "2 3     tos                 set tos value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "2 3     ttl                 set ttl value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "2 3     sgt                 set sgt value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "2 3     cos                 set cos value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "2 3     exp                 set exp value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "2 3     qosgroup            set qosgroup value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "2 3     flow                set flow value");
        l.add(null, "3 .       leave             leave value unchanged");
        l.add(null, "3 4       set               set value to a specific value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       add               add value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "3 4       sub               substract value to current value");
        l.add(null, "4 .         <num>           value");
        l.add(null, "1 2   access-rate           set access rate of traffic");
        l.add(null, "2 .     <num>               bits per second");
        l.add(null, "1 2   exceed-rate           set exceed rate of traffic");
        l.add(null, "2 .     <num>               bits per second");
        l.add(null, "1 2   service-policy        apply policy map to this traffic");
        l.add(null, "2 .     <name:pm>           name of policy map");
        l.add(null, "1 2   queue-limit           specify queue limit");
        l.add(null, "2 .     <num>               number of packets");
        l.add(null, "1 2   time-interval         specify time interval");
        l.add(null, "2 .     <num>               milliseconds");
        l.add(null, "1 .   random-detect         set random drop on queue full");
        l.add(null, "1 .   log                   set logging on match");
    }

    public List<String> getShRun(int filter) {
        List<String> l = new ArrayList<String>();
        l.add("policy-map " + name);
        l.addAll(plcmap.dump(cmds.tabulator));
        l.add(cmds.tabulator + cmds.finish);
        l.add(cmds.comment);
        if ((filter & 1) == 0) {
            return l;
        }
        return userFilter.filterText(l, defaultF);
    }

    public synchronized void doCfgStr(cmds cmd) {
        String a = cmd.word();
        if (a.equals("sequence")) {
            seq = bits.str2num(cmd.word());
            a = cmd.word();
            if (a.length() < 1) {
                return;
            }
        }
        if (a.equals("action")) {
            tabListingEntry.actionType i = tabPlcmapN.string2type(cmd.word());
            if (i == null) {
                cmd.error("invalid action");
                return;
            }
            tabPlcmapN ntry = getCurr();
            ntry.action = i;
            return;
        }
        if (a.equals("description")) {
            tabPlcmapN ntry = getCurr();
            ntry.description = cmd.getRemaining();
            return;
        }
        if (a.equals("rename")) {
            a = cmd.word();
            cfgPlymp v = cfgAll.plmpFind(a, false);
            if (v != null) {
                cmd.error("already exists");
                return;
            }
            name = a;
            plcmap.listName = a;
            return;
        }
        if (a.equals("access-rate")) {
            tabPlcmapN ntry = getCurr();
            ntry.accessRate = bits.str2long(cmd.word()) / 8;
            return;
        }
        if (a.equals("exceed-rate")) {
            tabPlcmapN ntry = getCurr();
            ntry.exceedRate = bits.str2long(cmd.word()) / 8;
            return;
        }
        if (a.equals("queue-limit")) {
            tabPlcmapN ntry = getCurr();
            ntry.queues = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("time-interval")) {
            tabPlcmapN ntry = getCurr();
            ntry.interval = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("service-policy")) {
            cfgPlymp plc = cfgAll.plmpFind(cmd.word(), false);
            if (plc == null) {
                cmd.error("no such policy");
                return;
            }
            tabPlcmapN ntry = getCurr();
            ntry.child = plc.plcmap;
            return;
        }
        if (a.equals("random-detect")) {
            tabPlcmapN ntry = getCurr();
            ntry.randomDetect = true;
            return;
        }
        if (a.equals("log")) {
            tabPlcmapN ntry = getCurr();
            ntry.logMatch = true;
            return;
        }
        if (a.equals("match")) {
            a = cmd.word();
            tabPlcmapN ntry = getCurr();
            if (a.equals("length")) {
                if (ntry.lengthMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("ttl")) {
                if (ntry.ttlMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("ethtyp")) {
                if (ntry.ethtypMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("access-group")) {
                cfgAceslst acl = cfgAll.aclsFind(cmd.word(), false);
                if (acl == null) {
                    cmd.error("no such access list");
                    return;
                }
                ntry.aclMatch = acl.aceslst;
                return;
            }
            if (a.equals("precedence")) {
                if (ntry.precedenceMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("dscp")) {
                if (ntry.dscpMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("tos")) {
                if (ntry.tosMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("flag")) {
                if (ntry.flagMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("frag")) {
                ntry.fragMatch = true;
                return;
            }
            if (a.equals("sgt")) {
                if (ntry.sgtMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("cos")) {
                if (ntry.cosMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("exp")) {
                if (ntry.expMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("qosgroup")) {
                if (ntry.qosMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("flow")) {
                if (ntry.flowMatch.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            cmd.badCmd();
            return;
        }
        if (a.equals("set")) {
            a = cmd.word();
            tabPlcmapN ntry = getCurr();
            if (a.equals("precedence")) {
                if (ntry.precedenceSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("dscp")) {
                if (ntry.dscpSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("tos")) {
                if (ntry.tosSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("ttl")) {
                if (ntry.ttlSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("sgt")) {
                if (ntry.sgtSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("cos")) {
                if (ntry.cosSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("exp")) {
                if (ntry.expSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("qosgroup")) {
                if (ntry.qosSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            if (a.equals("flow")) {
                if (ntry.flowSet.fromString(cmd.getRemaining())) {
                    cmd.error("invalid action");
                    return;
                }
                return;
            }
            cmd.badCmd();
            return;
        }
        if (a.equals("reindex")) {
            int i = bits.str2num(cmd.word());
            plcmap.reindex(i, bits.str2num(cmd.word()));
            return;
        }
        if (!a.equals("no")) {
            cmd.badCmd();
            return;
        }
        a = cmd.word();
        if (a.equals("description")) {
            tabPlcmapN ntry = getCurr();
            ntry.description = "";
            return;
        }
        if (a.equals("access-rate")) {
            tabPlcmapN ntry = getCurr();
            ntry.accessRate = 0;
            return;
        }
        if (a.equals("exceed-rate")) {
            tabPlcmapN ntry = getCurr();
            ntry.exceedRate = 0;
            return;
        }
        if (a.equals("queue-limit")) {
            tabPlcmapN ntry = getCurr();
            ntry.queues = 0;
            return;
        }
        if (a.equals("time-interval")) {
            tabPlcmapN ntry = getCurr();
            ntry.interval = 0;
            return;
        }
        if (a.equals("service-policy")) {
            tabPlcmapN ntry = getCurr();
            ntry.child = null;
            return;
        }
        if (a.equals("random-detect")) {
            tabPlcmapN ntry = getCurr();
            ntry.randomDetect = false;
            return;
        }
        if (a.equals("log")) {
            tabPlcmapN ntry = getCurr();
            ntry.logMatch = false;
            return;
        }
        if (a.equals("match")) {
            a = cmd.word();
            tabPlcmapN ntry = getCurr();
            if (a.equals("length")) {
                ntry.lengthMatch.set2always();
                return;
            }
            if (a.equals("ttl")) {
                ntry.ttlMatch.set2always();
                return;
            }
            if (a.equals("ethtyp")) {
                ntry.ethtypMatch.set2always();
                return;
            }
            if (a.equals("access-group")) {
                ntry.aclMatch = null;
                return;
            }
            if (a.equals("precedence")) {
                ntry.precedenceMatch.set2always();
                return;
            }
            if (a.equals("dscp")) {
                ntry.dscpMatch.set2always();
                return;
            }
            if (a.equals("tos")) {
                ntry.tosMatch.set2always();
                return;
            }
            if (a.equals("flag")) {
                ntry.flagMatch.set2always();
                return;
            }
            if (a.equals("frag")) {
                ntry.fragMatch = false;
                return;
            }
            if (a.equals("sgt")) {
                ntry.sgtMatch.set2always();
                return;
            }
            if (a.equals("cos")) {
                ntry.cosMatch.set2always();
                return;
            }
            if (a.equals("exp")) {
                ntry.expMatch.set2always();
                return;
            }
            if (a.equals("qosgroup")) {
                ntry.qosMatch.set2always();
                return;
            }
            if (a.equals("flow")) {
                ntry.flowMatch.set2always();
                return;
            }
            cmd.badCmd();
            return;
        }
        if (a.equals("set")) {
            a = cmd.word();
            tabPlcmapN ntry = getCurr();
            if (a.equals("precedence")) {
                ntry.precedenceSet.set2unchange();
                return;
            }
            if (a.equals("dscp")) {
                ntry.dscpSet.set2unchange();
                return;
            }
            if (a.equals("tos")) {
                ntry.tosSet.set2unchange();
                return;
            }
            if (a.equals("ttl")) {
                ntry.ttlSet.set2unchange();
                return;
            }
            if (a.equals("sgt")) {
                ntry.sgtSet.set2unchange();
                return;
            }
            if (a.equals("cos")) {
                ntry.cosSet.set2unchange();
                return;
            }
            if (a.equals("exp")) {
                ntry.expSet.set2unchange();
                return;
            }
            if (a.equals("qosgroup")) {
                ntry.qosSet.set2unchange();
                return;
            }
            if (a.equals("flow")) {
                ntry.flowSet.set2unchange();
                return;
            }
            cmd.badCmd();
            return;
        }
        if (a.equals("sequence")) {
            tabPlcmapN ntry = new tabPlcmapN();
            ntry.sequence = bits.str2num(cmd.word());
            if (plcmap.del(ntry)) {
                cmd.error("invalid sequence");
                return;
            }
            return;
        }
        cmd.badCmd();
    }

}
