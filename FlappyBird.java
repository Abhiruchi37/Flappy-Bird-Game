package flappyBird; // user created package 

import java.awt.*; // It is imported because it is a set of API. GUI is used in the project.
import java.awt.event.*; //It helps in the change of state of the graphics, like clicking buttons etc
import java.util.ArrayList; //resizable array implementation of list interface.
import java.util.Random; //to generate pseudorandom numbers.

import javax.swing.*; //It contains classes like JLabel, JTextField, JButton etc.

public class FlappyBird implements ActionListener, MouseListener, KeyListener //Flappybird class(subclass) implements interfaces ActionListener, MouseListener, KeyListener
{

	public static FlappyBird flappyBird; // instance of FlappyBird

	public final int WIDTH = 800, HEIGHT = 600; //final unchangeable size of frame

	public Renderer renderer; //upon rendering, double buffering occurs to reduce       flickering issues

	public Rectangle bird; //initializing bird

	public ArrayList<Rectangle> columns; //array of rectangles ie the pipes

	public int ticks, yMotion, score; //clicks,motion of bird,score of game

	public boolean gameOver, started; //to tell if the game started or is over

	public Random rand; //initialize random number

	public FlappyBird() //constructor
	{
		JFrame jframe = new JFrame(); //to construct a new frame which is initially          invisible

		Timer timer = new Timer(4, this); //to repaint the program; the delay in millisec; 


		renderer = new Renderer(); //to make it not null and avoid the NullPointException

		rand = new Random(); //to create random numbers

		jframe.add(renderer); //adds component object-renderer in toplevel container object-jframe

		jframe.setTitle("Flappy Bird"); //title of frame

		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //to close gui;inside JFrame class

		jframe.setSize(WIDTH, HEIGHT); //(800,600)

		jframe.addMouseListener(this); //listens to mouse actions

		jframe.addKeyListener(this); //listens to keystrokes of keyboard

		jframe.setResizable(false); // so that the frame size cant be changed

		jframe.setVisible(true); //so that the frame is visible

		bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); //location ,size of bird

		columns = new ArrayList<Rectangle>(); //to create arrays of rectangles

		//add 2 columns initially before game starts;hidden 
		addColumn(true);  
		addColumn(true);
		
		timer.start(); //start the timer
	}

	public void addColumn(boolean start) //to add a column
	{
		int space = 300; //distance between two columns
		int width = 80; //width of each column
		int height = 50 + rand.nextInt(250); //min-50 and max-250

		if (start) //for start column
		{
			columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT  - height - 120, width, height)); //columnsize to move multiple columns at once;bottom

			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space)); //top
		}
		else //append till end column
		{
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height)); //bottom

			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space)); //top
		}
	}

	public void paintColumn(Graphics g, Rectangle column) //the columns
	{
		g.setColor(Color.green.darker()); //to darken the colour

		g.fillRect(column.x, column.y, column.width, column.height); //fill the rectangle; x,y coordinate,width ,height
	}

	public void jump() //for the bird to jump
	{
		if (gameOver) //conditions when game is over
		{
			bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);

			columns.clear(); //removes all the columns in the list

			yMotion = 0; //bird doesnt have any motion

			score = 0; //score set to zero after game is over

			addColumn(true);
			addColumn(true);
			

			gameOver = false;
		}

		if (!started)  //if not started then start game
		{
			started = true;
		}
		else if (!gameOver) //if game not over
		{
			if (yMotion > 0)
			{
				yMotion = 0;
			}

			yMotion -= 9; //speed at which bird moves
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) //actions being done in game
	{
		int speed = 5; //speed at which games moves forward

		ticks++; //increment

		if (started) //if game starts, allow columns to move out of frame
		{
			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				column.x -= speed; //columns moves out of frame
			}

			if (ticks % 2 == 0 && yMotion < 15) //number of clicks should be a multiple of 2 and ymotion<15 to avoid glitches with bird
			{
				yMotion += 2; 
			}

			for (int i = 0; i < columns.size(); i++) //add columns
			{
				Rectangle column = columns.get(i);

				if (column.x + column.width < 0) //if column out of frame, then remove the first occurrence of column
				{
					columns.remove(column);

					if (column.y == 0) //if the column height is 0 
					{
						addColumn(false); //no column added
					}
				}
			}

			bird.y += yMotion; //to add motion to bird

			for (Rectangle column : columns) //to check collisions
			{
				if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 5 && bird.x + bird.width / 2 < column.x + column.width / 2 + 5)  //if bird successfully passes through the columns without colliding
				{
					score++; //increment score
				}

				if (column.intersects(bird)) //collision with column
				{
					gameOver = true;

					if (bird.x <= column.x)
					{
						bird.x = column.x - bird.width; //if bird falls column moves bird out of frame

					}
					else
					{
						if (column.y != 0)
						{
							bird.y = column.y - bird.height; ///coordinate value of bird
						}
						else if (bird.y < column.height)
						{
							bird.y = column.height;
						}
					}
				}
			}

			if (bird.y > HEIGHT - 120 || bird.y < 0) //touches top column
			{
				gameOver = true;
			}

			if (bird.y + yMotion >= HEIGHT - 120) //touches bottom column
			{
				bird.y = HEIGHT - 120 - bird.height;
				gameOver = true;
			}
		}

		renderer.repaint();
	}

	public void repaint(Graphics g) 
	{
		g.setColor(Color.cyan); //background

		g.fillRect(0, 0, WIDTH, HEIGHT); //x,y coordinate, height,width

		g.setColor(Color.orange); //ground

		g.fillRect(0, HEIGHT - 120, WIDTH, 120);

		g.setColor(Color.green); //grass

		g.fillRect(0, HEIGHT - 120, WIDTH, 20);

		g.setColor(Color.red); //bird

		g.fillRect(bird.x, bird.y, bird.width, bird.height);

		for (Rectangle column : columns) //for each rectangle in(:) columns
		{
			paintColumn(g, column); //to paint rectangles
		}

		g.setColor(Color.white); 

		g.setFont(new Font("Arial", 1, 100));

		if (!started)
		{
			g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
		}

		if (gameOver)
		{
			g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
		}

		if (!gameOver && started)
		{
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100); //display score
		}
	}

	public static void main(String[] args)
	{
		flappyBird = new FlappyBird(); // calls constructor->jframe
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		jump(); //call jump method if mouse is clicked
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE) 
		{
			jump(); //go to jump method if "spacebar" is clicked 
		}
	}
	
	//default methods for MouseListener and KeyListener
	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{

	}

}

