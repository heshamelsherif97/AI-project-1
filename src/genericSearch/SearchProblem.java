package genericSearch;

import java.util.ArrayList;
import java.util.Iterator;

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
		Node n = new Node(0, 0, null, null, "", s.getInitialState());
		n = s.heuristicfun1(n);
		n = s.heuristicfun2(n);
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
					case "ID": return s.ID(s);
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
		ArrayList<StateWithOperator> possibleStates = problem.transition(n);
		ArrayList<Node> children = new ArrayList<>();
		for (int i = 0; i < possibleStates.size(); i++) {
			Node newNode = new Node(n.getDepth()+1, problem.costOfOperator(possibleStates.get(i).getOperator()), n, null, possibleStates.get(i).getOperator(), possibleStates.get(i).getState());
			newNode = problem.heuristicfun1(newNode);
			newNode = problem.heuristicfun2(newNode);
			children.add(newNode);
				}
		problem.setExpandedNodes(problem.getExpandedNodes()+1);
		n.setChildren(children);
		return n;
	}
	
	public ArrayList<Node> BF(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				this.getStateSpace().add(children.get(i).getState());
				nodes.add(children.get(i));
			}
		}
		return nodes;
	}
	
	public ArrayList<Node> DF(ArrayList<Node> nodes, ArrayList<Node> children) {
		ArrayList<Node> possibleChildren = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				this.getStateSpace().add(children.get(i).getState());
				possibleChildren.add(children.get(i));
			}
		}
		nodes.addAll(0, possibleChildren);
		return nodes;
	}
	
	public ArrayList<Node> UC(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(pathCost(children.get(i)) < pathCost(nodes.get(j))) break;
			}
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				this.getStateSpace().add(children.get(i).getState());
				nodes.add(j, children.get(i));		
			}
		}
		return nodes;
	}
	
	public Node ID(SearchProblem problem) {
		int depth = 0;
		ArrayList<Node> depths= new ArrayList<>();
		Node root = new Node(0, 0, null, null, "", problem.getInitialState());
		while(depth <= Integer.MAX_VALUE) {
			problem.setExpandedNodes(0);
			Node result = depthLimitedSearch(problem, depth, root, depths);
			if (result != null) {
				return result;
			}
			depth++;
			problem.getStateSpace().removeAll(problem.getStateSpace());
		}
		return null;
	}
		
	public Node depthLimitedSearch(SearchProblem problem, int depth, Node current, ArrayList<Node> nodes) {
		Node expandedBefore = problem.checkExpandedBefore(current, nodes);
		if(expandedBefore != null) {
			if(expandedBefore.getDepth() < current.getDepth()) {
				return null;
			}else if(!expandedBefore.getOperator().equals(current.getOperator())) {
				return null;
			}
		}else {
			nodes.add(current);
		}
		if(!problem.getStateSpace().contains(current.getState())) {
			problem.getStateSpace().add(current.getState());
		}
		if(depth == 0 && problem.goalTest(current.getState())) {
			return current;
		}
		if(depth > 0) {
			current = expand(current, problem);
			for (int i = 0; i < current.getChildren().size(); i++) {
				Node child = current.getChildren().get(i);
				Node result = depthLimitedSearch(problem, depth-1, child, nodes);
				if(result != null) {
					return result;
				}
			}
		}
		return null;
	}
		
	public Node checkExpandedBefore(Node current, ArrayList<Node> nodes) {
		Node checked = null;
		for (int i = 0; i < nodes.size(); i++) {
			if(current.getState().getState().equals(nodes.get(i).getState().getState())) {
				checked = nodes.get(i);
				break;
			}
		}
		return checked;
	}
	
	public ArrayList<Node> GR1(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(children.get(i).getHeuristicfun1() < nodes.get(j).getHeuristicfun1()) break;
			}
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				nodes.add(j, children.get(i));		
				this.getStateSpace().add(children.get(i).getState());
			}
		}
		return nodes;
	}
	
	public ArrayList<Node> AS1(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(children.get(i).getHeuristicfun1()+pathCost(children.get(i)) < nodes.get(j).getHeuristicfun1()+pathCost(nodes.get(j))) break;
			}
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				nodes.add(j, children.get(i));		
				this.getStateSpace().add(children.get(i).getState());
			}
		}
		return nodes;
	}
	
	public ArrayList<Node> GR2(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(children.get(i).getHeuristicfun2() < nodes.get(j).getHeuristicfun2()) break;
			}
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				nodes.add(j, children.get(i));		
				this.getStateSpace().add(children.get(i).getState());
			}
		}
		return nodes;
	}
	
	public ArrayList<Node> AS2(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(children.get(i).getHeuristicfun2()+pathCost(children.get(i)) < nodes.get(j).getHeuristicfun2()+pathCost(nodes.get(j))) break;
			}
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				nodes.add(j, children.get(i));		
				this.getStateSpace().add(children.get(i).getState());
			}
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
