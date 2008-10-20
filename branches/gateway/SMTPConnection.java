
import java.net.Socket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This SMTPConnection class actually talks to a MTA and retrieves 
 * a mail from it.
 * @author cl224670
 */
public class SMTPConnection implements Runnable
{
  private Socket socket;
  
  public SMTPConnection(Socket socket)
  {
    this.socket = socket;
  }
  
  public void run()
  {
    
  }
}
