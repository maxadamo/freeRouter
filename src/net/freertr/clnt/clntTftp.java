package net.freertr.clnt;

import java.io.File;
import java.io.RandomAccessFile;
import net.freertr.addr.addrIP;
import net.freertr.cfg.cfgAll;
import net.freertr.ip.ipFwd;
import net.freertr.ip.ipFwdIface;
import net.freertr.ip.ipFwdTab;
import net.freertr.pack.packHolder;
import net.freertr.pack.packTftp;
import net.freertr.pipe.pipeDiscard;
import net.freertr.pipe.pipeLine;
import net.freertr.pipe.pipeProgress;
import net.freertr.pipe.pipeSide;
import net.freertr.prt.prtGenConn;
import net.freertr.prt.prtServS;
import net.freertr.prt.prtUdp;
import net.freertr.user.userTerminal;
import net.freertr.util.debugger;
import net.freertr.util.logger;
import net.freertr.util.uniResLoc;

/**
 * trivial file transfer protocol (rfc1350) client
 *
 * @author matecsaba
 */
public class clntTftp implements prtServS {

    private final int locprt = 16969;

    private pipeSide pipe;

    private addrIP adr;

    private clntProxy prx;

    private ipFwd fwd;

    private prtUdp udp;

    private ipFwdIface ifc;

    private final pipeSide conp;

    private final pipeProgress cons;

    private RandomAccessFile fr;

    /**
     * create new client
     *
     * @param console console to use
     */
    public clntTftp(pipeSide console) {
        conp = pipeDiscard.needAny(console);
        cons = new pipeProgress(conp);
    }

    /**
     * clean up state
     */
    public void cleanUp() {
        try {
            closeConn();
        } catch (Exception e) {
        }
        try {
            fr.close();
        } catch (Exception e) {
        }
    }

    /**
     * download one file
     *
     * @param src source
     * @param trg target
     * @return result code
     */
    public boolean download(uniResLoc src, File trg) {
        if (openConn(src.server)) {
            return true;
        }
        try {
            trg.createNewFile();
        } catch (Exception e) {
            closeConn();
            return true;
        }
        try {
            fr = new RandomAccessFile(trg, "rw");
        } catch (Exception e) {
            closeConn();
            return true;
        }
        cons.debugStat("receiving " + cons.getMax() + " bytes");
        packTftp pckTft = new packTftp();
        pckTft.typ = packTftp.msgRead;
        pckTft.nam = src.toPathName();
        pckTft.mod = "octet";
        pckTft = xchgPack(pckTft, packTftp.msgData);
        if (pckTft == null) {
            closeConn();
            return true;
        }
        long blk = 0;
        long siz = 0;
        for (;;) {
            if (((pckTft.blk - 1) & 0xffff) == (blk & 0xffff)) {
                try {
                    fr.write(pckTft.dat);
                } catch (Exception e) {
                    closeConn();
                    return true;
                }
                blk++;
                siz += pckTft.dat.length;
            }
            if (pckTft.dat.length < packTftp.size) {
                break;
            }
            pckTft = new packTftp();
            pckTft.blk = (int) blk;
            pckTft.typ = packTftp.msgAck;
            pckTft = xchgPack(pckTft, packTftp.msgData);
            if (pckTft == null) {
                closeConn();
                return true;
            }
        }
        cons.debugRes(siz + " bytes done");
        closeConn();
        return false;
    }

