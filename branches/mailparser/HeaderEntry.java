
/**
 *
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
  
  private int findNextWhitespace(int startidx)
  {
    int idx = this.value.indexOf(" ", startidx);
    if(idx != -1 && (idx - startidx) < Mail.MAX_LINE_LENGTH)
      return idx;
    return startidx;
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
      if(idx > 0)
        buf.append(" ");
      buf.append(sstr);
      idx = chunkend;
    }
    
    String sstr = this.value.substring(idx);
    if(idx > 0)
      buf.append(" ");
    buf.append(sstr);
    
    return buf.toString();
  }
}
