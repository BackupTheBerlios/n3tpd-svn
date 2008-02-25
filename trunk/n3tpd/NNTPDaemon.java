/*
 *   Netvader NNTP Daemon (n3tpd)
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

package n3tpd;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Server component of the n3tpd.
 * @author Christian Lins
 * @author Dennis Schwerdel
 */
public class NNTPDaemon extends Thread
{
  private ServerSocket socket;

  public NNTPDaemon() throws IOException
  {
    int port    = Config.getInstance().get("n3tpd.port", 119);
    int backlog = Config.getInstance().get("n3tpd.server.backlog", 10);
    socket = new ServerSocket(port, backlog);
  }

  public void run()
  {
    for(;;)
    {
      try
      {
        new NNTPConnection(socket.accept()).start();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
