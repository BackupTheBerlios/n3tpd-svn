/*
 *   Neat NNTP Daemon (n3tpd)
 *   Copyright (C) 2007, 2008 by Christian Lins <christian.lins@web.de>
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Stack;
import n3tpd.Config;
import n3tpd.Debug;

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

    this.interval = Config.getInstance().get("n3tpd.article.lifetime", 30) * 24 * 60 * 60 * 1000; // Milliseconds
    if(this.interval < 0)
      this.interval = Integer.MAX_VALUE;
  }
  
  public void run()
  {
    for(;;)
    {
      purge();

      try
      {
        sleep(interval);
      }
      catch(InterruptedException e)
      {
        e.printStackTrace(Debug.getInstance().getStream());
      }
    }
  }
  
  private Stack<String> getAllFiles(File root)
  {
    Stack<String> all = new Stack<String>();
    for(File file : root.listFiles())
    {
      if(file.isDirectory())
        all.addAll(getAllFiles(file));
      else
        all.push(file.getAbsolutePath());
    }

    return all;
  }

  private void purge()
  {
    Debug.getInstance().log("Purging old messages...");

    String dataPath     = Config.getInstance().get("n3tpd.datadir", ".");
    File   dataPaFi     = new File(dataPath);
    long   timeTreshold = new Date().getTime() - this.interval;

    Stack<String> allFiles = getAllFiles(dataPaFi);
    while(!allFiles.isEmpty())
    {
      try
      {
        String  path = allFiles.pop();
        Article art  = new Article(path);
        art.loadFromFile();
        
        // Check date
        if(art.getDate().getTime() < timeTreshold)
        {
          Debug.getInstance().log("Deleting " + art);
          if(!art.delete())
            Debug.getInstance().log("Deletion of " + art + " failed!");
        }
      }
      catch(IOException e)
      {
        e.printStackTrace(Debug.getInstance().getStream());
      }
    }
  }
}
