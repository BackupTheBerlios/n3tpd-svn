import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser the reads and generates Mail instances from an given InputStream.
 * @author Christian Lins (christian.lins@web.de)
 */
public class MailParser 
{
  /**
   * Reads and parses lines from the given InputStream and returns an
   * interpreted Mail instance.
   * @param in
   * @return
   */
  public Mail parse(InputStream in)
    throws IOException
  {
    BufferedReader      br       = new BufferedReader(new InputStreamReader(in));
    boolean             isHeader = true;
    String              lastHeaderField = null;
    Map<String, List<HeaderEntry>> header   = new HashMap<String, List<HeaderEntry>>();
    
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
          // Perhaps it is an unfolded header field
          if(line.startsWith(" ") || line.startsWith("\t"))
          {
            // Check for invalid header
            if(lastHeaderField == null)
            {
              System.out.println("Invalid header field. Trying to continue...");
              continue;
            }
            
            // Add the current line to the last header field
            List<HeaderEntry> entries = header.get(lastHeaderField);
            if(entries == null)
            {
              // TODO: Error handling
              // This happens only when the mail is extremly malformed
            }
            else
            {
              HeaderEntry lastVal = entries.get(entries.size() - 1);
              lastVal.addToValue(line.trim());
            }
          }
          else // Begin of a new header field
          {
            String[] headline         = line.split(":", 2); // Split at first ':'
            List<HeaderEntry> entries = header.get(headline[0]);
            
            // If it's the first one of this entry type
            if(entries == null)
            {
              entries = new ArrayList<HeaderEntry>();
              header.put(headline[0], entries);
            }
            
            // Add new entry
            String value = headline[1].trim();
            HeaderEntry entry = new HeaderEntry(headline[0], value);
            entries.add(entry);
            
            lastHeaderField = headline[0];
          }
        }
      }
      else // Read the body
      {
        
      }
    }
    
    return new Mail(header, "");
  }
}
