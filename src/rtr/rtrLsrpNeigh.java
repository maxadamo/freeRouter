package rtr;

import addr.addrIP;
import addr.addrIPv4;
import auth.authConstant;
import cfg.cfgAll;
import cry.cryBase64;
import cry.cryHashSha512;
import ip.ipMpls;
import java.util.Comparator;
import pipe.pipeLine;
import pipe.pipeSide;
import prt.prtAccept;
import sec.secClient;
import sec.secServer;
import serv.servGeneric;
import tab.tabGen;
import tab.tabLabel;
import tab.tabLabelNtry;
import util.bits;
import util.cmds;
import util.debugger;
import util.logger;
import util.notifier;

/**
 * lsrp neighbor
 *
 * @author matecsaba
 */
public class rtrLsrpNeigh implements Runnable, rtrBfdClnt, Comparator<rtrLsrpNeigh> {

    /**
     * transport address of peer
     */
    public addrIP peer;

    /**
     * hostname of peer
     */
    public String name;

    /**
     * router id of peer
     */
    public addrIPv4 rtrId;

    /**
     * time last heard
     */
    public long lastHeard;

    /**
     * notified on route change
     */
    protected notifier notif = new notifier();

    /**
     * protocol handler
     */
    protected final rtrLsrp lower;

    /**
     * interface handler
     */
    protected final rtrLsrpIface iface;

    /**
     * uptime
     */
    protected long upTime;

    /**
     * segment routing label
     */
    protected tabLabelNtry segrouLab;

    /**
     * advertised data
     */
    protected tabGen<rtrLsrpData> advert;

    private pipeSide conn;

    private boolean need2run;

    /**
     * start one peer
     *
     * @param parent protocol handler
     * @param ifc interface handler
     * @param peer transport address
     */
    public rtrLsrpNeigh(rtrLsrp parent, rtrLsrpIface ifc, addrIPv4 peer) {
        lower = parent;
        iface = ifc;
        rtrId = peer.copyBytes();
        lastHeard = bits.getTime();
        advert = new tabGen<rtrLsrpData>();
    }

    public int compare(rtrLsrpNeigh o1, rtrLsrpNeigh o2) {
        return o1.rtrId.compare(o1.rtrId, o2.rtrId);
    }

    public String toString() {
        return "lsrp with " + peer;
    }

    public void bfdPeerDown() {
        stopWork();
    }

    /**
     * start work
     */
    public void startWork() {
        if (debugger.rtrLsrpEvnt) {
            logger.debug("starting peer " + rtrId + " (" + peer + ")");
        }
        lastHeard = bits.getTime();
        need2run = true;
        upTime = bits.getTime();
        new Thread(this).start();
    }

    /**
     * stop work
     */
    public void stopWork() {
        if (debugger.rtrLsrpEvnt) {
            logger.debug("stopping peer " + rtrId + " (" + peer + ")");
        }
        boolean oldrun = need2run;
        need2run = false;
        if (conn != null) {
            conn.setClose();
        }
        if (oldrun) {
            iface.neighs.del(this);
        }
        tabLabel.release(segrouLab, 14);
        lower.todo = 0;
        lower.notif.wakeup();
        iface.iface.bfdDel(peer, this);
        notif.wakeup();
    }

    public void run() {
        try {
            for (;;) {
                if (!need2run) {
                    break;
                }
                doRun();
            }
        } catch (Exception e) {
            logger.traceback(e);
        }
        stopWork();
    }

    /**
     * receive line
     *
     * @return commands
     */
    protected cmds recvLn() {
        if (conn.isClosed() != 0) {
            return null;
        }
        String a = conn.lineGet(0x11);
        if (debugger.rtrLsrpTraf) {
            logger.debug(peer + " rx " + a);
        }
        return new cmds("rx", a);
    }

    /**
     * send one line
     *
     * @param s line to send
     */
    protected void sendLn(String s) {
        if (debugger.rtrLsrpTraf) {
            logger.debug(peer + " tx " + s);
        }
        conn.linePut(s);
    }

