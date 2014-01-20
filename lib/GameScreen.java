import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class GameScreen extends JPanel
{
	public static final int LOGOWIDTH = 150, LOGOHEIGHT = 150;
	public static final double CHARTGRANULARITY = 1000.0;
	
	private final JLabel PLAYERNAME = new JLabel(), FIRMLOGO = new JLabel(), TURNNUM = new JLabel();
	private final JPanel SLIDERPANEL = new JPanel(new GridBagLayout());
	private final JTabbedPane BUTTONPANEL = new JTabbedPane();
	private final Box LASTTURNPANEL = Box.createVerticalBox(), SCORESPANEL = Box.createVerticalBox(), NEWSPANEL = Box.createVerticalBox(), COSTPANEL = Box.createVerticalBox();
	private final JLabel NEWS = new JLabel();
	private final JLabel PRODCOST = new JLabel("Production:"),
								ADSCOST = new JLabel("Advertisement:"),
								INTERCOST = new JLabel("Interference:"),
								TOTALCOST = new JLabel("Total:");
	private final JButton NEXTTURNBUTTON;
	private final ActionListener RESETSLIDERS;
	
	private int price, quant;
	
	private final Border BORDERBASE = BorderFactory.createLineBorder(Color.BLACK);
	
	private final GameIO GAME;
	private Firm firm;		// Current turn's firm
	private Firm[] firms;	// All firms
	private int curFirm = 0;	// Array index of current firm
	
	private static final double C1WEIGHT = 1.0, C2WEIGHT = 1.0, C3WEIGHT = 1.0;
	private static final double R1WEIGHT = 0.2, R2WEIGHT = 0.2, R3WEIGHT = 1.0, R4WEIGHT = 1.0;
	
	public GameScreen(GameIO game)
	{
		super(new GridBagLayout());
		this.GAME = game;
		
		add(PLAYERNAME, new GBC(0, 0).setWeight(C1WEIGHT, R1WEIGHT).setAnchor(GBC.CENTER));
		add(TURNNUM, new GBC(1, 0).setSpan(2, 1).setWeight(C2WEIGHT, R1WEIGHT).setAnchor(GBC.CENTER));
		add(FIRMLOGO, new GBC(0, 1).setWeight(C1WEIGHT, R2WEIGHT));
		
		/* Last turn panel */
		add(LASTTURNPANEL, new GBC(1, 1).setWeight(C2WEIGHT, R2WEIGHT).setFill(GBC.BOTH));
		/* End last turn panel */
		
		/* Scores panel */
		SCORESPANEL.setBorder(BorderFactory.createTitledBorder(BORDERBASE, "Funds"));
		add(SCORESPANEL, new GBC(2, 1).setWeight(C3WEIGHT, R2WEIGHT).setFill(GBC.BOTH));
		/* End scores panel */
		
		/* Slider panel */
		SLIDERPANEL.setBorder(BorderFactory.createTitledBorder(BORDERBASE, "Firm management"));
		
		JLabel curCost = new JLabel();
		SLIDERPANEL.add(curCost, new GBC(1, 4).setWeight(1, 1));
		
		SLIDERPANEL.add(new JLabel("Asking price"), new GBC(0, 0).setWeight(1, 1));
		final JSlider priceSlider = new JSlider(1, Firm.MAXPRICE);
		SLIDERPANEL.add(priceSlider, new GBC(1, 0).setWeight(1, 1));
		priceSlider.setValue(0);
		final JLabel priceLabel = new JLabel();
		SLIDERPANEL.add(priceLabel, new GBC(1, 1).setWeight(1, 1));
		priceSlider.addChangeListener(new SliderListener(priceLabel, curCost, 0));
		SLIDERPANEL.add(new JLabel("Production"), new GBC(0, 2));
		final JSlider quantSlider = new JSlider(0, Firm.MAXQUANT);
		quantSlider.setValue(0);
		SLIDERPANEL.add(quantSlider, new GBC(1, 2).setWeight(1, 1));
		final JLabel quantLabel = new JLabel();
		SLIDERPANEL.add(quantLabel, new GBC(1, 3).setWeight(1, 1));
		quantSlider.addChangeListener(new SliderListener(quantLabel, curCost, 1));
		
		JButton equi = new JButton("Equilibrium");
		equi.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					priceSlider.setValue(GAME.getMarket().equiPrice());
					quantSlider.setValue(GAME.getMarket().equiQuant());
				}
			});
		SLIDERPANEL.add(equi, new GBC(0, 4).setWeight(0.5, 0.5).setFill(GBC.BOTH));
		RESETSLIDERS = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					priceSlider.setValue(1);
					quantSlider.setValue(0);
				}
			};
		add(SLIDERPANEL, new GBC(0, 2).setWeight(C1WEIGHT, R3WEIGHT).setFill(GBC.BOTH));
		/* End slider panel */
		
		/* News panel */
		NEWSPANEL.setBorder(BorderFactory.createTitledBorder(BORDERBASE, "News"));
		NEWSPANEL.add(NEWS);
		add(NEWSPANEL, new GBC(0, 3).setWeight(C1WEIGHT, R4WEIGHT).setFill(GBC.BOTH));
		/* End news panel*/
		
		/* Button panel */
		BUTTONPANEL.setBorder(BorderFactory.createTitledBorder(BORDERBASE, "Market management"));
		final int BUTTONGAP = 20;
		
		GridLayout l = new GridLayout(2, 2);
		l.setHgap(BUTTONGAP);
		l.setVgap(BUTTONGAP);
		JPanel statPanel = new JPanel(l);
		
		firms = GAME.getFirms();
		for(int i = 0; i < firms.length; i++)
		{
			final Firm STATFIRM = firms[i];
			JButton statButton = new JButton(STATFIRM.ICON);
			final int temp = i;	// oh come on Java
			statButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						Statistics.getFirmStats(GAME, temp);
					}
				});
			statPanel.add(statButton);
		}
		
		BUTTONPANEL.addTab("Statistics", statPanel);
		BUTTONPANEL.addTab("Public relations", new JPanel(new GridBagLayout()));
		add(BUTTONPANEL, new GBC(1, 2).setSpan(1, 2).setWeight(C2WEIGHT, R3WEIGHT + R4WEIGHT).setFill(GBC.BOTH));
		/* End button panel */
		
		/* Cost panel */
		COSTPANEL.setBorder(BorderFactory.createTitledBorder(BORDERBASE, "Costs"));
		COSTPANEL.add(PRODCOST);
		COSTPANEL.add(ADSCOST);
		COSTPANEL.add(INTERCOST);
		COSTPANEL.add(TOTALCOST);
		COSTPANEL.add(Statistics.getChart(GAME, Statistics.SHORTRUN, CHARTGRANULARITY, null));
		COSTPANEL.add(Statistics.getChart(GAME, Statistics.SUPPLYDEMAND, CHARTGRANULARITY, null));
		add(COSTPANEL, new GBC(2, 2).setSpan(1, 2).setWeight(C3WEIGHT, R3WEIGHT).setFill(GBC.BOTH));
		/* End cost panel */
		
		NEXTTURNBUTTON = new JButton("Next Turn");
		NEXTTURNBUTTON.addActionListener(new NextTurnListener(this));
		add(NEXTTURNBUTTON, new GBC(2, 4).setAnchor(GBC.PAGE_END));
		
		setFirm(firms[curFirm]);
		update();
	}
	
	public int curFirm()
	{
		return curFirm;
	}
	
	public void setFirm(Firm firm) // replaces firm-specific data; implies interface turn change
	{
		this.firm = firm;
		PLAYERNAME.setText(firm.PLAYER);
		
		FIRMLOGO.setIcon(firm.ICON);
		
		LASTTURNPANEL.removeAll();
		History.Turn turn = GAME.getMarket().getHistory().getLast();
		int revenue = turn.quant[curFirm] * turn.price[curFirm];
		LASTTURNPANEL.add(new JLabel("Revenue Earned: $" + revenue));
		LASTTURNPANEL.add(new JLabel("Unsold product: $" + firm.getUnsold()));
		
		RESETSLIDERS.actionPerformed(new ActionEvent(this, 0, "Reset sliders"));
		
		//if(GAME.getMarket().getYear() > 1)
		//	NEWS.setText(turn.verdict[curFirm].message() + "\n For a variety of reasons, this advice is not always reliable, Take with a grain of salt.");
		
		JPanel prPanel = (JPanel)BUTTONPANEL.getComponentAt(1);
		prPanel.removeAll();
		final JLabel curCost = new JLabel();
		prPanel.add(curCost, new GBC(0, 4).setAnchor(GBC.CENTER));
		for(int i = 0; i < Firm.FIRMNUM; i++)
		{
			final JLabel prLabel = new JLabel(firms[i].ICON);
			prLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			prLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
			prPanel.add(prLabel, new GBC(i%2, i/2*2));
			final JSlider prSlider = new JSlider(0, Firm.MAXPR);
			prSlider.setValue(0);
			prPanel.add(prSlider, new GBC(i%2, i/2*2+1).setWeight(1, 1));
			if(firms[i].NAME.compareTo(firm.NAME) == 0)
				prSlider.addChangeListener(new SliderListener(prLabel, curCost, 3));
			else
				prSlider.addChangeListener(new SliderListener(prLabel, curCost, 2, firm, i));
		}
		BUTTONPANEL.setComponentAt(1, prPanel);
		
		PRODCOST.setText("Production: $" + Firm.getProdCost(quant));
		ADSCOST.setText("Advertising: $" + firm.getAds());
		INTERCOST.setText("Interference: $" + firm.getInterCost());
		TOTALCOST.setText("Total: $" + firm.getTotalCost());
		
		validate();
		repaint();
	}
	
	public void update()	// updates market data, implies logic turn change
	{
		int curYear = GAME.getMarket().getYear();
		TURNNUM.setText("Year " + curYear);
		LASTTURNPANEL.setBorder(BorderFactory.createTitledBorder(BORDERBASE, "Year " + (curYear-1) + " results")); 

		SCORESPANEL.removeAll();
		for(Firm f : firms)
			SCORESPANEL.add(new JLabel(f.NAME + ":     $" + f.getFunds()));
		
		validate();
		repaint();
	}
	
	private class SliderListener implements ChangeListener
	{
		JLabel label, cur;
		int iden; // 0 is price, 1 is quant
		Firm spender;
		int target; // Index of target
		
		private SliderListener(JLabel label, JLabel cur, int iden)
		{
			this.label = label;
			this.cur = cur;
			this.iden = iden;
		}
		
		private SliderListener(JLabel label, JLabel cur, int iden, Firm spender, int target)
		{
			this.label = label;
			this.cur = cur;
			this.iden = iden;
			this.spender = spender;
			this.target = target;
		}
	
		public void stateChanged(ChangeEvent e)
		{
			JSlider source = (JSlider)e.getSource();
			int value = source.getValue();
			int demand = firm.getDemand() / Firm.FIRMNUM;
			switch(iden)
			{
				case 0:	// Setting price
					price = value;
					firm.setProduction(price, quant);
					label.setText(Integer.toString(value));
					cur.setText("<html>Projected demand: " + demand + "<br>Projected revenue: $" + (price * Math.min(demand, quant)) + "<br>Costs: $" + firm.getProdCost(quant) + "</html>");
					break;
				case 1:
					quant = value;	// Setting quantity
					firm.setProduction(price, quant);
					label.setText(Integer.toString(value));
					cur.setText("<html>Projected demand: " + demand + "<br>Projected revenue: $" + (price * Math.min(demand, quant)) + "<br>Costs: $" + firm.getProdCost(quant) + "</html>");
					PRODCOST.setText("Production: " + firm.getProdCost(quant));
					TOTALCOST.setText("Total: $" + firm.getTotalCost());
					break;
				case 2:	// Setting interference
					label.setText("Interference cost: " + value);
					spender.addInterFrom(target, value);
					cur.setText("Total cost: $" + ( firm.getInterCost() + firm.getAds() ));
					INTERCOST.setText("Interference: $" + firm.getInterCost());
					TOTALCOST.setText("Total: $" + firm.getTotalCost());
					break;
				case 3:	// Setting advertisement
					label.setText("Advertisement cost: " + value);
					firm.addAds(value);
					cur.setText("Total cost: $" + ( firm.getInterCost() + firm.getAds() ));
					ADSCOST.setText("Advertising: $" + firm.getAds());
					TOTALCOST.setText("Total: $" + firm.getTotalCost());
					break;
			}
		}
	}
	
	private class NextTurnListener implements ActionListener
	{
		private final GameScreen GS;
	
		public NextTurnListener(GameScreen gs)
		{
			GS = gs;
		}
	
		public void actionPerformed(ActionEvent e)	// Interface turn, after each firm finishes its selections
		{
			if( JOptionPane.showConfirmDialog(GAME.getContentPane(), "Do you really want to end your turn?", "Next turn", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				if(firm.getFunds() < firm.getTotalCost())
					JOptionPane.showInternalMessageDialog(GAME.getContentPane(),
						"You are spending beyond your means. There is no credit in this game!",
						"Not enough funds",
						JOptionPane.ERROR_MESSAGE);
				else
				{
					int[] interFrom = firm.getInterFrom();
					for(int i = 0; i < interFrom.length; i++)
						firms[i].addInterTo(interFrom[i]);
				
					if(curFirm == Firm.FIRMNUM - 1)
					{
						GAME.getMarket().turn();
						curFirm = 0;
						setFirm(firms[curFirm]);
						update();
						JOptionPane.showInternalMessageDialog(GAME.getContentPane(), GAME.getMarket().getResults(), "End-of-year results", JOptionPane.INFORMATION_MESSAGE, null);
					}
					else
						setFirm(firms[++curFirm]);
				}
				
				if(GAME.getMarket().getYear() > GAME.getGameLength())
				{
					Firm winner = firms[0];
					for(Firm f : firms)
						if(f.getFunds() > winner.getFunds())
							winner = f;
					JOptionPane.showInternalMessageDialog(GAME.getContentPane(), winner.PLAYER + " is the winner with " + winner.getFunds() + "!",
						"End of the game", JOptionPane.INFORMATION_MESSAGE);
					FileIO.writeHighScores(winner);
					GAME.removeComponent(GS);
					GAME.addComponent(MainScreen.get(GAME));
					// make sure there aren' memory leaks
				}
			}
		}
	}
}

// Fonts
// Add icons everywhere?
// When costs > funds print costs in red