package isse.mbr.model.types;

public class NamedRef<T> {
	public String name;
	public T literal;
		
	@SuppressWarnings("unchecked")
	public NamedRef(Object value) {
		if(value instanceof String){
			this.name = (String) value;
			this.literal = null;
		} else {
			this.name = null;
			this.literal = (T) value;
		}	
	}
	
	@Override
	public String toString() {
		if(name == null) return literal.toString(); else return name;
	}

	public void update(Object object) {
		literal = (T) object;
	}
}
