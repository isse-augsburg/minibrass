package isse.mbr.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.CodeGenerator.AtomicPvsInformation;
import isse.mbr.parsing.CodegenAtomicPVSHandler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.parsing.MiniBrassParser;
import isse.mbr.tools.SolutionRecorder.Solution;

/**
 * Performs actual pairwise comparison of solutions 
 * 
 * @author Alexander Schiendorfer
 *
 */
public class PairwiseComparator {
	private final class VoteResultsListener implements MiniZincResultListener {
		private final int[][] leftLikes;
		private final int[][] rightLikes;
		private final int[][] indifferents;
		private int i;
		private int j;

		private VoteResultsListener(int[][] leftLikes, int[][] rightLikes, int[][] indifferents, int i, int j) {
			this.leftLikes = leftLikes;
			this.rightLikes = rightLikes;
			this.indifferents = indifferents;
			this.i = i;
			this.j = j;
		}

		@Override
		public void notifySolved() {}

		@Override
		public void notifyOptimality() {}

		@Override
		public void notifyLine(String line) {
			System.out.println("That's what I see: "+ line);
			String[] splits = line.split(","); // first element: Name, second: likes left more, right: likes right more 
			String name = splits[0];
			boolean likesLeftMore = "true".equals(splits[1].trim());
			boolean likesRightMore = "true".equals(splits[2].trim());
			
			System.out.println(name + " likes left more? "+likesLeftMore + " or likes right more? "+likesRightMore);
			if(likesLeftMore) {
				++leftLikes[i][j];
				++rightLikes[j][i];
			} else if(likesRightMore) {
				++rightLikes[i][j];
				++leftLikes[j][i];
			} else {
				++indifferents[i][j];
				++indifferents[j][i];
			}
		}
	}

	private final class PairwiseHandler implements CodegenAtomicPVSHandler {
		private final Solution right;
		private final StringBuilder outputBuilder;
		private final Solution left;
		private boolean firstAtomic;

		private PairwiseHandler(Solution right, StringBuilder outputBuilder, Solution left) {
			this.right = right;
			this.outputBuilder = outputBuilder;
			this.left = left;
			this.firstAtomic = true;
		}

		@Override
		public void handleAtomicPvs(AtomicPvsInformation api, MiniBrassAST model, StringBuilder sb) {
			sb.append("var bool: ");

			String pvsLikesLeftMore = api.getInstance().getName() + LIKES_LEFT_MORE_SUFFIX;
			sb.append(pvsLikesLeftMore);
			sb.append(" = ");
			sb.append(api.getInstance().getType().instance.getOrder());
			sb.append("(");
			sb.append(right.objectives.get(api.getOverall()));
			sb.append(", ");
			sb.append(left.objectives.get(api.getOverall()));
			sb.append(", ");
			sb.append(api.getInstanceArguments().toString());
			sb.append(")");
			sb.append(";\n");
			
			// and vice versa 
			sb.append("var bool: ");
			String pvsLikesRightMore = api.getInstance().getName() + LIKES_RIGHT_MORE_SUFFIX;
			sb.append(pvsLikesRightMore);
			sb.append(" = ");
			sb.append(api.getInstance().getType().instance.getOrder());
			sb.append("(");
			sb.append(left.objectives.get(api.getOverall()));
			sb.append(", ");
			sb.append(right.objectives.get(api.getOverall()));
			sb.append(", ");
			sb.append(api.getInstanceArguments().toString());
			sb.append(")");
			sb.append(";\n");
			
			if(firstAtomic)
				firstAtomic = false;
			else 
				outputBuilder.append(", ");
				
			outputBuilder.append("\"");				
			outputBuilder.append(String.format("%s, \\(%s), \\(%s)\\n", api.getInstance().getName(), pvsLikesLeftMore, pvsLikesRightMore));
			outputBuilder.append("\"");
		}
	}

	public static final String LIKES_LEFT_MORE_SUFFIX = "_likesLeftMore";
	public static final String LIKES_RIGHT_MORE_SUFFIX = "_likesRightMore";
	public static final String VOTES_PREFIX = "Votes: ";
	private int[][] leftLikes;
	private int[][] rightLikes;
	private int[][] indifferents;
	private List<Solution> solutions;
	
