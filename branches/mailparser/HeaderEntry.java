
/**
 * One entry of a Mail header. The entry is identified by its key, but note
 * that some keys can appear multiple times within one Mail (e.g. "Received").
 * One HeaderEntry represents only one of these multiple header fields.
 * @author Christian Lins (christian.lins@web.de)
 */
class HeaderEntry 
{
  private String key, value;
  
  public HeaderEntry(String key, String value)
  {
    this.key   = key;
    this.value = value;
  }
  
  public void addToValue(String value)
  {
    this.value += value;
  }
  
  /**
   * Finds the next whitespace searching backwards from startidx + DEFAULT_LINE_LENGTH.
   * If no whitespace can be found it will return startidx + DEFAULT_LINE_LENGTH.
   * @param startidx
   * @return
   */
  private int findNextWhitespace(int startidx)
  {
    int n;
    for(n = startidx + Mail.DEFAULT_LINE_LENGTH; n > startidx; n--)
    {
      if(n >= this.value.length()) // Range check
        continue;
      
      if(this.value.charAt(n) == ' ')
        return n;
    }
    
    if(n + Mail.DEFAULT_LINE_LENGTH >= this.value.length())
      return this.value.length() - 1;
    else
      return n + Mail.DEFAULT_LINE_LENGTH;
  }
  
  public String getKey()
  {
    return this.key;
  }
  
  /**
   * The result of this method looks like this:
   * "bla bla bla ... bla\n
   *   bla continued...\n
   *   bla more continued"
   * @return String containing the value of this entry
   */
  public String getValueUnfolded()
  {
    StringBuffer buf = new StringBuffer();
    
    int idx = 0;
    while(idx + Mail.DEFAULT_LINE_LENGTH < this.value.length())
    {
      int chunkend = findNextWhitespace(idx);
      String sstr = this.value.substring(idx, chunkend);
      if(idx == chunkend + Mail.DEFAULT_LINE_LENGTH)
        buf.append(" ");
      buf.append(sstr);
      buf.append("\n");
      idx = chunkend;
    }
    
    String sstr = this.value.substring(idx);
    if(idx > 0)
      buf.append(" ");
    buf.append(sstr);
    
    return buf.toString();
  }
}
