package genericSearch;

import java.util.ArrayList;
import java.util.Iterator;

import state.State;
import state.StateWithOperator;
import tree.Node;

public abstract class SearchProblem {
	
	//List of all operators
	private ArrayList<String> operators;
	//Initial state
	private State initialState;
	//State space to keep track of expanded nodes (states) and avoid repeated states
	private ArrayList<State> stateSpace;
	//To keep track of how much nodes were chosen for expansion
	private int expandedNodes = 0;
	
	//Abstract Methods
	
	//Goal test is specific for every subclass of problem
	public abstract boolean goalTest(State s);
	//Transition Function to generate children of states
	public abstract ArrayList<StateWithOperator> transition(Node n);
	//Function to return the cost of specific operator
	public abstract int costOfOperator(String operator);
	//function to calculate first heuristic function for a node
	public abstract Node heuristicfun1(Node n);
	//function to calculate second heuristic function for a node
	public abstract Node heuristicfun2(Node n);

	//Getting the path cost from root to specific node
	public int pathCost(Node n) {
		Node current = n;
		int cost = 0;
		while(current != null) {
			cost += current.getCost();
			current = current.getParent();
		}
		return cost;
	}
	
	//General Search algorithm
	public static Node genericSearch(SearchProblem s, String strategy) {
		//Reset state space and expanded nodes
		s.getStateSpace().removeAll(s.getStateSpace());						
		s.setExpandedNodes(0);			
		//Add initial state to state space
		s.getStateSpace().add(s.getInitialState());
		//Queue
		ArrayList<Node> nodes = new ArrayList<>();
		//Create node for the root with 0 cost, 0 depth and "" operator
		Node n = new Node(0, 0, null, null, "", s.getInitialState());
		//Calculate heuristic functions
		n = s.heuristicfun1(n);
		n = s.heuristicfun2(n);
		//Add root to queue
		nodes.add(n);
		//Generic search algorithm
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
	
	//Expand function 
	public static Node expand(Node n, SearchProblem problem) {
		ArrayList<StateWithOperator> possibleStates = problem.transition(n);
		ArrayList<Node> children = new ArrayList<>();
		//Loop over all possible states coming from the transition function
		for (int i = 0; i < possibleStates.size(); i++) {
			Node newNode = new Node(n.getDepth()+1, problem.costOfOperator(possibleStates.get(i).getOperator()), n, null, possibleStates.get(i).getOperator(), possibleStates.get(i).getState());
			newNode = problem.heuristicfun1(newNode);
			newNode = problem.heuristicfun2(newNode);
			children.add(newNode);
				}
		//Increment number of expanded nodes
		problem.setExpandedNodes(problem.getExpandedNodes()+1);
		n.setChildren(children);
		return n;
	}
	
	//Breadth first Search algorithm
	public ArrayList<Node> BF(ArrayList<Node> nodes, ArrayList<Node> children) {
		//loop over the expanded Nodes
		for (int i = 0; i < children.size(); i++) {
			//Checking if the node expanded state was not before added to the queue to avoid repeated states 
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				this.getStateSpace().add(children.get(i).getState());
				//enqueue at end
				nodes.add(children.get(i));
			}
		}
		return nodes;
	}
	
