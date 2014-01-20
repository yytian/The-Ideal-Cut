import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;

public class FileIO
{
	public static Image mainLogo, background;
	public static final Image[] firmLogos = new BufferedImage[Firm.FIRMNUM];
	public static final String[] firmNames = new String[Firm.FIRMNUM];
	public static String instructions = "";
	
	public static final HashMap<String, Float> CONSTANTS = new HashMap<String, Float>();
	
	public static final int HIGHSCORESNUM = 10;
	public static int[] highScores = new int[HIGHSCORESNUM]; 
	public static String[] highScoresPlayers = new String[HIGHSCORESNUM];
	
	static
	{

		/* Image reading */
		try
		{
			mainLogo = ImageIO.read(FileIO.class.getResourceAsStream("mainlogo.png"));
			background = ImageIO.read(FileIO.class.getResourceAsStream("background.png"));
			for(int i = 0; i < firmLogos.length; i++)
				firmLogos[i] = ImageIO.read(FileIO.class.getResourceAsStream("logo" + Integer.toString(i+1) + ".png"));
		} catch(IOException e) { System.err.println("An image could not be loaded."); }
		
		/* Text reading */
		Scanner in = new Scanner(FileIO.class.getResourceAsStream("instructions.txt"));
		while(in.hasNextLine())
			instructions += in.nextLine() + '\n';
		in = new Scanner(FileIO.class.getResourceAsStream("firmnames.txt"));
		for(int i = 0; i < firmNames.length; i++)
			firmNames[i] = in.nextLine();
		in = new Scanner(FileIO.class.getResourceAsStream("constants.txt"));
		while(in.hasNext())
		{
			String token = in.next();
			if(token.compareTo("/*") == 0)
			{
				String temp;
				do
				{
					temp = in.next();
				}
				while(temp.compareTo("*/") != 0);
			}
			else
			{
				while(!in.hasNextFloat())
					token += in.next();
				CONSTANTS.put(token, in.nextFloat());
			}
		}
		in.close();
		
		for(int i = 0; i < highScoresPlayers.length; i++)
			highScoresPlayers[i] = "";
		readHighScores();
	}
	
	public static void readHighScores()
	{
		try
		{
			Scanner in = new Scanner(new File("highscores.txt"));
			for(int i = 0; in.hasNext(); i++)
			{
				while(!in.hasNextInt())
					highScoresPlayers[i] += in.next() + ' ';
				highScores[i] = in.nextInt();
			}
			in.close();
		} catch(FileNotFoundException e) { System.err.println("The high scores could not be written to."); }
		catch(ArrayIndexOutOfBoundsException e) { System.err.println("There were too many high scores."); }
		catch(NoSuchElementException e) { System.err.println("There were too few high scores."); }
	}
	
	public static void writeHighScores(Firm firm)
	{
		int score = firm.getFunds();
		int place;
		for(place = 0; highScores[place] > score && place < highScores.length; place++)
			;
		System.out.println(place);
		if(place < highScores.length)
		{
			try
			{
				PrintWriter pw = new PrintWriter(new File("highscores.txt"));
				for(int i = 0; i < place; i++)
				{
					pw.print(highScoresPlayers[i]);
					pw.print(new char[30 - highScoresPlayers[i].length()]);
					pw.println(highScores[i]);
				}
				pw.print(firm.PLAYER);
				pw.print(new char[30 - firm.PLAYER.length()]);
				pw.println(score);
				for(int i = place+1; i < HIGHSCORESNUM; i++)
				{
					pw.print(highScoresPlayers[i]);
					pw.print(new char[30 - highScoresPlayers[i].length()]);
					pw.println(highScores[i]);
				}
				pw.flush();
				pw.close();
			} catch(FileNotFoundException e) { System.err.println("The high scores could not be written to."); }
		}
	}
}

// explain that ties in high scores are broken by newest on top