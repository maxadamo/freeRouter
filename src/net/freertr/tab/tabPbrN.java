package net.freertr.tab;

import java.util.ArrayList;
import java.util.List;
import net.freertr.addr.addrIP;
import net.freertr.addr.addrPrefix;
import net.freertr.cfg.cfgAceslst;
import net.freertr.cfg.cfgAll;
import net.freertr.cfg.cfgIfc;
import net.freertr.cfg.cfgVrf;
import net.freertr.ip.ipFwd;
import net.freertr.ip.ipFwdIface;
import net.freertr.pack.packHolder;
import net.freertr.util.bits;
import net.freertr.util.cmds;

/**
 * represents one pbr config
 *
 * @author matecsaba
 */
public class tabPbrN extends tabListingEntry<addrIP> {

    /**
     * create instance
     */
    public tabPbrN() {
    }

    /**
     * matcher access list
     */
    public tabListing<tabAceslstN<addrIP>, addrIP> matcher;

    /**
     * target vrf
     */
    public ipFwd setVrf;

    /**
     * target interface
     */
    public ipFwdIface setIfc;

    /**
     * target nexthop
     */
    public addrIP setHop;

    /**
     * target service path
     */
    public int setSp;

    /**
     * target service index
     */
    public int setSi;

    /**
     * convert string to address
     *
     * @param p protocol version
     * @param s string to convert
     * @return true if error happened
     */
    public boolean fromString(int p, String s) {
        cmds cmd = new cmds("", s);
        s = cmd.word();
        if (s.equals("sequence")) {
            sequence = bits.str2num(cmd.word());
            s = cmd.word();
        }
        cfgAceslst acl = cfgAll.aclsFind(s, false);
        if (acl == null) {
            return true;
        }
        matcher = acl.aceslst;
        cfgVrf vrf = cfgAll.vrfFind(cmd.word(), false);
        if (vrf == null) {
            return true;
        }
        if (p == 4) {
            setVrf = vrf.fwd4;
        } else {
            setVrf = vrf.fwd6;
        }
        for (;;) {
            s = cmd.word();
            if (s.length() < 1) {
                break;
            }
            if (s.equals("interface")) {
                cfgIfc ifc = cfgAll.ifcFind(cmd.word(), false);
                if (ifc == null) {
                    continue;
                }
                if (p == 4) {
                    setIfc = ifc.fwdIf4;
                } else {
                    setIfc = ifc.fwdIf6;
                }
                continue;
            }
            if (s.equals("nexthop")) {
                addrIP adr = new addrIP();
                if (adr.fromString(cmd.word())) {
                    continue;
                }
                setHop = adr;
                continue;
            }
            if (s.equals("nsh")) {
                setSp = bits.str2num(cmd.word());
                setSi = bits.str2num(cmd.word());
                continue;
            }
            return true;
        }
        return false;
    }

    public String toString() {
        String s = matcher.listName + " " + setVrf.cfgName;
        if (setIfc != null) {
            s += " interface " + setIfc;
        }
        if (setHop != null) {
            s += " nexthop " + setHop;
        }
        if ((setSp > 0) && (setSi > 0)) {
            s += " nsh " + setSp + " " + setSi;
        }
        return s;
    }

    public List<String> usrString(String beg) {
        List<String> l = new ArrayList<String>();
        l.add(beg + "sequence " + sequence + " " + this);
        return l;
    }

    public boolean matches(int afi, int asn, addrPrefix<addrIP> net) {
        return false;
    }

    public boolean matches(int afi, int asn, tabRouteEntry<addrIP> net) {
        return false;
    }

    public boolean matches(packHolder pck) {
        return matcher.matches(false, false, pck);
    }

    public void update(int afi, int asn, tabRouteEntry<addrIP> net) {
    }

}
