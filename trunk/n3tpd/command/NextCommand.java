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
import n3tpd.NNTPConnection;
import n3tpd.storage.Article;
import n3tpd.storage.Group;

/**
 * Class handling the NEXT and LAST command.
 * @author Christian Lins
 * @author Dennis Schwerdel
 */
public class NextCommand extends Command
{
  public NextCommand(NNTPConnection conn)
  {
    super(conn);
  }

  public boolean process(String[] command) throws IOException
  {
    String commandName = command[0];
    if (!(commandName.equalsIgnoreCase("NEXT") || commandName
        .equalsIgnoreCase("LAST")))
      return false;
    // untested, RFC977 complient
    Article currA = getCurrentArticle();
    Group currG = getCurrentGroup();
    if (currA == null)
    {
      printStatus(420, "no current article has been selected");
      return true;
    }
    if (currG == null)
    {
      printStatus(412, "no newsgroup selected");
      return true;
    }
    Article article;
    if (commandName.equalsIgnoreCase("NEXT"))
    {
      article = currA.nextArticleInGroup(currG);
      if (article == null)
      {
        printStatus(421, "no next article in this group");
        return true;
      }
    }
    else
    {
      article = currA.prevArticleInGroup(currG);
      if (article == null)
      {
        printStatus(422, "no previous article in this group");
        return true;
      }
    }
    setCurrentArticle(article);
    printStatus(223, article.getID() + " " + article.getMessageID()
        + " article retrieved - request text separately");
    return true;
  }

}
