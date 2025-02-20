package net.freertr.prt;

import net.freertr.ip.ipFwdIface;
import net.freertr.pipe.pipeSide;

/**
 * stream servers have to use this to able to work with protocols
 *
 * @author matecsaba
 */
public interface prtServS {

    /**
     * notified that listening interface closed
     *
     * @param ifc iface closing
     */
    public void closedInterface(ipFwdIface ifc);

    /**
     * called by protocol handler when connection ready
     *
     * @param pipe pipeline that can used to communicate with remote
     * @param id connection handler
     * @return false to accept connection, true to refuse it
     */
    public boolean streamAccept(pipeSide pipe, prtGenConn id);

    /**
     * get blocking mode is cloned or not
     *
     * @return true means cloned, false means dynamic
     */
    public boolean streamForceBlock();

}
