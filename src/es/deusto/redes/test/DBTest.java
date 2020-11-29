package es.deusto.redes.test;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.deusto.redes.data.dao.Cell;
import es.deusto.redes.data.dao.Measure;
import es.deusto.redes.data.dao.Sensor;
import es.deusto.redes.data.dao.User;
import es.deusto.redes.data.dao.Vehicle;

public class DBTest {
	private PersistenceManagerFactory pmf;
	private PersistenceManager pm;
	
	@Before
	public void setUp() throws Exception {
		pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		pm = pmf.getPersistenceManager();
	}

	@Test
	public void dataInsertionTest() {
		long t0,t1;
		
		User u1 = new User("Asier", "1234", true);
	    User u2 = new User("David", "1234", true);
	    Cell cell1 = new Cell(0, 1000, "HASDAAA");
	    Cell cell2 = new Cell(1, 5000, "ASDA");
	    Cell cell3 = new Cell(2, 500, "5555asd5");
	    Measure m1 = new Measure(Calendar.getInstance().getTime(), "HAAA", 0);
	    
		t0 = System.currentTimeMillis();
		do {
			t1 = System.currentTimeMillis();
		} while (t1 - t0 < 1000);
        
	    Measure m2 = new Measure(Calendar.getInstance().getTime(), "HASA", 5);
	    
	    t0 = System.currentTimeMillis();
		do {
			t1 = System.currentTimeMillis();
		} while (t1 - t0 < 1000);
		
	    Measure m3 = new Measure(Calendar.getInstance().getTime(), "HAQA", 10);
	    Sensor s1 = new Sensor("Temperatura", true, null);
	    Sensor s2 = new Sensor("Humedad", true, null);
	    Sensor s3 = new Sensor("Velocidad del viento", false, null);
	    Vehicle v1 = new Vehicle(true, "HASDA", null, cell1, null);
	    Vehicle v2 = new Vehicle(true, "ASODALSD", null, cell1, null);
	    Vehicle v3 = new Vehicle(false, "AKLMASF", null, cell2, null);
	    Set<Measure> lm1 = new HashSet<Measure>();
	    Set<Measure> lm2 = new HashSet<Measure>();
	    Set<Sensor> ss1 = new HashSet<Sensor>();
	    Set<Sensor> ss2 = new HashSet<Sensor>();
	    lm1.add(m1);
	    lm1.add(m2);
	    lm2.add(m3);
	    s1.setMeasures(lm1);
	    s2.setMeasures(lm2);
	    ss1.add(s1);
	    ss2.add(s2);
	    v1.setSensors(ss1);
	    v2.setSensors(ss2);
		
		Transaction tx=pm.currentTransaction();
		try
		{
		    tx.begin();
		    
		    pm.makePersistent(cell1);
		    pm.makePersistent(cell2);
		    pm.makePersistent(cell3);
		    pm.makePersistent(u1);
		    pm.makePersistent(u2);
		    pm.makePersistent(v1);
		    pm.makePersistent(v2);
		    pm.makePersistent(v3);
		    pm.makePersistent(s1);
		    pm.makePersistent(s2);
		    pm.makePersistent(s3);
		    pm.makePersistent(m1);
		    pm.makePersistent(m2);
		    pm.makePersistent(m3);
		    
		    tx.commit();
		}
		finally
		{
		    if (tx.isActive())
		    {
		        tx.rollback();
		    }
		    pm.close();
		}
	}
	
	@After
	public void terminate() {
		pm.close();
	}
}
