package SaveWesteros;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import Cell.Cell;
import genericSearch.SearchProblem;
import state.State;
import tree.Node;

public class SaveWesteros extends SearchProblem {
	private Cell [][] grid;
	private int positionI;
	private int positionJ;
	private int maxDragonGlass;
	private int currentDragonGlass;
	private int numWhiteWalkers;
	
	
	
	public SaveWesteros() {
		super();
		genGrid();
		ArrayList<String> o = new ArrayList<>();
		o.add("up");
		o.add("down");
		o.add("left");
		o.add("right");
		o.add("attack");
		o.add("getDragonGlass");
		this.setOperators(o);
		this.setInitialState(generateState(positionI, positionJ, numWhiteWalkers, currentDragonGlass));
		setStateSpace(new ArrayList<>());
	}
	
	public State generateState(int i, int j, int k, int d) {
		ArrayList<String> state = new ArrayList<>();
		state.add(i+"");
		state.add(j+"");
		state.add(k+"");
		state.add(d+"");
		State s = new State(state);
		return s;
	}

	public void genGrid() {
		Random r = new Random();
		int randomX = r.nextInt(7) + 4;
		int randomY = r.nextInt(7) + 4;
		maxDragonGlass = r.nextInt(10) + 1;
		this.positionI = randomX - 1;
		this.positionJ = randomY - 1;
		grid = new Cell[randomX][randomY];
		grid[this.positionI][this.positionJ] = new Cell("JonSnow", 0);
		int randomDragonX = r.nextInt(randomX);
		int randomDragonY = r.nextInt(randomY);
		grid[randomDragonX][randomDragonY] = new Cell("dragonStone", maxDragonGlass);
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if(grid[i][j] == null) {
					int randomSpawn = r.nextInt(3);
					grid[i][j] = new Cell(generateCell(randomSpawn), 0);
				}
			}
		}
	}
	
	public String generateCell(int x) {
		switch(x) {
		case 0 : return "empty";
		case 1 : numWhiteWalkers++; return "whiteWalker";
		case 2 : return "obstacle";
		default: return "empty";
		}
	}
	
	public void printGrid() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print(grid[i][j].getType().substring(0, 3) + ", ");
			}
			System.out.println("");
		}
	}
	
	public static void main(String[] args) {
		SaveWesteros s= new SaveWesteros();
		s.printGrid();
	}

	@Override
	public boolean goalTest(State s) {
		if(numWhiteWalkers == 0) {
			return true;
		}
		return false;
	}

	@Override
	public int pathCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Node genericSearch(SearchProblem s, String strategy) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
//	public boolean isValid(State s, String operator) {
//		int posI = Integer.parseInt(s.getState().get(0));
//		int posJ = Integer.parseInt(s.getState().get(1));
//		switch(operator) {
//		case "up": return checkMove();
//		}
//	}
	
	public void Search(Cell [][] grid, String strategy, boolean visualize) {
		Node n = new Node(0, 0, null, null, null, this.getInitialState());
		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(n);
		while(!nodes.isEmpty()) {
			if(nodes.isEmpty()) {
				return;
			}else {
				Node current = nodes.remove(0);
				if(goalTest(current.getState())) {
					return ;//return node
				}else {
					switch(strategy) {
					case "BF": nodes = BF(grid, visualize, current);break;
					case "DF": nodes = DF(grid, visualize, current);break;
					case "ID": nodes = ID(grid, visualize, current);break;
					case "UC": nodes = UC(grid, visualize, current);break;
					case "GRi":nodes = GRi(grid, visualize, current);break;
					case "ASi":nodes = ASi(grid, visualize, current);break;
					}
				}
			}
		}
	}
	
	public ArrayList<Node> BF(Cell [][] grid, boolean visualize, Node current) {
		this.setStateSpace(transition(current));
	}
	
	public ArrayList<Node> DF(Cell [][] grid, boolean visualize) {
		
	}
	
	public ArrayList<Node> UC(Cell [][] grid, boolean visualize) {
		
	}
	
	public ArrayList<Node> ID(Cell [][] grid, boolean visualize) {
		
	}
	
	public ArrayList<Node> GRi(Cell [][] grid, boolean visualize) {
		
	}
	
	public ArrayList<Node> ASi(Cell [][] grid, boolean visualize) {
		
	}
	
	public ArrayList<State> transition(Node n){
		ArrayList<State> s = new ArrayList<>();
		for (int i = 0; i < getOperators().size(); i++) {
			String operator = getOperators().get(i);
			switch(operator) {
			case "up" : if(positionI != 0 && (grid[positionI-1][positionJ].getType().equals("empty") || grid[positionI-1][positionJ].getType().equals("dragonStone"))) {
				s.add(generateState(positionI-1, positionJ, numWhiteWalkers, currentDragonGlass));
			}
			case "down" : if(positionI != grid.length-1 && (grid[positionI+1][positionJ].getType().equals("empty") || grid[positionI+1][positionJ].getType().equals("dragonStone"))) {
				s.add(generateState(positionI+1, positionJ, numWhiteWalkers, currentDragonGlass));
			}
			case "left" : if(positionJ != 0 && (grid[positionI][positionJ-1].getType().equals("empty") || grid[positionI][positionJ-1].getType().equals("dragonStone"))) {
				s.add(generateState(positionI, positionJ-1, numWhiteWalkers, currentDragonGlass));
			}
			case "right" : if(positionI != grid[0].length-1 && (grid[positionI][positionJ+1].getType().equals("empty") || grid[positionI][positionJ+1].getType().equals("dragonStone"))) {
				s.add(generateState(positionI, positionJ+1, numWhiteWalkers, currentDragonGlass));
			}
			case "getDragonGlass" : if(grid[positionI][positionJ].getType().equals("dragonStone") && currentDragonGlass != maxDragonGlass) {
				s.add(generateState(positionI, positionJ, numWhiteWalkers, maxDragonGlass));
			}
			case "attack" : if(positionI != grid[0].length-1 && (grid[positionI][positionJ+1].getType().equals("empty") || grid[positionI][positionJ+1].getType().equals("dragonStone"))) {
				s.add(generateState(positionI, positionJ+1, numWhiteWalkers, currentDragonGlass));
			}
			}
		}
	}

}