    private void doRun() {
        if (conn != null) {
            conn.setClose();
        }
        advert.clear();
        if (peer.compare(peer, iface.iface.addr) > 0) {
            if (debugger.rtrLsrpEvnt) {
                logger.debug("accepting " + peer);
            }
            prtAccept ac = new prtAccept(lower.tcpCore, new pipeLine(65536, false), iface.iface, rtrLsrp.port, peer, 0, 0, "lsrp", null, -1);
            ac.wait4conn(iface.deadTimer);
            conn = ac.getConn(true);
        } else {
            if (debugger.rtrLsrpEvnt) {
                logger.debug("connecting " + peer);
            }
            bits.sleep(1000);
            conn = lower.tcpCore.streamConnect(new pipeLine(65536, false), iface.iface, 0, peer, rtrLsrp.port, "lsrp", null, -1);
        }
        if (conn == null) {
            return;
        }
        conn.timeout = iface.deadTimer * 3;
        conn.lineRx = pipeSide.modTyp.modeCRtryLF;
        conn.lineTx = pipeSide.modTyp.modeCRLF;
        if (conn.wait4ready(0)) {
            return;
        }
        if (!need2run) {
            sendLn("error notNeeded");
            return;
        }
        if (iface.encryptionMethod > 0) {
            sendLn("startEncrypt " + servGeneric.proto2string(iface.encryptionMethod));
            cmds cmd = recvLn();
            if (cmd == null) {
                return;
            }
            String a = cmd.word();
            if (!a.equals("startEncrypt")) {
                sendLn("error startEncryptRequired");
                return;
            }
            if (peer.compare(peer, iface.iface.addr) > 0) {
                if (debugger.rtrLsrpEvnt) {
                    logger.debug("secure client " + peer);
                }
                conn = secClient.openSec(conn, iface.encryptionMethod, "", "");
            } else {
                if (debugger.rtrLsrpEvnt) {
                    logger.debug("secure server " + peer);
                }
                conn = secServer.openSec(conn, iface.encryptionMethod, new pipeLine(65536, false), new authConstant(true), iface.keyRsa.key, iface.keyDsa.key, iface.keyEcDsa.key, iface.certRsa.cert, iface.certDsa.cert, iface.certEcDsa.cert);
            }
            if (conn == null) {
                return;
            }
            conn.timeout = iface.deadTimer * 3;
            conn.lineRx = pipeSide.modTyp.modeCRtryLF;
            conn.lineTx = pipeSide.modTyp.modeCRLF;
            conn.wait4ready(0);
        }
        if (!need2run) {
            sendLn("error notNeeded");
            return;
        }
        sendLn("open rtrid=" + lower.routerID + " name=" + cfgAll.hostName);
        cmds cmd = recvLn();
        if (cmd == null) {
            return;
        }
        String a = cmd.word();
        if (!a.equals("open")) {
            sendLn("error openRequired");
            return;
        }
        name = "?" + rtrId;
        for (;;) {
            a = cmd.word();
            if (a.length() < 1) {
                break;
            }
            if (a.startsWith("rtrid")) {
                continue;
            }
            if (a.startsWith("name")) {
                name = a.substring(5, a.length());
                continue;
            }
        }
        if (iface.authentication != null) {
            byte[] authenticate = new byte[128];
            for (int i = 0; i < authenticate.length; i++) {
                authenticate[i] = (byte) bits.randomB();
            }
            sendLn("password-request " + cryBase64.encodeBytes(authenticate));
            cmd = recvLn();
            if (cmd == null) {
                return;
            }
            a = cmd.word();
            if (!a.equals("password-request")) {
                sendLn("error passReqRequired");
                return;
            }
            a = cmd.word();
            byte[] chl = cryBase64.decodeBytes(a);
            cryHashSha512 hsh = new cryHashSha512();
            hsh.init();
            hsh.update(chl);
            hsh.update(iface.authentication.getBytes());
            hsh.update(chl);
            chl = hsh.finish();
            sendLn("password-reply " + cryBase64.encodeBytes(chl));
            cmd = recvLn();
            if (cmd == null) {
                return;
            }
            a = cmd.word();
            if (!a.equals("password-reply")) {
                sendLn("error passRepRequired");
                return;
            }
            hsh.init();
            hsh.update(authenticate);
            hsh.update(iface.authentication.getBytes());
            hsh.update(authenticate);
            chl = hsh.finish();
            a = cryBase64.encodeBytes(chl);
            if (!a.equals(cmd.word())) {
                sendLn("error badPassword");
                return;
            }
        }
        advert.clear();
        logger.warn("neighbor " + name + " (" + peer + ") up");
        new rtrLsrpNeighRcvr(this).startWork();
        if (lower.segrouLab != null) {
            segrouLab = tabLabel.allocate(14);
            segrouLab.setFwdMpls(14, lower.fwdCore, iface.iface, peer, tabLabel.int2labels(ipMpls.labelImp));
        }
        lower.todo = 0;
        lower.notif.wakeup();
        if (iface.bfdTrigger) {
            iface.iface.bfdAdd(peer, this, "lsrp");
        }
        for (;;) {
            if (!need2run) {
                break;
            }
            if (notif.missedWakes() < 1) {
                notif.sleep(iface.helloTimer);
                sendLn("keepalive " + iface.deadTimer);
            }
            if (conn.isClosed() != 0) {
                break;
            }
            if (conn.ready2tx() > 1024) {
                doAdvert();
            }
        }
        stopWork();
        logger.error("neighbor " + name + " (" + peer + ") down");
    }