	//Depth First Search algorithm
	public ArrayList<Node> DF(ArrayList<Node> nodes, ArrayList<Node> children) {
		ArrayList<Node> possibleChildren = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {
			//Checking if the node expanded state was not before added to the queue to avoid repeated states 
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				this.getStateSpace().add(children.get(i).getState());
				possibleChildren.add(children.get(i));
			}
		}
		//Enqueue expanded nodes at front
		nodes.addAll(0, possibleChildren);
		return nodes;
	}
	
	//Uniform Cost search Algorithm
	public ArrayList<Node> UC(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				if(pathCost(children.get(i)) < pathCost(nodes.get(j))) break;
			}
			//Checking if the node expanded state was not before added to the queue to avoid repeated states 
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				this.getStateSpace().add(children.get(i).getState());
				//Adding the specific node in the correct position according to the path cost from the root
				nodes.add(j, children.get(i));		
			}
		}
		return nodes;
	}
	
	//Iterative deepining Search algorithm
	public Node ID(SearchProblem problem) {
		//Begin with depth 0
		int depth = 0;
		//Arraylist To keep track of expanded nodes at lower depth
		ArrayList<Node> depths= new ArrayList<>();
		//Root
		Node root = new Node(0, 0, null, null, "", problem.getInitialState());
		//Loop till the maximum value Integer data type can hold
		while(depth <= Integer.MAX_VALUE) {
			//Reset Expanded nodes at every depth
			problem.setExpandedNodes(0);
			//Helper function
			Node result = depthLimitedSearch(problem, depth, root, depths);
			//If there is a result return it
			if (result != null) {
				return result;
			}
			depth++;
			problem.getStateSpace().removeAll(problem.getStateSpace());
		}
		return null;
	}
		
	//Helper method for iterative deepining
	public Node depthLimitedSearch(SearchProblem problem, int depth, Node current, ArrayList<Node> nodes) {
		//Check if this node (state) was expanded before
		Node expandedBefore = problem.checkExpandedBefore(current, nodes);
		if(expandedBefore != null) {
			//If the node being expanded has been expanded before with lower depth don't expand
			if(expandedBefore.getDepth() < current.getDepth()) {
				return null;
			//If the node being expanded has been expanded before with same depth but different operator don't expand
			}else if(!expandedBefore.getOperator().equals(current.getOperator())) {
				return null;
			}
		}//If it was not expanded before add it to list of expanded nodes
		else {
			nodes.add(current);
		}
		//If it was not in the state space before add it
		if(!problem.getStateSpace().contains(current.getState())) {
			problem.getStateSpace().add(current.getState());
		}
		//Base case if the current node is a goald and the depth is equal to 0 return the current node
		if(depth == 0 && problem.goalTest(current.getState())) {
			return current;
		}
		//Else if depth greater than 0
		if(depth > 0) {
			//Expand current node
			current = expand(current, problem);
			//Loop over its children
			for (int i = 0; i < current.getChildren().size(); i++) {
				Node child = current.getChildren().get(i);
				//Call the recursive function with depth -1
				Node result = depthLimitedSearch(problem, depth-1, child, nodes);
				//If it returns a result return the node
				if(result != null) {
					return result;
				}
			}
		}
		return null;
	}
	
	//Checks if a node(state) has been expanded before
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
	
	//Greedy heuristic 1 algorithm
	public ArrayList<Node> GR1(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				//Checks the heurisitc value
				if(children.get(i).getHeuristicfun1() < nodes.get(j).getHeuristicfun1()) break;
			}
			//Not expanded before
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				//Add the node at the appropriate position 
				nodes.add(j, children.get(i));		
				this.getStateSpace().add(children.get(i).getState());
			}
		}
		return nodes;
	}
	
	//A* algorithm
	public ArrayList<Node> AS1(ArrayList<Node> nodes, ArrayList<Node> children) {
		for (int i = 0; i < children.size(); i++) {
			int j=0;
			for (j = 0; j < nodes.size(); j++) {
				//checks the heuristic value + the path cost
				if(children.get(i).getHeuristicfun1()+pathCost(children.get(i)) < nodes.get(j).getHeuristicfun1()+pathCost(nodes.get(j))) break;
			}
			if(!((children.get(i)).getState()).checkSameState(getStateSpace())){
				nodes.add(j, children.get(i));		
				this.getStateSpace().add(children.get(i).getState());
			}
		}
		return nodes;
	}
	
	//Greedy 2 same as Greedy 1 but checks second heuristic value
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
	
	//A* 2 same as A* 1 but checks second heuristic value and path cost
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
