package com.zealjagannatha.parsebuilder;

import java.io.IOException;
import java.util.List;


public interface AnnotatedDeclaration {
	
	@Override
	public boolean equals(Object other);
	
	public Object read(ParserStream stream) throws IOException;
	public List<AnnotatedDeclaration> getSubdeclarations();
	public String generateGrammar();

}
