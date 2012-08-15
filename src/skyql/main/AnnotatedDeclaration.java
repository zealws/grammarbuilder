package skyql.main;

import java.io.IOException;
import java.util.List;


public interface AnnotatedDeclaration {
	
	@Override
	public boolean equals(Object other);
	
	public Object read(CreatorStream stream) throws IOException;
	public List<AnnotatedDeclaration> getSubdeclarations();
	public String generateGrammar();

}
