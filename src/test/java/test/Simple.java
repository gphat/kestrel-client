package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import iinteractive.kestrel.Client;
import iinteractive.kestrel.KestrelException;

import java.io.IOException;

import org.junit.Test;

public class Simple {

	@Test
	public void testClient() {

		Client kestrel = null;
		String queueName = "test-net-kestrel";

		try {
			kestrel = new Client("10.0.1.36");
			kestrel.connect();
		} catch(Exception e) {
			e.printStackTrace();
			fail("Failed to connect");
		}

		try {
			kestrel.flush(queueName);
		} catch(Exception e) {
			e.printStackTrace();
			fail("Failed to flush queue");
		}

		try {
			kestrel.put(queueName, "hello world!");
		} catch(Exception e) {
			e.printStackTrace();
			fail("Failed to put value");
		}

		try {
			String peek = kestrel.peek(queueName);
			assertEquals("peek", "hello world!", peek);
		} catch(Exception e) {
			e.printStackTrace();
			fail("Failed to peek value");
		}

		try {
			String val = kestrel.get(queueName);
			assertEquals("get", "hello world!", val);
		} catch(Exception e) {
			e.printStackTrace();
			fail("Failed to get value");
		}

		try {
			kestrel.confirm(queueName, 1);
		} catch(Exception e) {
			e.printStackTrace();
			fail("Failed to confirm items");
		}

		boolean thrown = false;
		try {
			kestrel.confirm(queueName, 1);
		} catch(KestrelException e) {
			thrown = true;
		} catch(IOException e) {
			e.printStackTrace();
			fail("Failed to properly fail confirm");
		}
		assertTrue("empty confirm failed", thrown);

		try {
			kestrel.disconnect();
		} catch(Exception e) {
			e.printStackTrace();
			fail("Failed to disconnect");
		}
	}
}
