package net.freertr.cfg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.freertr.auth.authLocal;
import net.freertr.clnt.clntTrack;
import net.freertr.serv.servGeneric;
import net.freertr.tab.tabGen;
import net.freertr.user.userFilter;
import net.freertr.user.userHelping;
import net.freertr.util.bits;
import net.freertr.util.cmds;

/**
 * tracker configuration
 *
 * @author matecsaba
 */
public class cfgTrack implements Comparator<cfgTrack>, cfgGeneric {

    /**
     * create instance
     */
    public cfgTrack() {
    }

    /**
     * name of tracker
     */
    public String name;

    /**
     * description
     */
    public String description;

    /**
     * worker
     */
    public final clntTrack worker = new clntTrack();

    /**
     * defaults text
     */
    public final static String[] defaultL = {
        "tracker .*! no description",
        "tracker .*! force normal",
        "tracker .*! no script",
        "tracker .*! no hidden",
        "tracker .*! no target",
        "tracker .*! no wake-vrf",
        "tracker .*! no exec-up",
        "tracker .*! no exec-down",
        "tracker .*! no chat-script",
        "tracker .*! no security",
        "tracker .*! no vrf",
        "tracker .*! no source",
        "tracker .*! no log",
        "tracker .*! random-interval 0",
        "tracker .*! random-initial 0",
        "tracker .*! interval 0",
        "tracker .*! timeout 0",
        "tracker .*! tos 0",
        "tracker .*! flow 0",
        "tracker .*! ttl 255",
        "tracker .*! size 80",
        "tracker .*! delay-up 0",
        "tracker .*! delay-down 0"
    };

    /**
     * defaults filter
     */
    public static tabGen<userFilter> defaultF;

    public int compare(cfgTrack o1, cfgTrack o2) {
        return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
    }

    /**
     * get prompt
     *
     * @return prompt
     */
    public String getPrompt() {
        return "trck";
    }

    public String toString() {
        return name;
    }

    /**
     * get help text
     *
     * @param l help text
     */
    public void getHelp(userHelping l) {
        l.add(null, "1  3,. description                   specify description");
        l.add(null, "3  3,.   <str>                       text");
        l.add(null, "1  2      mode                       specify mode of runs");
        l.add(null, "2  .        icmp                     icmp echo request");
        l.add(null, "2  .        nrpe                     nrpe remote check");
        l.add(null, "2  .        other                    other tracker");
        l.add(null, "2  .        tcp                      tcp connection");
        l.add(null, "2  .        bfd                      bidirectional forwarding detection");
        l.add(null, "2  .        interface                interface state");
        l.add(null, "2  .        route                    any route table entry for address");
        l.add(null, "2  .        prefix                   exact route table entry for prefix");
        l.add(null, "2  .        script                   tcl script");
        l.add(null, "1  2,.    script                     modify result with script");
        l.add(null, "2  2,.      <str>                    script");
        l.add(null, "1  2      force                      specify result of runs");
        l.add(null, "2  .        up                       always up");
        l.add(null, "2  .        down                     always down");
        l.add(null, "2  .        negate                   negate result");
        l.add(null, "2  .        normal                   leave result");
        l.add(null, "1  2      target                     specify address of test");
        l.add(null, "2  2,.      <addr>                   address of target");
        l.add(null, "1  2      vrf                        specify vrf of test");
        l.add(null, "2  .        <name:vrf>               vrf to use");
        l.add(null, "1  2      security                   select security protocol");
        l.add(null, "2  .        ssh                      use secure shell");
        l.add(null, "2  .        tls                      use transport layer security");
        l.add(null, "2  .        dtls                     use datagram transport layer security");
        l.add(null, "2  .        telnet                   use telnet protocol");
        l.add(null, "1  2      chat-script                specify script to use");
        l.add(null, "2  .        <name:cht>               chatter to use");
        l.add(null, "1  2      source                     specify source of test");
        l.add(null, "2  .        <name:ifc>               interface to use");
        l.add(null, "1  2      random-interval            specify random time between runs");
        l.add(null, "2  .        <num>                    milliseconds between runs");
        l.add(null, "1  2      random-initial             specify random time before run");
        l.add(null, "2  .        <num>                    milliseconds between runs");
        l.add(null, "1  2      interval                   specify time between runs");
        l.add(null, "2  .        <num>                    milliseconds between runs");
        l.add(null, "1  2      timeout                    specify timeout value");
        l.add(null, "2  .        <num>                    timeout in milliseconds");
        l.add(null, "1  2      tos                        specify tos");
        l.add(null, "2  .        <num>                    value");
        l.add(null, "1  2      ttl                        specify ttl");
        l.add(null, "2  .        <num>                    value");
        l.add(null, "1  2      size                       size of payload");
        l.add(null, "2  .        <num>                    value");
        l.add(null, "1  2      delay-up                   number of successes before up");
        l.add(null, "2  .        <num>                    value");
        l.add(null, "1  2      delay-down                 number of failures before down");
        l.add(null, "2  .        <num>                    value");
        l.add(null, "1  2      wake-vrf                   wake vrf on state change");
        l.add(null, "2  .        <str>                    name of vrf");
        l.add(null, "1  2      exec-up                    exec command to execute on up");
        l.add(null, "2  2,.      <cmd>                    value");
        l.add(null, "1  2      exec-down                  exec command to execute on down");
        l.add(null, "2  2,.      <cmd>                    value");
        l.add(null, "1  .      stop                       stop working");
        l.add(null, "1  .      start                      start working");
        l.add(null, "1  .      runnow                     run one round now");
        l.add(null, "1  .      hidden                     hide exec commands");
        l.add(null, "1  .      log                        log actions");
    }

