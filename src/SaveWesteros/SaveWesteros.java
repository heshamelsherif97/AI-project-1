package SaveWesteros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import Cell.Cell;
import genericSearch.SearchProblem;
import state.State;
import state.StateWithOperator;
import tree.Node;

//A State in SaveWesteros are in this form [2, 3, 5, 4, /2,5/5,6/7,2/]
//This means Jon snow is cell [2,3]
//Number of alive white walkers are 5
//Number of dragonglass Jon is holding is 4
//There are alive white walkers at position [2,5] , [5,6] , [7,2]
public class SaveWesteros extends SearchProblem {
	//Grid
	private Cell [][] grid;
	//Jon Snow row position
	private int positionI;
	//Jon Snow column position
	private int positionJ;
	//Dragon stone row position
	private int positionIDragon;
	//Dragon Stone column position
	private int positionJDragon;
	//Max number of Dragon glass the dragon stone can provide
	private int maxDragonGlass;
	//Number of dragon Glass Jon Snow holds
	private int currentDragonGlass;
	//Number of white walkers present in the grid
	private int numWhiteWalkers;
	//String describes the positions of the white walkers
	private String whiteWalkersPositions = "";
	
	
	//Constructor
	public SaveWesteros() {
		super();
		//Add the operators of this problem
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
	
	//Function to generate a state in the appropriate representation given the values of the state
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
	
	//Cost of any given operator
	//All operators have the cost of 1
	public int costOfOperator(String operator) {
		return 1;
	}
	
	//Function to compute the first heuristic value of a node
	public Node heuristicfun1(Node n) {
		//Parse the state of the node
		ArrayList<String> state = n.getState().getState();
		int positionI = Integer.parseInt(state.get(0));
		int positionJ = Integer.parseInt(state.get(1));
		int numWhiteWalkers = Integer.parseInt(state.get(2));
		int currentDragonGlass = Integer.parseInt(state.get(3));
		int dragonI = getPositionIDragon();
		int dragonJ = getPositionJDragon();
		int maxDragon = getMaxDragonGlass();
		int h = 0;
		//No white walkers return 0 (Goal)
		if(numWhiteWalkers == 0) {
			h = 0;
		}
		//No dragon Glass then best option is to go to the dragon stone h(n)
		//becomes the diagonal distance of from Jon's position to the dragonStone
		else if(currentDragonGlass == 0) {
			h = (int)Math.sqrt(Math.pow(positionI - dragonI, 2) + Math.pow(positionJ - dragonJ, 2));
		}
		//If Jon has maximum number of dragon stone h(n) is equal to number of white walker - number of adjacent 
		//white walkers on the cell Jon is on
		else if(currentDragonGlass == maxDragon) {
			h = numWhiteWalkers - numAdjacentWW(n.getState(), positionI, positionJ);
		}
		//If the dragonglass is less than the number of white walkers/3 then 
		//h(n) is the min of the distance of jon snow to the dragon stone and number of adjacent 
		//white walkers on the cell Jon is on
		else if(currentDragonGlass < numWhiteWalkers/3) {
			h = (int)Math.sqrt(Math.pow(positionI - dragonI, 2) + Math.pow(positionJ - dragonJ, 2));
			h = Math.min(h, numWhiteWalkers - numAdjacentWW(n.getState(), positionI, positionJ));
		}
		//If dragon glass Jon is holding is equal or greater than number of white walkers/3 then 
		//h(n) is the number of adjacent white walkers on the cell Jon is on
		else if(currentDragonGlass >= numWhiteWalkers/3) {
			h = numWhiteWalkers - numAdjacentWW(n.getState(), positionI, positionJ);
		}
		n.setHeuristicfun1(h); 
		return n;
	}
	
	
	public Node heuristicfun2(Node n) {
		ArrayList<String> state = n.getState().getState();
		int positionI = Integer.parseInt(state.get(0));
		int positionJ = Integer.parseInt(state.get(1));
		int numWhiteWalkers = Integer.parseInt(state.get(2));
		String WWpos = state.get(4);
		int h = 0;
		//Goal
		if(numWhiteWalkers == 0) {
				h = 0;
			}
		//Heuristic value is the diagonal distance from Jon Snow to the nearest white walker
		else {
				h =  getNearestWW(positionI, positionJ, WWpos);
			}
		n.setHeuristicfun2(h); 
		return n;
	}
	
	//Function to get the nearest white walker from Jon's Position
	public int getNearestWW(int i, int j, String wwPositions) {
		String whiteWalkersPositions = wwPositions;
		int minDistance = Integer.MAX_VALUE;
		//Parse the white walkers position element from the state tuple
		String [] parsedWhite = (whiteWalkersPositions.split("/"));
		for (int k = 0; k < parsedWhite.length; k++) {
			if(!parsedWhite[k].equals("")) {
				String [] splitComma = parsedWhite[k].split(",");
				int posX = Integer.parseInt(splitComma[0]);
				int posY = Integer.parseInt(splitComma[1]);
				if(minDistance >= (int)Math.sqrt(Math.pow(i - posX, 2) + Math.pow(j - posY, 2))) {
					minDistance = (int)Math.sqrt(Math.pow(i - posX, 2) + Math.pow(j - posY, 2));
				}
			}
		}
		return minDistance;
	}
	
	//Returns 1 if there is an alive white walker on position i,j
	public int isWhiteWalker(State s, int i, int j) {
		if(parseStateWhite(s, i, j)) {
			return 1;
		}
		return 0;
	}
	
	//Calculates the number of white walkers adjacent to Jon Snow postion
	private int numAdjacentWW(State s,int i, int j) {
		int num = 0;
		//Conditions of special cases (Corners, edges) 
		if(i == 0) {
			if(j == 0) {
				num += (isWhiteWalker(s, i+1, j) + isWhiteWalker(s, i, j+1));
			}else if(j == getGrid()[0].length-1){
				num += (isWhiteWalker(s, i, j-1) + isWhiteWalker(s, i+1, j));
			}else {
				num += (isWhiteWalker(s, i, j-1) + isWhiteWalker(s, i+1, j) + isWhiteWalker(s, i, j+1));
			}
		} else if(i == getGrid().length-1) {
			if(j == 0) {
				num += (isWhiteWalker(s, i-1, j) + isWhiteWalker(s, i, j+1));
			}else if(j == getGrid()[0].length-1){
				num += (isWhiteWalker(s, i-1, j) + isWhiteWalker(s, i, j-1));
			}else {
				num += (isWhiteWalker(s, i, j-1) + isWhiteWalker(s, i-1, j) + isWhiteWalker(s, i, j+1));
			}
		}else if(j == 0) {
			if(i > 0 && i < getGrid().length-1) {
				num += (isWhiteWalker(s, i+1, j) + isWhiteWalker(s, i, j+1) + isWhiteWalker(s, i-1, j));
			}
		}else if(j == getGrid()[0].length-1) {
			if(i > 0 && i < getGrid().length-1) {
				num += (isWhiteWalker(s, i+1, j) + isWhiteWalker(s, i, j-1) + isWhiteWalker(s, i-1, j));
			}
		}else {
			num += (isWhiteWalker(s, i+1, j) + isWhiteWalker(s, i, j-1) + isWhiteWalker(s, i-1, j) + isWhiteWalker(s, i, j+1));
		}
		
		return num;
	}
	
	//Generates a Random Grid
	public void genGrid() {	
		Random r = new Random();
		//Generate a grid with random number of rows and cols not less than 4 and not greater than 10
		int randomX = r.nextInt(7) + 4;
		int randomY = r.nextInt(7) + 4;
		//Random number of dragons glass between 1 and 10 a dragon stone can provide at a time
		maxDragonGlass = r.nextInt(10) + 1;
		this.positionI = randomX - 1;
		this.positionJ = randomY - 1;
		grid = new Cell[randomX][randomY];
		//Initialize Jon Snow position
		grid[this.positionI][this.positionJ] = new Cell("JonSnow", 0);
		//Get a random position for the dragon stone not the same as Jon Snow position
		while(true) {
			int randomDragonX = r.nextInt(randomX);
			int randomDragonY = r.nextInt(randomY);
			if(randomDragonX != positionI && randomDragonY != positionJ) {
				grid[randomDragonX][randomDragonY] = new Cell("dragonStone", maxDragonGlass);
				positionIDragon = randomDragonX;
				positionJDragon = randomDragonY;
				break;
			}
		}
		//Generate the rest of the grid
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
		//Set the initial State for the save westros problem
		this.setInitialState(generateState(positionI, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions));
	}
		
	//Generates a grid from a txt file
	public void genGrid2(String filePath) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(filePath));
		String st;
		ArrayList<String []> cells = new ArrayList<>();
		int maxD = Integer.parseInt(bf.readLine());
		while ((st = bf.readLine()) != null) {
			String [] splitComma = st.split(",");
			cells.add(splitComma);
		}
		int rows = cells.size();
		int cols = cells.get(0).length;
		grid = new Cell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				switch(cells.get(i)[j].trim()) {
				case "emp": 
					grid[i][j] = new Cell("empty", 0);
					break;
				case "dra": 
					grid[i][j] = new Cell("dragonStone", maxD);
					maxDragonGlass = maxD;
					positionIDragon = i;
					positionJDragon = j;
					break;
				case "whi": 
					grid[i][j] = new Cell("whiteWalker", 0);
					numWhiteWalkers++;
					whiteWalkersPositions +=i+","+j+"/";
					break;
				case "Jon":
					grid[i][j] = new Cell("JonSnow", 0);
					this.positionI = i;
					this.positionJ = j;
					break;
				case "obs":
					grid[i][j] = new Cell("obstacle", 0);
					break;
				}
			}
		}
		this.setInitialState(generateState(positionI, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions));
	}
	
	//Used to randomly select a type of cell in the grid from the genGrid() function
	public String generateCell(int x) {
		switch(x) {
		case 0 : return "empty";
		case 1 : return "whiteWalker";
		case 2 : return "obstacle";
		default: return "empty";
		}
	}
	
	//Prints a grid called to visualize various action and sequences to achieve a result
	public void printGrid(Cell [][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print(grid[i][j].getType().substring(0, 3) + ", ");
			}
			System.out.println("");
		}
		System.out.println("------------------------------------");
	}

	//Overridden function from the super class 
	//The goal test is to check the state if the number of white walkers are zero then it is a goal
	@Override
	public boolean goalTest(State s) {
		if(Integer.parseInt(s.getState().get(2)) == 0) {
			return true;
		}
		return false;
	}

	//Search function calls the generic Search from Search problem class and visualize sequences to solve the grid
	public ArrayList<String> Search(Cell [][] grid, String strategy, boolean visualize) {
		//Call the generic Search
		Node result = genericSearch(this, strategy);
		ArrayList<String> output = new ArrayList<>();
		String actionSequences = "";
		Stack<Node> s = new Stack<>();
		Node current = result;
		//Push nodes to stack starting from goal till the parent
		while(current != null) {
			s.push(current);
			current = current.getParent();
		}
		//If visualize is true visualize the solution
		if(visualize && result!=null) s = visualize(s);
		//Pop the stack 1 by 1 to get the order of actions from the root till the end
		while(!s.isEmpty()) {
			Node n = s.pop();
			if(n.getOperator() != null) {
				actionSequences += n.getOperator()+"/ ";
			}
		}
		//If there is a solution
		if(result!=null) {
			output.add("Actions: "+actionSequences);
			output.add("Solution Cost: "+pathCost(result));
			output.add("Expanded nodes: "+getExpandedNodes());
		}
		//If no Solution
		else {
			output.add("No Solution");
			output.add("No Solution");
			output.add("Expanded nodes: "+getExpandedNodes());
		}
		return output;
	}
	
	//Clones a grid cell by cell
	public Cell[][] cloneGrid() {
		Cell [][] clone = new Cell[grid.length][grid[0].length];
		for (int i = 0; i < clone.length; i++) {
			for (int j = 0; j < clone[0].length; j++) {
				Cell c = grid[i][j];
				clone[i][j] = new Cell(c.getType(), c.getDragonGlass());
			}
		}
		return clone;
	}
	
	//Visualize function used to visualize the sequences of actions and the grid when a solution exist
	public Stack<Node> visualize(Stack<Node> s) {
		Stack<Node> oldS = new Stack<>();
		Stack<Node> newS = new Stack<>();
		//Clones the intial grid
		Cell[][] grid2 = cloneGrid();
		//Pop the nodes 1 by 1 to start from the root
		while(!s.isEmpty()) {
			Node poped = s.pop();
			oldS.push(poped);
			//Parse the state
			ArrayList<String> current = poped.getState().getState();
			int positionI = Integer.parseInt(current.get(0));
			int positionJ = Integer.parseInt(current.get(1));
			String whiteWalkersPositions = current.get(4);
			//Adjust the positions after cloning from the original grid
			for (int i = 0; i < grid2.length; i++) {
				for (int j = 0; j < grid2[0].length; j++) {
					if(grid2[i][j].getType().equals("dragonStone") || grid2[i][j].getType().equals("JDS")) {
						if(positionI == i && positionJ ==j) {
							grid2[i][j].setType("JDS");
						}else {
							grid2[i][j].setType("dragonStone");
						}
					}
					if(grid2[i][j].getType().equals("empty") && positionI == i && positionJ == j) {
						grid2[i][j].setType("JonSnow");
					}
					if(grid2[i][j].getType().equals("empty") && positionI != i && positionJ != j) {
						grid2[i][j].setType("empty");
					}
					if(grid2[i][j].getType().equals("JonSnow")) {
						if(positionI == i && positionJ == j) {
							grid2[i][j].setType("JonSnow");
						}else {
							grid2[i][j].setType("empty");
						}
					}
					if(grid2[i][j].getType().equals("whiteWalker")) {
						grid2[i][j].setType("empty");
					}
				}
			}
			grid2 = adjustWhiteWalkers(whiteWalkersPositions, grid2);
			System.out.println("Current DragonGlass: "+ Integer.parseInt(current.get(3)));
			System.out.println("Heuristic1 Cost: "+ poped.getHeuristicfun1());
			System.out.println("Heuristic2 Cost: "+ poped.getHeuristicfun2());
			System.out.println("State: "+ poped.getState().getState().toString());
			System.out.println("Depth: "+ poped.getDepth());
			printGrid(grid2);
		}
		while(!oldS.isEmpty()) {
			newS.push(oldS.pop());
		}
		return newS;
	}
	
	//Puts white walkers that are alive in the grid
	public Cell[][] adjustWhiteWalkers(String s, Cell[][] grid) {
		String [] parsedWhite = (s.split("/"));
		for (int k = 0; k < parsedWhite.length; k++) {
			if(!parsedWhite[k].equals("")) {
				String [] splitComma = parsedWhite[k].split(",");
				int posX = Integer.parseInt(splitComma[0]);
				int posY = Integer.parseInt(splitComma[1]);
				grid[posX][posY].setType("whiteWalker");
			}
		}
		return grid;
	}
	
	
	//Parse the state to get the white walkers positions
	//Returns true if cell i,j contains an alive white walker and not dead
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
	
	//Transition function to get possible transitions from Node n
	public ArrayList<StateWithOperator> transition(Node n){
		ArrayList<StateWithOperator> s = new ArrayList<>();
		//Parse the state
		State currentState = n.getState();
		int positionI = Integer.parseInt(currentState.getState().get(0));
		int positionJ = Integer.parseInt(currentState.getState().get(1));
		int numWhiteWalkers = Integer.parseInt(currentState.getState().get(2));
		int currentDragonGlass = Integer.parseInt(currentState.getState().get(3));
		String whiteWalkersPositions = currentState.getState().get(4);

		//Iterate over all possible operators
		for (int i = 0; i < getOperators().size(); i++) {
			String operator = getOperators().get(i);
			switch(operator) {
			case "up" :
				//If Jon is not on the very top and there is no alive white walker and no obstacle
				//add the possibility of moving upwards
				if(positionI != 0 &&!parseStateWhite(currentState, positionI-1, positionJ) && !grid[positionI-1][positionJ].getType().equals("obstacle")) {
				State newState = generateState(positionI-1, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "up"));
			}break;
			case "down" :
				//If Jon is not on the very bottom and there is no alive white walker and no obstacle 
				//add the possibility of moving downwards
				if(positionI != grid.length-1 && !parseStateWhite(currentState, positionI+1, positionJ) && !grid[positionI+1][positionJ].getType().equals("obstacle")) {
				State newState = generateState(positionI+1, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "down"));
			}break;
			case "left" :
				//If Jon is not on the very left and there is no alive white walker and no obstacle 
				//add the possibility of moving let
				if(positionJ != 0 && !parseStateWhite(currentState, positionI, positionJ-1) && !grid[positionI][positionJ-1].getType().equals("obstacle")) {
				State newState = generateState(positionI, positionJ-1, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "left"));
			}break;
			case "right" :
				//If Jon is not on the very right and there is no alive white walker and no obstacle 
				//add the possibility of moving right
				if(positionJ != grid[0].length-1 && !parseStateWhite(currentState, positionI, positionJ+1) && !grid[positionI][positionJ+1].getType().equals("obstacle")) {
				State newState = generateState(positionI, positionJ+1, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "right"));
			}break;
			case "getDragonGlass" :
				//If the cell is the dragon Stone and Jon doesn't hold max number of dragon glass
				if((grid[positionI][positionJ].getType()).equals("dragonStone") && currentDragonGlass != maxDragonGlass) {
				State newState = generateState(positionI, positionJ, numWhiteWalkers, maxDragonGlass, whiteWalkersPositions);
				s.add(new StateWithOperator(newState, "getDragonGlass")); 
			}break;
			case "attack" :
				//Checks if it is valid attack to add the the transition to attack
				State newState = checkValidAttack(currentState);
				if(newState!=null) {
					s.add(new StateWithOperator(newState, "attack"));
				}
				break;
			}
		}
		return s;
	}

	//Checks if from the current state Jon can perform an attack
	public State checkValidAttack(State currentState) {
		//Parse the state
		int positionI = Integer.parseInt(currentState.getState().get(0));
		int positionJ = Integer.parseInt(currentState.getState().get(1));
		int numWhiteWalkers = Integer.parseInt(currentState.getState().get(2));
		int currentDragonGlass = Integer.parseInt(currentState.getState().get(3));
		String whiteWalkersPositions = currentState.getState().get(4);
		boolean attacked = false;
		//If Jon doesn't have any dragon then he can't attack
		if(currentDragonGlass == 0) {
			return null;
		}
		//If Jon is not on the very bottom and there is an alive white walker down to him attack
		if(positionI!=grid.length-1 && parseStateWhite(currentState, positionI+1, positionJ)) {
			numWhiteWalkers--;
			attacked = true;
			whiteWalkersPositions = removeWhiteWalker(whiteWalkersPositions, positionI+1, positionJ);
		}
		//If Jon is not on the very Top and there is an alive white walker up to him attack
		if(positionI!=0 && parseStateWhite(currentState, positionI-1, positionJ)) {
			numWhiteWalkers--;
			attacked = true;
			whiteWalkersPositions = removeWhiteWalker(whiteWalkersPositions, positionI-1, positionJ);
		}
		//If Jon is not on the very right and there is an alive white walker right to him attack
		if(positionJ!=grid[0].length-1 && parseStateWhite(currentState, positionI, positionJ+1) ) {
			numWhiteWalkers--;
			attacked = true;
			whiteWalkersPositions = removeWhiteWalker(whiteWalkersPositions, positionI, positionJ+1);
		}
		//If Jon is not on the very left and there is an alive white walker left to him attack
		if(positionJ!=0 && parseStateWhite(currentState, positionI, positionJ-1)) {
			numWhiteWalkers--;
			attacked = true;
			whiteWalkersPositions = removeWhiteWalker(whiteWalkersPositions, positionI, positionJ-1);
		}
		//If he at least attack one white walker decrement number of dragon glass
		if(attacked) {
			currentDragonGlass--;
			return generateState(positionI, positionJ, numWhiteWalkers, currentDragonGlass, whiteWalkersPositions);
		}else {
			return null;
		}
	}
	
	//Function to remove white walkers after performing an attack
	public String removeWhiteWalker(String currentState, int i, int j) {
		String whiteWalkersPositions = currentState;
		String position = i+","+j;
		//Parse the postions of white walkers from the state
		String [] parsedWhitePositions = whiteWalkersPositions.split("/");
		for (int k = 0; k < parsedWhitePositions.length; k++) {
			if(parsedWhitePositions[k].equals(position)) {
				parsedWhitePositions[k] = "";
				break;
			}
		} 
		String newWhitePositions = "";
		//Create new parsed String with the removal of the white walker position
		for (int k = 0; k < parsedWhitePositions.length; k++) {
			if(!parsedWhitePositions[k].equals("")){
				newWhitePositions+=parsedWhitePositions[k]+"/";
			}
		}
		return newWhitePositions;
	}
	
	//Deletes all files
	public void deleteFiles() {
        ArrayList<File> files = new ArrayList<>();
        files.add(new File("BF.txt"));
        files.add(new File("DF.txt"));
        files.add(new File("UC.txt"));
        files.add(new File("ID.txt"));
        files.add(new File("GR1.txt"));
        files.add(new File("GR2.txt"));
        files.add(new File("AS1.txt"));
        files.add(new File("AS2.txt"));
        for (int i = 0; i < files.size(); i++) 
            files.get(i).delete();
	}
	
	//Function to call the Search function and print time taken to compute a solution using a search strategy
	public void getSolution(String filePath, String strategy, SaveWesteros s, boolean visualize) throws FileNotFoundException {
		System.out.println("Searching using "+strategy);
		PrintStream fileStream;
		fileStream = new PrintStream(filePath);
		PrintStream console = System.out;
		System.setOut(fileStream);
		long startTime = System.nanoTime();
		ArrayList<String> output = s.Search(s.getGrid(), strategy, visualize);
		System.out.println(output.toString());
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime)/Math.pow(10, 9) + " seconds"); 
		System.setOut(console);
	}

	public static void main(String[] args) throws FileNotFoundException {
		SaveWesteros s= new SaveWesteros();
		//Generates a random Grid
		//s.genGrid();
		
		//Reads a txt file and generates the grid from it
		try {
			s.genGrid2("p2.txt");
		} catch (IOException e) {
			System.out.println("File Not Found");
			System.exit(0);
		}
		//deletes all the files 
		s.deleteFiles();
		
		//Search the generated grid using all search strategies
		s.getSolution("BF.txt", "BF", s, true);
		s.getSolution("DF.txt", "DF", s, true);
		s.getSolution("UC.txt", "UC", s, true);
		s.getSolution("ID.txt", "ID", s, true);
		s.getSolution("GR1.txt", "GR1", s, true);
		s.getSolution("GR2.txt", "GR1", s, true);
		s.getSolution("AS1.txt", "AS1", s, true);
		s.getSolution("AS2.txt", "AS2", s, true);
		System.out.println("Done :)");
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

	public int getPositionIDragon() {
		return positionIDragon;
	}

	public void setPositionIDragon(int positionIDragon) {
		this.positionIDragon = positionIDragon;
	}

	public int getPositionJDragon() {
		return positionJDragon;
	}

	public void setPositionJDragon(int positionJDragon) {
		this.positionJDragon = positionJDragon;
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