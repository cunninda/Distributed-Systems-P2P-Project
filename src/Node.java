
/**
 *
 * @author Darragh Cunningham
 * @Student Number: 09553321
 *
 *
 */
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Node {

    DatagramSocket client_socket;
    DatagramSocket client_listen_socket;
    static int portnumbers = 8767;
    InetAddress ip;
    int node_id; // Hash of the word
    String id; // ID relates to specific word in the Node
    ArrayList<String> URLS = new ArrayList<>(); //List of URLS
    ArrayList<Integer> port = new ArrayList<>();//List of ports
    ArrayList<Integer> other_node_ids = new ArrayList<>();//List of other node ids

    public Node(String ident) throws Exception {
        id = ident;
        ip = InetAddress.getByName("localhost");

    }

    // Creates a socket to use to connect to other nodes as well as a thread to handle packets.
    public void init(DatagramSocket udp_socket) throws Exception {
        client_socket = udp_socket;
        Thread thread1;
        try {
            thread1 = new Thread(new Threads("ServerThread", portnumbers, this));
            thread1.start();
        } catch (SocketException se) {
            se.printStackTrace();
        }
        int myport = portnumbers;
        portnumbers = portnumbers + 1;
        System.out.println("Ports in use " + myport);

    }

    // Stores a  URL in the node.
    public void storeURL(String url) {
        URLS.add(url);
    }

    public void indexPage(String url, String[] destinations) throws IOException {
        // Stores a URL for every word.		
        byte[] sendData;
        String[] linkedlist = {}, search = {}, table = {};
        for (String destination : destinations) {
            String sentence = new Packet("INDEX", hashCode(destination), node_id, 0, 0, "localhost", url, "", linkedlist, search, table).getPacket();
            sendData = sentence.getBytes();
            int node_ID = hashCode(destination);
            int index = 0;
            for (int q = 0; q < other_node_ids.size(); q++) {
                if (other_node_ids.get(q) == node_ID) {
                    index = q;
                }
            }
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port.get(index));
            try {
                client_socket.send(sendPacket);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    //returns NETWORK_ID, a generated number(locally) to identify peer network
    public long joinNetwork(InetSocketAddress bootstrap_node, String identifier, String target_identifier) {
        port.add(8767);
        port.add(8768);
        port.add(8769); // port numbers of the servers
        port.add(8770);
        port.add(8771);

        other_node_ids.add(hashCode("bootstrap"));
        other_node_ids.add(hashCode("beatles"));
        other_node_ids.add(hashCode("apple"));
        other_node_ids.add(hashCode("bacon"));
        other_node_ids.add(hashCode("bird"));// Node IDs

        //Receives list of nodes from bootstap node.
        byte[] sendData;
        String[] linkedlist = {}, search = {}, table = {};
        String sentence = new Packet("JOINING", hashCode(target_identifier), node_id, 0, 0, "localhost", "", "", linkedlist, search, table).getPacket();
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port.get(0));
        try {
            client_socket.send(sendPacket);
        } catch (IOException io) {
            io.printStackTrace();
        }
        return 0;
    }

    // Send packets to all nodes in my routing table telling them I'm leaving.
    public boolean leaveNetwork(long network_id) {
        byte[] sendData;
        String[] linkedlist = {}, search = {}, table = {};
        DatagramPacket sendPacket = null;
        for (int q = 0; q < other_node_ids.size(); q++) {
            String sentence = new Packet("LEAVING", other_node_ids.get(q), node_id, 0, 0, "localhost", "", "", linkedlist, search, table).getPacket();
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, ip, port.get(q));
        }
        try {
            client_socket.send(sendPacket);
        } catch (IOException io) {
            io.printStackTrace();
        }
        return true;
    }

    // Displays the search results of URLS found in the array.
    public SearchResult[] search(String[] words) {
        SearchResult[] resultsofSearch = new SearchResult[words.length];
        int n = 0;
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        String[] linkedlist = {}, search = {}, table = {};
        DatagramPacket sendPacket;

        for (int i = 0; i < words.length; i++) { // Send packet for search and wait for response.
            String sentence = new Packet("SEARCHING", hashCode(words[i]), node_id, 0, 0, "localhost", id, id, linkedlist, search, table).getPacket();
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, ip, port.get(i));

            try {
                client_socket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                client_socket.receive(receivePacket);
                String Sentence = new String(receivePacket.getData());
                String[] parsedPacket = parseString(Sentence);
                if (parsedPacket[1].equals("SEARCHER")) {
                    for (int j = 0, z = 0; j < parsedPacket.length; j++) {
                        if (parsedPacket[j].charAt(0) == 'x' && parsedPacket[j].charAt(1) == 'x' && parsedPacket[j].charAt(2) == 'x') {
                            resultsofSearch[n] = new SearchResult();
                            resultsofSearch[n].word = parsedPacket[5];
                            resultsofSearch[n].urls[z] = parsedPacket[j];
                            z++;
                        }
                    }
                }
                n++;

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return resultsofSearch;
    }

    // Parse the given string, necessory as parse lib wont work...
    static public String[] parseString(String sentence) {
        String[] hello = sentence.split(" ");
        return hello;
    }

    // Hashing Function
    public int hashCode(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = hash * 31 + str.charAt(i);
        }
        return Math.abs(hash);
    }

}
