
public class CustomerProxy {

	public int save(String firstName, String lastName){
		return 1;
	}
	
	public Customer load(int id){
		return new Customer();
	}

	public Customer delete(int id){
		return new Customer();
	}
}
