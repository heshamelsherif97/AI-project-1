package tree;

import java.util.ArrayList;

import state.State;
/*The search tree node class describes how the nodes are implemented
 * All nodes have a depth, a cost, a self referential node which refernces to the parent of this node,
 * An arraylist which includes the children of this node after it is expanded, an operator which was applied 
 * to generate this node, the state accompanied to this node and 2 attributes holding the 2 heuristic
 * values for the node 
 */
public class Node {
	private int depth;
	private int cost;
	private Node parent;
	private ArrayList<Node> children;
	private String operator;
	private State state;
	private int heuristicfun1;
	private int heuristicfun2;
	
	public Node(int depth, int cost, Node parent, ArrayList<Node> children, String operator, State state) {
		super();
		this.depth = depth;
		this.cost = cost;
		this.parent = parent;
		this.children = children;
		this.operator = operator;
		this.state = state;
	}

	public int getDepth() {
		return depth;
	}
	

	public double getHeuristicfun1() {
		return heuristicfun1;
	}

	public void setHeuristicfun1(int heuristicfun1) {
		this.heuristicfun1 = heuristicfun1;
	}

	public double getHeuristicfun2() {
		return heuristicfun2;
	}

	public void setHeuristicfun2(int heuristicfun2) {
		this.heuristicfun2 = heuristicfun2;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
}