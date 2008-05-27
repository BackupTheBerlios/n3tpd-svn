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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.zip.CRC32;
import n3tpd.Debug;

/**
 * Class that loads the groups.list file and ensures that the
 * groups in the Database are consistent with this file.
 * @author Christian Lins (christian.lins@web.de)
 */
public class GroupList 
{
  public static void arise()
  {
    try
    {
      BufferedReader in = new BufferedReader(
              new InputStreamReader(new FileInputStream("groups.list")));
      
      Database db = Database.getInstance();
      
      for(;;)
      {
        String line = in.readLine();
        if(line == null)
          break;
        
        line = line.trim();
        if(line.equals(""))
          continue;
        
        if(!db.isGroupExisting(line))
        {
          System.out.println("Create group " + line);
          if(!db.addGroup(line))
            System.err.println("Error inserting group!");
        }
      }
    }
    catch(FileNotFoundException ex)
    {
      System.err.println("groups.list file not found. Please provide one!");
    }
    catch(Exception ex)
    {
      ex.printStackTrace(Debug.getInstance().getStream());
    }
  }
}
