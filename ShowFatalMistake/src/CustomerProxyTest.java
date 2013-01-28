import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class CustomerProxyTest {

	private Customer customer;
	private CustomerProxy customerProxy = new CustomerProxy();
	private int customerId;
	
	@Before
	public void setUp() throws Exception {
		customer = new Customer();
		customer.setFirstName("Hans");
		customer.setLastName("Wurst");
	}

	@After
	public void tearDown() throws Exception {
		customerProxy.delete(customerId);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
}
