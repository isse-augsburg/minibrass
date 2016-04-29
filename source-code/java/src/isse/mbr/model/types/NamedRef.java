package isse.mbr.model.types;

public class NamedRef<T> {
	public String name;
	public T instance;
		
	@SuppressWarnings("unchecked")
	public NamedRef(Object value) {
		if(value instanceof String){
			this.name = (String) value;
			this.instance = null;
		} else {
			this.name = null;
			this.instance = (T) value;
		}	
	}
	
	@Override
	public String toString() {
		if(instance != null) return "[lit] "+ instance.toString(); else return name;
	}

	public void update(Object object) {
		instance = (T) object;
	}
}
