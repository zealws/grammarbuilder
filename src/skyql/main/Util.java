package skyql.main;

import java.util.List;

public class Util {
	
	public static <K> String join(List<K> list, String insert) {
		boolean already = false;
		StringBuilder result = new StringBuilder();
		for(K cond : list) {
			if(already)
				result.append(insert);
			result.append(cond.toString());
			already = true;
		}
		return result.toString();
	}
	
	public static <K> String join(K[] list, String insert) {
		boolean already = false;
		StringBuilder result = new StringBuilder();
		for(K cond : list) {
			if(already)
				result.append(insert);
			result.append(cond.toString());
			already = true;
		}
		return result.toString();
	}

}
