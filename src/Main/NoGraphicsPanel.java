package Main;

public class NoGraphicsPanel {

	private Grid grid1;
	private Grid grid2;

	private Grid grid;
	private Grid opponent;

	private boolean turn;
	private int totalTurns;

	public boolean gameover;

	public NoGraphicsPanel() {

		grid1 = new NoGraphicsGrid();
		grid2 = new NoGraphicsGrid();

		grid1.randomShips();
		grid2.randomShips();

		grid = grid1;
		opponent = grid2;

		turn = false;
		totalTurns = 0;
		gameover = false;

	}

	public void next() {

		turn = !turn;
		grid = turn ? grid2 : grid1;
		opponent = turn ? grid1 : grid2;

	}

	public void play() {

		Guess guess = grid.ai.guess();
		attemptShipAttack(guess.getX(), guess.getY());
		totalTurns++;
		next();

	}

	public void attemptShipAttack(int x, int y) {

		Ship ship = opponent.getShip(x, y);

		if (ship == null) {

			grid.setStatus(x, y, Grid.MISS);

		} else {

			grid.setStatus(x, y, Grid.HIT);
			ship.hits++;

			if (ship.hits >= ship.length) {

				grid.sink(ship);
				ship.sunk = true;

			}

			boolean won = true;

			for (Ship s : opponent.ships) if (!s.sunk) {

				won = false;
				break;

			}

			if (won) {

				grid.won = true;
				gameover = true;

			}

		}

	}

	public int turns() {

		return totalTurns / 2;

	}

}
