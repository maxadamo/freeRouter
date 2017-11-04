package user;

import auth.authGeneric;
import auth.authResult;
import cfg.cfgAll;
import cfg.cfgAuther;
import cfg.cfgIfc;
import cfg.cfgInit;
import java.util.List;
import pipe.pipeSide;
import util.bits;
import util.cmds;
import util.logger;

/**
 * one line descriptor
 *
 * @author matecsaba
 */
public class userLine {

    /**
     * interface to use
     */
    public cfgIfc execIface;

    /**
     * exec timeout
     */
    public int execTimeOut = 5 * 60 * 1000;

    /**
     * width of terminal screen
     */
    public int execWidth = 79;

    /**
     * height of terminal screen
     */
    public int execHeight = 24;

    /**
     * log exec events
     */
    public boolean execLogging = false;

    /**
     * log login events
     */
    public boolean loginLogging = false;

    /**
     * authentication list
     */
    public authGeneric authenticList;

    /**
     * command to use on login
     */
    public String autoCommand = "";

    /**
     * terminate session after command
     */
    public boolean autoHangup = false;

    /**
     * username prompt
     */
    public String promptUser = "username:";

    /**
     * password prompt
     */
    public String promptPass = "password:";

    /**
     * failed prompt
     */
    public String promptFailed = "authentication failed";

    /**
     * welcome prompt
     */
    public String promptWelcome = "welcome";

    /**
     * success prompt
     */
    public String promptSuccess = "line ready";

    /**
     * goodbye prompt
     */
    public String promptGoodbye = "see you later";

    /**
     * prompt retry
     */
    public int promptRetry = 3;

    /**
     * activation character
     */
    public int promptActivate = 13;

    /**
     * deactivation character
     */
    public int promptDeActive = 255;

    /**
     * prompt delay
     */
    public int promptDelay = 3000;

    /**
     * prompt timeout
     */
    public int promptTimeout = 60 * 1000;

    /**
     * prompt privilege
     */
    public int promptPrivilege = 15;

    /**
     * get running configuration
     *
     * @param beg beginning string
     * @param lst list to append
     */
    public void getShRun(String beg, List<String> lst) {
        if (execIface == null) {
            lst.add(beg + "no exec interface");
        } else {
            lst.add(beg + "exec interface " + execIface.name);
        }
        lst.add(beg + "exec timeout " + execTimeOut);
        lst.add(beg + "exec width " + execWidth);
        lst.add(beg + "exec height " + execHeight);
        lst.add(beg + "exec welcome " + promptWelcome);
        lst.add(beg + "exec ready " + promptSuccess);
        lst.add(beg + "exec bye " + promptGoodbye);
        cmds.cfgLine(lst, !execLogging, beg, "exec logging", "");
        lst.add(beg + "exec autocommand " + autoCommand);
        cmds.cfgLine(lst, !autoHangup, beg, "exec autohangup", "");
        lst.add(beg + "exec privilege " + promptPrivilege);
        if (authenticList == null) {
            lst.add(beg + "no login authentication");
        } else {
            lst.add(beg + "login authentication " + authenticList.autName);
        }
        lst.add(beg + "login activate " + promptActivate);
        lst.add(beg + "login deactivate " + promptDeActive);
        lst.add(beg + "login timeout " + promptTimeout);
        lst.add(beg + "login retry " + promptRetry);
        lst.add(beg + "login delay " + promptDelay);
        lst.add(beg + "login user " + promptUser);
        lst.add(beg + "login pass " + promptPass);
        lst.add(beg + "login fail " + promptFailed);
        cmds.cfgLine(lst, !loginLogging, beg, "login logging", "");
    }

