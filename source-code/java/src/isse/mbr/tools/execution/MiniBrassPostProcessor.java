package isse.mbr.tools.execution;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for post-processing the generated MiniZinc
 * statements (i.e., sol() statements)
 * 
 * @author alexander
 *
 */
public class MiniBrassPostProcessor {

	public String processSolution(String input, MiniZincSolution solution) {
		String origCopy = new String(input);
		Matcher m = Pattern.compile("sol\\((.*?)\\)").matcher(input);
		List<MiniZincVariable> variablesToReplace = solution.getAllVariables(); 
		
		while (m.find()) {
			String content = m.group(1);
			String origContent = new String(content);
			boolean changed = false;
			for(MiniZincVariable varToReplace : variablesToReplace) {
				if(content.contains(varToReplace.getName()) ) {
					String replacement = String.format("(%s)", varToReplace.getMznExpression());
					content = content.replaceAll(varToReplace.getName(), replacement);
					changed = true;
				}
			}
			if (changed) 
				origCopy = origCopy.replace(String.format("sol(%s)", origContent), content);
		}
		return origCopy;
	}
}
