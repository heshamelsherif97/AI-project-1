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
	public abstract ArrayList<StateWithOperator> transition(Node n);
	public abstract int costOfOperator(String operator);
	
	public int pathCost(Node n) {
		Node current = n;
		int cost = 0;
		while(current != null) {
			cost += current.getCost();
			current = current.getParent();
		}
		return cost;
	}
	
	public Node genericSearch(SearchProblem s, String strategy) {
		ArrayList<Node> nodes = new ArrayList<>();
		Node n = new Node(0, 0, null, null, null, this.getInitialState());
		nodes.add(n);
		int depth = 0;
		while(!nodes.isEmpty()) {
			if(nodes.isEmpty()) {
				return null;
			}else {
				Node current = nodes.remove(0);
				current = expand(current);
				if(goalTest(current.getState())) {
//					System.out.println(nodes.toString());
//					System.out.println(nodes.size());
					return current;
				}else {
					switch(strategy) {
					case "BF": nodes = BF(nodes, current.getChildren());break;
					case "DF": nodes = DF(nodes, current.getChildren());break;
					case "ID": Node N=ID(current, depth);
					if(N!=null) {
						return N;
					}
					else {
						if(depth<30) {
						depth++;
						}
						else {
							return null;
						}
					}
					
					break;
					case "UC": nodes = UC(nodes, current.getChildren());break;
					case "GR1": nodes = GR1(nodes, current.getChildren());break;
					case "AS1": nodes = AS1(nodes, current.getChildren());break;
					case "GR2": nodes = GR2(nodes, current.getChildren());break;
					case "AS2": nodes = AS2(nodes, current.getChildren());break;
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
				//System.out.println("Check Complete");
				Node newNode = new Node(n.getDepth()+1, costOfOperator(possibleStates.get(i).getOperator()), n, null, possibleStates.get(i).getOperator(), possibleStates.get(i).getState());
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
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(pathCost(children.get(i)) < pathCost(nodes.get(j))) break;
			}
			nodes.add(j, children.get(i));		
		}
		return nodes;
	}
	
	public Node ID(Node n, int depth) {
		
		ArrayList<Node> newNodes= new ArrayList<>();
		newNodes.add(n);
		getStateSpace().removeAll(getStateSpace());
		int d=0;
		while(d<=depth) {
			System.out.println(d);
			for (int i = 0; i < newNodes.size(); i++) {
				if(newNodes.get(i).getDepth()==d) {
					newNodes.get(i).setChildren(expand(newNodes.get(i)).getChildren());
					for (int k = 0; k < newNodes.get(i).getChildren().size(); k++) {
						int j=0;
						for (j = 0; j < newNodes.size(); j++) {
							if(newNodes.get(i).getChildren().get(k).getCost() < newNodes.get(j).getCost()) break;
						}
						newNodes.add(j, newNodes.get(i).getChildren().get(k));		
					}
					
				}
			}
			 d++;
		
		
	}
		for (int i = 0; i < newNodes.size(); i++) {
			if(goalTest(newNodes.get(i).getState())) {
				return newNodes.get(i);
			}
		
			
		}
		return null;
		
	}
	public ArrayList<Node> GR1(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(children.get(i).getHeuristicfun1() < nodes.get(j).getHeuristicfun1()) break;
			}
			nodes.add(j, children.get(i));		
		}
		return nodes;
	}
	
	public ArrayList<Node> AS1(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(children.get(i).getHeuristicfun1()+pathCost(children.get(i)) < nodes.get(j).getHeuristicfun1()+pathCost(nodes.get(j))) break;
			}
			nodes.add(j, children.get(i));		
		}
		return nodes;
	}
	
	public ArrayList<Node> GR2(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(children.get(i).getHeuristicfun2() < nodes.get(j).getHeuristicfun2()) break;
			}
			nodes.add(j, children.get(i));		
		}
		return nodes;
	}
	
	public ArrayList<Node> AS2(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(children.get(i).getHeuristicfun2()+pathCost(children.get(i)) < nodes.get(j).getHeuristicfun2()+pathCost(nodes.get(j))) break;
			}
			nodes.add(j, children.get(i));		
		}
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
