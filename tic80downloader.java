/*
	launch as script with "java tic80downloader.java" with a recent version of java
*/


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;

public class Tic80Scraper {
static class StringUtils {
	
	public static boolean isEmpty(String s)
	{
		return (s==null)||(s.equals(""));
	}
	  
		 public static String getStringBetween(String in, String token1, String token2, boolean first1, boolean first2) {
			 
			 if(isEmpty(in) || isEmpty(token1) || isEmpty(token2)) {
				 return "";
			 }

			 int index1 = (first1 ? in.indexOf(token1) : in.lastIndexOf(token1));
			 int index2 = (first2 ? in.indexOf(token2, index1) : in.lastIndexOf(token2));
			 
			 if (index1==-1 || index2==-1) {
				 return "";
			 }
			 else {
			      index1 += token1.length();
			 }
			 return in.substring(index1, index2);
		 }
		 
		 public static String getSubstring(String field, int begin, int end) {
			 int length = field.length();
			 if (length < begin){
				 return "";
			 }
			 else {
				 if(length >= end) {
					 return field.substring(begin, end);
				 }
				 else {
					 return field.substring(begin, length);
				 }
			 }
		 }
	
	

    public static final String unescapeHtml3(final String input) {
        StringWriter writer = null;
        int len = input.length();
        int i = 1;
        int st = 0;
        while (true) {
            // look for '&'
            while (i < len && input.charAt(i-1) != '&')
                i++;
            if (i >= len)
                break;

            // found '&', look for ';'
            int j = i;
            while (j < len && j < i + MAX_ESCAPE + 1 && input.charAt(j) != ';')
                j++;
            if (j == len || j < i + MIN_ESCAPE || j == i + MAX_ESCAPE + 1) {
                i++;
                continue;
            }

            // found escape 
            if (input.charAt(i) == '#') {
                // numeric escape
                int k = i + 1;
                int radix = 10;

                final char firstChar = input.charAt(k);
                if (firstChar == 'x' || firstChar == 'X') {
                    k++;
                    radix = 16;
                }

                try {
                    int entityValue = Integer.parseInt(input.substring(k, j), radix);

                    if (writer == null) 
                        writer = new StringWriter(input.length());
                    writer.append(input.substring(st, i - 1));

                    if (entityValue > 0xFFFF) {
                        final char[] chrs = Character.toChars(entityValue);
                        writer.write(chrs[0]);
                        writer.write(chrs[1]);
                    } else {
                        writer.write(entityValue);
                    }

                } catch (NumberFormatException ex) { 
                    i++;
                    continue;
                }
            }
            else {
                // named escape
                CharSequence value = lookupMap.get(input.substring(i, j));
                if (value == null) {
                    i++;
                    continue;
                }

                if (writer == null) 
                    writer = new StringWriter(input.length());
                writer.append(input.substring(st, i - 1));

                writer.append(value);
            }

            // skip escape
            st = j + 1;
            i = st;
        }

        if (writer != null) {
            writer.append(input.substring(st, len));
            return writer.toString();
        }
        return input;
    }

