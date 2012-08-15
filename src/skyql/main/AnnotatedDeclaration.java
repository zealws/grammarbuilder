package skyql.main;

import java.io.IOException;


public interface AnnotatedDeclaration {
	
	public Object read(CreatorStream stream) throws IOException;
	public String generateGrammar();

}
