package es.deusto.redes.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.deusto.redes.protocol.MAProtocol;

public class MAProtocolTest {	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		List<String> arr = new ArrayList<String>(6);
		
		for (int i = 0; i < 6; ++i) {
			arr.add(Integer.toString(i));
		}
		
		for (int i = 100; i < 115; ++i) {
			String code = MAProtocol.getCode(i, arr);
			
			if (code != null) {
				System.out.println(code);
			}
		}
	}

}
