package genericSearch;

import java.util.ArrayList;
import java.util.Queue;

import state.State;
import tree.Node;
import tree.Tree;

public abstract class SearchProblem {
	private ArrayList<String> operators;
	private State initialState;
	private ArrayList<State> stateSpace;
	
	public abstract boolean goalTest(State s);
	public abstract int pathCost();
	
	public abstract Node genericSearch(SearchProblem s, String strategy);
	public ArrayList<String> getOperators() {
		return operators;
	}
	public void setOperators(ArrayList<String> operators) {
		this.operators = operators;
	}
	public State getInitialState() {
		return initialState;
	}
	public void setInitialState(State initialState) {
		this.initialState = initialState;
	}
	public ArrayList<State> getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(ArrayList<State> stateSpace) {
		this.stateSpace = stateSpace;
	}
	
	
//	public Tree generateTree(State s){
//
//	}
	
}
