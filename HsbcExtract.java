
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
	private static String findYear(String statementDateStr, String entryDateStr)
	{
		// This is a nonsense method to set the right year for an entry when it occurs in
		// a previous year to the statement date.
		// Too much effort for the HSBC to include the year in the entry date, it would seem.

		int statementMonth;
		int entryMonth;
		int year;

		statementMonth = 0;
		if (statementDateStr.matches(".*Jan.*")) statementMonth = 1;
		if (statementDateStr.matches(".*Feb.*")) statementMonth = 2;
		if (statementDateStr.matches(".*Mar.*")) statementMonth = 3;
		if (statementDateStr.matches(".*Apr.*")) statementMonth = 4;
		if (statementDateStr.matches(".*May.*")) statementMonth = 5;
		if (statementDateStr.matches(".*Jun.*")) statementMonth = 6;
		if (statementDateStr.matches(".*Jul.*")) statementMonth = 7;
		if (statementDateStr.matches(".*Aug.*")) statementMonth = 8;
		if (statementDateStr.matches(".*Sep.*")) statementMonth = 9;
		if (statementDateStr.matches(".*Oct.*")) statementMonth = 10;
		if (statementDateStr.matches(".*Nov.*")) statementMonth = 11;
		if (statementDateStr.matches(".*Dec.*")) statementMonth = 12;

		entryMonth=0;
		if (entryDateStr.matches(".*Jan.*")) entryMonth = 1;
		if (entryDateStr.matches(".*Feb.*")) entryMonth = 2;
		if (entryDateStr.matches(".*Mar.*")) entryMonth = 3;
		if (entryDateStr.matches(".*Apr.*")) entryMonth = 4;
		if (entryDateStr.matches(".*May.*")) entryMonth = 5;
		if (entryDateStr.matches(".*Jun.*")) entryMonth = 6;
		if (entryDateStr.matches(".*Jul.*")) entryMonth = 7;
		if (entryDateStr.matches(".*Aug.*")) entryMonth = 8;
		if (entryDateStr.matches(".*Sep.*")) entryMonth = 9;
		if (entryDateStr.matches(".*Oct.*")) entryMonth = 10;
		if (entryDateStr.matches(".*Nov.*")) entryMonth = 11;
		if (entryDateStr.matches(".*Dec.*")) entryMonth = 12;


		String yearString =	statementDateStr.substring(statementDateStr.length()-4, statementDateStr.length());
		year = Integer.parseInt(yearString);
		if (statementMonth < entryMonth)
		{
			year--;
		}


		return entryDateStr.substring(0,2).concat("/").concat(Integer.toString(entryMonth).concat("/").concat(Integer.toString(year)));

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
			if (table != null)
			{
				allData = table.select("td");
				if (allData != null)
				{
					Iterator<Element> itr = allData.iterator();
					Element itrElement;
					int kounter = 0;
					String statementDateString = statementDate.text().trim();
					while(itr.hasNext()) {

						itrElement =itr.next();
						String truncated = itrElement.text().replace((char)160,(char)32).trim();

						if (kounter == 0)
						{

							System.out.print(findYear(statementDateString,truncated));
						} else {
							System.out.print(truncated);
						}
						System.out.print("|");
						kounter++;
						if (kounter == 7)
						{
							kounter = 0;
							System.out.println("");
						}
					}
				}
			}
		} catch (IOException ioe) {
			System.err.println("An I/O error has occured:");
			ioe.printStackTrace();
		}

	}

}
