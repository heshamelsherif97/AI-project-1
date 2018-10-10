package genericSearch;

import java.util.ArrayList;
import state.State;
import state.StateWithOperator;
import tree.Node;

public abstract class SearchProblem {
	private ArrayList<String> operators;
	private State initialState;
	private ArrayList<State> stateSpace;
	
	public abstract boolean goalTest(State s);
	public abstract int pathCost();
	public abstract ArrayList<StateWithOperator> transition(Node n);
	
	public Node genericSearch(SearchProblem s, String strategy) {
		ArrayList<Node> nodes = new ArrayList<>();
		Node n = new Node(0, 0, null, null, null, this.getInitialState());
		nodes.add(n);
		while(!nodes.isEmpty()) {
			if(nodes.isEmpty()) {
				return null;
			}else {
				Node current = nodes.remove(0);
				current = expand(current);
				if(goalTest(current.getState())) {
					System.out.println(nodes.toString());
					System.out.println(nodes.size());
					return current;
				}else {
					switch(strategy) {
					case "BF": nodes = BF(nodes, current.getChildren());break;
					case "DF": nodes = DF(nodes, current.getChildren());break;
					case "ID": nodes = ID(nodes, current.getChildren());break;
					case "UC": nodes = UC(nodes, current.getChildren());break;
					case "GRi": nodes = GRi(nodes, current.getChildren());break;
					case "ASi": nodes = ASi(nodes, current.getChildren());break;
					}
				}
			}
		}
		return null;
	}
	
	public Node expand(Node n) {
		ArrayList<StateWithOperator> possibleStates = transition(n);
		ArrayList<Node> children = new ArrayList<>();
		for (int i = 0; i < possibleStates.size(); i++) {
			if(!((possibleStates.get(i)).getState()).checkSameState(getStateSpace())){
				System.out.println("Check Complete");
				Node newNode = new Node(n.getDepth()+1, n.getCost()+1, n, null, possibleStates.get(i).getOperator(), possibleStates.get(i).getState());
				children.add(newNode);
				getStateSpace().add(possibleStates.get(i).getState());
			}
		}
		n.setChildren(children);
		return n;
	}
	
	public ArrayList<Node> BF(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			nodes.add(children.get(i));
		}
		return nodes;
	}
	
	public ArrayList<Node> DF(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			nodes.add(0, children.get(i));
		}
		return nodes;
	}
	
	public ArrayList<Node> UC(ArrayList<Node> nodes, ArrayList<Node> children) {

		return nodes;
	}
	
	public ArrayList<Node> ID(ArrayList<Node> nodes, ArrayList<Node> children) {

		return nodes;
	}
	
	public ArrayList<Node> GRi(ArrayList<Node> nodes, ArrayList<Node> children) {

		return nodes;
	}
	
	public ArrayList<Node> ASi(ArrayList<Node> nodes, ArrayList<Node> children) {
		
		return nodes;
	}
	
	
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
