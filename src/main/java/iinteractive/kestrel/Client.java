package iinteractive.kestrel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

	static Logger logger = LoggerFactory.getLogger(Client.class);

    private String host = "localhost";
    private int port = 2222;
	private Socket sock;
	private PrintWriter out;
	private BufferedReader in;

    public Client(String host) {

        this.host = host;
    }

    public Client(String host, int port) {

        this.host = host;
        this.port = port;
    }

    /**
     * Confirm n items from the queue.
     *
     * @param queueName  The name of the queue
     * @param count The number of items to confirm
     * @return the result of the command
     * @throws IOException
     * @throws KestrelException
     */
    public String confirm(String queueName, int count) throws IOException, KestrelException {

    	return writeAndRead("confirm " + queueName + " " + count);
    }

    /**
     * Connect to Kestrel.
     *
     * @throws IOException
     * @throws UnknownHostException
     */
    public void connect() throws IOException, UnknownHostException {

    	this.sock = new Socket(this.host, this.port);
    	out = new PrintWriter(this.sock.getOutputStream(), true);
    	in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    /**
     * Disconnect from Kestrel
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {

    	if(this.out != null) {
    		this.out.close();
    	}
    	if(this.in != null) {
    		this.in.close();
    	}
    	if(this.sock != null) {
    		this.sock.close();
    	}
    }

    /**
     * Flush the specified queue.
     *
     * @param queueName
     */
    public void flush(String queueName) throws IOException, KestrelException {

    	writeAndRead("flush " + queueName);
    }

    /**
     * Get an item from the specified queue.
     *
     * @param queueName THe name of the queue
     * @return The item from the queue (or null)
     * @throws IOException
     * @throws KestrelException
     */
    public String get(String queueName) throws IOException, KestrelException {

    	return writeAndRead("get " + queueName);
    }

    /**
     * "Peek" at the queue to see what the next item is.
     *
     * @param queueName The name of the queue
     * @return The item from the queue
     * @throws IOException
     * @throws KestrelException
     */
    public String peek(String queueName) throws IOException, KestrelException {

    	return writeAndRead("peek " + queueName);
    }

    /**
     * Put a value into the specified queue.
     *
     * @param queueName The queue to add to.
     * @param value The value
     * @throws IOException
     * @throws KestrelException
     */
    public void put(String queueName, String value) throws IOException, KestrelException {

    	writeAndRead("put " + queueName + ":\n" + value + "\n");
    }

    private String writeAndRead(String command) throws IOException, KestrelException {

    	logger.debug("SENDING: " + command);

    	out.println(command);
    	String resp = in.readLine();

    	if(resp == null) {
    		throw new KestrelException("Got a null response");
    	}

    	logger.debug("RESPONSE: " + resp);

    	String retVal = null;

    	if(resp.startsWith(":")) {

    		retVal = resp.substring(1);
    	} else if(resp.startsWith("+")) {

    		retVal = resp.substring(1);
    	} else if(resp.startsWith("-")) {

    		throw new KestrelException(resp.substring(1));
    	} else if(resp.equals("*")) {

    		// There's no reason to set this, but I want it to be clear that
    		// this means Kestrel returned null
    		retVal = null;
    	}

    	return retVal;
    }
}