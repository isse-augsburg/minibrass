package isse.mbr.MiniBrassWeb.shared.model.parsetree;

import java.util.LinkedHashMap;
import java.util.Map;

import isse.mbr.MiniBrassWeb.shared.extensions.ExternalMorphism;
import isse.mbr.MiniBrassWeb.shared.model.types.NamedRef;
import isse.mbr.MiniBrassWeb.shared.model.types.PVSType;

/**
 * Defines a morphism between two PVS types 
 * @author Alexander Schiendorfer
 *
 */
public class Morphism {
	private NamedRef<PVSType> from;
	private NamedRef<PVSType> to; 
	private String name;
	private String mznFunction;
	private Map<String, ParamMapping> paramMappings;
	private ExternalMorphism externalMorphism;
	
	public static class ParamMapping {
		private String param; 
		private String mznExpression;
		private String mznFunction;
		private boolean generated; // by external morphism
		
		public ParamMapping() {
			this.generated = false;
		}
		
		public String getParam() {
			return param;
		}
		public void setParam(String param) {
			this.param = param;
		}
		public String getMznExpression() {
			return mznExpression;
		}
		public void setMznExpression(String mznExpression) {
			this.mznExpression = mznExpression;
		}
		public String getMznFunction() {
			return mznFunction;
		}
		public void setMznFunction(String mznFunction) {
			this.mznFunction = mznFunction;
		}

		public boolean isGenerated() {
			return generated;
		}

		public void setGenerated(boolean generated) {
			this.generated = generated;
		}
	}
	
	public Morphism() {
		this.paramMappings = new LinkedHashMap<>();
	}
	
	public NamedRef<PVSType> getFrom() {
		return from;
	}
	public void setFrom(NamedRef<PVSType> from) {
		this.from = from;
	}
	public NamedRef<PVSType> getTo() {
		return to;
	}
	public void setTo(NamedRef<PVSType> to) {
		this.to = to;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMznFunction() {
		return mznFunction;
	}
	public void setMznFunction(String mznFunction) {
		this.mznFunction = mznFunction;
	}

	public Map<String, ParamMapping> getParamMappings() {
		return paramMappings;
	}
	
	public ExternalMorphism getExternalMorphism() {
		return externalMorphism;
	}

	public void setExternalMorphism(ExternalMorphism externalMorphism) {
		this.externalMorphism = externalMorphism;
	} 
	

}
