package de.isse.conf;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * Takes a configuration class and a properties file specifying possible values
 * and returns an iterator for configurations
 * 
 * @author Alexander Schiendorfer
 * @param <ConfClass> a pure DTO - should only contain public fields with boxed types!
 */
public class ConfigurationProvider<ConfClass> {
	public Collection<ConfClass> getConfigurations(ConfClass confObject, Properties props) {
		Class<? extends Object> confClass = confObject.getClass();
		HashMap<String, Field> parameters = new HashMap<>();
		DirectedGraph<Field, DefaultEdge> dependencyGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
		HashMap<Field, DependsOn> rawDependencies = new HashMap<>();

		for (Field f : confClass.getFields()) {
			System.out.println(f.getName());
			dependencyGraph.addVertex(f);

			parameters.put(f.getName(), f);
			if (f.isAnnotationPresent(DependsOn.class)) {
				System.out.println("Annotated ... ");
				DependsOn depOn = f.getAnnotation(DependsOn.class);
				rawDependencies.put(f, depOn);
			}
		}

		for (Entry<Field, DependsOn> entry : rawDependencies.entrySet()) {
			String otherParam = entry.getValue().parameter();
			if (!parameters.containsKey(otherParam)) {
				throw new RuntimeException("Parameter dependency '" + otherParam + "' referenced from '"
						+ entry.getKey() + "' not found!");
			}
			Field otherField = parameters.get(otherParam);
			// read this edge as "otherField" before "entry.getKey()"
			dependencyGraph.addEdge(otherField, entry.getKey());

		}
		List<Field> consistentOrdering = getParameterOrdering(dependencyGraph);

		Collection<ConfClass> configurations = enumerateConfigurations(confObject, consistentOrdering, parameters,
				props);
		return configurations;
	}

	@SuppressWarnings("unchecked")
	private Collection<ConfClass> enumerateConfigurations(ConfClass confObject, List<Field> consistentOrdering,
			HashMap<String, Field> parameters, Properties props) {
		Collection<ConfClass> configurations = new LinkedList<>();

		Class<? extends Object> confClass = confObject.getClass();
		ConfClass newObj = null;
		try {
			newObj = (ConfClass) confClass.newInstance();
			enumerateConfigurationsRec(newObj, 0, configurations, consistentOrdering, parameters, props);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}		
		return configurations;
	}

	@SuppressWarnings("unchecked")
	private void enumerateConfigurationsRec(ConfClass currInstance, int index, Collection<ConfClass> configurations,
			List<Field> consistentOrdering, HashMap<String, Field> parameters, Properties props)
					throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		System.out.println("Index: "+index);
		if (index == consistentOrdering.size()) { // make a new instance and
													// store it
			Class<? extends Object> confClass = currInstance.getClass();
			ConfClass newObj = null;

			newObj = (ConfClass) confClass.newInstance();

			for (Field f : confClass.getFields()) {
				f.set(newObj, f.get(currInstance));
			}
			System.out.println("NewObj: " + newObj);
			configurations.add(newObj);

		} else {
			// go for parameter index
			Field nextPar = consistentOrdering.get(index);
			boolean skip = false;
			
			if (props.containsKey(nextPar.getName())) {
				if (nextPar.isAnnotationPresent(DependsOn.class)) {
					DependsOn depOn = nextPar.getAnnotation(DependsOn.class);
					Field dependentPar = parameters.get(depOn.parameter());

					// this one has already been set previously due to topsort
					Object depValue = dependentPar.get(currInstance);
					if(!Arrays.asList(depOn.allowedValues()).contains(depValue.toString())) {
						skip = true;
					}
				}
			} else {
				skip = true;
			}
			if(skip) {
				enumerateConfigurationsRec(currInstance, index + 1, configurations, consistentOrdering, parameters,
						props);
			} else {
				Object[] values = getPossibles(nextPar, props);
				Object oldVal = nextPar.get(currInstance);
				for(int i = 0; i < values.length; ++i) {
					nextPar.set(currInstance,values[i]);
					enumerateConfigurationsRec(currInstance, index + 1, configurations, consistentOrdering, parameters,
							props);
				}
				nextPar.set(currInstance,oldVal);
			}
		}
		
	}

	private Object[] getPossibles(Field nextPar, Properties props) {
		String rawString = props.getProperty(nextPar.getName());
		// can be comma-separated
		String[] parts = rawString.split(",");
		Object[] values = new Object[parts.length];
		
		for(int i = 0; i < parts.length; ++i) {
			try {
				Object parsed = parseObjectFromString(parts[i], nextPar.getType(), nextPar);
			
				values[i] = parsed;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	
	public static <T> T parseObjectFromString(String s, Class<T> clazz, Field nextPar) throws Exception {
		if(clazz.isEnum()) {
			Class<? extends Enum> enumType = (Class<? extends Enum>) clazz;
			return (T) Enum.valueOf(enumType, s);
		} else {
		    try {
				return clazz.getConstructor(new Class[] {String.class }).newInstance(s);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("No string constructor found (for field "+nextPar.getName()+"). Did you perhaps specify a primitive type? Please use boxed variant.", e);
			} 
		}
	}

	private List<Field> getParameterOrdering(DirectedGraph<Field, DefaultEdge> dependencyGraph) {
		CycleDetector<Field, DefaultEdge> cycleDetector = new CycleDetector<Field, DefaultEdge>(dependencyGraph);
		if (cycleDetector.detectCycles()) {
			throw new RuntimeException("Cyclic dependency detected!");
		} else {
			TopologicalOrderIterator<Field, DefaultEdge> orderIterator;
			Field f;
			orderIterator = new TopologicalOrderIterator<Field, DefaultEdge>(dependencyGraph);
			System.out.println("\nOrdering:");

			List<Field> output = new ArrayList<>(dependencyGraph.vertexSet().size());

			while (orderIterator.hasNext()) {
				f = orderIterator.next();
				System.out.println(f.getName());
				output.add(f);
			}

			return output;
		}
	}
}
