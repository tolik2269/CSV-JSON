import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.xml";
        List<Employee> list = parseXML(new String[]{"data.xml"});
        String json = listToJson(list);
        List<Employee> listCsv = parseCSV(new String[]{"data.csv"});

        String jsonCSV = listToJson(listCsv);
        writeString(json);
    }

    private static List<Employee> parseCSV(String[] columnMapping) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader("data.csv"))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
            staff.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    private static List<Employee> parseXML(String[] columnMapping) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("data.xml"));
        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                System.out.println("Текущий узел: " + node.getNodeName());
                Element element = (Element) node;
                NamedNodeMap map = (NamedNodeMap) element.getAttributes();
                Employee employee = new Employee(
                        Long.valueOf(element.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue()),
                        element.getElementsByTagName("firstName").item(0).getChildNodes().item(0).getNodeValue(),
                        element.getElementsByTagName("lastName").item(0).getChildNodes().item(0).getNodeValue(),
                        element.getElementsByTagName("country").item(0).getChildNodes().item(0).getNodeValue(),
                        Integer.valueOf(element.getElementsByTagName("age").item(0).getChildNodes().item(0).getNodeValue())
                );
                    employees.add(employee);
                }

            }
        return employees;
    }
       private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        System.out.println(gson.toJson(list));
           return gson.toJson(list, listType);
    }
    private static void writeString(String json){

        try (FileWriter file = new FileWriter("new_data.json")) {
            file.write(json.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}