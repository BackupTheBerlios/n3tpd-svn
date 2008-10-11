
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parser the reads and generates Mail instances from an given InputStream.
 * @author Christian Lins (christian.lins@web.de)
 */
public class MailParser 
{
  /**
   * Reads and parses lines from the given InputStream and returns an
   * intrepeted Mail instance.
   * @param in
   * @return
   */
  public Mail parse(InputStream in)
    throws IOException
  {
    BufferedReader br       = new BufferedReader(new InputStreamReader(in));
    boolean        isHeader = true;
    String         lastHeaderField = null;
    
    for(String line = br.readLine(); line != null; line = br.readLine())
    {
      if(line.length() > Mail.MAX_LINE_LENGTH)
      {
        System.out.println("ERROR: Line exceeds maximum allowed line length!");
        System.out.println("I will continue...");
      }
      else if(line.length() > Mail.DEFAULT_LINE_LENGTH)
      {
        System.out.println("WARNING: Line exceeds default line length!");
      }
      
      if(isHeader)
      {
        if(line.equals("")) 
          // An empty line is usally the terminator between header and body
          isHeader = false;
        else
        {
          if(line.startsWith(" ") || line.startsWith("\t"))
          {
            
          }
          else
          {
            String[] headline = line.split(":", 2);
            System.out.println("HEADER: " + headline[0]);
          }
        }
      }
      else
      {
        
      }
    }
    
    return null;
  }
}
