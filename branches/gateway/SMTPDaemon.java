
import java.io.IOException;
import java.net.ServerSocket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.Socket;

/**
 * Daemon listening for incoming SMTP connections.
 * @author cl224670
 */
public class SMTPDaemon extends Thread
{
  private ThreadPool smtpThreadPool = new ThreadPool(4);
  
  @Override
  public void run()
  {
    try
    {
      ServerSocket socket = new ServerSocket(2525);
      
      for(;;)
      {
        Socket clientSock = socket.accept();
        smtpThreadPool.addTask(new SMTPConnection(clientSock));
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