	public void performPairwiseComparison(SolutionRecorder sr) throws MiniBrassParseException, IOException {
		solutions = sr.getRecordedSolutions();
		leftLikes = new int[solutions.size()][solutions.size()];
		rightLikes = new int[solutions.size()][solutions.size()];
		indifferents = new int[solutions.size()][solutions.size()];
		
		for(int i = 0; i < solutions.size(); ++i) {
			final Solution left = solutions.get(i);
			
			for(int j = i+1; j < solutions.size(); ++j) {
				// comparing solution i and j 
				final Solution right = solutions.get(j);
				
				System.out.println("Comparing solutions .... ");
				System.out.println("   " + left + " with ");
				System.out.println("   " + right);
				
				MiniBrassParser parser = sr.getParser(); // this must not be null here 				
				MiniBrassAST model = parser.getLastModel();
				
				CodeGenerator codegen = new CodeGenerator();
				
				codegen.setOnlyMiniZinc(true);
				codegen.setGenHeuristics(false);
				codegen.setSuppressOutputGeneration(true);
				
				// suppress variables and constraints
				codegen.setWriteDecisionVariables(false);
				codegen.setWriteSoftConstraints(false);
				
				final StringBuilder outputBuilder = new StringBuilder("output [");
				codegen.addAtomicPVSHandler(new PairwiseHandler(right, outputBuilder, left));

				String generatedCode = codegen.generateComparisonCode(model, left, right);
				outputBuilder.append("];\n");
				generatedCode = generatedCode +  outputBuilder.toString() + "\nsolve satisfy;\n";
				
				// write code to file TODO change
				final File out = File.createTempFile("pairwisecomp", ".mzn");
				FileWriter fw = new FileWriter(out);
				fw.write(generatedCode);
				fw.close();
				
				MiniZincLauncher launcher = new MiniZincLauncher();
				launcher.addMiniZincResultListener(new VoteResultsListener(leftLikes, rightLikes, indifferents, i,j));
				launcher.runMiniZincModel(out, null, 1000);
				
			}
		}
		// output the resulting matrix
		
		for(int i = 0; i < solutions.size(); ++i) {
			for(int j = 0; j < solutions.size(); ++j) {
				System.out.print(String.format("%d / %d / %d | ", leftLikes[i][j], indifferents[i][j], rightLikes[i][j]));
			}
			System.out.println();
		}		
	}
	
	public String getHtml() {
		String rowColor = "MediumSeaGreen";
		String colColor = "#ffb366";
		String neutralColor = "#ECF0F1";
		
		StringBuilder sb = new StringBuilder("<table>\n");
		sb.append("<tr><th></th>\n");
		for(int i = 0; i < solutions.size(); ++i) {
			sb.append(String.format("<th style=\"background-color:%s;\">%s</th>", colColor, solutions.get(i) ));
		}
		sb.append("</tr>\n");
		for(int i = 0; i < solutions.size(); ++i) {
			sb.append(String.format("<tr><td style=\"background-color:%s;\"><b>%s</b></td>\n", rowColor, solutions.get(i)));
			for(int j = 0; j < solutions.size(); ++j) {
				String color = neutralColor;
				if(leftLikes[i][j] > rightLikes[i][j])
					color = rowColor;
				if(rightLikes[i][j] > leftLikes[i][j])
					color = colColor;
				
				sb.append(String.format("<td style=\"background-color:%s;\">%d / %d / %d </td>", color, leftLikes[i][j], indifferents[i][j], rightLikes[i][j]));
			}
			sb.append("</tr>\n");
		}		
		
		sb.append("</table>");
		return sb.toString();
	}
	
	public static void main(String[] args) throws MiniBrassParseException, IOException {
		SolutionRecorder sr = new SolutionRecorder();
		File miniZincFile = new File(args[0]);
		File miniBrassFile = new File(args[1]);
		Collection<File> dataFiles = new ArrayList<>(args.length-2);
		for(int i = 2; i < args.length; ++i) { 
			dataFiles.add(new File(args[i]));
		}
		
		sr.recordSolutions(miniZincFile, miniBrassFile, dataFiles);
		
		PairwiseComparator pc = new PairwiseComparator();
		pc.performPairwiseComparison(sr);
		String htmlTable = pc.getHtml();
		System.out.println(htmlTable);
		// write to output.html (this should be a parameter
		
		FileWriter fw = new FileWriter("output.html");
		fw.write(htmlTable);
		fw.close();
	}

}
