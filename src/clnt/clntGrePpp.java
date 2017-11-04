package clnt;

import addr.addrEmpty;
import addr.addrIP;
import addr.addrType;
import cfg.cfgIfc;
import cfg.cfgVrf;
import ifc.ifcDn;
import ifc.ifcNull;
import ifc.ifcUp;
import ip.ipFwd;
import ip.ipFwdIface;
import ip.ipFwdTab;
import pack.packHolder;
import pack.packPptp;
import prt.prtGre;
import user.userTerminal;
import util.bits;
import util.counter;
import util.logger;
import util.state;
import util.state.states;

/**
 * ppp over gre
 *
 * @author matecsaba
 */
public class clntGrePpp implements ifcDn, ifcUp, Runnable {

    /**
     * upper layer
     */
    public ifcUp upper = new ifcNull();

    /**
     * target of tunnel
     */
    public String target = null;

    /**
     * vrf of target
     */
    public cfgVrf vrf = null;

    /**
     * source interface
     */
    public cfgIfc srcIfc = null;

    /**
     * sending ttl value, -1 means maps out
     */
    public int sendingTTL = 255;

    /**
     * sending tos value, -1 means maps out
     */
    public int sendingTOS = -1;

    /**
     * vc id
     */
    public int vcid;

    /**
     * counter
     */
    public counter cntr = new counter();

    private boolean working = true;

    private ipFwd fwdCor;

    private addrIP fwdTrg;

    private ipFwdIface fwdIfc;

    private prtGre gre;

    public String toString() {
        return "greppp " + fwdTrg;
    }

    public addrType getHwAddr() {
        return new addrEmpty();
    }

    public void setFilter(boolean promisc) {
    }

    public state.states getState() {
        return state.states.up;
    }

    public void closeDn() {
        clearState();
    }

    public void flapped() {
        clearState();
    }

    public void setUpper(ifcUp server) {
        upper = server;
        upper.setParent(this);
    }

    public counter getCounter() {
        return cntr;
    }

    public int getMTUsize() {
        return 1500;
    }

    public long getBandwidth() {
        return 8000000;
    }

    public void sendPack(packHolder pck) {
        cntr.tx(pck);
        if (gre == null) {
            return;
        }
        pck.msbPutW(0, packPptp.ethtyp);
        pck.putSkip(2);
        pck.merge2beg();
        pck.putDefaults();
        gre.sendPack(pck);
    }

    /**
     * start connection
     */
    public void workStart() {
        new Thread(this).start();
    }

    /**
     * stop connection
     */
    public void workStop() {
        working = false;
        clearState();
    }

    public void run() {
        for (;;) {
            if (!working) {
                break;
            }
            try {
                clearState();
                workDoer();
            } catch (Exception e) {
                logger.traceback(e);
            }
            clearState();
            bits.sleep(1000);
        }
    }

    private void workDoer() {
        fwdTrg = userTerminal.justResolv(target, 0);
        if (fwdTrg == null) {
            return;
        }
        fwdCor = vrf.getFwd(fwdTrg);
        fwdIfc = null;
        if (srcIfc != null) {
            fwdIfc = srcIfc.getFwdIfc(fwdTrg);
        }
        if (fwdIfc == null) {
            fwdIfc = ipFwdTab.findSendingIface(fwdCor, fwdTrg);
        }
        if (fwdIfc == null) {
            return;
        }
        gre = new prtGre(fwdCor);
        gre.setEndpoints(fwdIfc, fwdTrg);
        gre.tunnelKey = vcid;
        gre.sendingTOS = sendingTOS;
        gre.sendingTTL = sendingTTL;
        gre.setUpper(this);
        for (;;) {
            bits.sleep(1000);
            if (!working) {
                break;
            }
        }
    }

    private void clearState() {
        if (gre == null) {
            return;
        }
        gre.closeDn();
        gre = null;
    }

    public void recvPack(packHolder pck) {
        cntr.rx(pck);
        if (pck.msbGetW(0) != packPptp.ethtyp) {
            return;
        }
        pck.getSkip(2);
        upper.recvPack(pck);
    }

    public void setParent(ifcDn parent) {
    }

    public void setState(states stat) {
    }

    public void closeUp() {
    }

}
