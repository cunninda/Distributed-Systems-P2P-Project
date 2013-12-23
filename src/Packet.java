/**
 *
 * @author Darragh Cunningham
 * @Student Number: 09553321
 * 
 * 
 */
public class Packet {

    int target_id;
    int sender_id;
    int gateway_id;
    int node_id;
    String ip_address;
    String keyword;
    String words;
    String[] search_response;
    String[] links;
    String[] routing_t;
    public String returnPacket;

    public Packet(String type, int target, int sender, int node, int gateway, String ip, String key, String word, String[] linkedlist, String[] search, String[] table) {
        this.target_id = target;
        this.sender_id = sender;
        this.keyword = key;
        this.links = linkedlist;
        this.node_id = node;
        this.ip_address = ip;
        this.gateway_id = gateway;
        this.routing_t = table;
        this.words = word;
        this.search_response = search;

        switch (type) {
            case  "INDEX":
                /*
                 Index Template as given
                 "type": "INDEX", //string
                 "target_id": "34", //the target id
                 "sender_id": "34", // a non-negative number of order 2'^32^', of the message originator
                 "keyword": "XXX", //the word being indexed
                 "link": [
                 "http://www.newindex.com", // the url the word is found in
                 "http://www.xyz.com"]
                 */

                returnPacket ="\n"+ "\"type\":\" INDEX \","+"\n"
                        + "\"target_id\":\" " + target_id + " \","+"\n"
                        + "\"sender_id\":\" " + sender_id + " \","+"\n"
                        + "\"keyword\":\" " + keyword + " \","+"\n"
                        + "\"link\":[ ";
                for (String linkedlist1 : linkedlist) {
                    returnPacket += "" + linkedlist1 + " ";
                }
                returnPacket += "]";
                break;

            case "JOINING": // Joining
		  /*"type": "JOINING_NETWORK_SIMPLIFIED", // a string
                 "node_id": "42", // a non-negative number of order 2'^32^', indicating the id of the joining node).
                 "target_id": "42", // a non-negative number of order 2'^32^', indicating the target node for this message.
                 "ip_address": "199.1.5.2" // the ip address of the joining node*/

                returnPacket = "\"type\":\" JOINING \","+"\n"
                        + "\"target_id\":\" " + target_id + " \","+"\n"
                        + "\"node_id\":\" " + node_id + " \","+"\n"
                        + "\"ip_address\":\" " + ip_address + " \"";
                break;

            case "JOINING_RELAY": // Joining Relay
		/*  "type": "JOINING_NETWORK_RELAY_SIMPLIFIED", // a string
                 "node_id": "42", // a non-negative number of order 2'^32^', indicating the id of the joining node).
                 "target_id": "42", // a non-negative number of order 2'^32^', indicating the target node for this message.
                 "gateway_id": "34", // a non-negative number of order 2'^32^', of the gateway node*/

                returnPacket = "\"type\":\" JOINING_RELAY \","
                        + "\"target_id\":\" " + target_id + " \","
                        + "\"node_id\":\" " + node_id + " \","
                        + "\"gateway_id\":\" " + gateway_id + " \"";
                break;

            case "ROUTING": //Routing Info
		 /* "type": "ROUTING_INFO", // a string
                 "gateway_id": "34", // a non-negative number of order 2'^32^', of the gateway node
                 "node_id": "42", // a non-negative number of order 2'^32^', indicating the target node (and also the id of the joining node).
                 "ip_address": "199.1.5.2" // the ip address of the node sending the routing information
                 "route_table":
                 [
                 {
                 "node_id": "3", // a non-negative number of order 2'^32^'.
                 "ip_address": "199.1.5.3" // the ip address of node 3
                 },
                 {
                 "node_id": "22", // a non-negative number of order 2'^32^'.
                 "ip_address": "199.1.5.4" // the ip address of  node 22
                 }
                 ]*/

                returnPacket = "\"type\":\" ROUTING \","
                        + "\"target_id\":\" " + target_id + " \","
                        + "\"gateway_id\":\" " + gateway_id + " \","
                        + "\"node_id\":\" " + node_id + " \","
                        + "\"ip_address\":\" " + ip_address + " \","
                        + "\"route_table\":[ ";
                for (String routing_t1 : routing_t) {
                    returnPacket += "" + routing_t1 + " ";
                }
                returnPacket += "]";
                break;
            default:
                System.out.println("Default");
                returnPacket = "Type was wrong, sorry about that.";
                break;

            case "LEAVING": // Leaving
		 /* "type": "LEAVING_NETWORK", // a string
                 "node_id": "42", // a non-negative number of order 2'^32^' identifying the leaving node.*/

                returnPacket = "\"type\":\" LEAVING \","
                        + "\"target_id\":\" " + target_id + " \","
                        + "\"node_id\":\" " + node_id + " \"";
                break;

            case "PING": // Ping
			/*"type": "PING", // a string
                 "target_id": "23", // a non-negative number of order 2'^32^', identifying the suspected dead node.
                 "sender_id": "56", // a non-negative number of order 2'^32^', identifying the originator  of the ping (does not change)
                 "ip_address": "199.1.5.4" // the ip address of  node sending the message (changes each hop) */

                returnPacket = "\"type\":\" PING \","
                        + "\"target_id\":\" " + target_id + " \","
                        + "\"sender_id\":\" " + sender_id + " \","
                        + "\"ip_address\":\" " + ip_address + " \"";
                break;

            case "ACK": // ACK
		 /* "type": "ACK", // a string
                 "node_id": "23", // a non-negative number of order 2'^32^', identifying the suspected dead node.
                 "ip_address": "199.1.5.4" // the ip address of  sending node, this changes on each hop*/

                returnPacket = "\"type\":\" ACK \","
                        + "\"target_id\":\" " + target_id + " \","
                        + "\"node_id\":\" " + node_id + " \","
                        + "\"ip_address\":\" " + ip_address + " \"";
                break;

            case "SEARCHER": // Search Response
		 /* "type": "SEARCH_RESPONSE",
                 "word": "word", // The word to search for
                 "node_id": "45",  // target node id
                 "sender_id": "34", // a non-negative number of order 2'^32^', of this message originator
                 "response":
                 [
                 {
                 url: "www.dsg.cs.tcd.ie/",  //url
                 rank: "32"  //rank
                 },
                 {
                 url: "www.scss.tcd.ie/courses/mscnds/",  //url
                 rank: "1" //rank
                 }
                 ]*/

                returnPacket = "\"type\":\" SEARCHER \","
                        + "\"target_id\":\" " + target_id + " \","
                        + "\"word\":\" " + words + " \","
                        + "\"sender_id\":\" " + sender_id + " \","
                        + "\"response\":[ ";
                for (String search1 : search) {
                    returnPacket += "" + search1 + " ";
                }
                returnPacket += "]";
                break;

            case "SEARCHING": // Searching
		/*  "type": "SEARCH", // string
                 "word": "apple", // The word to search for
                 "node_id": "34",  // target node id
                 "sender_id": "34", // a non-negative number of order 2'^32^', of this message originator*/

                returnPacket = "\"type\":\" SEARCHING \","
                        + "\"target_id\":\" " + target_id + " \","
                        + "\"word\":\" " + words + " \","
                        + "\"sender_id\":\" " + sender_id + " \"";
                break;
        }
    }

    public String getPacket() {
        return returnPacket;
    }

}
