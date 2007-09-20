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

package n3tpd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

/**
 * Provides logging and debugging methods.
 * @author Christian Lins
 */
public class Debug
{
  private static Debug instance = null;
  
  public static Debug getInstance()
  {
    if(instance == null)
      instance = new Debug();
    
    return instance;
  }
  
  private PrintStream out = System.err;
  
  private Debug()
  {
    try
    {
      String filename = Config.getInstance().get(Config.CONFIG_N3TPD_LOGFILE);
      
      this.out = new PrintStream(new FileOutputStream(filename));
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public PrintStream getStream()
  {
    return out;
  }
  
  public void log(String msg)
  {
    out.print(new Date().toString());
    out.print(": ");
    out.println(msg);
    out.flush();
  }
}
