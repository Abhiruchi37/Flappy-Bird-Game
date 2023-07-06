package flappyBird; 

import java.awt.Graphics;    

import javax.swing.JPanel;

public class Renderer extends JPanel //container class that provides space in which an application can attach any component;inherits JComponents class

{

	private static final long serialVersionUID = 1L; //used during deserialization and to avoid warning

	@Override
	protected void paintComponent(Graphics g) //to add graphic objects on something other than background colour; already present in JPanel class so use super to inherit

	{
		super.paintComponent(g); //to inherit features

		FlappyBird.flappyBird.repaint(g);// to perform erase and perform redraw of components after some time; to pass graphics into the flappybird
	}
	
}
