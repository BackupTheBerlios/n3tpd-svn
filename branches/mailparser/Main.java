
import java.io.FileInputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;

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
    mailParser.parse(new FileInputStream("test.txt"));
  }
}
