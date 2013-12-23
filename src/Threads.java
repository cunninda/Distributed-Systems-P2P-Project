/**
 *
 * @author Darragh Cunningham
 * @Student Number: 09553321
 * 
 * 
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Threads implements Runnable{

    InetAddress ip;
    Node myNode;
    DatagramSocket listen_socket;
    DatagramSocket send_socket;
    Thread runner;

    public Threads(String threadName, int portNumber, Node node) throws Exception {
        this.ip = InetAddress.getByName("localhost");
        this.myNode = node;
        this.listen_socket = new DatagramSocket(portNumber);
        this.send_socket = new DatagramSocket();
        runner = new Thread(this, threadName);
        runner.start(); // 
    }

    public void run() {
        String[] linkedlist = {}, urls = {}, table = {};
        byte[] receiveData = new byte[1024];
        byte[] sendData;

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                listen_socket.receive(receivePacket);
                System.out.println("\n"+"Received Packet");
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
            int port = receivePacket.getPort();
            int index = 0;
            String sentence = new String(receivePacket.getData());
            System.out.println("Packet Details: " + sentence);
            System.out.println();
            String[] parsedPacket = parseString(sentence);

            if (Integer.parseInt(parsedPacket[3]) == myNode.node_id) {
                switch (parsedPacket[1]) {
                    case "INDEX": // Index a particular URL
                        System.out.println("Index");
                        myNode.storeURL(parsedPacket[7]);
                        break;

                    case "JOINING": // Joining
                        System.out.println("Joining Network");
                        myNode.other_node_ids.add(Integer.parseInt(parsedPacket[5]));
                        //send back my routing information
                        table = new String[myNode.other_node_ids.size() * 2];
                        for (int k = 0; k < myNode.other_node_ids.size(); k++) {
                            if (k % 2 == 0) {
                                table[k] = "" + myNode.other_node_ids.get(k);
                            } else {
                                table[k] = "" + myNode.port.get(k);
                            }
                        }
                        String routinginfo = new Packet("ROUTING", Integer.parseInt(parsedPacket[5]), myNode.node_id, 0, 0, "localhost", myNode.id, myNode.id, linkedlist, urls, table).getPacket();
                        for (int q = 0; q < myNode.other_node_ids.size(); q++) {
                            if (myNode.other_node_ids.get(q) == Integer.parseInt(parsedPacket[5])) {
                                index = q;
                                break;
                            }
                        }
                        // Send message back to requesting node.
                        sendData = routinginfo.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, myNode.port.get(index));
                        try {
                            send_socket.send(sendPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "JOINING_RELAY": // Joining Relay
                        System.out.println("JoiningRelay");
                        break;

                    case "LEAVING": // Leaving
                        System.out.println("I'm Leaving");
                        //Remove entry from routing table
                        for (int q = 0; q < myNode.other_node_ids.size(); q++) {
                            if (myNode.other_node_ids.get(q) == Integer.parseInt(parsedPacket[3])) {
                                myNode.other_node_ids.remove(q);
                                myNode.port.remove(q);
                                break;
                            }
                        }
                        break;

                    case "PING": // Ping
                        System.out.println("Ping");
                        String ack = new Packet("ACK", Integer.parseInt(parsedPacket[5]), myNode.node_id, 0, 0, "localhost", myNode.id, myNode.id, linkedlist, urls, table).getPacket();

                        index = 0;
                        for (int q = 0; q < myNode.other_node_ids.size(); q++) {
                            if (myNode.other_node_ids.get(q) == Integer.parseInt(parsedPacket[5])) {
                                index = q;
                            }
                        }
                        sendData = ack.getBytes();
                        DatagramPacket sendPacketAck = new DatagramPacket(sendData, sendData.length, ip, myNode.port.get(index));
                        try {
                            send_socket.send(sendPacketAck);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "ACK": // ACK
                        //If recieved print. See Packet for more details.
                        System.out.println("ACK");
                        break;

                    case "SEARCHING": // Searching
                        System.out.println("Searching");
                        // Request from a node for a list of all URLS that I possess.
                        // Create that list.
                        urls = new String[myNode.URLS.size()];
                        for (int i = 0; i < myNode.URLS.size(); i++) {
                            urls[i] = myNode.URLS.get(i);
                        }
                        String message = new Packet("SEARCHER", Integer.parseInt(parsedPacket[3]), myNode.node_id, 0, 0, "localhost", myNode.id, myNode.id, linkedlist, urls, table).getPacket();

                        // Send message back to requesting node.
                        sendData = message.getBytes();
                        DatagramPacket sendPackeT = new DatagramPacket(sendData, sendData.length, ip, receivePacket.getPort());
                        try {
                            send_socket.send(sendPackeT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "SEARCHER": // Search Response is done in node.search
                        System.out.println("SearchResponse");
                        break;

                    case "ROUTING": //Routing Info
                        //Received a routing message from a node, message should be filled with their routing information, 
                        // need to parse the message to get the routing information, fill it into our routing table
                        //The joining node should be receiving this message
                        for (int k = 11; k < parsedPacket.length - 1; k++) {
                            if (k % 2 == 1) {
                                myNode.other_node_ids.add(Integer.parseInt(parsedPacket[k]));
                            } else {
                                myNode.port.add(Integer.parseInt(parsedPacket[k]));
                            }
                        }
                        System.out.println("Routing Information");
                        break;

                    default:
                        System.out.println("Default");
                        break;
                }
            } else {
                InetAddress IPAddress = receivePacket.getAddress();
                DatagramPacket sendPacket = new DatagramPacket(receiveData, receiveData.length, IPAddress, port);
                try {
                    listen_socket.send(sendPacket);
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                }
            }
        }
    }

    private String[] parseString(String sentence) {
        String[] hello = sentence.split(" ");
        return hello;
    }

    

}
