package net.talentum.jackie.image;

@FunctionalInterface
public interface ImageOutputSupplier {

	public ImageOutput get(String parameter);

	public static class ComboBoxItem {

		private ImageOutputSupplier imageOutputSupplier;
		private String name;

		public ComboBoxItem(ImageOutputSupplier imageOutputSupplier) {
			this.imageOutputSupplier = imageOutputSupplier;
			
			ImageOutput io = imageOutputSupplier.get("");
			this.name = io.getName();
		}

		public ImageOutputSupplier getValue() {
			return imageOutputSupplier;
		}

		@Override
		public String toString() {
			return name;
		}

	}

}
