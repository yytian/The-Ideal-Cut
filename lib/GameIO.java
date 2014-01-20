/**
 * Economics game input/output
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

public class GameIO
{
	private Dimension screenSize;	// Size of frame
	/* GUI elements */
	private JFrame frame = new JFrame("Placeholder");
	private Container contentPane = frame.getContentPane();
	/* Game panels */
	/* Game logic */
	private Firm[] firms;
	private Market market;
	private int gameLength = 10;
	
	/**
	 * Constructor for GameIO; initializes GUI and game logic elements
	 */
	public GameIO(boolean test)
	{		
		/* Settings */
		Toolkit toolkit = Toolkit.getDefaultToolkit();	// http://www.javacoffeebreak.com/faq/faq0015.html
		screenSize = toolkit.getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		frame.setSize(width, height);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);	// Marks that both dimensions are maximized
		frame.setFocusable(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// Ends program when window is closed
		/* Initialization of content pane */
		contentPane.setBackground(Color.white);
		if(test)
		{
			firms = new Firm[Firm.FIRMNUM];
			for(int i = 0; i < firms.length; i++)
				firms[i] = new Firm(i, "HAHA");
			market = new Market(firms);
			addComponent(new GameScreen(this));
		}
		else
			addComponent(MainScreen.get(this));
		/* Initiates GUI */
		frame.setVisible(true);
		/* Game-logic-related */
	}
	
	public void addComponent(JComponent comp)
	{
		contentPane.add(comp);
		contentPane.validate();
		contentPane.repaint();
	}
	
	public void removeComponent(JComponent comp)
	{
		contentPane.remove(comp);
		contentPane.validate();	// having validate in both seems a bit wasteful
		contentPane.repaint();
	}
	
	public Container getContentPane()
	{
		return contentPane;
	}
	
	public Dimension getDimensions()
	{
		return screenSize;
	}
	
	public Market getMarket()
	{
		return market;
	}
	
	public Firm[] getFirms()
	{
		return firms;
	}
	
	public void setFirms(Firm[] firms)
	{
		this.firms = firms;
		market = new Market(firms);
	}
	
	public int getGameLength()
	{
		return gameLength;
	}
	
	public void setGameLength(int gameLength)
	{
		this.gameLength = gameLength;
	}
	
	public static void main(String[] args)
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e)	{
			System.err.println(e.getCause());
		}
		catch (ClassNotFoundException e)	{
			System.err.println(e.getCause());
		}
		catch (InstantiationException e)	{
			System.err.println(e.getCause());
		}
		catch (IllegalAccessException e)	{
			System.err.println(e.getCause());
		}
		 	
		new GameIO(false);
	}
}

/*
Current:

Future:

Misc:
make everything final that can be final
*/