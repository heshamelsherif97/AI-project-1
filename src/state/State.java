package state;

import java.util.ArrayList;

public class State {
	private ArrayList<String> state;

	public State(ArrayList<String> state) {
		super();
		this.state = state;
	}

	public ArrayList<String> getState() {
		return state;
	}

	public void setState(ArrayList<String> state) {
		this.state = state;
	}
	
}
