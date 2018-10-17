package state;


/*State class is a description of how a state tuple is described
 * It is an array list of strings which include the state
 */
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
	
	
	/*
	 * This function is used to check if a state is already existing in a list of states
	 * It is called to check if a state is already existing in a state space of a problem
	 * to avoid repeated states
	 * */
	public  boolean checkSameState(ArrayList<State> state1){
		for(int i = 0 ; i<state1.size() ; i++) {
			 State currentState = state1.get(i);
			 boolean checkStateElement = true;
			 for(int k=0 ; k<5 ; k++){				 
				 if(!(currentState.getState().get(k)).equals(this.getState().get(k))){
					 checkStateElement= false;
				 }
			 }
			 if(checkStateElement == true) return true;
		}
		return false;
		
	}
	
}