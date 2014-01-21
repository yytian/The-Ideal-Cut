import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.*;
import org.jfree.data.xy.*;

public class Statistics
{
	public static final int SHORTRUN = 0, SUPPLYDEMAND = 1, FIRMSALES = 2, FIRMCOSTS = 3, FIRMPR = 4;
	public static final Color[] COLORS = {Color.BLUE, Color.RED, Color.BLACK, Color.CYAN, Color.MAGENTA};
	private static int curFirmIndex;	// for firm-dependent charts

	public static void getFirmStats(GameIO game, int index)
	{
		curFirmIndex = index;
		JPanel panel = new JPanel(new GridLayout(1, 3));
		panel.add(Statistics.getChart(game, Statistics.FIRMSALES, game.getMarket().getYear()-1, game.getMarket()));
		panel.add(Statistics.getChart(game, Statistics.FIRMCOSTS, game.getMarket().getYear()-1, game.getMarket()));
		panel.add(Statistics.getChart(game, Statistics.FIRMPR, game.getMarket().getYear()-1, game.getMarket()));
		JOptionPane.showInternalMessageDialog(game.getContentPane(), panel, "Statistics", JOptionPane.INFORMATION_MESSAGE);
	}

	public static JButton getChart(final GameIO game, final int type, final double granularity, final Market market)
	{
		final int index;
		final History history;
		if(market != null)
		{
			index = curFirmIndex;
			history = market.getHistory();
		}
		else
		{
			index = -1;
			history = null;
		}
		
		JFreeChart chart = null;
		Function[] func = null;
		FunctionDataset dataset = null;
		String title = "", xAxisLabel = "", yAxisLabel = "";
		String[] seriesNames = null;
		int xSize = 0, ySize = 0;
		int seriesCount = 1;
		switch(type)
		{
			case SHORTRUN:
				title = "Firm short run";
				xAxisLabel = "Quantity";
				yAxisLabel = "Dollars";
				xSize = 300;
				ySize = 200;
				seriesCount = 5;
				func = new Function[seriesCount];
				func[0] = new Function() {		// Average total cost
					public double xEval(int arg) {
						return arg * Firm.MAXQUANT / granularity;
					}
					public double yEval(int arg) {
						double quant = xEval(arg);
						if(quant < 1)
							return Double.NaN;
						return Firm.getProdCost(quant) / quant;
					}
				};
				func[1] = new Function() {		// Demand (competitive)
					public double xEval(int arg) {
						return arg * Firm.MAXQUANT / granularity;
					}
					public double yEval(int arg) {
						return Market.EQUIPRICE;
					}
				};
				func[2] = new Function() {		// Demand (oligopolistic)
					public double xEval(int arg) {
						return arg * Firm.MAXQUANT / granularity;
					}
					public double yEval(int arg) {
						double result = Market.getPrice(xEval(arg) * Firm.FIRMNUM);
						return result < 0 ? Double.NaN : result;
					}
				};
				func[3] = new Function() {		// Marginal rev. (oligopolistic)
					public double xEval(int arg) {
						return arg * Firm.MAXQUANT / granularity;
					}
					public double yEval(int arg) {
						double quant = xEval(arg), quant1 = quant-1;
						double result = quant * Market.getPrice(quant * Firm.FIRMNUM) - quant1 * Market.getPrice(quant1 * Firm.FIRMNUM);
						return result < 0 ? Double.NaN : result;
					}
				};
				func[4] = new Function() {		// Marginal cost
					public double xEval(int arg) {
						return arg * Firm.MAXQUANT / granularity;
					}
					public double yEval(int arg) {
						double quant = xEval(arg), quant1 = quant-1;
						return Firm.getProdCost(quant) - Firm.getProdCost(quant1);
					}
				};
				String[] temp = {"Average total cost", "Competitive demand", "Oligopolistic demand", "Oligopolistic marginal revenue", "Marginal cost"};
				seriesNames = temp;	// Brace notation can only be used while declaring for some reason
				break;
			case SUPPLYDEMAND:
				title = "Competitive market supply and demand";
				xAxisLabel = "Quantity";
				yAxisLabel = "Dollars";
				xSize = 300;
				ySize = 200;
				seriesCount = 2;
				func = new Function[seriesCount];
				func[0] = new Function() {		// Market supply
					public double xEval(int arg) {
						return arg * Firm.MAXQUANT * Firm.FIRMNUM / granularity;
					}
					public double yEval(int arg) {
						double quant = xEval(arg) / Firm.FIRMNUM, quant1 = quant-1;
						double result = Firm.getProdCost(quant) - Firm.getProdCost(quant1);
						return result < 0 || result > Firm.MAXPRICE ? Double.NaN : result;
					}
				};
				func[1] = new Function() {		// Market demand
					public double xEval(int arg) {
						double result = Market.getDemand(yEval(arg));
						return result < 0 || result > Firm.MAXQUANT * Firm.FIRMNUM ? Double.NaN : result;
					}
					public double yEval(int arg) {
						return arg * Firm.MAXPRICE / granularity;
					}
				};
				String[] temp1 = {"Market supply", "Market demand"};
				seriesNames = temp1;
				break;
			case FIRMSALES:
				title = "Sales";
				xAxisLabel = "Year";
				yAxisLabel = "Quantity";
				xSize = 300;
				ySize = 300;
				seriesCount = 3;
				func = new Function[seriesCount];
				func[0] = new Function() {		// Firm sales
					public double xEval(int arg) { return arg; }	// just returns it back as a year
					public double yEval(int arg) {
						return history.turnList.get(arg).sales[index];
					}
				};
				func[1] = new Function() {		// Firm production
					public double xEval(int arg) { return arg; }
					public double yEval(int arg) {
						return history.turnList.get(arg).quant[index];
					}
				};
				func[2] = new Function() {		// Firm price
					public double xEval(int arg) { return arg; }
					public double yEval(int arg) {
						return history.turnList.get(arg).price[index];
					}
				};
				String[] temp2 = {"Sales", "Production", "Price"};
				seriesNames = temp2;
				break;
			case FIRMCOSTS:
				title = "Operations";
				xAxisLabel = "Year";
				yAxisLabel = "Dollars";
				xSize = 300;
				ySize = 300;
				seriesCount = 5;
				func = new Function[seriesCount];
				func[0] = new Function() {		// Funds
					public double xEval(int arg) { return arg; }
					public double yEval(int arg) {
						return history.turnList.get(arg).funds[index];
					}
				};
				func[1] = new Function() {		// Total cost
					public double xEval(int arg) { return arg; }
					public double yEval(int arg) {
						return history.turnList.get(arg).totalCost[index];
					}
				};
				func[2] = new Function() {		// Production cost
					public double xEval(int arg) { return arg; }
					public double yEval(int arg) {
						return history.turnList.get(arg).prodCost[index];
					}
				};
				func[3] = new Function() {		// Advertisement cost
					public double xEval(int arg) { return arg; }
					public double yEval(int arg) {
						return history.turnList.get(arg).adsCost[index];
					}
				};
				func[4] = new Function() {		// Interference cost
					public double xEval(int arg) { return arg; }
					public double yEval(int arg) {
						return history.turnList.get(arg).interCost[index];
					}
				};
				String[] temp3 = {"Funds", "Total cost", "Production cost", "Advertisement cost", "Interference cost"};
				seriesNames = temp3;
				break;
			case FIRMPR:
				title = "Public Relations";
				xAxisLabel = "Year";
				yAxisLabel = "Dollars";
				xSize = 300;
				ySize = 300;
				seriesCount = 4;
				func = new Function[seriesCount];
				for(int i = 0; i < seriesCount; i++)
				{
					if(i == index)
						func[index] = new Function() {
							public double xEval(int arg) { return arg; }
							public double yEval(int arg) {
								return history.turnList.get(arg).adsCost[index];
							}
						};
					else
					{
						final int tempIndex = i;
						func[tempIndex] = new Function() {
							public double xEval(int arg) { return arg; }
							public double yEval(int arg) {
								return history.turnList.get(arg).inter[index][tempIndex];
							}
						};
					}
				}
				seriesNames = new String[4];
				for(int i = 0; i < seriesNames.length; i++)
					seriesNames[i] = FileIO.firmNames[i] + " PR cost";
				break;
		}
		dataset = new FunctionDataset(seriesNames[0], func[0], granularity+1);
		chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, false, false);
		XYPlot plot = chart.getXYPlot();
		plot.setDataset(0, dataset);
		for(int i = 1; i < seriesCount; i++)
			plot.setDataset(i, new FunctionDataset(seriesNames[i], func[i], granularity+1));
		switch(type)	// extra setup if needed
		{
			case FIRMSALES:
				NumberAxis axis = (NumberAxis)plot.getDomainAxis();	// only want integer years
				TickUnitSource units = NumberAxis.createIntegerTickUnits();
				axis.setStandardTickUnits(units);
				
				plot.setRangeAxis(1, new NumberAxis("Dollars"));		// Adding second y-axis for price
				plot.mapDatasetToRangeAxis(2, 1);
				break;
			case FIRMCOSTS:
				NumberAxis axis1 = (NumberAxis)plot.getDomainAxis();
				TickUnitSource units1 = NumberAxis.createIntegerTickUnits();
				axis1.setStandardTickUnits(units1);
				break;
		}
		
