import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class XPathParserDemo
{
    public static void main(String[] args)
    {

        BufferedReader br = null;
        StringBuilder aBuilder = new StringBuilder();
        try
        {

            String sCurrentLine;

            br = new BufferedReader(new FileReader("C:\\Users\\narendar_s\\Downloads\\Demo.xml"));

            while ((sCurrentLine = br.readLine()) != null)
            {

                aBuilder.append(sCurrentLine);

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (br != null)
                    br.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        QueryParser aParser = new QueryParser();
        
        String aSample = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <jobs><job><Name>Test</Name><Design>Product</Design><Date day=\"21\" month=\"Feb\" year=\"2015\"></Date></job><job><Name>Test1</Name><Design>Developer</Design><Date day=\"28\" month=\"Mar\" year=\"2015\"></Date></job><job><Name>Test3</Name><Design>Engineer</Design><Date day=\"23\" month=\"Apr\" year=\"2015\"></Date></job></jobs>";

        String modifiedString = aBuilder.toString().replaceAll("&", " and ");
        aParser.parse(modifiedString.replaceAll("&amp;", " and "));
        

        List<Event> events = aParser.getEvents("//job[Date[@month='Mar' and @year='2014']]");

        for (Event event : events)
        {
            if (event != null)
            {
                System.out.println(event.toString());
                System.out.println("-----------------");
            }

        }

    }
}
