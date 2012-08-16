package skyql.query;



import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;
import com.zealjagannatha.parsebuilder.TokenField.Token;

@Buildable
public class ColumnName extends Value {
	
	@Token
	private String name;
	
	public ColumnName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
