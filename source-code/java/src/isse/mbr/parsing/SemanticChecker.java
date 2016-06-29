package isse.mbr.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.types.ArrayType;
import isse.mbr.model.types.FloatType;
import isse.mbr.model.types.IntType;
import isse.mbr.model.types.IntervalType;
import isse.mbr.model.types.MiniZincVarType;
import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.PVSParamInst;
import isse.mbr.model.types.PVSParameter;
import isse.mbr.model.types.PVSType;
import isse.mbr.model.types.PrimitiveType;

/**
 * For deferred reference updates
 * 
 * @author Alexander Schiendorfer
 *
 */
public class SemanticChecker {
	private Queue<ReferenceJob> referenceJobs;
	private Queue<ArrayJob> arrayJobs;
	private Map<String, DirectedGraph<String, DefaultEdge>> parameterDependencies;
	
	public SemanticChecker() {
		referenceJobs = new LinkedList<>();
		arrayJobs = new LinkedList<>();
		parameterDependencies = new HashMap<String, DirectedGraph<String,DefaultEdge>>();
	}

	private static class ReferenceJob {
		public NamedRef<?> reference;
		public Map<String, ?> map;

		public ReferenceJob(NamedRef<?> reference, Map<String, ?> map) {
			super();
			this.reference = reference;
			this.map = map;
		}

	}
	
	private static class ArrayJob {
		public ArrayType arrayType;
		public 	List<MiniZincVarType> pendingIndexTypes;
		public String name;
		
		public ArrayJob(ArrayType arrayType, List<MiniZincVarType> pendingIndexTypes, String name) {
			super();
			this.arrayType = arrayType;
			this.pendingIndexTypes = pendingIndexTypes;
			this.name = name;
		}
	
		public void execute() throws MiniBrassParseException {
			for(MiniZincVarType pendingIndexType : pendingIndexTypes) {
				if(pendingIndexType instanceof IntType) {
					arrayType.getIndexSets().add((PrimitiveType) pendingIndexType);
				} else if(pendingIndexType instanceof FloatType) {
					throw new MiniBrassParseException("Index type of array "+name + " must not be float!");
				} else if(pendingIndexType instanceof IntervalType) {
					IntervalType it = (IntervalType) pendingIndexType;
					PrimitiveType superType = it.getSuperSort();
					if(superType instanceof FloatType) {
						throw new MiniBrassParseException("Index type of array "+name + " must not be float!");						
					}
					arrayType.getIndexSets().add(it);					
				}
			}
		}

	}

	public <T> void scheduleUpdate(NamedRef<T> reference, Map<String, T> map) {
		ReferenceJob newJob = new ReferenceJob(reference, map);
		referenceJobs.add(newJob);

	}

	public void updateReferences() throws MiniBrassParseException {
		for (ReferenceJob job : referenceJobs) {
			if (job.reference.name != null) {
				if (!job.map.containsKey(job.reference.name)) {
					throw new MiniBrassParseException("Unresolved reference: " + job.reference.name);
				} else {
					job.reference.update(job.map.get(job.reference.name));
				}
			}
		}
	}
	
	public void executeArrayJobs() throws MiniBrassParseException {
		for(ArrayJob aj : arrayJobs) {
			aj.execute();
		}
	}

	public void scheduleParameterReference(NamedRef<?> lRef, PVSType scopeType) {
		if (lRef != null) {
			ReferenceJob refJob = new ReferenceJob(lRef, scopeType.getParamMap());
			referenceJobs.add(refJob);
		}
	}

	/**
	 * Make sure all types are int types now (after references to parameters have been resolved)
	 * @param arrayType
	 * @param pendingIndexTypes
	 * @param name 
	 */
	public void scheduleArrayTypeCheck(ArrayType arrayType,
			List<MiniZincVarType> pendingIndexTypes, String name) {
		ArrayJob aj = new ArrayJob(arrayType, pendingIndexTypes, name);
		arrayJobs.add(aj);
	}

	/**
	 * array[a..b] of c..d: e:
	 * 
	 * {a,b, c,d} would be "to", e would be "from"
	 * @param scopeType
	 * @param from
	 * @param toType
	 * @throws MiniBrassParseException 
	 */
	public void scheduleTypeDependencyCheck(PVSType scopeType, String from, MiniZincVarType toType) throws MiniBrassParseException {
		DirectedGraph<String, DefaultEdge> parameterGraph = null;
		if(!parameterDependencies.containsKey(scopeType.getName())) {
			parameterGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
			
			parameterDependencies.put(scopeType.getName(), parameterGraph);
		} else {
			parameterGraph = parameterDependencies.get(scopeType.getName());
		}
		if(toType instanceof IntervalType) {
			IntervalType safeTo = (IntervalType) toType;
			for(String to : safeTo.getReferencedParameters()) {
				try{
					if(!parameterGraph.containsVertex(from))
						parameterGraph.addVertex(from);
					
					if(!parameterGraph.containsVertex(to))
						parameterGraph.addVertex(to);
										
					parameterGraph.addEdge(from, to);
				} catch(IllegalArgumentException ce) {
					throw new MiniBrassParseException("Detected cyclic dependency when adding parameter dependency: "+from + " -> "+ to);
				}				
			}
		}
	}

	public void checkPvsInstances(MiniBrassAST model) throws MiniBrassParseException {
		for( Entry<String, AbstractPVSInstance> entry : model.getPvsInstances().entrySet()) {
			if(entry.getValue() instanceof PVSInstance) {
				PVSInstance pvsInst = (PVSInstance) entry.getValue();
				PVSType pvsType = pvsInst.getType().instance;
				
				List<String> actualParameters = new ArrayList<>(pvsInst.getParameterValues().size()+1);
				// number of soft constraints as one built-in parameter of type int
				actualParameters.add(Integer.toString(pvsInst.getNumberSoftConstraints()));
				actualParameters.addAll(pvsInst.getParameterValues());
				
				List<PVSParameter> formalParameters =  pvsType.getPvsParameters();

				System.out.println("Actual: ("+actualParameters.size() + ") "+Arrays.toString(actualParameters.toArray()));
				System.out.println("Formal: ("+formalParameters.size() + ") "+Arrays.toString(formalParameters.toArray()));
				
				if(formalParameters.size() != actualParameters.size()) {
					String message = String.format("Mismatch of parameters in instance %s of type %s; Expecting %d but got %d parameters.", pvsInst.getName(), pvsType.getName(), formalParameters.size()-1, actualParameters.size()-1);
					throw new MiniBrassParseException(message);
				}
				
				Iterator<PVSParameter> formParIt = formalParameters.iterator();
				Map<String, PVSParamInst> parInst = new HashMap<>(formalParameters.size());
				
				for(String actParam : actualParameters) {
					PVSParamInst pi = new PVSParamInst();
					PVSParameter formalParameter = formParIt.next();
					pi.parameter = formalParameter;
					pi.expression = actParam;
					
					parInst.put(formalParameter.getName(), pi);
				}
				
				// inject this back into instance:
				pvsInst.setParametersLinked(parInst);				
			}
		}
		
	}
}
