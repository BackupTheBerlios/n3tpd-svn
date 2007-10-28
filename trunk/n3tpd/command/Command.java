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

package n3tpd.command;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import n3tpd.NNTPConnection;
import n3tpd.storage.Article;
import n3tpd.storage.Group;

/**
 * Base class for all command handling classes.
 * @author Christian Lins
 * @author Dennis Schwerdel
 */
public abstract class Command
{
  protected NNTPConnection connection;

  public Command(NNTPConnection connection)
  {
    this.connection = connection;
  }

  protected static String NEWLINE = NNTPConnection.NEWLINE;

  protected LinkedList<String> readText() throws IOException
  {
    return connection.readText();
  }

  protected String readTextLine() throws IOException
  {
    return connection.readTextLine();
  }

  protected void printStatus(int status, String text) throws IOException
  {
    connection.printStatus(status, text);
  }

  protected void printTextLine(CharSequence text) throws IOException
  {
    connection.printTextLine(text);
  }

  protected void printTextPart(CharSequence text) throws IOException
  {
    connection.printTextPart(text);
  }
  
  protected void printText(CharSequence text) throws IOException
  {
    connection.printText(text);
  }

  protected void println(CharSequence text) throws IOException
  {
    connection.println(text);
  }

  protected void flush() throws IOException
  {
    connection.flush();
  }

  protected Article getCurrentArticle()
  {
    return connection.getCurrentArticle();
  }

  protected Group getCurrentGroup()
  {
    return connection.getCurrentGroup();
  }

  protected void setCurrentArticle(Article current)
  {
    connection.setCurrentArticle(current);
  }

  protected void setCurrentGroup(Group current)
  {
    connection.setCurrentGroup(current);
  }

  protected File getArticleDir()
  {
    return connection.getArticleDir();
  }

  public abstract boolean process(String[] command)
    throws IOException;
}
