import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class represents an E-Mail as defined in RFC2822
 * (http://tools.ietf.org/html/rfc2822). Here are some notes from this RFC:
 * 
 * At the most basic level, a message is a series of characters.  A
 * message that is conformant with this standard is comprised of
 * characters with values in the range 1 through 127 and interpreted as
 * US-ASCII characters [ASCII].
 * 
 * There are other documents,
 * specifically the MIME document series [RFC2045, RFC2046, RFC2047,
 * RFC2048, RFC2049], that extend this standard to allow for values
 * outside of that range.
 * 
 * Messages are divided into lines of characters.  A line is a series of
 * characters that is delimited with the two characters carriage-return
 * and line-feed; that is, the carriage return (CR) character (ASCII
 * value 13) followed immediately by the line feed (LF) character (ASCII
 * value 10).
 * 
 * There are two limits that this standard places on the number of
 * characters in a line. Each line of characters MUST be no more than
 * 998 characters, and SHOULD be no more than 78 characters, excluding
 * the CRLF.
 * 
 * @author Christian Lins (christian.lins@web.de)
 */
public class Mail 
{
  public static int DEFAULT_LINE_LENGTH = 78;  // 80 incl. CRLF
  public static int MAX_LINE_LENGTH     = 998; // 1000 incl. CRLF
  
  private Map<String, List<HeaderEntry>> header    = null;
  
  Mail(Map<String, List<HeaderEntry>> header, String body)
  {
    this.header = header;
  }
  
  @Override
  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    
    List<String> keys = new ArrayList<String>(this.header.keySet());
    Collections.sort(keys);
    
    // Loop over all header keys
    for(String key : keys)
    {
      String rawval = "";// this.header.get(key);
      
      // Some header fields can occure multiple times (e.g. Received).
      // They are stored with only one key, but splitted by newlines.
      String[] values = rawval.split("\n");
      
      for(String value : values)
      {
        buf.append(key);
        buf.append(": ");

        if(value.length() <= DEFAULT_LINE_LENGTH)
        {
          buf.append(value);
        }
        else
        {
          // Special handling for the first line, so that the output looks
          // more beautiful
          buf.append(value.substring(0, DEFAULT_LINE_LENGTH));
          buf.append("\n");
          value = value.substring(DEFAULT_LINE_LENGTH);
          
          // Some header lines are very long, so we have to unfold them
          while(value.length() > DEFAULT_LINE_LENGTH)
          {
            buf.append("  ");
            buf.append(value.substring(0, DEFAULT_LINE_LENGTH));
            buf.append("\n");
            value = value.substring(DEFAULT_LINE_LENGTH);
          }
          
          // And add the last chunk
          buf.append("  ");
          buf.append(value);
        }
        buf.append("\n");
      }
    }
    
    return buf.toString();
  }
}
