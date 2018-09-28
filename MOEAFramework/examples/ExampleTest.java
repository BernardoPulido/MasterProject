/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bernardo Pulido
 */
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.problem.AbstractProblem;

/**
 * Demonstrates how a new problem is defined and used within the MOEA
 * Framework.
 */
public class ExampleTest {

	/**
	 * Implementation of the VRPTest function.
	 */
	public static class VRPTest extends AbstractProblem {

		/**
		 * Constructs a new instance of the DTLZ2 function, defining it
		 * to include 11 decision variables and 2 objectives.
		 */
		public VRPTest() {
			super(1, 3);
		}

		/**
		 * Constructs a new solution and defines the bounds of the decision
		 * variables.
		 */
		@Override
		public Solution newSolution() {
			Solution solution = new Solution(getNumberOfVariables(), 
					getNumberOfObjectives());

			for (int i = 0; i < getNumberOfVariables(); i++) {
				solution.setVariable(i, new Permutation(10));
			}

			return solution;
		}
		
		/**
		 * Extracts the decision variables from the solution, evaluates the
		 * Rosenbrock function, and saves the resulting objective value back to
		 * the solution. 
		 */
		@Override
		public void evaluate(Solution solution) {
			int[] x = EncodingUtils.getPermutation(solution.getVariable(0));
			double[] f = new double[numberOfObjectives];

			int k = numberOfVariables - numberOfObjectives + 1;

			double g = 0.0;
			for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
				g += Math.pow(x[i] - 0.5, 2.0);
			}

			for (int i = 0; i < numberOfObjectives; i++) {
				f[i] = 1.0 + g;

				for (int j = 0; j < numberOfObjectives - i - 1; j++) {
					f[i] *= Math.cos(0.5 * Math.PI * x[j]);
				}

				if (i != 0) {
					f[i] *= Math.sin(0.5 * Math.PI * x[numberOfObjectives - i - 1]);
				}
			}

			solution.setObjectives(f);
		}
		
	}
        
	
	public static void main(String[] args) {
		
		NondominatedPopulation result = new Executor()
				.withProblemClass(VRPTest.class)
				.withAlgorithm("MOCell-JMetal")
				.withMaxEvaluations(10000)
				.run();
				
		//display the results
		System.out.format("Objective1  Objective2  Objective3%n");
		
		for (Solution solution : result) {
			System.out.format("%.4f      %.4f      %.4f%n",
					solution.getObjective(0),
					solution.getObjective(1),
                                        solution.getObjective(2));
		}
	}
	
}

