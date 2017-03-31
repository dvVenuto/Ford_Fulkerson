import java.io.*;
import java.util.*;


public class FordFulkerson {


    public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){
        ArrayList<Integer> Stack = new ArrayList<Integer>(); // Store the DFS path from source to destination
        ArrayList<Integer> visitedNodes = new ArrayList<Integer>(); // Any node stored in this array has been visited

        visitedNodes.add(source);
        //temporary stack
        Stack<Integer> temp_stack = new Stack<Integer>();
        //push the souce to tmp
        temp_stack.push(source);

        while(!temp_stack.empty()) {
            Integer v = temp_stack.pop();
            // mark node as visited
            visitedNodes.add(v);

            // Remove all nodes until the node that brought us to v is found,  must check stack size, if E.weight = 0 also preform, since path is optimal
            while(Stack.size() != 0 && (graph.getEdge(getTop(Stack), v) == null || graph.getEdge(getTop(Stack), v).weight == 0)) {
                Stack.remove(Stack.size() - 1);
            }
            // Node v is now is the path we are going to take
            Stack.add(v);
            for(Edge e : graph.getEdges()) {
                // This generates the adj list
                if(e.nodes[0] == v && e.weight > 0 && !visitedNodes.contains(e.nodes[1])) {
                    // Check if we reach dest
                    if(e.nodes[1] == destination) {
                        // complete path and return
                        Stack.add(destination);
                        // clear the temp, dest not found, stop loop
                        temp_stack.clear();
                        break;
                    } else {
                        // create adj list of all [1] position nodes, destination nodes temp stack stores list
                        temp_stack.push(e.nodes[1]);
                    }
                }
            }
        }
        //end found
        return Stack;
    }

    //Pops from an array list
    public static int getTop(ArrayList<Integer> s){
        return s.get(s.size() -1);
    }



    public static void fordfulkerson(Integer source, Integer destination, WGraph graph, String filePath){
        String answer="";

        int maxFlow = 0;

        //init residual graph
        WGraph residual = new WGraph(graph); //path
        ArrayList<Integer> Path = new ArrayList<Integer>();
        // set all weights to 0 in init
        for(Edge e :graph.getEdges()) {
            e.weight = 0;
        }
        //do dfs until destination is found
        while((Path = pathDFS(source,destination,residual)).get(Path.size() - 1) == destination) {
            // find edge in the path with the minimum availibe capacity

            //augment is set to max intially
            Integer augment_flow = Integer.MAX_VALUE;
            for(int i = 1; i < Path.size(); i++) {
                //check residual and find edge
                Edge e = residual.getEdge(Path.get(i - 1), Path.get(i));
                //increase augment up to weight
                if(e.weight < augment_flow)
                    augment_flow = e.weight;
            }
            // Found new max, must update
            maxFlow += augment_flow;

            // update residual graph after augmenting
            for(int i = 1; i <Path.size(); i++) {
                // decrease the capacity of the edge in the residual graph by augmentation
                Edge e = residual.getEdge(Path.get(i - 1), Path.get(i));
                e.weight -= augment_flow;
                // update the previous (back) previous edge in residual
                Edge residualBack_edge = residual.getEdge(e.nodes[1], e.nodes[0]);
                if(residualBack_edge != null) {
                    // back edge already exists in the residual graph
                    residualBack_edge.weight += augment_flow;
                } else {
                    // add previous edge (back)
                    residual.addEdge(new Edge(e.nodes[1], e.nodes[0],augment_flow));
                }
            }
            // change the flows in original based on new path
            for(int i = 1; i < Path.size();i++) {
                Edge next_edge = graph.getEdge(Path.get(i - 1), Path.get(i));
                if(next_edge != null) {
                    next_edge.weight += augment_flow;
                } else {
                    // back edge
                    Edge prev_edge = graph.getEdge(Path.get(i), Path.get(i - 1));
                    prev_edge.weight -= augment_flow;
                }
            }
        }


        answer += maxFlow + "\n" + graph.toString();
        System.out.println(answer);
    }


    public static void writeAnswer(String path, String line){
        BufferedReader br = null;
        File file = new File(path);
        // if file doesnt exists, then create it

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(line+"\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        String file = args[0];
        File f = new File(file);
        WGraph g = new WGraph(file);
        fordfulkerson(g.getSource(),g.getDestination(),g,f.getAbsolutePath().replace(".txt",""));
    }
}
