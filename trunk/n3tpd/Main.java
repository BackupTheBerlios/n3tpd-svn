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

package n3tpd;

import java.net.BindException;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import n3tpd.storage.Database;
import n3tpd.storage.Purger;

/**
 * Startup class of the n3tpd.
 * @author Christian Lins
 */
public class Main
{
  /** Version information of the n3tpd daemon */
  public static final String VERSION = "n3tpd/0.4alpha2";

  /**
   * The main entrypoint.
   * @param args
   * @throws Exception
   */
  public static void main(String args[]) throws Exception
  {
    System.out.println(VERSION);
    System.out.println("Copyright (C) 2007, 2008 by Christian Lins <christian.lins@web.de>");

    // Command line arguments
    boolean auxPort = false;
    
    for(int n = 0; n < args.length; n++)
    {
      if(args[n].equals("--dumpjdbcdriver"))
      {
        System.out.println("Available JDBC drivers:");
        Enumeration<Driver> drvs =  DriverManager.getDrivers();
        while(drvs.hasMoreElements())
          System.out.println(drvs.nextElement());
        return;
      }
      else if(args[n].equals("--useaux"))
        auxPort = true;
    }
    
    // Try to load the Database
    try
    {
      Database.arise();
    }
    catch(Exception ex)
    {
      ex.printStackTrace(Debug.getInstance().getStream());
      System.err.println("Database initialization failed with " + ex.toString());
      
      return;
    }
    
    // Start the n3tpd garbage collector
    new Purger().start();
    
    // Start the listening daemon
    try
    {
      new NNTPDaemon(false).start();
    }
    catch(BindException ex)
    {
      ex.printStackTrace(Debug.getInstance().getStream());
      System.err.println("Could not bind to interface. Perhaps you need superuser rights?");
    }
    
    // Start auxilary listening port...
    if(auxPort)
      new NNTPDaemon(true).start();
  }
}
