

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
}
