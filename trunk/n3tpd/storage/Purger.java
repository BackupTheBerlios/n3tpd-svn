/*
 *   Netvader NNTP Daemon (n3tpd)
 *   Copyright (C) 2007 by Christian Lins <christian.lins@web.de>
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

package n3tpd.storage;

/**
 * The purger is started in configurable intervals to search
 * for old messages that can be purged.
 * @author Christian Lins
 */
public class Purger extends Thread
{
  private int interval;
  
  public Purger()
  {
    setDaemon(false);
    setPriority(Thread.MIN_PRIORITY);
  }
  
  public void run()
  {
    for(;;)
    {
      purge();
    }
  }
  
  private void purge()
  {
    
  }
}
