package net.freertr.clnt;

import java.util.List;
import net.freertr.cfg.cfgAll;
import net.freertr.cfg.cfgDial;
import net.freertr.pack.packRtp;
import net.freertr.pack.packSip;
import net.freertr.pipe.pipeLine;
import net.freertr.pipe.pipeSide;
import net.freertr.snd.sndCodec;
import net.freertr.snd.sndScript;

/**
 * voice script client
 *
 * @author matecsaba
 */
public class clntVoice {

    /**
     * create instance
     */
    public clntVoice() {
    }

    /**
     * called number
     */
    public String called = null;

    /**
     * calling number
     */
    public String calling = null;

    private cfgDial pipePer;

    private String pipeRcd;

    private packRtp pipeRtp;

    private pipeSide pipeUsr;

    private pipeSide pipeScr;

    private sndScript scr;

    /**
     * get pipe side
     *
     * @return pipe side
     */
    public pipeSide getPipe() {
        return pipeUsr;
    }

    /**
     * stop call
     */
    public void callStop() {
        try {
            pipePer.stopCall(pipeRcd);
        } catch (Exception e) {
        }
        try {
            pipeRtp.setClose();
        } catch (Exception e) {
        }
        try {
            pipeUsr.setClose();
        } catch (Exception e) {
        }
        try {
            pipeScr.setClose();
        } catch (Exception e) {
        }
    }

    /**
     * start call
     *
     * @return false on success, true on error
     */
    public boolean callStart() {
        pipePer = cfgAll.dialFind(calling, called, null);
        if (pipePer == null) {
            return true;
        }
        pipeRcd = pipePer.makeCall(calling, called);
        if (pipeRcd == null) {
            return true;
        }
        pipeRtp = pipePer.getCall(pipeRcd);
        sndCodec codec = pipePer.getCodec();
        pipeLine pip = new pipeLine(32768, false);
        pipeUsr = pip.getSide();
        pipeScr = pip.getSide();
        pipeScr.setTime(120000);
        pipeScr.lineTx = pipeSide.modTyp.modeCRLF;
        pipeScr.lineRx = pipeSide.modTyp.modeCRtryLF;
        scr = new sndScript(pipeScr, codec, pipeRtp, packSip.removeTag(calling), packSip.removeTag(called));
        return false;
    }

    /**
     * set prompt
     *
     * @param need needed
     */
    public void setPrompt(boolean need) {
        scr.prompt = need;
    }

    /**
     * send message
     *
     * @param lst text
     * @return false on success, true on error
     */
    public boolean sendMessage(List<String> lst) {
        cfgDial per = cfgAll.dialFind(calling, called, null);
        if (per == null) {
            return true;
        }
        return per.sendMsg(calling, called, lst);
    }

}