    private static final String[][] ESCAPES = {
        {"\"",     "quot"}, // " - double-quote
        {"&",      "amp"}, // & - ampersand
        {"<",      "lt"}, // < - less-than
        {">",      "gt"}, // > - greater-than

        // Mapping to escape ISO-8859-1 characters to their named HTML 3.x equivalents.
        {"\u00A0", "nbsp"}, // non-breaking space
        {"\u00A1", "iexcl"}, // inverted exclamation mark
        {"\u00A2", "cent"}, // cent sign
        {"\u00A3", "pound"}, // pound sign
        {"\u00A4", "curren"}, // currency sign
        {"\u00A5", "yen"}, // yen sign = yuan sign
        {"\u00A6", "brvbar"}, // broken bar = broken vertical bar
        {"\u00A7", "sect"}, // section sign
        {"\u00A8", "uml"}, // diaeresis = spacing diaeresis
        {"\u00A9", "copy"}, // © - copyright sign
        {"\u00AA", "ordf"}, // feminine ordinal indicator
        {"\u00AB", "laquo"}, // left-pointing double angle quotation mark = left pointing guillemet
        {"\u00AC", "not"}, // not sign
        {"\u00AD", "shy"}, // soft hyphen = discretionary hyphen
        {"\u00AE", "reg"}, // ® - registered trademark sign
        {"\u00AF", "macr"}, // macron = spacing macron = overline = APL overbar
        {"\u00B0", "deg"}, // degree sign
        {"\u00B1", "plusmn"}, // plus-minus sign = plus-or-minus sign
        {"\u00B2", "sup2"}, // superscript two = superscript digit two = squared
        {"\u00B3", "sup3"}, // superscript three = superscript digit three = cubed
        {"\u00B4", "acute"}, // acute accent = spacing acute
        {"\u00B5", "micro"}, // micro sign
        {"\u00B6", "para"}, // pilcrow sign = paragraph sign
        {"\u00B7", "middot"}, // middle dot = Georgian comma = Greek middle dot
        {"\u00B8", "cedil"}, // cedilla = spacing cedilla
        {"\u00B9", "sup1"}, // superscript one = superscript digit one
        {"\u00BA", "ordm"}, // masculine ordinal indicator
        {"\u00BB", "raquo"}, // right-pointing double angle quotation mark = right pointing guillemet
        {"\u00BC", "frac14"}, // vulgar fraction one quarter = fraction one quarter
        {"\u00BD", "frac12"}, // vulgar fraction one half = fraction one half
        {"\u00BE", "frac34"}, // vulgar fraction three quarters = fraction three quarters
        {"\u00BF", "iquest"}, // inverted question mark = turned question mark
        {"\u00C0", "Agrave"}, // ? - uppercase A, grave accent
        {"\u00C1", "Aacute"}, // ? - uppercase A, acute accent
        {"\u00C2", "Acirc"}, // ? - uppercase A, circumflex accent
        {"\u00C3", "Atilde"}, // ? - uppercase A, tilde
        {"\u00C4", "Auml"}, // ? - uppercase A, umlaut
        {"\u00C5", "Aring"}, // ? - uppercase A, ring
        {"\u00C6", "AElig"}, // ? - uppercase AE
        {"\u00C7", "Ccedil"}, // ? - uppercase C, cedilla
        {"\u00C8", "Egrave"}, // ? - uppercase E, grave accent
        {"\u00C9", "Eacute"}, // ? - uppercase E, acute accent
        {"\u00CA", "Ecirc"}, // ? - uppercase E, circumflex accent
        {"\u00CB", "Euml"}, // ? - uppercase E, umlaut
        {"\u00CC", "Igrave"}, // ? - uppercase I, grave accent
        {"\u00CD", "Iacute"}, // ? - uppercase I, acute accent
        {"\u00CE", "Icirc"}, // ? - uppercase I, circumflex accent
        {"\u00CF", "Iuml"}, // ? - uppercase I, umlaut
        {"\u00D0", "ETH"}, // ? - uppercase Eth, Icelandic
        {"\u00D1", "Ntilde"}, // ? - uppercase N, tilde
        {"\u00D2", "Ograve"}, // ? - uppercase O, grave accent
        {"\u00D3", "Oacute"}, // ? - uppercase O, acute accent
        {"\u00D4", "Ocirc"}, // ? - uppercase O, circumflex accent
        {"\u00D5", "Otilde"}, // ? - uppercase O, tilde
        {"\u00D6", "Ouml"}, // ? - uppercase O, umlaut
        {"\u00D7", "times"}, // multiplication sign
        {"\u00D8", "Oslash"}, // ? - uppercase O, slash
        {"\u00D9", "Ugrave"}, // ? - uppercase U, grave accent
        {"\u00DA", "Uacute"}, // ? - uppercase U, acute accent
        {"\u00DB", "Ucirc"}, // ? - uppercase U, circumflex accent
        {"\u00DC", "Uuml"}, // ? - uppercase U, umlaut
        {"\u00DD", "Yacute"}, // ? - uppercase Y, acute accent
        {"\u00DE", "THORN"}, // ? - uppercase THORN, Icelandic
        {"\u00DF", "szlig"}, // ? - lowercase sharps, German
        {"\u00E0", "agrave"}, // ? - lowercase a, grave accent
        {"\u00E1", "aacute"}, // ? - lowercase a, acute accent
        {"\u00E2", "acirc"}, // ? - lowercase a, circumflex accent
        {"\u00E3", "atilde"}, // ? - lowercase a, tilde
        {"\u00E4", "auml"}, // ? - lowercase a, umlaut
        {"\u00E5", "aring"}, // ? - lowercase a, ring
        {"\u00E6", "aelig"}, // ? - lowercase ae
        {"\u00E7", "ccedil"}, // ? - lowercase c, cedilla
        {"\u00E8", "egrave"}, // ? - lowercase e, grave accent
        {"\u00E9", "eacute"}, // ? - lowercase e, acute accent
        {"\u00EA", "ecirc"}, // ? - lowercase e, circumflex accent
        {"\u00EB", "euml"}, // ? - lowercase e, umlaut
        {"\u00EC", "igrave"}, // ? - lowercase i, grave accent
        {"\u00ED", "iacute"}, // ? - lowercase i, acute accent
        {"\u00EE", "icirc"}, // ? - lowercase i, circumflex accent
        {"\u00EF", "iuml"}, // ? - lowercase i, umlaut
        {"\u00F0", "eth"}, // ? - lowercase eth, Icelandic
        {"\u00F1", "ntilde"}, // ? - lowercase n, tilde
        {"\u00F2", "ograve"}, // ? - lowercase o, grave accent
        {"\u00F3", "oacute"}, // ? - lowercase o, acute accent
        {"\u00F4", "ocirc"}, // ? - lowercase o, circumflex accent
        {"\u00F5", "otilde"}, // ? - lowercase o, tilde
        {"\u00F6", "ouml"}, // ? - lowercase o, umlaut
        {"\u00F7", "divide"}, // division sign
        {"\u00F8", "oslash"}, // ? - lowercase o, slash
        {"\u00F9", "ugrave"}, // ? - lowercase u, grave accent
        {"\u00FA", "uacute"}, // ? - lowercase u, acute accent
        {"\u00FB", "ucirc"}, // ? - lowercase u, circumflex accent
        {"\u00FC", "uuml"}, // ? - lowercase u, umlaut
        {"\u00FD", "yacute"}, // ? - lowercase y, acute accent
        {"\u00FE", "thorn"}, // ? - lowercase thorn, Icelandic
        {"\u00FF", "yuml"}, // ? - lowercase y, umlaut
    };

