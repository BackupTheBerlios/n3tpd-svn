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
import n3tpd.storage.Group;

/**
 * Class handling the GROUP command.
 * @author Christian Lins
 * @author Dennis Schwerdel
 */
public class GroupCommand extends Command
{
  public GroupCommand(NNTPConnection conn)
  {
    super(conn);
  }

  public boolean process(String[] command) throws IOException
  {
    // untested, RFC977 compliant
    Group g = null;
    if (command.length >= 2)
    {
      g = Group.getByName(command[1]);
    }
    if (g == null)
    {
      printStatus(411, "no such news group");
      return true;
    }
    else
    {
      setCurrentGroup(g);

      printStatus(211, g.getEstimatedArticleCount() + " " + g.getFirstArticle()
          + " " + g.getLastArticle() + " " + g.getName() + " group selected");
      return true;
    }
  }

}
