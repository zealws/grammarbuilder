package skyql.query;



import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

@Buildable
public class Ordering {

	@Token(position=0)
	private ColumnName name;
	
	@Token(position=1,optional=true,either={"asc","desc"})
	private String order;
	
	public Ordering(ColumnName name, String order) {
		this.name = name;
		this.order = order;
	}
	
	public String toString() {
		return name + " " + order;
	}
	
}
