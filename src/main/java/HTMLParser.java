import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The HTML Parser class parse the HTML content of the Web Page and read
 * the Table Cell of the HTML Content and Convert it to a XML Node
 * Formatted String.
 * 
 * @author narendar_s
 *
 */
public class HTMLParser
{
    private int itsPageCount;

    private int itsPageNumber = 1;

    private StringBuilder XMLValues = new StringBuilder();
    
    private static String URL = "https://www.elitmus.com/jobs?page=";

    /**
     * Default Constructor
     */
    public HTMLParser()
    {
        try
        {
            // Create Instance for TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]
            {
                new X509TrustManager()
                {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers()
                    {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException
                    {
                        // Nothing to do.
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException
                    {
                        // Nothing to do.
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");

            /*
             * Initialize the Certificate needed for HTTPS based site to
             * create URL connection. For creating URL connection in HTTPS
             * Sites, we need to init SSLContext.
             */
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            // Sets the default SSLSocketFactory for the HttpsURLConnection
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier()
            {
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            };

            // Sets the default HostnameVerifier inherited by a new
            // instance of this class for the HttpsURLConnection.
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Make connection to the Page
            fetchDataFromPage(itsPageNumber);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Make Connection method create a new URL Connection to the Site and
     * create an open connection to get the Input Stream of the Page
     * 
     * @param theJobValue the Job Page Number
     */
    private void fetchDataFromPage(int theJobValue)
    {
        URL url;
        try
        {
            /*
             * Check Whether the Job Page Number is equal to the Total Page
             * Count.Once, this condition satisfy, we have reached to the
             * last page and need to stop the recursion process
             */
            if (theJobValue == itsPageCount)
            {
                XMLValues.append("</jobs>");
                return;
            }
            else
            {
                // Create URL Instance
                url = new URL(URL + theJobValue);
                URLConnection con = url.openConnection();
                parseContent(con);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Return the XML String Value for the Content of the Total Pages.
     * 
     * @return string value
     */
    public String getXMLStringValue()
    {
        if (XMLValues != null)
        {
            String modifiedString = XMLValues.toString().replaceAll("&", " and ");
            return modifiedString.replaceAll("&amp;", " and ");
        }
        return null;
    }

    /**
     * Parse the Content of the Page by getting the inputStream from the
     * URLConnection. Read the Content from InputStream and append it to a
     * String Builder.
     * 
     * @param con URLConnection
     */
    private void parseContent(URLConnection con)
    {
        if (con != null)
        {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())))
            {
                StringBuilder aBuilder = new StringBuilder();

                String input;

                // Read the Content
                while ((input = br.readLine()) != null)
                {
                    aBuilder.append(input);
                }

                // Pass the String Content of the Page to JSoup Parser to
                // Parse the HTML Content.
                jsoupParser(aBuilder.toString());

                // Recursive to get the content of the Next Page.
                fetchDataFromPage(itsPageNumber++);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * The JSoup Parser Parse the HTML Content from the Webpage Content and
     * read the Table td and get append to the XML Node Tag. Finally, the
     * HTML Table Td content wil be converted in XML node structure.
     */
    private void jsoupParser(String value)
    {
        try
        {
            // Jsoup will parse the HTML Content.
            Document doc = Jsoup.parse(value);

            // Find the Page Number only for one time.
            if (itsPageNumber == 1)
            {
                // Get the Page Count from the Page
                Element aPagination = doc.select("div[class=pagination]").first();
                String text = aPagination.text();

                if (text != null && !text.isEmpty())
                {
                    String[] pageArray = text.split(" ");
                    if (pageArray != null)
                    {
                        // Read the Page Count from the data array.
                        itsPageCount = Integer.parseInt(pageArray[pageArray.length - 3]);
                    }
                }

                // Create the Root Node and XML Format
                XMLValues.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                XMLValues.append("<jobs>");
            }

            // Select the Table which has the all the Events.
            Element table = doc.select("table[class=table_box]").first();

            // Looping the TR of the Table.
            for (Element element : table.select("tr"))
            {
                // Create a Job Node.
                XMLValues.append("<job>");

                // Select the TD of the TR to get the individual TD.
                Elements aElement = element.select("td");

                // Create Name Node.
                XMLValues.append("<Name>" + aElement.get(0).text() + "</Name>");

                // Create Designation Node.
                XMLValues.append("<Design>" + aElement.get(1).text() + "</Design>");

                String Date = aElement.get(2).text();
                String[] aDateValue = Date.split(" ");
                if (aDateValue.length > 2)
                {
                    // Create Date Node with Attribute as Day Month and
                    // Year.
                    XMLValues.append("<Date day=\"" + aDateValue[0] + "\" month=\"" + aDateValue[1] + "\" year=\"" + aDateValue[2]
                        + "\">" + "</Date>");
                }
                else
                {
                    XMLValues.append("<Date>" + Date + "</Date>");
                }
                XMLValues.append("</job>");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
