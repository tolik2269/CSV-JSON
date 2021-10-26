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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.xml";
        String fileNamecsv = "data.csv";
        List<Employee> list = parseXML(fileName);
        List<Employee> list2 = parseCSV(columnMapping, fileNamecsv);
        String json = listToJson(list);
        String json2 = listToJson(list2);
        String outputFileXml = "data2.json";
        String outputFileCvt = "data.json";
        writeString(json, outputFileXml);
        writeString(json2, outputFileCvt);
    }


    private static List<Employee> parseCSV(String [] columnMapping, String fileNamecsv) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileNamecsv))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
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

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(fileName));
        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                System.out.println("Текущий узел: " + node.getNodeName());
                Element element = (Element) node;
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

    private static void writeString(String json, String fileNamecsv) {
        try (FileWriter file = new FileWriter(fileNamecsv)){
            file.write(json.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
            }

}