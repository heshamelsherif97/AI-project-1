package SaveWesteros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Random;

import Cell.Cell;
import genericSearch.SearchProblem;
import state.State;
import state.StateWithOperator;
import tree.Node;

public class SaveWesteros extends SearchProblem {
	private Cell [][] grid;
	private int positionI;
	private int positionJ;
	private int maxDragonGlass;
	private int currentDragonGlass;
	private int numWhiteWalkers;
	private String whiteWalkersPositions = "";
	
	
	
	public SaveWesteros() {
		super();
		ArrayList<String> o = new ArrayList<>();
		o.add("attack");
		o.add("getDragonGlass");
		o.add("up");
		o.add("down");
		o.add("left");
		o.add("right");
		this.setOperators(o);
		setStateSpace(new ArrayList<>());
	}
	
	public State generateState(int i, int j, int k, int d, String white) {
		ArrayList<String> state = new ArrayList<>();
		state.add(i+"");
		state.add(j+"");
		state.add(k+"");
		state.add(d+"");
		state.add(white+"");
		State s = new State(state);
		return s;
	}
	
	
	
	public void genGrid() {
		
		Random r = new Random();
		int randomX = r.nextInt(1) + 3;//7,4
		int randomY = r.nextInt(1) + 3;//7,4
		maxDragonGlass = r.nextInt(10) + 1;
		this.positionI = randomX - 1;
		this.positionJ = randomY - 1;
		grid = new Cell[randomX][randomY];
		grid[this.positionI][this.positionJ] = new Cell("JonSnow", 0);
		while(true) {
			int randomDragonX = r.nextInt(randomX);
			int randomDragonY = r.nextInt(randomY);
			if(randomDragonX != positionI && randomDragonY != positionJ) {
				grid[randomDragonX][randomDragonY] = new Cell("dragonStone", maxDragonGlass);
				break;
			}
		}
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if(grid[i][j] == null) {
					int randomSpawn = r.nextInt(3);
					grid[i][j] = new Cell(generateCell(randomSpawn), 0);
					if(generateCell(randomSpawn).equals("whiteWalker")) {
						numWhiteWalkers++;
						whiteWalkersPositions +=i+","+j+"/";
					}
				}
			}
		}
		this.setInitialState(generateState(positionI, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions));
	}
	
	public String generateCell(int x) {
		switch(x) {
		case 0 : return "empty";
		case 1 : return "whiteWalker";
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

	@Override
	public boolean goalTest(State s) {
		if(Integer.parseInt(s.getState().get(2)) == 0) {
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
	
	public Node Search(Cell [][] grid, String strategy, boolean visualize) {
		Node n = new Node(0, 0, null, null, null, this.getInitialState());
		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(n);
		while(!nodes.isEmpty()) {
			if(nodes.isEmpty()) {
				return null;
			}else {
				Node current = nodes.remove(0);
				if(goalTest(current.getState())) {
					return current;//return node
				}else {
					switch(strategy) {
					case "BF": nodes = BF(grid, visualize, current, nodes);break;
//					case "DF": nodes = DF(grid, visualize, current);break;
//					case "ID": nodes = ID(grid, visualize, current);break;
//					case "UC": nodes = UC(grid, visualize, current);break;
//					case "GRi":nodes = GRi(grid, visualize, current);break;
//					case "ASi":nodes = ASi(grid, visualize, current);break;
					}
				}
			}
		}
		return null;
	}
	
	public ArrayList<Node> BF(Cell [][] grid, boolean visualize, Node current, ArrayList<Node> nodes) {
		//this.setStateSpace(transition(current));
		ArrayList<StateWithOperator> possibleStates = transition(current);
		for (int i = 0; i < possibleStates.size(); i++) {
			Node n = new Node(current.getDepth()+1, current.getCost()+1, current, null, possibleStates.get(i).getOperator(), possibleStates.get(0).getState());
			nodes.add(n);
			System.out.println(n.getState().getState().toString());
		}
		System.out.println(nodes.toString());
		System.out.println(nodes.size());
		return nodes;
	}
	
	public ArrayList<Node> DF(Cell [][] grid, boolean visualize, Node current, ArrayList<Node> nodes) {
		ArrayList<StateWithOperator> possibleStates = transition(current);
		for (int i = 0; i < possibleStates.size(); i++) {
			Node n = new Node(current.getDepth()+1, current.getCost()+1, current, null, possibleStates.get(i).getOperator(), possibleStates.get(0).getState());
			nodes.add(0, n);
		}
		System.out.println(nodes.toString());
		return nodes;
	}
	
//	public ArrayList<Node> UC(Cell [][] grid, boolean visualize) {
//		
//	}
//	
//	public ArrayList<Node> ID(Cell [][] grid, boolean visualize) {
//		
//	}
//	
//	public ArrayList<Node> GRi(Cell [][] grid, boolean visualize) {
//		
//	}
//	
//	public ArrayList<Node> ASi(Cell [][] grid, boolean visualize) {
//		
//	}
	
	public boolean parseStateWhite(State currentState, int i, int j) {
		if(!grid[i][j].getType().equals("whiteWalker") && !grid[i][j].getType().equals("obstacle")) return false;
		String whiteWalkersPositions = currentState.getState().get(4);
		String [] parsedWhite = (whiteWalkersPositions.split("/"));
		for (int k = 0; k < parsedWhite.length; k++) {
			int posX = Character.getNumericValue(parsedWhite[k].charAt(0));
			int posY = Character.getNumericValue(parsedWhite[k].charAt(2));
			if(posX == i && posY == j) {
				return true;
			}
		}
		if(grid[i][j].getType().equals("obstacle")) return true;
		return false;
	}
	
	
	public ArrayList<StateWithOperator> transition(Node n){
		ArrayList<StateWithOperator> s = new ArrayList<>();
		State currentState = n.getState();
		int positionI = Integer.parseInt(currentState.getState().get(0));
		int positionJ = Integer.parseInt(currentState.getState().get(1));
		int numWhiteWalkers = Integer.parseInt(currentState.getState().get(2));
		int currentDragonGlass = Integer.parseInt(currentState.getState().get(3));
		String whiteWalkersPositions = currentState.getState().get(4);

		for (int i = 0; i < getOperators().size(); i++) {
			String operator = getOperators().get(i);
			switch(operator) {
			case "up" :
				if(positionI != 0 &&!parseStateWhite(currentState, positionI-1, positionJ)) {
				State newState = generateState(positionI-1, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				System.out.println("up");
				s.add(new StateWithOperator(newState, "up"));
			}break;
			case "down" :
				if(positionI != grid.length-1 && !parseStateWhite(currentState, positionI+1, positionJ)) {
				System.out.println("down");
				State newState = generateState(positionI+1, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "down"));
			}break;
			case "left" :
				if(positionJ != 0 && !parseStateWhite(currentState, positionI, positionJ-1)) {
				State newState = generateState(positionI, positionJ-1, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "left"));
				System.out.println("left");
			}break;
			case "right" :
				if(positionJ != grid[0].length-1 && !parseStateWhite(currentState, positionI, positionJ+1)) {
				State newState = generateState(positionI, positionJ+1, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "right"));
				System.out.println("right");
			}break;
			case "getDragonGlass" :
				if(grid[positionI][positionJ].getType().equals("dragonStone") && currentDragonGlass != maxDragonGlass) {
				State newState = generateState(positionI, positionJ, numWhiteWalkers, maxDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "getDragonGlass"));
				System.out.println("DS");
			}break;
			case "attack" :
				State newState = checkValidAttack(currentState);
				if(newState!=null) {
					s.add(new StateWithOperator(newState, "attack"));
					System.out.println("Attack");
				}
				break;
			}
		}
		return s;
	}

	
	public State checkValidAttack(State currentState) {
		
		int positionI = Integer.parseInt(currentState.getState().get(0));
		int positionJ = Integer.parseInt(currentState.getState().get(1));
		int numWhiteWalkers = Integer.parseInt(currentState.getState().get(2));
		int currentDragonGlass = Integer.parseInt(currentState.getState().get(3));
		String whiteWalkersPositions = currentState.getState().get(4);
		boolean attacked = false;
		if(currentDragonGlass == 0) {
			return null;
		}
		if(positionI!=grid.length-1 && parseStateWhite(currentState, positionI+1, positionJ)) {
			numWhiteWalkers--;
			attacked = true;
			whiteWalkersPositions = removeWhiteWalker(whiteWalkersPositions, positionI+1, positionJ);
		}
		if(positionI!=0 && parseStateWhite(currentState, positionI-1, positionJ)) {
			numWhiteWalkers--;
			attacked = true;
			whiteWalkersPositions = removeWhiteWalker(whiteWalkersPositions, positionI-1, positionJ);
		}
		if(positionJ!=grid.length-1 && parseStateWhite(currentState, positionI, positionJ+1) ) {
			numWhiteWalkers--;
			attacked = true;
			whiteWalkersPositions = removeWhiteWalker(whiteWalkersPositions, positionI, positionJ+1);
		}
		if(positionJ!=0 && parseStateWhite(currentState, positionI, positionJ-1)) {
			numWhiteWalkers--;
			attacked = true;
			whiteWalkersPositions = removeWhiteWalker(whiteWalkersPositions, positionI, positionJ-1);
		}
		if(attacked) {
			currentDragonGlass--;
			return generateState(positionI, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
		}else {
			return null;
		}
	}
	
	public String removeWhiteWalker(String currentState, int i, int j) {
		String whiteWalkersPositions = currentState;
		String position = i+","+j;
		String [] parsedWhitePositions = whiteWalkersPositions.split("/");
		for (int k = 0; k < parsedWhitePositions.length; k++) {
			if(parsedWhitePositions[k].equals(position)) {
				parsedWhitePositions[k] = "";
				break;
			}
		} 
		String newWhitePositions = "";
		for (int k = 0; k < parsedWhitePositions.length; k++) {
			newWhitePositions+=parsedWhitePositions[k]+"/";
		}
		return newWhitePositions;
	}

	public static void main(String[] args) {
		SaveWesteros s= new SaveWesteros();
		s.genGrid();
		//State st = s.generateState(0, 0, s.getNumWhiteWalkers(), s.getMaxDragonGlass(), s.getWhiteWalkersPositions());
		//System.out.println(s.removeWhiteWalker(st, s.getPositionI()-1, s.getPositionJ()));
		s.printGrid();
		System.out.println(s.Search(s.getGrid(), "BF", false));
	}

	public Cell[][] getGrid() {
		return grid;
	}

	public void setGrid(Cell[][] grid) {
		this.grid = grid;
	}

	public int getPositionI() {
		return positionI;
	}

	public void setPositionI(int positionI) {
		this.positionI = positionI;
	}

	public int getPositionJ() {
		return positionJ;
	}

	public void setPositionJ(int positionJ) {
		this.positionJ = positionJ;
	}

	public int getMaxDragonGlass() {
		return maxDragonGlass;
	}

	public void setMaxDragonGlass(int maxDragonGlass) {
		this.maxDragonGlass = maxDragonGlass;
	}

	public int getCurrentDragonGlass() {
		return currentDragonGlass;
	}

	public void setCurrentDragonGlass(int currentDragonGlass) {
		this.currentDragonGlass = currentDragonGlass;
	}

	public int getNumWhiteWalkers() {
		return numWhiteWalkers;
	}

	public void setNumWhiteWalkers(int numWhiteWalkers) {
		this.numWhiteWalkers = numWhiteWalkers;
	}

	public String getWhiteWalkersPositions() {
		return whiteWalkersPositions;
	}

	public void setWhiteWalkersPositions(String whiteWalkersPositions) {
		this.whiteWalkersPositions = whiteWalkersPositions;
	}
	
	
	
}
