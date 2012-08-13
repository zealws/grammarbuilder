package skyql.builders;

import java.util.LinkedList;
import java.util.List;

import skyql.query.AndCondition;
import skyql.query.Condition;

public class AndConditionCreator extends Creator<AndCondition> {

	public AndConditionCreator(CreatorStream stream) {
		super(stream);
	}

	@Override
	public AndCondition read() throws Exception {
		List<Condition> results = new LinkedList<Condition>();
		boolean cont = true;
		while(cont) {
			results.add(read(stream,Condition.class));
			cont = stream.compareAndDiscardIfEq("and", false);
		}
		return new AndCondition(results);
	}

}
