package es.deusto.redes.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.deusto.redes.client.Client;
import es.deusto.redes.protocol.MAProtocol;
import es.deusto.redes.server.VehicleServer;

public class ClientTest {
	private VehicleServer s;
	private Client thc;
	
	@Before
	public void setUp() throws Exception {
		s = new VehicleServer(1);
	}

	@Test
	public void dataGetterTest() {
		new Thread(s).start();
		thc = new Client("localhost", 4444);
		String data, arg;
		List<String> args = new ArrayList<String>();
		
		try {
			arg = "Facon";
			args.add(arg);
			thc.sendData("USER " + arg);
			data = thc.getData();
			assert(data.equals(MAProtocol.getCode(201, args)));
			args.clear();
			
			arg = "1234";
			thc.sendData("PASS " + arg);
			data = thc.getData();
			assert(data.equals(MAProtocol.getCode(202, null)));
			args.clear();
			
			thc.sendData("SALIR");
			data = thc.getData();
			assert(data.equals(MAProtocol.getCode(208, null)));
			args.clear();
			
			Thread.sleep(1 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Parando servidor.");
		s.stop();
	}
	
	@Test
	public void justWrongTest() {
		new Thread(s).start();
		thc = new Client("localhost", 4444);
		String data, arg;
		List<String> args = new ArrayList<String>();
		
		try {
			arg = "Focom";
			args.add(arg);
			thc.sendData("USER " + arg);
			data = thc.getData();
			assert(data.equals(MAProtocol.getCode(201, args)));
			args.clear();
			
			arg = "1234";
			thc.sendData("PASS " + arg);
			data = thc.getData();
			assert(data.equals(MAProtocol.getCode(401, null)));
			args.clear();
			
			thc.sendData("SALIR");
			data = thc.getData();
			assert(!data.equals("^_^"));
			args.clear();
			
			Thread.sleep(1 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Parando servidor.");
		s.stop();
	}
}
