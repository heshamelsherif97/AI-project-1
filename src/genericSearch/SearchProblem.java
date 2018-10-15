package genericSearch;

import java.util.ArrayList;
import state.State;
import state.StateWithOperator;
import tree.Node;

public abstract class SearchProblem {
	private ArrayList<String> operators;
	private State initialState;
	private ArrayList<State> stateSpace;
	private int expandedNodes = 0;
	
	public abstract boolean goalTest(State s);
	public abstract ArrayList<StateWithOperator> transition(Node n);
	public abstract int costOfOperator(String operator);
	public abstract Node heuristicfun1(Node n);
	public abstract Node heuristicfun2(Node n);

	
	public int pathCost(Node n) {
		Node current = n;
		int cost = 0;
		while(current != null) {
			cost += current.getCost();
			current = current.getParent();
		}
		return cost;
	}
	
	public static Node genericSearch(SearchProblem s, String strategy) {
		s.getStateSpace().removeAll(s.getStateSpace());
		s.setExpandedNodes(0);
		s.getStateSpace().add(s.getInitialState());
		ArrayList<Node> nodes = new ArrayList<>();
		Node n = new Node(0, 0, null, null, null, s.getInitialState());
		n = s.heuristicfun1(n);
		nodes.add(n);
		while(!nodes.isEmpty()) {
			if(nodes.isEmpty()) {
				return null;
			}else {
				Node current = nodes.remove(0);
				if(s.goalTest(current.getState())) {
					return current;
				}else {
					current = expand(current, s);
					switch(strategy) {
					case "BF": nodes = s.BF(nodes, current.getChildren());break;
					case "DF": nodes = s.DF(nodes, current.getChildren());break;
					case "ID": s.setExpandedNodes(0); return s.ID(s);
					case "UC": nodes = s.UC(nodes, current.getChildren());break;
					case "GR1": nodes = s.GR1(nodes, current.getChildren());break;
					case "AS1": nodes = s.AS1(nodes, current.getChildren());break;
					case "GR2": nodes = s.GR2(nodes, current.getChildren());break;
					case "AS2": nodes = s.AS2(nodes, current.getChildren());break;
					}
				}
			}
		}
		return null;
	}
	
	public static Node expand(Node n, SearchProblem problem) {
		problem.setExpandedNodes(problem.getExpandedNodes()+1);
		ArrayList<StateWithOperator> possibleStates = problem.transition(n);
		ArrayList<Node> children = new ArrayList<>();
		for (int i = 0; i < possibleStates.size(); i++) {
			if(!((possibleStates.get(i)).getState()).checkSameState(problem.getStateSpace())){
				Node newNode = new Node(n.getDepth()+1, problem.costOfOperator(possibleStates.get(i).getOperator()), n, null, possibleStates.get(i).getOperator(), possibleStates.get(i).getState());
				newNode = problem.heuristicfun1(newNode);
				newNode = problem.heuristicfun2(newNode);
				children.add(newNode);
				problem.getStateSpace().add(possibleStates.get(i).getState());
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
	
	public Node ID(SearchProblem problem) {
		int depth = 0;
		while(depth <= Integer.MAX_VALUE) {
			problem.getStateSpace().removeAll(problem.getStateSpace());
			problem.getStateSpace().add(problem.getInitialState());		
			ArrayList<Node> nodes = new ArrayList<>();
			Node root = new Node(0, 0, null, null, null, problem.getInitialState());
			nodes.add(root);
			Node result = depthLimitedSearch(problem, depth, nodes, 0);
			if (result != null) {
				return result;
			}
			depth++;
		}
		return null;
	}
	
	public Node depthLimitedSearch(SearchProblem problem, int depth, ArrayList<Node> nodes, int index) {
		Node current = nodes.get(index);
		if(depth == 0) {
			if(goalTest(current.getState())) {
				return current;
			}else {
				return null;
			}
		}else if(depth > 0) {
			current = expand(current, problem);
			for (int i = 0; i < current.getChildren().size(); i++) {
				Node child = current.getChildren().get(i);
				nodes.add(child);
				Node result = depthLimitedSearch(problem, depth-1, nodes, nodes.indexOf(child));
				if(result != null) {
					return result;
				}
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
	public int getExpandedNodes() {
		return expandedNodes;
	}
	public void setExpandedNodes(int expandedNodes) {
		this.expandedNodes = expandedNodes;
	}
	
}
