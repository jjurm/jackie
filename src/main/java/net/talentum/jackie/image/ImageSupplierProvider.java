package net.talentum.jackie.image;

/**
 * 
 * @author padr31
 *
 */
public abstract class ImageSupplierProvider {

	private String name;
	
	public ImageSupplierProvider(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
	
	public abstract ImageSupplier provide(String param);
	
}

