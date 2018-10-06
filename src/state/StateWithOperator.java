package state;

import java.util.ArrayList;

public class StateWithOperator {
	private State state;
	private String operator;

	public StateWithOperator(State state, String operator) {
		super();
		this.state = state;
		this.operator = operator;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
}
