package net.freertr.clnt;

import net.freertr.addr.addrIP;
import net.freertr.addr.addrMac;
import net.freertr.addr.addrType;
import net.freertr.cfg.cfgIfc;
import net.freertr.cfg.cfgVrf;
import net.freertr.ifc.ifcDn;
import net.freertr.ifc.ifcNull;
import net.freertr.ifc.ifcUp;
import net.freertr.ip.ipFwd;
import net.freertr.ip.ipFwdIface;
import net.freertr.ip.ipFwdTab;
import net.freertr.ip.ipPrt;
import net.freertr.pack.packHolder;
import net.freertr.user.userTerminal;
import net.freertr.util.bits;
import net.freertr.util.counter;
import net.freertr.util.logger;
import net.freertr.util.state;

/**
 * universal transport interface
 *
 * @author matecsaba
 */
public class clntUti implements Runnable, ipPrt, ifcDn {

    /**
     * create instance
     */
    public clntUti() {
    }

    /**
     * protocol number
     */
    public static final int prot = 120;

    /**
     * header size
     */
    public static final int size = 16;

    /**
     * upper layer
     */
    public ifcUp upper = new ifcNull();

    /**
     * preferred ip protocol version
     */
    public int prefer = 0;

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
     * sending tos value, -1 means maps out
     */
    public int sendingFLW = -1;

    /**
     * tunnel key
     */
    public int tunKey;

    /**
     * counter
     */
    public counter cntr = new counter();

    private ipFwd fwdCor;

    private addrIP fwdTrg;

    private ipFwdIface fwdIfc;

    private boolean working = true;

    public String toString() {
        return "uti to " + fwdTrg;
    }

    /**
     * get hw address
     *
     * @return hw address
     */
    public addrType getHwAddr() {
        return addrMac.getRandom();
    }

    /**
     * set filter
     *
     * @param promisc promiscous mode
     */
    public void setFilter(boolean promisc) {
    }

    /**
     * get state
     *
     * @return state
     */
    public state.states getState() {
        return state.states.up;
    }

    /**
     * close interface
     */
    public void closeDn() {
        clearState();
    }

    /**
     * flap interface
     */
    public void flapped() {
        clearState();
    }

    /**
     * set upper layer
     *
     * @param server upper layer
     */
    public void setUpper(ifcUp server) {
        upper = server;
        upper.setParent(this);
    }

    /**
     * get counter
     *
     * @return counter
     */
    public counter getCounter() {
        return cntr;
    }

    /**
     * get mtu size
     *
     * @return mtu size
     */
    public int getMTUsize() {
        return 1400;
    }

    /**
     * get bandwidth
     *
     * @return bandwidth
     */
    public long getBandwidth() {
        return 10000000;
    }

    /**
     * send packet
     *
     * @param pck packet
     */
    public void sendPack(packHolder pck) {
        pck.merge2beg();
        if (fwdIfc == null) {
            return;
        }
        pck.msbPutD(0, tunKey & 0xfff); // session id
        pck.msbPutD(4, tunKey); // high key
        pck.msbPutD(8, tunKey); // low key
        pck.putSkip(size);
        pck.merge2beg();
        cntr.tx(pck);
        pck.putDefaults();
        if (sendingTTL >= 0) {
            pck.IPttl = sendingTTL;
        }
        if (sendingTOS >= 0) {
            pck.IPtos = sendingTOS;
        }
        if (sendingFLW >= 0) {
            pck.IPid = sendingFLW;
        }
        pck.IPprt = prot;
        pck.IPsrc.setAddr(fwdIfc.addr);
        pck.IPtrg.setAddr(fwdTrg);
        fwdCor.protoPack(fwdIfc, null, pck);
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
        fwdTrg = userTerminal.justResolv(target, prefer);
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
        fwdCor.protoAdd(this, fwdIfc, fwdTrg);
        for (;;) {
            if (!working) {
                break;
            }
            bits.sleep(1000);
        }
    }

    private void clearState() {
        if (fwdIfc != null) {
            fwdCor.protoDel(this, fwdIfc, fwdTrg);
            fwdIfc = null;
        }
    }

    /**
     * get protocol number
     *
     * @return number
     */
    public int getProtoNum() {
        return prot;
    }

    /**
     * closed interface
     *
     * @param iface interface
     */
    public void closeUp(ipFwdIface iface) {
    }

    /**
     * set state
     *
     * @param iface interface
     * @param stat state
     */
    public void setState(ipFwdIface iface, state.states stat) {
    }

    /**
     * received packet
     *
     * @param rxIfc interface
     * @param pck packet
     */
    public void recvPack(ipFwdIface rxIfc, packHolder pck) {
        if (pck.msbGetD(0) != (tunKey & 0xfff)) { // session
            cntr.drop(pck, counter.reasons.badHdr);
            return;
        }
        if (pck.msbGetD(4) != tunKey) { // high key
            cntr.drop(pck, counter.reasons.badHdr);
            return;
        }
        if (pck.msbGetD(8) != tunKey) { // low key
            cntr.drop(pck, counter.reasons.badHdr);
            return;
        }
        pck.getSkip(size);
        cntr.rx(pck);
        upper.recvPack(pck);
    }

    /**
     * alert packet
     *
     * @param rxIfc interface
     * @param pck packet
     * @return false on success, true on error
     */
    public boolean alertPack(ipFwdIface rxIfc, packHolder pck) {
        return true;
    }

    /**
     * error packet
     *
     * @param err error code
     * @param rtr address
     * @param rxIfc interface
     * @param pck packet
     */
    public void errorPack(counter.reasons err, addrIP rtr, ipFwdIface rxIfc, packHolder pck) {
    }

}
