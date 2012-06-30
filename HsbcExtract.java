
/* Copyright (C) 2012  Ian Murray

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.
    <http://www.gnu.org/licenses/>.
    
    Usage:
    
    Access "your statement" of hsbc's UK Personal banking website and right-click on the
    links to your individual statements. Then use "save link as..." and save them all to
    a single folder. Then use :-
    
    java -jar hsbcextract.jar <FileOrFoldername> > hsbc.csv
    
    ... to create a CSV file of your entire statements for analysis.
    
 */

package org.ianmurray.hsbcextract;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HsbcExtract {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length!=1)
		{
			System.out.println("Need file/path name");
			return;
		}
		File inp = new File(args[0]);
		if (inp.isDirectory()) {
			for (File f : inp.listFiles())
			{
				if (f.isFile())
				{
					doFile(f);
				}
			}

		} else {
			doFile(inp);
		}
	}
	
	private static void doFile(File input)
	{

		Document doc;
		Element table;
		Elements allData;

		doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "http://www.hsbc.co.uk/");
			Element statementDate = doc.select("div:contains(Statement date) + div").first();

			table = doc.select("tbody:eq(1)").first();
			allData = table.select("td");
			Iterator<Element> itr = allData.iterator();
			Element itrElement;
			int kounter = 0;
			String statementDateString = statementDate.text().trim();
			statementDateString = statementDateString.substring(statementDateString.length()-4);
			while(itr.hasNext()) {

				itrElement =itr.next();
				String truncated = itrElement.text().replace((char)160,(char)32).trim();
				System.out.print(truncated);
				if (kounter == 0)
				{

					System.out.print(" ".concat(statementDateString));
				}
				System.out.print(",");
				kounter++;
				if (kounter == 7)
				{
					kounter = 0;
					System.out.println("");
				}
			}
		} catch (IOException ioe) {
			System.err.println("An I/O error has occured:");
			ioe.printStackTrace();
		}

	}

}
