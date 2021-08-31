import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
//        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.xml";
        List<Employee> list = parseXML("data.xml");
//        String json = listToJson(list);
    }

    private static List<Employee> parseXML(String s) throws ParserConfigurationException, IOException, SAXException {

//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document document= (Document) builder.parse(new File(s));
//        Node root = doc.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("data.xml"));
        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                System.out.println("Текущий узел: " + node.getNodeName());
                Element element = (Element) node;
                NamedNodeMap map = (NamedNodeMap) element.getAttributes();
                for (int a = 0; a < map.getLength(); a++) {
                    String attrName = map.item(a).getNodeName();
                    String attrValue = map.item(a).getNodeValue();
                    System.out.println("Атрибут: " + attrName + "; значение: " + attrValue);
                }

            }
        }
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee(
                Long.valueOf(employeeData.item(0).getNodeValue()),
                employeeData.item(1).getNodeValue(),
                employeeData.item(2).getNodeValue(),
                employeeData.item(3).getNodeValue(),
                Integer.valueOf(employeeData.item(4).getNodeValue())
        );
        employees.add(employee);
    }
    //   private static String listToJson(List<Employee> list) {
//        Type listType = new TypeToken<List<Employee>>() {
//        }.getType();
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        System.out.println(gson.toJson(list));
//        String json = gson.toJson(list, listType);
//        return listToJson(list);
//    }
//    private static String writeString(String json){
//        try (FileWriter file = new FileWriter("new_data.json")) {
//            file.write(json);
//            file.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return writeString(json);
//    }
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader("data.csv"))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            staff.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseCSV(columnMapping, fileName);
    }
}