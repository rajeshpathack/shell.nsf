import java.io.*;

public class Command extends Thread {
    private PrintWriter pw;
    private String command;
    private Integer timeout;

    public Command(PrintWriter pw, String command, Integer timeout)
    {
        this.pw = pw;
        this.command = command;
        this.timeout = timeout;
    }

    public void run() {
        try {            
            String ret = startProcess(command, timeout);
            pw.println(ret);
        }
        catch(Exception e) {
            e.printStackTrace();
        }  
    }

    public String startProcess(String command, Integer timeout) throws IOException {
        StringBuffer out = new StringBuffer();
        long st = System.currentTimeMillis();
        Process p = Runtime.getRuntime().exec("cmd.exe /c " + command);
        BufferedInputStream p_is = new BufferedInputStream(p.getInputStream());
        BufferedInputStream p_es = new BufferedInputStream(p.getErrorStream());
        boolean exit = false;
        while (!exit) {
            while (p_is.available() > 0) {
                out.append(pretty(p_is.read()));
            }
            while (p_es.available() > 0) {
                out.append(pretty(p_es.read()));
            }            
            try {
                p.exitValue();
                while (p_is.available() > 0)
                    out.append(pretty(p_is.read()));
                while (p_es.available() > 0)
                    out.append(pretty(p_es.read()));
                exit = true;
            }
            catch (IllegalThreadStateException e) {
                e.printStackTrace();
            }
            if (timeout < System.currentTimeMillis() - st) {
                p.destroy();
                exit = true;
            }
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return out.toString();
    }

    static String pretty(int i) {
        switch (i) {
            case '&':
                return "&amp;";
            case '>':
                return "&gt;";
            case '<':
                return "&lt;";
            case '"':
                return "&quot;";
            default:
                return "" + (char)i;
        }
    }
}
