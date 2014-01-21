import java.util.*;

public class History
{
	public List<Turn> turnList = new ArrayList<Turn>();
	
	public History()
	{
		Turn firstTurn = new Turn();
		for(int i = 0; i < Firm.FIRMNUM; i++)
		{
			firstTurn.funds[i] = Firm.STARTFUNDS;
		}
		turnList.add(firstTurn);
	}
	
	public void addTurn(Firm[] firms)
	{
		Turn turn = new Turn();
		for(int i = 0; i < Firm.FIRMNUM; i++)
		{
			turn.sales[i] = firms[i].getSales();
			turn.price[i] = firms[i].getPrice();
			turn.quant[i] = firms[i].getQuant();
			turn.funds[i] = firms[i].getFunds();
			turn.totalCost[i] = firms[i].getTotalCost();
			turn.prodCost[i] = firms[i].getProdCost(turn.quant[i]);
			turn.adsCost[i] = firms[i].getAds();
			turn.interCost[i] = firms[i].getInterCost();
			turn.inter[i] = firms[i].getInterFrom();
		/*	if(Math.abs(Market.EQUIQUANT - turn.quant[i]) < 0 || turn.price[i] - Market.EQUIPRICE < -500)	// Produced above equilibrium or really low price
			{
				if(turn.sales[i] > Market.EQUIQUANT && turn.price[i] > Market.EQUIPRICE)
					turn.verdict[i] = Verdict.TEMPTATION;
				else
					turn.verdict[i] = Verdict.DUMB;
			}
			else if(Math.abs(Market.EQUIQUANT - turn.quant[i]) < 2)	// produced near equilibrium
			{
				if(turn.price[i] > Market.EQUIPRICE && turn.quant[i] == turn.sales[i])
					turn.verdict[i] = Verdict.TEMPTATION;
				else
					turn.verdict[i] = Verdict.PUNISHMENT;
			}
			else	// cartel
			{
				if(turn.quant[i] - turn.sales[i] > 1)
					turn.verdict[i] = Verdict.SUCKER;
				else
					turn.verdict[i] = Verdict.REWARD;
			}*/
		}
		turnList.add(turn);
	}
	
	public Turn getLast()
	{
		return turnList.get(turnList.size()-1);
	}
	
	static class Turn
	{
		public int[] sales = new int[Firm.FIRMNUM];
		public int[] price = new int[Firm.FIRMNUM], quant = new int[Firm.FIRMNUM];
		public int[] funds = new int [Firm.FIRMNUM], totalCost = new int[Firm.FIRMNUM], prodCost = new int[Firm.FIRMNUM],
			adsCost = new int[Firm.FIRMNUM], interCost = new int[Firm.FIRMNUM];
		public int[][] inter = new int[Firm.FIRMNUM][Firm.FIRMNUM];
		public Verdict[] verdict;
	}
	
	static enum Verdict	// How the firm did this turn
	{
		TEMPTATION		("It seems that you've outsmarted your peers. Will it have been worth it?"),
		REWARD			("Your plans are working smoothly; you've made a tidy profit."),
		PUNISHMENT		("You've competed well, but you can't help but think that that could have gone better."),
		SUCKER			("You are not in alignment with the industry; this has cost you."),
		DUMB				("What are you doing? Please rethink your business practices.");
		
		private final String message;
		private Verdict(String message)
		{
			this.message = message;
		}
		
		public String message()
		{
			return message;
		}
	}
}