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

package n3tpd;

import java.io.*;
import java.net.*;

public class NNTPServer extends Thread
{
  private ServerSocket socket;

  public NNTPServer() throws IOException
  {
    socket = new ServerSocket(
        Integer.parseInt(Config.getInstance().get("n3tpd.port")),
        Integer.parseInt(Config.getInstance().get("n3tpd.server.backlog")));
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
        System.err.println(e.getMessage());
      }
    }
  }

}
