/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bernardo Pulido
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import org.moeaframework.Analyzer;
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
  
        public static Integer dimension;
        protected static double [][] distanceMatrix ;
        protected static double [][] costMatrix;
        public static String distanceFile;
        public static String costFile;
        protected static int [][] adjacenciasMatrix;
        protected static int init_node = 0;
        protected static int destine_node = 24;
        
	public static class VRPTest extends AbstractProblem {

		public VRPTest() throws IOException {
			super(1, 2); 
                        distanceFile="/vrp.txt";
                        readProblem(distanceFile);
                        llenarConCeros();
                
                 //       imprimirMatrices();
		}
                private void llenarConCeros() {
                  for(int i=0; i<dimension;i++){
                    for(int j=0; j<dimension;j++){
                        if(adjacenciasMatrix[i][j]!=1){
                          adjacenciasMatrix[i][j]=0;
                        }
                    }
                  }
                }
                
                  public void imprimirMatrices(){
                    for(int i=0; i<distanceMatrix.length;i++){
                      for(int j=0; j<distanceMatrix.length;j++){
                          System.out.print(""+distanceMatrix[i][j]+" ");
                      }
                      System.out.println();
                    }
                  }
                public void imprimirPermutation(int[] permutacion){
                       for(int i=0; i<permutacion.length;i++){
                            System.out.print(""+permutacion[i]+" ");
                        }
                        System.out.println("");
                        
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
                            Permutation permutacion = new Permutation(dimension);
                            permutacion.randomize();
			    solution.setVariable(i, permutacion);
			}
                        //imprimirPermutation(EncodingUtils.getPermutation(solution.getVariable(0)));

			return solution;
                        
		}
		
		/**
		 * Extracts the decision variables from the solution, evaluates the
		 * Rosenbrock function, and saves the resulting objective value back to
		 * the solution. 
		 */
		@Override
		public void evaluate(Solution solution) {
                    
                        double fitness1   ;
                        double fitness2   ;

                        fitness1 = 0.0 ;
                        fitness2 = 0.0 ;

                        ArrayList<Integer> nodes_visitados = new ArrayList<Integer>();

                       // for(int i=0; i<dimension;i++){
                          //System.out.print(EncodingUtils.getPermutation(solution.getVariable(0))[i]+" ");
                          
                        //}
                        //System.out.println();

                        int current_node = init_node;

                        while(current_node!=destine_node){

                          //System.out.println(current_node+" Current node");
                          int max=-1;
                          int pos_max=current_node;
                          for(int i=0; i<dimension;i++){
                            if(adjacenciasMatrix[current_node][i]==1){
                                if((max < EncodingUtils.getPermutation(solution.getVariable(0))[i]) && (nodes_visitados.indexOf(i)==-1)){
                                   max= EncodingUtils.getPermutation(solution.getVariable(0))[i];
                                   pos_max = i;
                                }
                            }
                          }
                          if(pos_max!=current_node){
                            fitness1 +=distanceMatrix[current_node][pos_max];
                            fitness2 +=costMatrix[current_node][pos_max];
                            nodes_visitados.add(current_node);
                            current_node=pos_max;
                          }else{
                            //System.err.println("El grafo no es conexo");
                            //System.exit(0);
                            fitness1+=1000;
                            fitness2+=1000;
                            current_node=destine_node;
                          }
                        }

                    double[] f = new double[numberOfObjectives];
                    f[0]=fitness1;
                    f[1]=fitness2;
                        
                    solution.setObjectives(f);

                                       
		}
                
                
		private void readProblem(String file) throws IOException {
                    int [][] matrix = null;
                    double [][] matrix_penalizacion = null;
                    double [][] matrix_distancias = null;

                    InputStream in = getClass().getResourceAsStream(file);
                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(isr);

                    StreamTokenizer token = new StreamTokenizer(br);
                    try {
                      boolean found ;
                      found = false ;

                      token.nextToken();
                      while(!found) {
                        if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0)))
                          found = true ;
                        else
                          token.nextToken() ;
                      }

                      token.nextToken() ;
                      token.nextToken() ;

                      dimension =  (int)token.nval ;

                      matrix = new int[dimension][dimension] ;
                      matrix_penalizacion = new double[dimension][dimension] ;
                      matrix_distancias = new double[dimension][dimension] ;

                      //Find the string ARISTAS
                      found = false ;
                      token.nextToken();
                      while(!found) {
                        if ((token.sval != null) && ((token.sval.compareTo("ARISTAS") == 0)))
                          found = true ;
                        else
                          token.nextToken() ;
                      }
                      token.nextToken() ;
                      token.nextToken() ;

                      int edges = (int)token.nval;

                      // Find the string SECTION  
                      found = false ;
                      token.nextToken();
                      while(!found) {
                        if ((token.sval != null) &&
                            ((token.sval.compareTo("SECTION") == 0)))
                          found = true ;
                        else
                          token.nextToken() ;
                      }


                      for (int i = 0; i < edges; i++) {
                        token.nextToken();
                        int j = (int)token.nval;

                        token.nextToken();
                        int k = (int)token.nval;

                        token.nextToken();
                        int distancia = (int)token.nval;

                        token.nextToken();
                        int penalizacion = (int)token.nval;

                        matrix[j-1][k-1] = 1;
                        matrix[k-1][j-1] = 1;

                        matrix_distancias[j-1][k-1]= distancia;
                        matrix_distancias[k-1][j-1] = distancia;

                        matrix_penalizacion[j-1][k-1]= penalizacion;
                        matrix_penalizacion[k-1][j-1] = penalizacion;

                      }
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                    adjacenciasMatrix = matrix;
                    distanceMatrix=matrix_distancias;
                    costMatrix=matrix_penalizacion;
                  }
	}
                
	
	public static void main(String[] args) {
		
                NondominatedPopulation result = new Executor()
				.withProblemClass(VRPTest.class)
				.withAlgorithm("MOCell-JMetal")
				.withMaxEvaluations(10000)
                        
                                .distributeOnAllCores()
				.run();
				
		//display the results
		System.out.format("Objective1  Objective2%n");
		
		for (Solution solution : result) {
			System.out.format("%.4f      %.4f%n",
					solution.getObjective(0),
					solution.getObjective(1));
		}
                
                /**
                 * Adaptación de Example2 a VRP
                 *
                 */
                String[] algorithms = {"MOCell-JMetal", "NSGAII"};
                
                Executor executor = new Executor()
                                .withProblemClass(VRPTest.class)
                                .distributeOnAllCores()
                                .withMaxEvaluations(10000);
                
                Analyzer analyzer = new Analyzer()
				.withProblemClass(VRPTest.class)
				.includeHypervolume()
                                .includeAdditiveEpsilonIndicator()
                                .includeInvertedGenerationalDistance()
				.showStatisticalSignificance();

		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(30));
		}

		//print the results
                System.out.println("");
                System.out.println("Resultados Comparación estadistica de algoritmos");
                System.out.println("");
		analyzer.printAnalysis();
        }
}