    /**
     * upload one file
     *
     * @param trg source
     * @param src target
     * @return result code
     */
    public boolean upload(uniResLoc trg, File src) {
        if (openConn(trg.server)) {
            return true;
        }
        long siz = 0;
        try {
            fr = new RandomAccessFile(src, "r");
            siz = fr.length();
        } catch (Exception e) {
            closeConn();
            return true;
        }
        cons.setMax(siz);
        cons.debugStat("sending " + cons.getMax() + " bytes");
        packTftp pckTft = new packTftp();
        pckTft.typ = packTftp.msgWrite;
        pckTft.nam = trg.toPathName();
        pckTft.mod = "octet";
        pckTft = xchgPack(pckTft, packTftp.msgAck);
        if (pckTft == null) {
            closeConn();
            return true;
        }
        long blk = 0;
        for (;;) {
            long pos = blk * packTftp.size;
            long len = siz - pos;
            if (len > packTftp.size) {
                len = packTftp.size;
            }
            if (len < 0) {
                len = 0;
            }
            pckTft = new packTftp();
            pckTft.dat = new byte[(int) len];
            try {
                fr.seek(pos);
                fr.read(pckTft.dat);
            } catch (Exception e) {
                closeConn();
                return true;
            }
            pckTft.blk = (int) (blk + 1);
            pckTft.typ = packTftp.msgData;
            cons.setCurr(pos);
            pckTft = xchgPack(pckTft, packTftp.msgAck);
            if (pckTft == null) {
                closeConn();
                return true;
            }
            if (((pckTft.blk - 1) & 0xffff) == (blk & 0xffff)) {
                blk++;
            }
            if (len != packTftp.size) {
                break;
            }
        }
        cons.debugRes(siz + " bytes done");
        closeConn();
        return false;
    }

    private boolean openConn(String trg) {
        adr = new userTerminal(conp).resolveAddr(trg, 0);
        if (adr == null) {
            return true;
        }
        prx = cfgAll.getClntPrx(cfgAll.tftpProxy);
        fwd = prx.vrf.getFwd(adr);
        udp = prx.vrf.getUdp(adr);
        ifc = null;
        if (prx.srcIfc != null) {
            ifc = prx.srcIfc.getFwdIfc(adr);
        }
        if (ifc == null) {
            ifc = ipFwdTab.findSendingIface(fwd, adr);
        }
        if (ifc == null) {
            return true;
        }
        udp.streamListen(this, new pipeLine(65536, true), ifc, locprt, adr, 0, "tftpc", null, -1);
        pipe = udp.streamConnect(new pipeLine(65536, true), ifc, locprt, adr, packTftp.port, "tftpc", null, -1);
        return pipe == null;
    }

    private void closeConn() {
        if (pipe != null) {
            pipe.setClose();
        }
        if (ifc != null) {
            udp.listenStop(ifc, locprt, adr, 0);
        }
    }

    private packTftp xchgPack(packTftp pckTx, int ned) {
        for (;;) {
            if (debugger.clntTftpTraf) {
                logger.debug("tx " + pckTx.dump());
            }
            packHolder pckBin = pckTx.createPacket();
            pckBin.merge2beg();
            pckBin.pipeSend(pipe, 0, pckBin.dataSize(), 2);
            for (int i = 0; i < 8; i++) {
                if (pipe.isClosed() != 0) {
                    return null;
                }
                pipe.notif.sleep(1000);
                pckBin = pipe.readPacket(false);
                if (pckBin == null) {
                    continue;
                }
                packTftp pckRx = new packTftp();
                if (pckRx.parsePacket(pckBin)) {
                    return null;
                }
                if (debugger.clntTftpTraf) {
                    logger.debug("rx " + pckRx.dump());
                }
                if (pckRx.typ != ned) {
                    return null;
                }
                return pckRx;
            }
        }
    }

    /**
     * closed interface
     *
     * @param ifc interface
     */
    public void closedInterface(ipFwdIface ifc) {
    }

    /**
     * accept connection
     *
     * @param pip pipeline
     * @param id connection
     * @return false on success, true on error
     */
    public boolean streamAccept(pipeSide pip, prtGenConn id) {
        if (pipe != null) {
            pipe.setClose();
        }
        pipe = pip;
        return false;
    }

    /**
     * get block mode
     *
     * @return mode
     */
    public boolean streamForceBlock() {
        return true;
    }

}
