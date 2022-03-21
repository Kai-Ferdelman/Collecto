package communication.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ServerTest {

	@BeforeEach
	void setUp() throws Exception {
		
	}
	@Disabled("")
	@Test
	void testServer() throws UnknownHostException, IOException {
		var sock = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
		var sock1 = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
		try (var out = new PrintWriter(sock.getOutputStream());
		       var in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				var out1 = new PrintWriter(sock1.getOutputStream());
				var in1 = new BufferedReader(new InputStreamReader(sock1.getInputStream()))) {
		    String name = "Kelvin";
		    // Connect and queue zero client.
		    out.println("HELLO~" + name);
		    out.flush();
		    assertThat(in.readLine(), containsString("HELLO"));
		    out.println("LOGIN~" + name);
		    out.flush();
		    assertThat(in.readLine(), containsString("LOGIN"));	
		    out.println("QUEUE");
		    out.flush();
		    out.println("LIST");
		    out.flush();
		    assertThat(in.readLine(), containsString("LIST~Kelvin"));
		    //client 0 gets out of queue
		    out.println("QUEUE");
		    out.flush();
		    // Connect and queue first client.
		    String name1 = "Denny";
		    out1.println("HELLO~" + name1);
		    out1.flush();
		    assertThat(in1.readLine(), containsString("HELLO"));
		    out1.println("LOGIN~" + name1);
		    out1.flush();
		    assertThat(in1.readLine(), containsString("LOGIN"));	
		    out1.println("QUEUE");
		    out1.flush();
		    out1.println("LIST");
		    out1.flush();
		    assertThat(in1.readLine(), containsString("LIST~" + name + "~" + name1));
		    // Get client zero on QUEUE again and give clients a new game.
		    //client 0 gets out of queue
		    out.println("QUEUE");
		    out.flush();
		    assertThat(in.readLine(), containsString("NEWGAME"));
		    assertThat(in1.readLine(), containsString("NEWGAME"));
		}
	}
	@Test
	public void testCapacity() throws UnknownHostException, IOException {
		for (int i = 0; i < 100; i++) {
			var sock = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
			var out = new PrintWriter(sock.getOutputStream());
		    var in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		    String name = "Kelvin" + i;
		    // Connect and queue zero client.
		    out.println("HELLO~" + name);
		    out.flush();
		    assertThat(in.readLine(), containsString("HELLO"));
		    out.println("LOGIN~" + name);
		    out.flush();
		    assertThat(in.readLine(), containsString("LOGIN"));	
		    out.println("QUEUE");
		    out.flush();
		    System.out.println("counter: " + i);
		}
	}
}