    private static final int MIN_ESCAPE = 2;
    private static final int MAX_ESCAPE = 6;

    private static final HashMap<String, CharSequence> lookupMap;
    static {
        lookupMap = new HashMap<String, CharSequence>();
        for (final CharSequence[] seq : ESCAPES) 
            lookupMap.put(seq[1].toString(), seq[0]);
    }

}
	
	private static String downloadAndCrc32(String url, OutputStream os) throws Exception
	{
		CRC32 c = new CRC32();
		try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {
		    byte dataBuffer[] = new byte[1024];
		    int bytesRead;
		    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
		    	c.update(dataBuffer, 0, bytesRead);
		        os.write(dataBuffer, 0, bytesRead);
		    }
		}
		return Long.toHexString(c.getValue());
	}

	private static String downloadAndCrc32(String url, String fileName) throws Exception
	{
		
		try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
		    return downloadAndCrc32(url, fileOutputStream);
		}
	}
	
	private static String downloadPage(String url) throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		downloadAndCrc32(url, baos);
		baos.flush();
		return new String(baos.toByteArray(), "UTF-8");
		 
	}

	private static String nameToFile(String s)
	{
		s = s.replace(' ', '.');
		s = s.replaceAll("[^a-zA-Z0-9\\.]", "");
		return s.toLowerCase()+".tic";
	}
	
	public static void main(String[] args) throws Exception 
	{
		System.out.println("Fetching main play page..");
		String s = downloadPage("https://tic.computer/play");
		List<String> items = new ArrayList<>();
		while(true)
		{
			String introtoken = "<div class=\"col-md-4\"><div class=\"cart\">";
			int i = s.indexOf(introtoken, 0);
			if (i>=0) {
				s = s.substring(i+introtoken.length());
				String id = escape( StringUtils.getStringBetween(s, "<a href=\"/play?cart=", "\"><img", true, true));
				String hash = escape( StringUtils.getStringBetween(s, "src=\"/cart/", "/cover.gif", true, true) );
				String name = escape( StringUtils.getStringBetween(s, "<h2>", "</h2>", true, true));
				String by = escape( StringUtils.getStringBetween(s, "<div class=\"text-muted\">by ", "</div>", true, true));
				String fileName = nameToFile(name);
				String url = "https://tic.computer/cart/"+hash+"/cart.tic";
				
				
				System.out.println("Downloading "+name + " to "+fileName);
				String crc = downloadAndCrc32(url, fileName);
				
				items.add(String.join("\t", notab(id), notab(hash), notab(name), notab(by), notab(fileName), notab(crc), notab(url)));
				
			}
			else
			{
				break;
			}
		}
		Files.write(Paths.get("tic80.tsv"), items);
	}

	private static CharSequence notab(String s) {
		
		return s.replace('\t',  ' ');
	}

	private static String escape(String s) {
		
		return StringUtils.unescapeHtml3(s);
	}

}

