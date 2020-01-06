import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Trace {

    public static void main(String[] args) {
        new Trace().getLogOfModel("File/Model2.pnml","File/test.txt");
    }

    private Map<String, Integer> tokenCount = new HashMap<>();
    private Map<String, Set<String>> tokenMap = new HashMap<>();
    private Map<String, List<Edge>> sourceEdgeMap;

    public void getLogOfModel(String modelFile, String logFile) {
        sourceEdgeMap = read(modelFile);
        List<String> traces = new ArrayList<>(findTrace("", new HashSet<>(sourceEdgeMap.get("-2"))));
        //写文件
        if (logFile == null || logFile.isEmpty()) {
            System.out.println("输出路径不正确");
            return;
        }
        try {
            FileWriter fileWriter = new FileWriter(new File(logFile));
            Collections.sort(traces);
            for (int i = 0; i < traces.size(); i++) {
                fileWriter.write(i + " : " + traces.get(i) + System.getProperty("line.separator"));
            }
            fileWriter.write("Path数量：" + traces.size());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<String> findTrace(String trace, Collection<Edge> nextOptions) {
        Set<String> traceList = new HashSet<>();
        nextOptions.removeIf(nextEdge -> !nextEdge.isPlace() && tokenCount.get(nextEdge.getTargetId()) != tokenMap.get(nextEdge.getTargetId()).size());
        for (Edge nextEdge : nextOptions) {
            List<Edge> edges = sourceEdgeMap.get(nextEdge.getTargetId());

            if (isEmpty(edges)) {
                return null;
            }
            if (exist(trace, nextEdge.getTargetId()) > 1) {
                continue;
            }
            if (nextEdge.isPlace()) {
                for (Edge edge : edges) {
                    Set<Edge> temp = new HashSet<>(nextOptions);
                    temp.remove(nextEdge);
                    temp.add(edge);
                    add(tokenMap, edge.getSourceId(), edge.getTargetId());
                    Set<String> targetTrace = findTrace(trace + "," + edge.getSourceId(), temp);
                    remove(tokenMap, edge.getSourceId(), edge.getTargetId());
                    traceList.addAll(targetTrace);
                }
            } else {
                Set<Edge> temp = new HashSet<>(nextOptions);
                temp.removeIf(edge -> edge.getTargetId().equals(nextEdge.getTargetId()));
                temp.addAll(edges);
                Set<String> targetTrace = findTrace(trace + "," + nextEdge.getTargetId(), temp);
                if (targetTrace == null) {
                    traceList.add(nextEdge.getTargetTitle());
                } else {
                    for (String string : targetTrace) {
                        String text = nextEdge.getTargetTitle() + "," + string;
                        traceList.add(text);
                    }
                }
            }
        }
        return traceList;
    }

    private static int exist(String source, String target) {
        String[] sources = source.split(",");
        int count = 0;
        for (int i = 0; i < sources.length; i++) {
            if (sources[i].equals(target)) {
                count++;
            }
        }
        return count;
    }

    private Map<String, List<Edge>> read(String fileInput) {
        Document doc = useDomReadXml(fileInput);

        NodeList nlst = doc.getElementsByTagName("place");
        Map<String, Node> nodeMap = new HashMap<>();
        int length = nlst.getLength();
        for (int i = 0; i < nlst.getLength(); i++) {
            String id = nlst.item(i).getAttributes().getNamedItem("id").getNodeValue();
            String title = doc.getElementsByTagName("text").item(i).getFirstChild().getNodeValue();

            nodeMap.put(id, new Node(id, title, true));
        }
        nlst = doc.getElementsByTagName("transition");

        for (int i = 0; i < nlst.getLength(); i++) {
            String id = nlst.item(i).getAttributes().getNamedItem("id").getNodeValue();
            String title = doc.getElementsByTagName("text").item(i + length).getFirstChild().getNodeValue();

            nodeMap.put(id, new Node(id, title, false));
        }

        Map<String, List<Edge>> sourceEdgeMap = new HashMap<>();
        Map<String, List<Edge>> targetEdgeMap = new HashMap<>();
        nlst = doc.getElementsByTagName("arc");
        for (int i = 0; i < nlst.getLength(); i++) {
            String id = nlst.item(i).getAttributes().getNamedItem("id").getNodeValue();
            String source = nlst.item(i).getAttributes().getNamedItem("source").getNodeValue();
            String sourceTitle = nodeMap.get(source).text;
            String target = nlst.item(i).getAttributes().getNamedItem("target").getNodeValue();
            String targetTitle = nodeMap.get(target).text;
            boolean isPlace = nodeMap.get(target).isPlace;

            Edge edge = new Edge(id, source, sourceTitle, isPlace, target, targetTitle);
            add(sourceEdgeMap, edge);
            if (targetEdgeMap.get(edge.getTargetId()) == null) {
                List<Edge> temp = new ArrayList<>();
                temp.add(edge);
                targetEdgeMap.put(edge.getTargetId(), temp);
            } else {
                targetEdgeMap.get(edge.getTargetId()).add(edge);
            }
        }
        for (String s : targetEdgeMap.keySet()) {
            tokenCount.put(s, targetEdgeMap.get(s).size());
        }
        Set<String> sourceIds = new HashSet<>(sourceEdgeMap.keySet());
        sourceIds.removeIf(targetEdgeMap.keySet()::contains);
        Node node = nodeMap.get(sourceIds.toArray()[0]);
        String sourceId = "-2";
        String targetId = node.id;
        Edge edge = new Edge(sourceId, sourceId, "start", true
                , targetId, node.text);
        add(sourceEdgeMap, edge);

        return sourceEdgeMap;
    }

    private static Document useDomReadXml(String sourcePath) {
        File file = new File(sourcePath);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            return doc;
        } catch (Exception e) {
            System.err.println("读取该xml文件失败");
            e.printStackTrace();
        }
        return null;
    }

    private static void add(Map<String, List<Edge>> sourceEdgeMap, Edge edge) {
        if (sourceEdgeMap.get(edge.getSourceId()) == null) {
            List<Edge> tempL = new ArrayList<>();
            tempL.add(edge);
            sourceEdgeMap.put(edge.getSourceId(), tempL);
        } else {
            sourceEdgeMap.get(edge.getSourceId()).add(edge);
        }
    }

    private static void add(Map<String, Set<String>> map, String value, String key) {
        if (map.get(key) == null) {
            Set<String> temp = new HashSet<>();
            temp.add(value);
            map.put(key, temp);
        } else {
            map.get(key).add(value);
        }
    }

    private static void remove(Map<String, Set<String>> map, String value, String key) {
        map.get(key).remove(value);
    }

    private static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

}

class Node{
    String id;
    boolean isPlace;
    String text;

    Node(String id, String text, boolean isPlace) {
        this.isPlace = isPlace;
        this.id = id;
        this.text = text;
    }
}

class Edge {
    private String id;
    private String sourceId;
    private String sourceTitle;
    private boolean isPlace;
    private String targetId;
    private String targetTitle;

    public Edge(String id, String sourceId, String sourceTitle, boolean isPlace, String targetId, String targetTitle) {
        this.id = id;
        this.sourceId = sourceId;
        this.sourceTitle = sourceTitle;
        this.isPlace = isPlace;
        this.targetId = targetId;
        this.targetTitle = targetTitle;
    }

    public String getSourceId() {
        return sourceId;
    }

    public boolean isPlace() {
        return isPlace;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(sourceId, edge.sourceId) && Objects.equals(targetId, edge.targetId) ;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, sourceId, sourceTitle, isPlace, targetId, targetTitle);
    }
}