		for(int i = 0; i < seriesCount; i++)
		{
			XYItemRenderer renderer =  new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
			renderer.setBasePaint(COLORS[i]);
			plot.setRenderer(i, renderer);
		}
		final ChartPanel chartPanel = new ChartPanel(chart);
		JButton button = new JButton(new ImageIcon(chart.createBufferedImage(xSize, ySize)));
		button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					JOptionPane.showMessageDialog(game.getContentPane(), chartPanel, "Chart display", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		return button;
	}
	
	
	private static interface Function
	{
		public double xEval(int arg);
		public double yEval(int arg);
	}
	
	private static class FunctionDataset extends AbstractXYDataset
	{
		Function func;
		String name;
		double domainSize;
		
		public FunctionDataset(String name, Function func, double domainSize)
		{
			this.func = func;
			this.name = name;
			this.domainSize = domainSize;
		}
		
		public DomainOrder getDomainOrder()	{ return DomainOrder.ASCENDING; }
		
		public int getItemCount(int series)
		{
			return (int)domainSize;
		}
		
		public int getSeriesCount() { return 1; }
		
		public Comparable getSeriesKey(int series)
		{
			return name;
		}
		
		public Number getX(int series, int item) { return new Double(getXValue(series, item)); }
		public Number getY(int series, int item) 
		{
			Double temp = new Double(getYValue(series, item));
			return temp == Double.NaN ? null : temp; 
		}
		
		public double getXValue(int series, int item)
		{
			return func.xEval(item);
		}
		
		public double getYValue(int series, int item)
		{
			return func.yEval(item);
		}
	}
}