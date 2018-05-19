package app.start.gui;

public enum VisibilityContract {
	PUBLIC(true), PRIVATE(false);
	
	private boolean mVisible; 
	
	private VisibilityContract(boolean visible) {
		setVisible(visible);
	}

	public boolean isVisible() {
		return mVisible;
	}

	public void setVisible(boolean mVisible) {
		this.mVisible = mVisible;
	}
}
