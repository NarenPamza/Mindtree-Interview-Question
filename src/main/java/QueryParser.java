import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Query Parser class is used to Initialize the Document Builder and
 * Create a Document for the XML Content String Value.This class is used
 * XPath to compile XPath query and get the matched data for the Query from
 * the XML Content.
 * 
 * @author narendar_s
 *
 */
public class QueryParser
{
    private DocumentBuilder itsDocumentBuilder;

    private Document itsDocument;

    private XPath itsXPath;

    /**
     * Default Constructor.
     */
    public QueryParser()
    {
        initDocumentBuilder();
    }

    /**
     * Initialize the Document Builder and XPath.
     * 
     * @return boolean to check initialization is success.
     */
    private boolean initDocumentBuilder()
    {
        try
        {
            // Initialize the Document Builder.
            itsDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            // Initialize the XPath
            itsXPath = XPathFactory.newInstance().newXPath();
            return true;
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Parse the XML String Content using Document Builder and Create a
     * document.
     * 
     * @param theSourceXML
     */
    public void parse(final String theSourceXML)
    {
        try
        {
            // Parse the String XML Content.
            itsDocument = itsDocumentBuilder.parse(new ByteArrayInputStream(theSourceXML.getBytes()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the List of Events for the compiled XPath Query.Based on the
     * condition of Xpath Query, it will return the List of Events.
     * 
     * @param xpathQuery the XPATH query.
     * @return List of Events.
     */
    public List<Event> getEvents(String xpathQuery)
    {
        // Initialize the List to add the Job Events
        List<Event> events = new ArrayList<Event>();
        NodeList aNodeList;
        try
        {
            // Compile the XPath Query and Evaluate the document to get the
            // Node List.
            aNodeList = (NodeList)itsXPath.compile(xpathQuery).evaluate(itsDocument, XPathConstants.NODESET);

            for (int temp = 0; temp < aNodeList.getLength(); temp++)
            {
                Node nNode = aNodeList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element)nNode;

                    Event event = new Event();

                    // Get the Name Tag Value
                    if (eElement.getElementsByTagName("Name").item(0) != null)
                    {
                        event.setName(eElement.getElementsByTagName("Name").item(0).getTextContent());
                    }

                    // Get the Designation Tag Value
                    if (eElement.getElementsByTagName("Design").item(0) != null)
                    {
                        event.setDesignation(eElement.getElementsByTagName("Design").item(0).getTextContent());
                    }

                    // Get the Date Tag Value
                    if (eElement.getElementsByTagName("Date").item(0) != null)
                    {
                        // Get the Attributes Values of Tag Date.
                        NamedNodeMap attributes = eElement.getElementsByTagName("Date").item(0).getAttributes();

                        // From the Attributes Node, get the Day, Month and
                        // Year Attribute.
                        event.setDate(attributes.getNamedItem("day").getTextContent()
                            + attributes.getNamedItem("month").getTextContent() + attributes.getNamedItem("year").getTextContent());
                    }
                    events.add(event);
                }
            }
            return events;
        }
        catch (XPathExpressionException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}