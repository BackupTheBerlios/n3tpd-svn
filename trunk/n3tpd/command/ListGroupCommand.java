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
import java.util.LinkedList;
import n3tpd.NNTPConnection;
import n3tpd.storage.Article;
import n3tpd.storage.Group;

/**
 * Class handling the LISTGROUP command.
 * @author Christian Lins
 * @author Dennis Schwerdel
 */
public class ListGroupCommand extends Command
{
  public ListGroupCommand(NNTPConnection conn)
  {
    super(conn);
  }

  public boolean process(String[] command) throws IOException
  {
    String commandName = command[0];
    if (!commandName.equalsIgnoreCase("LISTGROUP"))
      return false;
    // untested, RFC977 complient
    Group group = null;
    if (command.length >= 2)
    {
      group = Group.getByName(command[1]);
    }
    else
    {
      group = getCurrentGroup();
    }
    if (group == null)
    {
      printStatus(412, "Not currently in newsgroup");
      return true;
    }
    LinkedList<Article> list = group.getAllArticles();
    printStatus(211, "list of article numbers follow"); // argh, bad english in
    // RFC
    for (Article a : list)
    {
      printTextLine("" + a.getID());
    }
    println(".");
    flush();
    return true;
  }

}
