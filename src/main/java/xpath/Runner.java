package xpath;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Runner {

    public static final String XPATH_EXPRESSION = "/root/ZH1YxlJh/J_AH/OkBs";
    private static Document document;
    private static XPathFactory xPathFactory;
    private static OutputStream nullOutputStream;

    static {
        try {

            document = loadDom();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        xPathFactory = XPathFactory.newInstance();

        nullOutputStream = OutputStream.nullOutputStream();
    }

    public static void main(String[] args) throws Exception {
        int num = 40000;

        // warm up
        runDefault(num * 2);
        runJaxen(num * 2);

        long start = System.currentTimeMillis();
        runDefault(num * 2);
        System.out.println("default one, ms: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        runJaxen(num * 2);
        System.out.println("jaxen, ms: " + (System.currentTimeMillis() - start));
    }

    private static void runJaxen(int num) throws JaxenException, IOException {
        for(int i = 0; i < num; i++) {
            evaluateXpathJaxen();
        }
    }

    private static void runDefault(int num) throws IOException, XPathExpressionException {
        for(int i = 0; i < num; i++) {
            evaluateXpath();
        }
    }

    public static void evaluateXpath() throws IOException, XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        XPathExpression expr = xPath.compile(XPATH_EXPRESSION);
        String value = expr.evaluate(document);
        nullOutputStream.write(value.getBytes());
    }

    public static void evaluateXpathJaxen() throws JaxenException, IOException {
        DOMXPath domxPath = new DOMXPath(XPATH_EXPRESSION);
        Object value = domxPath.stringValueOf(document);
        nullOutputStream.write(value.toString().getBytes());
    }

    private static Document loadDom() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        InputStream xmlStream = Runner.class.getClassLoader().getResourceAsStream("test.xml");
        Document document = documentBuilder.parse(xmlStream);
        return document;
    }
}