    /**
     * parse commands
     *
     * @param cmd commands
     * @return true if error happened
     */
    public boolean doCfgStr(cmds cmd) {
        String s = cmd.word();
        if (s.equals("exec")) {
            s = cmd.word();
            if (s.equals("interface")) {
                execIface = cfgAll.ifcFind(cmd.word(), false);
                if (execIface == null) {
                    return false;
                }
                if (execIface.type != cfgIfc.ifaceType.dialer) {
                    execIface = null;
                    return false;
                }
                return false;
            }
            if (s.equals("logging")) {
                execLogging = true;
                return false;
            }
            if (s.equals("timeout")) {
                execTimeOut = bits.str2num(cmd.word());
                return false;
            }
            if (s.equals("width")) {
                execWidth = bits.str2num(cmd.word());
                return false;
            }
            if (s.equals("height")) {
                execHeight = bits.str2num(cmd.word());
                return false;
            }
            if (s.equals("ready")) {
                promptSuccess = cmd.getRemaining();
                return false;
            }
            if (s.equals("welcome")) {
                promptWelcome = cmd.getRemaining();
                return false;
            }
            if (s.equals("bye")) {
                promptGoodbye = cmd.getRemaining();
                return false;
            }
            if (s.equals("autohangup")) {
                autoHangup = true;
                return false;
            }
            if (s.equals("autocommand")) {
                autoCommand = cmd.getRemaining();
                return false;
            }
            if (s.equals("privilege")) {
                promptPrivilege = bits.str2num(cmd.word()) & 0xf;
                return false;
            }
            return true;
        }
        if (s.equals("login")) {
            s = cmd.word();
            if (s.equals("logging")) {
                loginLogging = true;
                return false;
            }
            if (s.equals("authentication")) {
                cfgAuther lst = cfgAll.autherFind(cmd.word(), null);
                if (lst == null) {
                    cmd.error("no such auth list");
                    return false;
                }
                authenticList = lst.getAuther();
                return false;
            }
            if (s.equals("activate")) {
                promptActivate = bits.str2num(cmd.word());
                return false;
            }
            if (s.equals("deactivate")) {
                promptDeActive = bits.str2num(cmd.word());
                return false;
            }
            if (s.equals("retry")) {
                promptRetry = bits.str2num(cmd.word());
                return false;
            }
            if (s.equals("delay")) {
                promptDelay = bits.str2num(cmd.word());
                return false;
            }
            if (s.equals("timeout")) {
                promptTimeout = bits.str2num(cmd.word());
                return false;
            }
            if (s.equals("user")) {
                promptUser = cmd.getRemaining();
                return false;
            }
            if (s.equals("pass")) {
                promptPass = cmd.getRemaining();
                return false;
            }
            if (s.equals("fail")) {
                promptFailed = cmd.getRemaining();
                return false;
            }
            return true;
        }
        if (!s.equals("no")) {
            return true;
        }
        s = cmd.word();
        if (s.equals("exec")) {
            s = cmd.word();
            if (s.equals("interface")) {
                execIface = null;
                return false;
            }
            if (s.equals("logging")) {
                execLogging = false;
                return false;
            }
            if (s.equals("autohangup")) {
                autoHangup = false;
                return false;
            }
            if (s.equals("autocommand")) {
                autoCommand = "";
                return false;
            }
            return true;
        }
        if (s.equals("login")) {
            s = cmd.word();
            if (s.equals("logging")) {
                loginLogging = false;
                return false;
            }
            if (s.equals("authentication")) {
                authenticList = null;
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * get help text
     *
     * @param l list of commands
     */
    public void getHelp(userHelping l) {
        l.add("1 2  exec                           set executable parameters");
        l.add("2 3    interface                    set interface to use for framing");
        l.add("3 .      <name>                     name of interface");
        l.add("2 .    logging                      enable logging");
        l.add("2 3    timeout                      set timeout value");
        l.add("3 .      <num>                      timeout in milliseconds");
        l.add("2 3    width                        number of columns");
        l.add("3 .      <num>                      width of terminal");
        l.add("2 3    height                       set height of terminal");
        l.add("3 .      <num>                      number of lines");
        l.add("2 3    ready                        set ready message");
        l.add("3 3,.    <text>                     text to display");
        l.add("2 3    bye                          set goodbye message");
        l.add("3 3,.    <text>                     text to display");
        l.add("2 3    welcome                      set welcome message");
        l.add("3 3,.    <text>                     text to display");
        l.add("2 3    autocommand                  set automatic command");
        l.add("3 3,.    <text>                     autocommand of user");
        l.add("2 .    autohangup                   disconnect user after autocommand");
        l.add("2 3    privilege                    set default privilege");
        l.add("3 .      <num>                      privilege of terminal");
        l.add("1 2  login                          set login parameters");
        l.add("2 .    logging                      enable logging");
        l.add("2 3    authentication               set authentication");
        l.add("3 .      <name>                     name of authentication list");
        l.add("2 3    activate                     set activation character");
        l.add("3 .      <num>                      ascii number");
        l.add("2 3    deactivate                   set deactivation character");
        l.add("3 .      <num>                      ascii number");
        l.add("2 3    timeout                      set timeout value");
        l.add("3 .      <num>                      timeout in milliseconds");
        l.add("2 3    retry                        set retry value");
        l.add("3 .      <num>                      number of tries");
        l.add("2 3    delay                        set delay value");
        l.add("3 .      <num>                      timeout in milliseconds");
        l.add("2 3    user                         set username prompt");
        l.add("3 3,.    <text>                     text to display");
        l.add("2 3    pass                         set password prompt");
        l.add("3 3,.    <text>                     text to display");
        l.add("2 3    fail                         set failed message");
        l.add("3 3,.    <text>                     text to display");
    }

    /**
     * create new cli handler
     *
     * @param pip pipeline to work on
     * @param nam name of remote
     * @param phys set true for physical lines
     */
    public void createHandler(pipeSide pip, String nam, boolean phys) {
        new userLineHandler(this, pip, nam, phys);
    }

}

class userLineHandler implements Runnable {

    /**
     * logged in user
     */
    public authResult user;

    private final pipeSide pipe;

    private final userLine parent;

    private final String remote;

    private final boolean physical;

    public userLineHandler(userLine prnt, pipeSide pip, String rem, boolean phys) {
        parent = prnt;
        pipe = pip;
        remote = rem;
        physical = phys;
        user = new authResult();
        user.privilege = parent.promptPrivilege;
        pipe.timeout = parent.execTimeOut;
        pipe.lineRx = pipeSide.modTyp.modeCRtryLF;
        pipe.lineTx = pipeSide.modTyp.modeCRLF;
        new Thread(this).start();
    }

    private void doAuth() {
        pipe.timeout = parent.promptTimeout;
        pipe.blockingPut(cfgAll.banner, 0, cfgAll.banner.length);
        pipe.linePut(parent.promptWelcome);
        if (parent.authenticList == null) {
            return;
        }
        for (int cnt = 0;; cnt++) {
            String usr;
            for (;;) {
                pipe.strPut(parent.promptUser);
                usr = pipe.lineGet(0x32);
                if (usr.length() > 0) {
                    break;
                }
                if (pipe.isClosed() != 0) {
                    break;
                }
            }
            pipe.strPut(parent.promptPass);
            String pwd = pipe.lineGet(0x31);
            user = parent.authenticList.authUserPass(usr, pwd);
            if (user.result == authResult.authSuccessful) {
                break;
            }
            if (parent.loginLogging) {
                logger.info("login failed (" + usr + ") from " + remote);
            }
            bits.sleep(parent.promptDelay);
            pipe.linePut(parent.promptFailed);
            if (cnt < parent.promptRetry) {
                continue;
            }
            pipe.setClose();
            return;
        }
    }

    private void doExec() {
        if (pipe.isClosed() != 0) {
            return;
        }
        if (parent.loginLogging) {
            logger.info(user.user + " logged in from " + remote);
        }
        pipe.linePut(parent.promptSuccess);
        pipe.timeout = parent.execTimeOut;
        userReader rdr = new userReader(pipe, parent.promptDeActive);
        rdr.from = remote;
        rdr.user = user.user;
        rdr.logging = parent.execLogging;
        rdr.width = parent.execWidth;
        rdr.height = parent.execHeight;
        userExec exe = new userExec(pipe, rdr);
        userConfig cfg = new userConfig(pipe, rdr);
        exe.privileged = user.privilege >= 15;
        exe.framedIface = parent.execIface;
        exe.physicalLin = physical;
        String s = parent.autoCommand;
        if (s.length() > 0) {
            s = exe.repairCommand(s);
            exe.executeCommand(s);
        }
        if (parent.autoHangup) {
            return;
        }
        s = user.autoCommand;
        if (s.length() > 0) {
            s = exe.repairCommand(s);
            exe.executeCommand(s);
        }
        if (user.autoHangup) {
            return;
        }
        for (;;) {
            userExec.cmdRes i = exe.doCommands();
            if (i == userExec.cmdRes.command) {
                continue;
            }
            if (i == userExec.cmdRes.logout) {
                if (parent.loginLogging) {
                    logger.info(user.user + " logged out from " + remote);
                }
                return;
            }
            if (i != userExec.cmdRes.config) {
                continue;
            }
            if (cfgAll.configExclusive > 0) {
                cfgAll.configExclusive++;
            }
            logger.warn(user.user + " configuring from " + remote);
            cfg.resetMode();
            List<String> sesStart = null;
            if (exe.rollback) {
                logger.info("configuration checkpoint frozen!");
                sesStart = cfgAll.getShRun(true);
            }
            for (;;) {
                s = cfg.doCommands();
                if (s == null) {
                    continue;
                }
                if (s.length() < 1) {
                    break;
                }
                s = exe.repairCommand(s);
                exe.executeCommand(s);
            }
            if (pipe.isClosed() == 0) {
                sesStart = null;
            }
            if (sesStart != null) {
                sesStart = userFilter.getDiffs(cfgAll.getShRun(true), sesStart);
                rdr.putStrArr(bits.lst2lin(sesStart, false));
                int res = cfgInit.executeSWcommands(sesStart, false);
                rdr.putStrArr(bits.str2lst("errors=" + res));
                logger.info("configuration reverted to frozen checkpoint with " + res + " errors.");
            }
            if (cfgAll.configExclusive > 1) {
                cfgAll.configExclusive--;
            }
            if (cfgAll.configAsave) {
                exe.executeCommand("write memory");
            }
            logger.warn(user.user + " configured from " + remote);
        }
    }

    public void run() {
        try {
            doAuth();
            doExec();
            pipe.linePut(parent.promptGoodbye);
        } catch (Exception e) {
            logger.traceback(e);
        }
        pipe.setClose();
    }

}
