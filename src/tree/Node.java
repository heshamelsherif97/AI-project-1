package tree;

import java.util.ArrayList;

import state.State;

public class Node {
	private int depth;
	private int cost;
	private Node parent;
	private ArrayList<Node> children;
	private String operator;
	private State state;
	
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
