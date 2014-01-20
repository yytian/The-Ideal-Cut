import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainScreen extends JPanel
								implements ActionListener
{
	private final JLabel LOGO = new JLabel(new ImageIcon(FileIO.mainLogo));
	private final JButton NEWGAME = new JButton("New Game");
	private final JButton INSTRUCTIONS = new JButton("Instructions");
	private final JButton HIGHSCORES = new JButton("High scores");
	private final JButton CREDITS = new JButton("Credits");
	private final GameIO GAME;
	private static JPanel storage;

	public MainScreen(GameIO game)
	{	
		super(new GridLayout(2, 2));
		this.GAME = game;
		
		NEWGAME.addActionListener(this);
		add(NEWGAME);
		
		INSTRUCTIONS.addActionListener(this);
		add(INSTRUCTIONS);
		
		HIGHSCORES.addActionListener(this);
		add(HIGHSCORES);
		
		CREDITS.addActionListener(this);
		add(CREDITS);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == NEWGAME)
		{
			try
			{
				Firm[] firms = new Firm[Firm.FIRMNUM];
				for(int i = 0; i < firms.length; i++)
					firms[i] = new Firm(i, (String)
						JOptionPane.showInputDialog(this, "Player " + (i+1) + ", choose your name", "Options", JOptionPane.QUESTION_MESSAGE, null, null, ""));
				GAME.setFirms(firms);
				GAME.setGameLength( Integer.parseInt((String)
					JOptionPane.showInputDialog(this, "How many years should the game last?", "Options", JOptionPane.QUESTION_MESSAGE, null, null, 10)));
				GAME.removeComponent(storage);
				GAME.addComponent(new GameScreen(GAME));
			} catch(Exception ex) { return; }
		}
		else if(e.getSource() == INSTRUCTIONS)
		{
			JOptionPane.showMessageDialog(GAME.getContentPane(), FileIO.instructions, "Instructions", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(e.getSource() == HIGHSCORES)
		{
			FileIO.readHighScores();
			String highScores = "";
			for(int i = 0; i < FileIO.HIGHSCORESNUM; i++)
				highScores += FileIO.highScoresPlayers[i] + " " + FileIO.highScores[i] + '\n';
			JOptionPane.showMessageDialog(GAME.getContentPane(), highScores, "Instructions", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(e.getSource() == CREDITS)
		{
			JOptionPane.showMessageDialog(GAME.getContentPane(),
				"This game was created for CIA 4UE (if that is the correct code) by Jim Tian, Neville Chan, Nida Noorani, Alice Wang and Min Jo.\n" +
				"Graphs were generated through the JFreeChart library released under the GNU Lesser General Public License (it's open source).\n" +
				"Any other problems you have with the game? Please tell us! Have a nice day.",
				"Instructions", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public static JPanel get(GameIO game)
	{
		if(storage == null)
		{
			storage = new JPanel(new GridBagLayout());
			MainScreen screen = new MainScreen(game);
			screen.setOpaque(false);
			storage.add(screen, new GBC(1, 1).setWeight(1, 1).setAnchor(GBC.CENTER));
			Dimension d = game.getDimensions();
			storage.add(new JLabel(new ImageIcon(FileIO.background.getScaledInstance((int)d.getWidth(), (int)d.getHeight(), Image.SCALE_SMOOTH))),
				new GBC(0, 0).setSpan(3, 3).setWeight(2, 2));
		}
		return storage;
	}
}