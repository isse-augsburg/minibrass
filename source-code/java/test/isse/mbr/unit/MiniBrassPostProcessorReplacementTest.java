package isse.mbr.unit;

import isse.mbr.model.types.IntType;
import isse.mbr.tools.execution.MiniBrassPostProcessor;
import isse.mbr.tools.execution.MiniZincVariable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MiniBrassPostProcessorReplacementTest {

	@Test
	public void test() throws Exception {
		// given a composite getBetterConstraint and some variables that partially are referenced in the constraint
		String getBetterConstraint = "( (is_worse_weighted(sol(mbr_overall_px), mbr_overall_px, mbr_nScs_px, mbr_k_px, mbr_weights_px, mbr_amplifier_px)) \\/ ( (sol(mbr_overall_px) = mbr_overall_px) /\\ is_worse_weighted(sol(mbr_overall_py), mbr_overall_py, mbr_nScs_py, mbr_k_py, mbr_weights_py, mbr_amplifier_py)) )";
		List<MiniZincVariable> variables = Arrays.asList(
				// type and value do not matter because they are not used
				new MiniZincVariable(new IntType(), null, "mbr_valuations_py", "array1d(1..3, [true, false, false])"),
				new MiniZincVariable(new IntType(), null, "y", "0"),
				new MiniZincVariable(new IntType(), null, "x", "1"),
				new MiniZincVariable(new IntType(), null, "mbr_overall_py", "3"),
				new MiniZincVariable(new IntType(), null, "mbr_valuations_px", "array1d(1..3, [false, true, false])"),
				new MiniZincVariable(new IntType(), null, "mbr_overall_px", "3")
		);

		// when replacing the references
		String actual = new MiniBrassPostProcessor().replaceVariables(getBetterConstraint, variables);

		// then the references should have been replaced correctly and nothing else
		String expectation =
				"( (is_worse_weighted((3), mbr_overall_px, mbr_nScs_px, mbr_k_px, mbr_weights_px, mbr_amplifier_px)) \\/ ( ((3) = mbr_overall_px) /\\ is_worse_weighted((3), mbr_overall_py, mbr_nScs_py, mbr_k_py, mbr_weights_py, mbr_amplifier_py)) )";
		Assert.assertEquals(expectation, actual);
	}
}
