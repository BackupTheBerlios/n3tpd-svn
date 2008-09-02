/*
 *   Neat NNTP Daemon (n3tpd)
 *   Copyright (C) 2007, 2008 by Christian Lins <christian.lins@web.de>
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
import java.text.SimpleDateFormat;
import java.util.Locale;

import n3tpd.NNTPConnection;
import n3tpd.storage.Article;

/**
 * Class handling the XOVER command.
 * @author Christian Lins
 */
public class XOverCommand extends Command
{
  public XOverCommand(NNTPConnection conn)
  {
    super(conn);
  }
  
  public boolean process(String[] command)
    throws IOException
  {
    if(getCurrentGroup() == null)
    {
      printStatus(412, "No news group current selected");
      return false;
    }
    
    int artStart = -1;
    int artEnd   = -1;
    String[] nums = command[1].split("-");
    if(nums.length > 1)
    {
      try
      {
        artStart = Integer.parseInt(nums[0]);
      }
      catch(Exception e) 
      {
        artStart = Integer.parseInt(command[1]);
      }
      try
      {
        artEnd = Integer.parseInt(nums[1]);
      }
      catch(Exception e) {}
    }
    
    if(artStart == -1)
    {
      Article art = getCurrentArticle();
      if(art == null)
      {
        printStatus(420, "No article(s) selected");
        return false;
      }
      
      String o = buildOverview(art, artStart);
      printText(o);
    }
    else
    {
      printStatus(224, "Overview information follows");
      for(int n = artStart; n <= artEnd; n++)
      {
        Article art = Article.getByNumberInGroup(getCurrentGroup(), n);
        printTextPart(buildOverview(art, n) + NEWLINE);
      }
      println(".");
      flush();
    }
    
    return true;
  }
  
  private String buildOverview(Article art, int nr)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    StringBuilder overview = new StringBuilder();
    overview.append(nr);
    overview.append('\t');
    overview.append(art.getHeader().get("Subject"));
    overview.append('\t');
    overview.append(art.getHeader().get("From"));
    overview.append('\t');
    overview.append(sdf.format(art.getDate()));
    overview.append('\t');
    overview.append(art.getHeader().get("Message-ID"));
    overview.append('\t');
    overview.append(art.getHeader().get("References"));
    overview.append('\t');
    overview.append(art.getHeader().get("Bytes"));
    overview.append('\t');
    overview.append(art.getHeader().get("Lines"));
    
    return overview.toString();
  }
}
