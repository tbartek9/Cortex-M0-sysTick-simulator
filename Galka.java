package appGui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.*;
import java.awt.*;
import java.lang.Math;
public class Galka extends JComponent implements MouseListener, MouseMotionListener, ActionListener {
	
	///private static final int PI = 0;
	int x=50;
	int y=50;
	int x_mouse;
	int y_mouse;
	double tga;
	double angle;
	int radius;
	double percent;
	double angle2;
	double real_angle;
	public Galka(int w, int z) {
		
		setSize(new Dimension(w,z));
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		Dimension size=getSize();
		radius=(Math.min(size.width,size.height))/2;
		int width=size.width;
		int height=size.height;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Dimension size=getSize();
		int width=size.width;
		int height=size.height;
		g.setColor(Color.gray);
		g.fillOval((width/2 )-radius,(height/2)-radius,radius*2 ,radius*2);
		
	
		
		int radius2=radius/5;
		int diffrence=radius-radius2;
		int in_x=(int)(diffrence*Math.sin(angle))+(width/2);  ///tutaj ustalenie srodka malej galki!!
		int in_y=-(int)(diffrence*Math.cos(angle))+(height/2);
		g.setColor(Color.black);
		g.fillOval(in_x-radius2,in_y-radius2,2*radius2,2*radius2);
		if(angle>=0) {
			real_angle=angle;
		}
		else {
			real_angle=(Math.PI)*2 +angle;
		}
		percent=real_angle/(2*Math.PI);
		
		
	}
	
	public static void main(String[] args)
	{
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		x_mouse=e.getX();
		y_mouse=e.getY();
		angle=Math.atan2(x_mouse-radius,radius-y_mouse);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public static void main() {
	 
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
