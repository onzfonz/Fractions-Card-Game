package logparser;

public class MiningUtils {
	public static String reversePairNames(String pairName) {
		String andStr = " & ";
		int andPos = pairName.indexOf(andStr);
		if(andPos == -1) {
			return pairName;
		}
		String name1 = pairName.substring(0, andPos);
		String name2 = pairName.substring(andPos+andStr.length());
		return name2+andStr+name1;
	}
	
	public static void main(String[] args) {
		System.out.println(reversePairNames("Selina & Josh"));
		System.out.println(reversePairNames("Belmin"));
	}
}
