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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Manages the n3tpd configuration.
 * @author Christian Lins
 */
public class Config
{
  public static final String CONFIG_N3TPD_LOGFILE = "n3tpd.logfile";
  public static final String FILE                 = "n3tpd.conf";

  private static final Properties defaultConfig = new Properties();
  
  private static Config instance = null;
  
  static
  {
    defaultConfig.setProperty("n3tpd.article.lifetime", "300"); // 300 days
    defaultConfig.setProperty("n3tpd.article.maxsize", "100"); // 100 kbyte
    defaultConfig.setProperty("n3tpd.datadir", "data");
    defaultConfig.setProperty("n3tpd.port", "119");
    defaultConfig.setProperty("n3tpd.auxport", "8080");
    defaultConfig.setProperty("n3tpd.server.backlog", "10");
    defaultConfig.setProperty("n3tpd.hostname", "localhost");
    
    instance = new Config();
  }
  
  public static Config getInstance()
  {
    return instance;
  }

  private Properties settings = defaultConfig;

  private Config()
  {
    try
    {
      load();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public void load() throws IOException
  {
    try
    {
      settings.load(new FileInputStream(FILE));
    }
    catch (FileNotFoundException e)
    {
      save();
    }
  }

  public void save() throws FileNotFoundException, IOException
  {
    settings.store(new FileOutputStream(FILE), "N3TPD Config File");
  }

  public Properties get()
  {
    return settings;
  }
  
  public String get(String key, String def)
  {
    return settings.getProperty(key, def);
  }

  /**
   * Returns the value that is stored within this config
   * identified by the given key. If the key cannot be found
   * the default value def is returned.
   * @param key
   * @param def
   * @return
   */
  public int get(String key, int def)
  {
    try
    {
      String val = get(key);
      return Integer.parseInt(val);
    }
    catch(Exception e)
    {
      return def;
    }
  }

  public long get(String key, long def)
  {
    try
    {
      String val = get(key);
      return Long.parseLong(val);
    }
    catch(Exception e)
    {
      return def;
    }
  }

  private String get(String key)
  {
    return settings.getProperty(key);
  }

  public void set(String key, String value)
  {
    settings.setProperty(key, value);
  }

}
