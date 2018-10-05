package SaveWesteros;

import java.util.ArrayList;
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
		ArrayList<String> state = new ArrayList<>();
		state.add(this.positionI+"");
		state.add(this.positionJ+"");
		state.add(numWhiteWalkers+"");
		State s = new State(state);
		setStateSpace(new ArrayList<>());
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
		
		switch(strategy) {
		case "BF": BF(grid, visualize);break;
		case "DF": DF(grid, visualize);break;
		case "ID": ID(grid, visualize);break;
		case "UC": UC(grid, visualize);break;
		case "GRi":GRi(grid, visualize);break;
		case "ASi":ASi(grid, visualize);break;
		}
	}
	
	public void BF(Cell [][] grid, boolean visualize) {
		
	}
	
	public void DF(Cell [][] grid, boolean visualize) {
		
	}
	
	public void UC(Cell [][] grid, boolean visualize) {
		
	}
	
	public void ID(Cell [][] grid, boolean visualize) {
		
	}
	
	public void GRi(Cell [][] grid, boolean visualize) {
		
	}
	
	public void ASi(Cell [][] grid, boolean visualize) {
		
	}

}
