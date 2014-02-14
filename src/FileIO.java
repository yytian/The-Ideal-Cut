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
	}	
}