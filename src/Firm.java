public class Firm
{
	public static final int FIRMNUM = 4;	// number of firms in the game
	public static final int MAXPRICE = FileIO.CONSTANTS.get("maximumprice").intValue(),
									MAXQUANT = FileIO.CONSTANTS.get("maximumquantity").intValue();
	public static final int MAXPR = FileIO.CONSTANTS.get("maximumPR(adsetc.)").intValue();
	public static final int STARTFUNDS = FileIO.CONSTANTS.get("startingfunds").intValue();
	public static final float PRQUANTUM = FileIO.CONSTANTS.get("PRgranularity");	// Amount of spending needed to double/halve modifier
	
	public static final float A = FileIO.CONSTANTS.get("a"), B = FileIO.CONSTANTS.get("b"), C = FileIO.CONSTANTS.get("c"), D = FileIO.CONSTANTS.get("d");
	/* Aesthetics */
	public final String PLAYER, NAME;
	public final java.awt.Image LOGO;
	public final javax.swing.ImageIcon ICON;
	/* Firm properties */
	private int funds = STARTFUNDS;
	private int sales = 0;
	private int price = 1, quant = 0;
	private int unsold = 0;
	private int ads = 0, interTo = 0, interFrom[] = new int[FIRMNUM], tempInterTo = 0;	// interTo is done to this firm, interFrom by it
	/* Market miscellaneous */
	private float demandModifier = 1.0f;	// The share depends on the total available in the market
	private int demandOffset = 0;	// If other firms have taken demand
	
	public Firm(int num, String player)
	{
		NAME = FileIO.firmNames[num];
		LOGO = FileIO.firmLogos[num];
		ICON = new javax.swing.ImageIcon(LOGO.getScaledInstance(GameScreen.LOGOWIDTH, GameScreen.LOGOHEIGHT, java.awt.Image.SCALE_SMOOTH));
		PLAYER = player;
	}
	
	public void reset()
	{
		price = 1;
		quant = 0;
		demandOffset = 0;
		demandModifier = 1;
		ads = 0;
		interTo = 0;
		interFrom = new int[FIRMNUM];
		tempInterTo = 0;
	}
	
	public void setSales(int sales)	// used by Market
	{
		this.sales = sales;
		funds += price * Math.min(quant, sales);
		funds -= getTotalCost();
		unsold = Math.max(0, quant - sales);
	}
	
	public int getSales()
	{
		return sales;
	}
	
	public int getDemand()
	{
		return (int) Math.round((Market.getDemand(price) - demandOffset) * demandModifier);
	}
	
	public void addOffset(int offset)
	{
		demandOffset += offset;
	}
	
	public void setProduction(int price, int quant)
	{
		this.price = price;
		this.quant = quant;
	}
	
	public void addAds(int spent)
	{
		ads = spent;
	}
	
	public void addInterTo(int inter)	// At ends of turn, add inter from the other firms
	{
		interTo = inter;
	}
	
	public void addInterFrom(int index, int spent)
	{
		interFrom[index] = spent;
	}
	
	public void setDemandModifier()	// At ends of turn, changes market share based on ads and interference
	{
		demandModifier *= ads / PRQUANTUM + 1;
		if(interTo > 0)
			demandModifier /= interTo / PRQUANTUM + 1;
	}
	
	public float getDemandModifier()
	{
		return demandModifier;
	}
	
	public int getFunds()
	{
		return funds;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public int getQuant()
	{
		return quant;
	}
	
	public int getAds()
	{
		return ads;
	}
	
	public int getInter()
	{
		return interTo;
	}
	
	public static int getProdCost(int quant)
	{
		if(quant == 0)
			return 0;
		return (int)(A * Math.pow(quant, 3) + B * Math.pow(quant, 2) + C * quant + D);
	}
	
	public static double getProdCost(double quant)	// for graphing
	{
		if(quant < 1)
			return Double.NaN;
		return (A * Math.pow(quant, 3) + B * Math.pow(quant, 2) + C * quant + D);
	}
	
	public int[] getInterFrom()
	{
		return interFrom;
	}
	
	public int getInterCost()
	{
		int temp = 0;
		for(int i = 0; i < FIRMNUM; i++)
			temp += interFrom[i];
		return temp;
	}
	
	public int getTotalCost()
	{
		return (int)getProdCost(quant) + ads + getInterCost();
	}
	
	public int getUnsold()
	{
		return unsold;
	}
	
	public static void main(String[] args)
	{
		System.out.println(getProdCost(10000));	// Testing
	}
}