/*
 *   Netvader NNTP Daemon (n3tpd)
 *   Copyright (C) 2007 by Christian Lins <christian.lins@web.de>
 *   based on tnntpd (C) 2003 by Dennis Schwerdel
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package n3tpd.command;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import n3tpd.Config;
import n3tpd.Debug;
import n3tpd.NNTPConnection;
import n3tpd.storage.Article;

public class PostCommand extends Command
{
  public PostCommand(NNTPConnection conn)
  {
    super(conn);
  }

  public boolean process(String[] command) throws IOException
  {
    printStatus(340, "send article to be posted. End with <CR-LF>.<CR-LF>");

    // some initialization
    Article article = new Article();
    int lineCount     = 0;
    long bodySize     = 0;
    long maxBodySize  = Config.getInstance().get("n3tpd.article.maxsize", 1024) * 1024; // Size in bytes

    // begin with a stringbuilder body
    StringBuilder body = new StringBuilder();
    HashMap<String, String> header = new HashMap<String, String>();

    boolean isHeader = true; // are we in the header part

    String line = readTextLine();
    while(line != null)
    {
      bodySize += line.length();
      if(bodySize > maxBodySize)
      {
        printStatus(500, "article is too long");
        return false;
      }

      if(!isHeader)
      { // body
        if(line.trim().equals("."))
          break;
        
        bodySize += line.length() + 1;
        lineCount++;
        body.append(line + NEWLINE);
      }
      
      if(line.equals(""))
      {
        isHeader = false; // we finally met the blank line
                          // separating headers from body
      }

      if(isHeader)
      { // header
        // split name and value and add the header to the map
        int colon = line.indexOf(':');
        String fieldName = line.substring(0, colon).trim();
        String fieldValue = line.substring(colon + 1).trim();
        header.put(fieldName, fieldValue);
      }
      line = readTextLine(); // read a new line
    } // end of input reading

    article.setBody(body.toString()); // set the article body
    article.setHeader(header);     // add the header entries for the article
    
    // Read the date header and fall back to the current date if it is not set
    try
    {
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
      String date = header.get("DATE");
      if(date == null)
        article.setDate(new Date());
      else
        article.setDate(new Date(sdf.parse(date).getTime())) ;
    }
    catch (Exception e)
    {
      e.printStackTrace(Debug.getInstance().getStream());
      printStatus(541, "posting failed - invalid date format");
      return true;
    }

    // read the message-id header
    if (header.containsKey("MESSAGE-ID"))
    {
      article.setMessageID(header.get("MESSAGE-ID")) ;
    }

    // check for a cancel command
    if ( header.containsKey("CONTROL") ) 
    {
      String[] control = header.get("CONTROL").split(" ") ;
      if ( control.length >= 2 && control[0].equalsIgnoreCase("cancel") ) 
      {
        // this article is a cancel-article, try to delete the old article
        try
        {
          Article.getByMessageID(control[1]).delete();
          printStatus(240, "article posted ok - original article canceled"); // quite
          return true; // quit, do not actually post this article since it
        }
        catch (Exception e)
        {
          e.printStackTrace();
          printStatus(441, "posting failed - original posting not found");
          return true;
        }
      }
    }

    // check for a supersede command
    if (header.containsKey("SUPERSEDES"))
    {
      // just set set the message id to that of the original article
      article.setMessageID(header.get("SUPERSEDES")) ;
      // try to delete the old article
      try
      {
        Article.getByMessageID(header.get("SUPERSEDES")).delete();
      }
      catch (Exception e)
      {

        printStatus(441, "posting failed - original posting not found");
        return false; // quit, no article to supersede
      }
      return true;
    }

    // if no message-id has been set so far, generate one
    if (article.getMessageID() == null)
      article.generateMessageID();

    // set some headers
    header.put("Message-ID", article.getMessageID());
    header.put("Lines", "" + lineCount);
    header.put("Bytes", "" + bodySize);

    // if needed, set an empty references header
    if (!header.containsKey("REFERENCES"))
      header.put("References", "");

    // try to create the article in the database
    article.writeToFile();
      
    printStatus(240, "article posted ok");

    return true;
  }
}
