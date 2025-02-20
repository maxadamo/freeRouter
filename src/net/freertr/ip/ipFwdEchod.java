package net.freertr.ip;

import net.freertr.addr.addrIP;
import net.freertr.util.counter;

/**
 * stores one echo reply
 *
 * @author matecsaba
 */
public class ipFwdEchod {

    /**
     * create instance
     */
    public ipFwdEchod() {
    }

    /**
     * reporting router
     */
    public addrIP rtr;

    /**
     * reported error
     */
    public counter.reasons err;

    /**
     * reported label
     */
    public int lab;
    
    /**
     * received ttl
     */
    public int ttl;
    
    /**
     * received tos
     */
    public int tos;

    /**
     * time elapsed
     */
    public int tim;

}