    /**
     * get config
     *
     * @param filter filter
     * @return config
     */
    public List<String> getShRun(int filter) {
        List<String> l = new ArrayList<String>();
        l.add("tracker " + name);
        cmds.cfgLine(l, description == null, cmds.tabulator, "description", description);
        cmds.cfgLine(l, !worker.hidden, cmds.tabulator, "hidden", "");
        cmds.cfgLine(l, !worker.logging, cmds.tabulator, "log", "");
        l.add(cmds.tabulator + "mode " + clntTrack.mode2string(worker.mode));
        l.add(cmds.tabulator + "force " + clntTrack.force2string(worker.force));
        cmds.cfgLine(l, worker.script == null, cmds.tabulator, "script", worker.script);
        cmds.cfgLine(l, worker.target == null, cmds.tabulator, "target", worker.target);
        if (worker.hidden) {
            cmds.cfgLine(l, worker.execUp == null, cmds.tabulator, "exec-up", authLocal.passwdEncode(worker.execUp, (filter & 2) != 0));
            cmds.cfgLine(l, worker.execDn == null, cmds.tabulator, "exec-down", authLocal.passwdEncode(worker.execDn, (filter & 2) != 0));
        } else {
            cmds.cfgLine(l, worker.execUp == null, cmds.tabulator, "exec-up", worker.execUp);
            cmds.cfgLine(l, worker.execDn == null, cmds.tabulator, "exec-down", worker.execDn);
        }
        if (worker.wakeVrf != null) {
            l.add(cmds.tabulator + "wake-vrf " + worker.wakeVrf.name);
        } else {
            l.add(cmds.tabulator + "no wake-vrf");
        }
        cmds.cfgLine(l, worker.secProto == 0, cmds.tabulator, "security", servGeneric.proto2string(worker.secProto));
        if (worker.chats != null) {
            l.add(cmds.tabulator + "chat-script " + worker.chats.scrName);
        } else {
            l.add(cmds.tabulator + "no chat-script");
        }
        if (worker.vrf != null) {
            l.add(cmds.tabulator + "vrf " + worker.vrf.name);
        } else {
            l.add(cmds.tabulator + "no vrf");
        }
        if (worker.srcIfc != null) {
            l.add(cmds.tabulator + "source " + worker.srcIfc.name);
        } else {
            l.add(cmds.tabulator + "no source");
        }
        l.add(cmds.tabulator + "random-interval " + worker.randInt);
        l.add(cmds.tabulator + "random-initial " + worker.randIni);
        l.add(cmds.tabulator + "interval " + worker.interval);
        l.add(cmds.tabulator + "timeout " + worker.timeout);
        l.add(cmds.tabulator + "tos " + worker.typOsrv);
        l.add(cmds.tabulator + "flow " + worker.flowLab);
        l.add(cmds.tabulator + "ttl " + worker.tim2liv);
        l.add(cmds.tabulator + "size " + worker.size);
        l.add(cmds.tabulator + "delay-up " + worker.delayUp);
        l.add(cmds.tabulator + "delay-down " + worker.delayDn);
        if (worker.working) {
            l.add(cmds.tabulator + "start");
        } else {
            l.add(cmds.tabulator + "stop");
        }
        l.add(cmds.tabulator + cmds.finish);
        l.add(cmds.comment);
        if ((filter & 1) == 0) {
            return l;
        }
        return userFilter.filterText(l, defaultF);
    }

