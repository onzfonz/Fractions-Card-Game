package basic;

public class DealerFactory {
	public static Dealer getNewDealer() {
		if(Constants.USE_RIGGED_DECK) {
			return new SequentialDealer();
		}
		return new SmartDealer();
	}
	
	public static Dealer getNewDealer(String tricksName, String teamsName, String teamsNameAlt) {
		if(Constants.USE_RIGGED_DECK) {
			return new SequentialDealer(tricksName, teamsName);
		}
		return new SmartDealer(tricksName, teamsName, teamsNameAlt);
	}
}
