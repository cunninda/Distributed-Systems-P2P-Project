/**
 *
 * @author Darragh Cunningham
 * @Student Number: 09553321
 * 
 * 
 */

import java.net.DatagramSocket;
import java.net.InetSocketAddress;


public class Main {
	
	public static void main(String[] args) throws Exception{
		InetSocketAddress bootstrap = new InetSocketAddress("localhost", 8768);
		String[] i = {"apple","bacon"};
                String[] j = {"beatles","bird"};
                
             
		Node bootstrapNode = new Node("bootstrap"); 
                bootstrapNode.init(new DatagramSocket()); 
                bootstrapNode.joinNetwork(bootstrap, "bootstrap", "bootstrap");
                
             
                Node n = new Node("bird");
		Node x = new Node("beatles"); 
		Node y = new Node("apple");
		Node z = new Node("bacon");
               
            
                x.init(new DatagramSocket()); x.joinNetwork(bootstrap, "beatles", "bacon");
                y.init(new DatagramSocket()); y.joinNetwork(bootstrap, "apple", "beatles");
                z.init(new DatagramSocket()); z.joinNetwork(bootstrap, "bacon", "apple");
                
             
		x.indexPage("www.facebook.com", i);	
		y.indexPage("www.google.com", j);		
		x.indexPage("www.bing.com", i);		
		z.indexPage("www.tcd.ie", j);		
		x.indexPage("www.irishtimes.com", i);
                z.indexPage("www.rte.ie", i);

		
	}

}
class SearchResult {
    String[] urls;
    String word;
    
}
