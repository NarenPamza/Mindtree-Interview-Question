import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Demo Class is the Entry Point for the Parsing the WebContent and
 * filtering the Content. HTML Parser and Query Parser are the two Parsing
 * class used in this class. First we will be parsing the HTML Content of
 * the page one by one by using JSoup,JSoup will parse the HTML content and
 * will be converting it as XML Node Format. By using the XPATH Query, we
 * will retrieve list of the Event from the XML Node Format based on the
 * input.
 * 
 * @author narendar_s
 *
 */
public class Demo
{
    /**
     * Main Method for the Demo Class.
     * 
     * @param args command line arguments month and year to be passed
     */
    public static void main(String[] args)
    {
    	try
    	{
    		String month = null;
    		String year = null;
    		if(args.length > 1)
    		{
    			// Get the Command Line Arguments.
    			month = args[0];
    			year = args[1];
    		}
	        
	        if((month == null || month.isEmpty()) && (year == null || year.isEmpty() ))
	        {
	        	System.out.println("Please enter the month and year. eg: Mar 2015");
	        	
	        	try(BufferedReader aBufferedReader = new BufferedReader(new InputStreamReader(System.in)))
	        	{
					String anInput = aBufferedReader.readLine();
					String[] split = anInput.trim().split(" ");
					if (split.length > 1) 
					{
						month = split[0].trim();
						year = split[1].trim();
					} 
					else 
					{
						usage();
					}
				}
           }

        System.out.println("The Events for the Month and Year " + month + year + " are fetching.Please wait!!:");

        // Create Instance for HTML Parser
        HTMLParser aHtmlParser = new HTMLParser();

        // Get the Table String Value of all the Pages in a XML Format
        String xmlStringValue = aHtmlParser.getXMLStringValue();

        
        if (xmlStringValue != null && !xmlStringValue.isEmpty())
        {
            // Create Instance for QueryParser
            QueryParser aParser = new QueryParser();

            // Parse the XML String Value using Document Builder.
            aParser.parse(xmlStringValue);

            /*
             * By compiling XPath Query, We will be getting list of Matched
             * Event for the Query. In the below XPath query , we have
             * added condition for Month and year. Based on the Month and
             * year values, the XPath query will return the Jobs. By
             * Modifying the Query , we can alter the Output Data. Since
             * Month and year are a attribute for Date Node, we are adding
             * condition as " @month='MonthName' "
             * 
             * For Example: "/jobs/job/Name/text()" --> to get all the
             * Event Name.
             * 
             * "//job[Date[@day='21']]/Name/text()" --> to get all Event
             * Job Name for the Particular Date.
             * 
             * "//job[Date[@month='Mar' and @year='2014']]" --> to get the
             * JOB Events for the March month and 2014 year.
             */
            List<Event> events = aParser.getEvents("//job[Date[@month='" + month + "' and @year='" + year + "']]");

            System.out.println("The Events for the Month and Year " + month + year + " are:");
            for (Event event : events)
            {
                if (event != null)
                {
                    System.out.println(event.toString());
                    System.out.println("----------------");
                }
            }
        }
    	}
    	catch(Exception theException)
    	{
    		theException.printStackTrace();
    	} 
    }

    private static void usage()
    {
        System.out.println("usage: Please enter the Month and year in the format: Mar 2015");
        System.out.println("First three letters of the month, followed by year in numeric");
    }
}