    /**
     * do config string
     *
     * @param cmd config
     */
    public void doCfgStr(cmds cmd) {
        String a = cmd.word();
        if (a.equals("description")) {
            description = cmd.getRemaining();
            return;
        }
        if (a.equals("script")) {
            worker.script = cmd.getRemaining();
            return;
        }
        if (a.equals("hidden")) {
            worker.hidden = true;
            return;
        }
        if (a.equals("log")) {
            worker.logging = true;
            return;
        }
        if (a.equals("mode")) {
            a = cmd.word();
            worker.mode = null;
            if (a.equals("icmp")) {
                worker.mode = clntTrack.operMod.icmp;
                return;
            }
            if (a.equals("tcp")) {
                worker.mode = clntTrack.operMod.tcp;
                return;
            }
            if (a.equals("bfd")) {
                worker.mode = clntTrack.operMod.bfd;
                return;
            }
            if (a.equals("interface")) {
                worker.mode = clntTrack.operMod.iface;
                return;
            }
            if (a.equals("route")) {
                worker.mode = clntTrack.operMod.route;
                return;
            }
            if (a.equals("prefix")) {
                worker.mode = clntTrack.operMod.prefix;
                return;
            }
            if (a.equals("script")) {
                worker.mode = clntTrack.operMod.script;
                return;
            }
            if (a.equals("nrpe")) {
                worker.mode = clntTrack.operMod.nrpe;
                return;
            }
            if (a.equals("other")) {
                worker.mode = clntTrack.operMod.other;
                return;
            }
            cmd.badCmd();
            return;
        }
        if (a.equals("force")) {
            a = cmd.word();
            worker.force = clntTrack.forMode.norm;
            if (a.equals("up")) {
                worker.force = clntTrack.forMode.up;
                return;
            }
            if (a.equals("down")) {
                worker.force = clntTrack.forMode.down;
                return;
            }
            if (a.equals("negate")) {
                worker.force = clntTrack.forMode.neg;
                return;
            }
            if (a.equals("normal")) {
                worker.force = clntTrack.forMode.norm;
                return;
            }
            cmd.badCmd();
            return;
        }
        if (a.equals("target")) {
            worker.target = cmd.getRemaining();
            return;
        }
        if (a.equals("exec-up")) {
            worker.execUp = authLocal.passwdDecode(cmd.getRemaining());
            return;
        }
        if (a.equals("exec-down")) {
            worker.execDn = authLocal.passwdDecode(cmd.getRemaining());
            return;
        }
        if (a.equals("security")) {
            worker.secProto = servGeneric.string2proto(cmd.word());
            return;
        }
        if (a.equals("chat-script")) {
            cfgChat cht = cfgAll.chatFind(cmd.word(), false);
            if (cht == null) {
                cmd.error("no such script");
                return;
            }
            worker.chats = cht.script;
            return;
        }
        if (a.equals("vrf")) {
            worker.vrf = cfgAll.vrfFind(cmd.word(), false);
            if (worker.vrf == null) {
                cmd.error("no such vrf");
                return;
            }
            return;
        }
        if (a.equals("wake-vrf")) {
            worker.wakeVrf = cfgAll.vrfFind(cmd.word(), false);
            if (worker.wakeVrf == null) {
                cmd.error("no such vrf");
                return;
            }
            return;
        }
        if (a.equals("source")) {
            worker.srcIfc = cfgAll.ifcFind(cmd.word(), false);
            if (worker.srcIfc == null) {
                cmd.error("no such interface");
                return;
            }
            return;
        }
        if (a.equals("random-interval")) {
            worker.randInt = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("random-initial")) {
            worker.randIni = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("interval")) {
            worker.interval = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("timeout")) {
            worker.timeout = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("tos")) {
            worker.typOsrv = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("flow")) {
            worker.flowLab = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("ttl")) {
            worker.tim2liv = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("size")) {
            worker.size = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("delay-up")) {
            worker.delayUp = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("delay-down")) {
            worker.delayDn = bits.str2num(cmd.word());
            return;
        }
        if (a.equals("stop")) {
            worker.stopNow();
            return;
        }
        if (a.equals("start")) {
            worker.startNow();
            return;
        }
        if (a.equals("runnow")) {
            worker.doRound();
            return;
        }
        if (!a.equals("no")) {
            cmd.badCmd();
            return;
        }
        a = cmd.word();
        if (a.equals("description")) {
            description = null;
            return;
        }
        if (a.equals("script")) {
            worker.script = null;
            return;
        }
        if (a.equals("hidden")) {
            worker.hidden = false;
            return;
        }
        if (a.equals("log")) {
            worker.logging = false;
            return;
        }
        if (a.equals("start")) {
            worker.stopNow();
            return;
        }
        if (a.equals("stop")) {
            worker.startNow();
            return;
        }
        if (a.equals("mode")) {
            worker.mode = null;
            return;
        }
        if (a.equals("target")) {
            worker.target = null;
            return;
        }
        if (a.equals("exec-up")) {
            worker.execUp = null;
            return;
        }
        if (a.equals("exec-down")) {
            worker.execDn = null;
            return;
        }
        if (a.equals("security")) {
            worker.secProto = 0;
            return;
        }
        if (a.equals("chat-script")) {
            worker.chats = null;
            return;
        }
        if (a.equals("vrf")) {
            worker.vrf = null;
            return;
        }
        if (a.equals("wake-vrf")) {
            worker.wakeVrf = null;
            return;
        }
        if (a.equals("source")) {
            worker.srcIfc = null;
            return;
        }
        if (a.equals("random-interval")) {
            worker.randInt = 0;
            return;
        }
        if (a.equals("random-initial")) {
            worker.randIni = 0;
            return;
        }
        if (a.equals("interval")) {
            worker.interval = 0;
            return;
        }
        if (a.equals("timeout")) {
            worker.timeout = 0;
            return;
        }
        if (a.equals("delay-up")) {
            worker.delayUp = 0;
            return;
        }
        if (a.equals("delay-down")) {
            worker.delayDn = 0;
            return;
        }
        if (a.equals("force")) {
            worker.force = clntTrack.forMode.norm;
            return;
        }
        cmd.badCmd();
    }
}
