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

import java.io.File;
import java.io.IOException;
import n3tpd.storage.Purger;

/**
 * Startup class of the n3tpd.
 * @author Christian Lins
 * @author Dennis Schwerdel
 */
public class Main
{
  public static final String VERSION = "n3tpd/0.2";

  public static void main(String args[]) throws Exception
  {
    // Checking configuration...
    File dataPath = new File(Config.getInstance().get("n3tpd.datadir"));
    if(!dataPath.exists())
      if(!dataPath.mkdir())
        throw new IOException("Could not create data directory!");
    
    Debug.getInstance().log(VERSION + " starting...");

    new Purger().start();
    new NNTPServer().start();
  }
}
