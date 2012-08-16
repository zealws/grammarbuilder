package skyql.query;



import com.zealjagannatha.parsebuilder.BuildableClass.Buildable;

@Buildable(resolvers={NotCondition.class,ParenCondition.class,ComparisonCondition.class})
public abstract class Condition {
	@Override
	public abstract String toString();

}