    private void doAdvert() {
        for (int i = 0; i < advert.size(); i++) {
            rtrLsrpData ntry = advert.get(i);
            if (ntry == null) {
                continue;
            }
            if (lower.database.find(ntry) == null) {
                advert.del(ntry);
                continue;
            }
        }
        for (int i = 0; i < lower.database.size(); i++) {
            rtrLsrpData ntry = lower.database.get(i);
            if (ntry == null) {
                continue;
            }
            if (!ntry.differs(advert.find(ntry))) {
                continue;
            }
            if (conn.ready2tx() < 1024) {
                return;
            }
            sendLn("update " + ntry.dump(rtrLsrpData.dmpFull));
            advert.put(ntry.copyHead());
        }
    }

}

class rtrLsrpNeighRcvr implements Runnable {

    private final rtrLsrpNeigh lower;

    public rtrLsrpNeighRcvr(rtrLsrpNeigh parent) {
        lower = parent;
    }

    public void startWork() {
        new Thread(this).start();
    }

    public void run() {
        try {
            doRun();
        } catch (Exception e) {
            logger.traceback(e);
        }
        lower.stopWork();
    }

    private void doRun() {
        for (;;) {
            cmds cmd = lower.recvLn();
            if (cmd == null) {
                return;
            }
            String a = cmd.word();
            if (a.length() < 1) {
                continue;
            }
            lower.lastHeard = bits.getTime();
            if (a.equals("error")) {
                lower.stopWork();
                continue;
            }
            if (a.equals("warning")) {
                continue;
            }
            if (a.equals("discard")) {
                continue;
            }
            if (a.equals("echoed")) {
                continue;
            }
            if (a.equals("echo")) {
                lower.sendLn("echoed " + cmd.getRemaining());
                continue;
            }
            if (a.equals("close")) {
                lower.stopWork();
                continue;
            }
            if (a.equals("resend")) {
                lower.advert.clear();
                continue;
            }
            if (a.equals("keepalive")) {
                lower.lastHeard += bits.str2num(cmd.word());
                continue;
            }
            if (a.equals("update")) {
                rtrLsrpData ntry = new rtrLsrpData();
                if (ntry.fromString(cmd)) {
                    continue;
                }
                if (ntry.differs(lower.advert.find(ntry))) {
                    lower.advert.put(ntry.copyHead());
                }
                if (ntry.better(lower.lower.database.find(ntry))) {
                    lower.lower.database.put(ntry);
                    lower.lower.todo = 0;
                    lower.lower.notif.wakeup();
                }
                continue;
            }
            lower.sendLn("error badCommand " + a + " " + cmd.getRemaining());
        }
    }

}
