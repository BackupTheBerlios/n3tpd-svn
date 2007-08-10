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

import java.io.*;
import java.util.*;

import n3tpd.*;
import n3tpd.storage.Article;

public class ArticleCommand extends Command
{
  public ArticleCommand(NNTPConnection connection)
  {
    super(connection);
  }

  public boolean process(String[] command) throws IOException
  {
    String commandName = command[0];

    // untested, RFC977 complient
    Article article = null;
    if (command.length <= 1)
    {
      article = getCurrentArticle();
      if (article == null)
      {
        printStatus(420, "no current article has been selected");
        return true;
      }
    }
    else if (command[1].matches(NNTPConnection.MESSAGE_ID_PATTERN))
    {
      // Message-ID
      article = Article.getByMessageID(command[1]);
      if (article == null)
      {
        printStatus(430, "no such article found");
        return true;
      }
    }
    else
    {
      // Message Number
      try
      {
        int num = Integer.parseInt(command[1]);
        article = Article.getByID(connection.getCurrentGroup(), num);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      if (article == null)
      {
        printStatus(423, "no such article number in this group");
        return true;
      }
      setCurrentArticle(article);
    }

    if (commandName.equalsIgnoreCase("ARTICLE"))
    {
      printStatus(220, article.getID() + " " + article.getMessageID()
          + " article retrieved - head and body follow");
      HashMap<String, String> header = article.getHeader();
      for(Map.Entry<String, String> entry : header.entrySet())
      {
        printTextPart(entry.getKey() + ": " + entry.getValue());
      }
      println("");
      printText(article.getBody());
    }
    else if (commandName.equalsIgnoreCase("HEAD"))
    {
      printStatus(500, "No longer supported! Use XOVER instead.");
      return false;
    }
    else if (commandName.equalsIgnoreCase("BODY"))
    {
      printStatus(222, article.getID() + " " + article.getMessageID()
          + " body");
      printText(article.getBody());
    }
    else if (commandName.equalsIgnoreCase("STAT"))
    {
      printStatus(223, article.getID() + " " + article.getMessageID()
          + " article retrieved - request text separately");
    }
    return true;
  }

}
