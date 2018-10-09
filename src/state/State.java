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
	
	public  boolean checkSameState(ArrayList<State> state1){
		for(int i = 0 ; i<state1.size() ; i++) {
			 State currentState = state1.get(i);
			 boolean checkStateElement = true;
			 for(int k=0 ; k<5 ; k++){
				 System.out.print("The first state compared : "  + currentState.getState().toString() + ",");
				 System.out.print("The current state being enqueued : " + this.getState().toString());
				 System.out.println();
				 
				 if(!(currentState.getState().get(k)).equals(this.getState().get(k))){
					 checkStateElement= false;
				 }
			 }
			 if(checkStateElement == true) return true;
		}
		return false;
		
	}
	
}