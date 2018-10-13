package SaveWesteros;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

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
		o.add("left");
		o.add("down");
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
	
	public int costOfOperator(String operator) {
		return 1;
	}
	
	
	
	public void genGrid() {
		
		Random r = new Random();
		int randomX = r.nextInt(1) + 2;//7,4
		int randomY = r.nextInt(1) + 2;//7,4
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
		System.out.println("------------------------------------");
	}

	@Override
	public boolean goalTest(State s) {
		if(Integer.parseInt(s.getState().get(2)) == 0) {
			return true;
		}
		return false;
	}

	public ArrayList<String> Search(Cell [][] grid, String strategy, boolean visualize) {//ArrayList<String>
		Node result = genericSearch(this, strategy);
		ArrayList<String> output = new ArrayList<>();
		String actionSequences = "";
		Stack<Node> s = new Stack<>();
		Node current = result;
		while(current != null) {
			s.push(current);
			current = current.getParent();
		}
		if(visualize && result!=null) s = visualize(s);
		while(!s.isEmpty()) {
			Node n = s.pop();
			if(n.getOperator() != null) {
				actionSequences += n.getOperator()+"/ ";
			}
			//System.out.println("Current State: "+ n.getState().getState().toString()+ " , Operator done: "+n.getOperator()+ " , Depth:"+n.getDepth()+ " , Cost:"+n.getCost());
		}
		if(result!=null) {
			output.add("Actions: "+actionSequences);
			output.add("Solution Cost: "+pathCost(result));
			output.add("Expanded nodes: "+getExpandedNodes());
		}else {
			output.add("No Solution");
			output.add("No Solution");
			output.add("Expanded nodes: "+getExpandedNodes());
		}
		return output;
	}
	
	public Stack<Node> visualize(Stack<Node> s) {
		Stack<Node> oldS = new Stack<>();
		Stack<Node> newS = new Stack<>();
		Cell[][] grid = this.grid;
		while(!s.isEmpty()) {
			Node poped = s.pop();
			oldS.push(poped);
			ArrayList<String> current = poped.getState().getState();
			int positionI = Integer.parseInt(current.get(0));
			int positionJ = Integer.parseInt(current.get(1));
			String whiteWalkersPositions = current.get(4);
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[0].length; j++) {
					if(grid[i][j].getType().equals("dragonStone") || grid[i][j].getType().equals("JDS")) {
						if(positionI == i && positionJ ==j) {
							grid[i][j].setType("JDS");
						}else {
							grid[i][j].setType("dragonStone");
						}
					}
					if(grid[i][j].getType().equals("empty") && positionI == i && positionJ == j) {
						grid[i][j].setType("JonSnow");
					}
					if(grid[i][j].getType().equals("empty") && positionI != i && positionJ != j) {
						grid[i][j].setType("empty");
					}
					if(grid[i][j].getType().equals("JonSnow")) {
						if(positionI == i && positionJ == j) {
							grid[i][j].setType("JonSnow");
						}else {
							grid[i][j].setType("empty");
						}
					}
					if(grid[i][j].getType().equals("whiteWalker")) {
						grid[i][j].setType("empty");
					}
				}
			}
			adjustWhiteWalkers(whiteWalkersPositions);
			System.out.println("Current DragonGlass: "+ Integer.parseInt(current.get(3)));
			printGrid();
		}
		while(!oldS.isEmpty()) {
			newS.push(oldS.pop());
		}
		return newS;
	}
	
	public void adjustWhiteWalkers(String s) {
		String [] parsedWhite = (s.split("/"));
		for (int k = 0; k < parsedWhite.length; k++) {
			if(!parsedWhite[k].equals("")) {
				String [] splitComma = parsedWhite[k].split(",");
				int posX = Integer.parseInt(splitComma[0]);
				int posY = Integer.parseInt(splitComma[1]);
				grid[posX][posY].setType("whiteWalker");
			}
		}
	}
		
	public boolean parseStateWhite(State currentState, int i, int j) {
		if(!grid[i][j].getType().equals("whiteWalker") && !grid[i][j].getType().equals("obstacle")) return false;
		String whiteWalkersPositions = currentState.getState().get(4);
		String [] parsedWhite = (whiteWalkersPositions.split("/"));
		for (int k = 0; k < parsedWhite.length; k++) {
			if(!parsedWhite[k].equals("")) {
				String [] splitComma = parsedWhite[k].split(",");
				int posX = Integer.parseInt(splitComma[0]);
				int posY = Integer.parseInt(splitComma[1]);
				if(posX == i && posY == j) {
					return true;
				}
			}
		}
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
				if(positionI != 0 &&!parseStateWhite(currentState, positionI-1, positionJ) && !grid[positionI-1][positionJ].getType().equals("obstacle")) {
				State newState = generateState(positionI-1, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "up"));
			}break;
			case "down" :
				if(positionI != grid.length-1 && !parseStateWhite(currentState, positionI+1, positionJ) && !grid[positionI+1][positionJ].getType().equals("obstacle")) {
				State newState = generateState(positionI+1, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "down"));
			}break;
			case "left" :
				if(positionJ != 0 && !parseStateWhite(currentState, positionI, positionJ-1) && !grid[positionI][positionJ-1].getType().equals("obstacle")) {
				State newState = generateState(positionI, positionJ-1, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "left"));
			}break;
			case "right" :
				if(positionJ != grid[0].length-1 && !parseStateWhite(currentState, positionI, positionJ+1) && !grid[positionI][positionJ+1].getType().equals("obstacle")) {
				State newState = generateState(positionI, positionJ+1, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "right"));
			}break;
			case "getDragonGlass" :
				if((grid[positionI][positionJ].getType()).equals("dragonStone") && currentDragonGlass != maxDragonGlass) {
				State newState = generateState(positionI, positionJ, numWhiteWalkers, maxDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "getDragonGlass")); 
			}break;
			case "attack" :
				State newState = checkValidAttack(currentState);
				if(newState!=null) {
					s.add(new StateWithOperator(newState, "attack"));
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
		if(positionJ!=grid[0].length-1 && parseStateWhite(currentState, positionI, positionJ+1) ) {
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
			if(!parsedWhitePositions[k].equals("")){
				newWhitePositions+=parsedWhitePositions[k]+"/";
			}
		}
		return newWhitePositions;
	}

	public static void main(String[] args) {
		SaveWesteros s= new SaveWesteros();
		s.genGrid();
		s.printGrid();
		System.out.println("Searching for Solotion");
		ArrayList<String> output = s.Search(s.getGrid(), "ID", true);
		System.out.println(output.toString());

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