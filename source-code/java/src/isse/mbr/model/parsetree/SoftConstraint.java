package isse.mbr.model.parsetree;

public class SoftConstraint {
	private int id;
	private String name;
	private String mznLiteral;
	
	public SoftConstraint(int id, String name, String mznLiteral) {
		super();
		this.id = id;
		this.name = name;
		this.mznLiteral = mznLiteral;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMznLiteral() {
		return mznLiteral;
	}

	public void setMznLiteral(String mznLiteral) {
		this.mznLiteral = mznLiteral;
	}
	
	
}
