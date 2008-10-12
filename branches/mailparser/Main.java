
import java.io.FileInputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Christian Lins (christian.lins@web.de)
 */
public class Main 
{ 
  public static void main(String[] args)
    throws IOException
  {
    MailParser mailParser = new MailParser();
    Mail mail = mailParser.parse(new FileInputStream("test.txt"));
    
    PrintWriter out = new PrintWriter("test1.txt");
    out.write(mail.toString());
    out.close();
  }
}
