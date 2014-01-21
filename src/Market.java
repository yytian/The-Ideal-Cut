import java.util.*;
import javax.swing.*;

public class Market
{
	public static int EQUIPRICE = 1500, EQUIQUANT = 10; // Equilibrium for the market
	
	private static final float SLOPE = FileIO.CONSTANTS.get("slope"), Y_INT = FileIO.CONSTANTS.get("y-intercept");
	private int year = 1;
	private String curNews = "Placeholder news";
	private JPanel results;
	private History history = new History();
	
	private Firm[] firms;

	public Market(Firm[] firms)
	{
		this.firms = firms;
		results = new JPanel();
		results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
	}
	
	public void turn()	// Game turn, after all firms have finished
	{
		year++;
		for(Firm f : firms)
			f.setDemandModifier();	// Effects advertisement and interference
		setSales();
		makeResults();
		history.addTurn(firms);
		
		for(Firm f : firms)
			f.reset();
	}
	
	public int getYear()
	{
		return year;
	}
	
	public int equiPrice()
	{
		return EQUIPRICE;
	}
	
	public int equiQuant()
	{
		return EQUIQUANT;
	}
	
	public String getCurNews()
	{
		return curNews;
	}
	
	public static int getDemand(int price)
	{
		return (int)(price * SLOPE + Y_INT);
	}
	
	public static int getPrice(int demand)	// inverse of above
	{
		return (int)((demand - Y_INT) / SLOPE);
	}
	
	public static double getDemand(double price)	// graphing
	{
		return price * SLOPE + Y_INT;
	}
	
	public static double getPrice(double demand)
	{
		return (demand - Y_INT) / SLOPE;
	}
	
	public void setSales()
	{
		List<Firm> rankList = new LinkedList<Firm>( Arrays.asList((Firm[])firms.clone()) );
		Collections.sort(rankList, new Comparator<Firm>()
			{
				public int compare(Firm f1, Firm f2)
				{
					return f1.getPrice() - f2.getPrice();
				}
			});
		while(!rankList.isEmpty())
		{
			int price = rankList.get(0).getPrice();
			int last = 0;	// Last firm which is tied for lowest price
			for( ; last < rankList.size() && rankList.get(last).getPrice() <= price; last++)
				;
			List<Firm> temp = rankList.subList(0, last);	// List of firms with the lowest price
			int totalQuant = 0;	// Total quantity sold by the listed firms
			ListIterator<Firm> iterator = temp.listIterator();
			while(iterator.hasNext())
			{
				Firm f = iterator.next();
				int firmQuant = Math.max( Math.min(f.getDemand() / temp.size(), f.getQuant()), 0 );	 // Quantity sold by this firm
				f.setSales(firmQuant);
				totalQuant += firmQuant;
			}
			rankList.subList(0, last).clear();
			for(Firm f : rankList)
				f.addOffset(totalQuant);
		}
	}
	
	public void makeResults()
	{
		results.removeAll();
		results.setName("Events of year " + year);
		for(Firm f : firms)
		{
			results.add(new JLabel(f.NAME + " sold " + f.getSales() + " at $ " + f.getPrice() + "."));
			int[] interFrom = f.getInterFrom();
			for(int i = 0; i < Firm.FIRMNUM; i++)
				if(interFrom[i] > 0)
					results.add(new JLabel(f.NAME + " interfered with the operations of " + firms[i].NAME + "."));
		}
	}
	
	public JPanel getResults()
	{
		return results;
	}
	
	public History getHistory()
	{
		return history;
	}
}