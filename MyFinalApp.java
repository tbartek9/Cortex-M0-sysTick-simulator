package appGui;
import java.util.concurrent.TimeUnit;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.EventQueue;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.*;
public class MyFinalApp extends JFrame implements ActionListener{
	
	
	private JPanel contentPane;
	private Galka galka=new Galka(100,100);
	private Cortex_M0_SysTick cortex=new Cortex_M0_SysTick(); ///tu zmienilem na private
	public Generator generator=new Generator();
	public boolean burst_mode;
	public int time;
	public int burst_count;
	public boolean is_on;
	public int time_min;
	public int time_max;
	public boolean thread_started;
	JTextField show_freq=new JTextField("Tick time:"+time);
	JLabel showCVR=new JLabel("CVR:"+cortex.CVR,JLabel.CENTER);	///potem w obsludze zdarzen showCVR.setText("CVR"+cortex.CVR)
	JLabel showRVR=new JLabel("RVR:"+cortex.RVR,JLabel.CENTER);
	JLabel interrupt=new JLabel("INTERRUPT",JLabel.CENTER);
	
	
	///start class
		public class Generator extends Thread{
			ActionListener listener;
			int time_gen;
			volatile boolean im_alive;
			public void run() {
				
				while(true) {
				int n=0;
				while(im_alive&(burst_mode==true || n<burst_count)&cortex.clksrc==true) {
					cortex.tickInternal();
					showCVR.setText("CVR:"+cortex.CVR);
		            showRVR.setText("RVR:"+cortex.RVR);
		            if(cortex.interrupt) {
		            	interrupt.setForeground(Color.red);
		            }
		            else {
		            	interrupt.setForeground(Color.gray);
		            }
					n++;
					try {
						Thread.sleep(time_gen);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				im_alive=false;
				}
			}
			public Generator() {
				im_alive=false;
				time_gen=100;
			}
			public void kill() {
				im_alive=false;
			}
			public void activate_again() {
				im_alive=true;
			}
			
		}
		///end class
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFinalApp frame = new MyFinalApp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyFinalApp() {
		super("Cortex M0 Systick"); ///konstruktor po klasie dziedziczonej
		burst_mode=false;
		time=100;
		time_min=0;
		time_max=100;
		burst_count=1;
		is_on=false;
		thread_started=false;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(700,600));
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		Container c1=new Container(); //generator galka, start stop, burst,continous
		Container c2=new Container(); //wypis danych cvr rvr przerwanie enable itd SAMO INFO (isSometinh)
		Container c3=new Container(); //ustawianie danych rvr cvr enable internal
		Container c4=new Container(); //obsluga externala
		contentPane.add(c1);
		contentPane.add(c2);
		contentPane.add(c3);
		contentPane.add(c4);
		contentPane.setLayout(new GridLayout(2,2,1,1));
		JMenuBar menu=new JMenuBar();
		JMenu colorMenu=new JMenu("Color");
		JMenuItem color1=new JMenuItem("orange");
		JMenuItem color2=new JMenuItem("pink");
		JMenuItem color3=new JMenuItem("default");
		colorMenu.add(color1);
		colorMenu.add(color2);
		colorMenu.add(color3);
		menu.add(colorMenu);
		JMenu sizeMenu=new JMenu("Size");
		JMenuItem size1=new JMenuItem("700x600");
		JMenuItem size2=new JMenuItem("800x700");
		JMenuItem size3=new JMenuItem("900x800");
		sizeMenu.add(size1);
		sizeMenu.add(size2);
		sizeMenu.add(size3);
		menu.add(sizeMenu);
		setJMenuBar(menu);
		repaint();
		size1.addActionListener(new ActionListener() { 
	         public void actionPerformed(ActionEvent e) {
	        	 setSize(700,600);
	        		}
	      });
		size2.addActionListener(new ActionListener() { 
	         public void actionPerformed(ActionEvent e) {
	        	 setSize(800,700);
	        		}
	      });
		size3.addActionListener(new ActionListener() { 
	         public void actionPerformed(ActionEvent e) {
	        	 setSize(900,800);
	        		}
	      });
		
		
		///organizacja 1 panelu
		c1.setLayout(new GridLayout(2,2,1,1));///2x2, 1 start/stop, drugi ga³ka, 3 wybor burst continous, 4 wyswietlanie czestotliwosci
		JButton button_start=new JButton("START");
		JButton button_stop=new JButton("STOP");
		button_stop.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        	 is_on=false;
	        	}
	      });
		Container toFreq=new Container();
		toFreq.setLayout(new GridLayout(3,1,1,1));
		///zastapic kontenerem, dodac jtext fieldy, ustawiona, min i max (3x3) label +text field do kazdego
		JTextField set_min_freq=new JTextField();
		JTextField set_max_freq=new JTextField();
		JButton set_freq=new JButton("set");
		show_freq.setEditable(false); //to wypierdolic bo patrz wyzej
		toFreq.add(set_freq);
		set_freq.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        	 time=(int)((galka.percent)*(time_max-time_min)) +time_min;
	        	 show_freq.setText("Tick time:"+time);
	        	 generator.time_gen=time;
	       }
	      });
		toFreq.add(show_freq);
		toFreq.add(set_min_freq);
		set_min_freq.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        		
	        	try{
	        		time_min=Integer.parseInt(set_min_freq.getText()); 
	        		if(time_min<0) {
	        			time_min=0;
	        			set_min_freq.setText("0");
	        			
	        		}
	        		
	        	
	        	}
			    catch (NumberFormatException event) {
			         set_min_freq.setText("");
	         }}
	      });
		toFreq.add(set_max_freq);
		set_max_freq.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        		
	        	try{
	        		time_max=Integer.parseInt(set_max_freq.getText()); 
	        		if(time_max<=time_min) {
	        			time_max=time_min+1;
	        			set_max_freq.setText(""+time_max);
	        			
	        		}
	        		
	        	
	        	}
			    catch (NumberFormatException event) {
			         set_max_freq.setText("");
	         }}
	      });
		
		c1.add(button_start);
		c1.add(button_stop);
		c1.add(galka);
		c1.add(toFreq);
		
 		///organizacja 2 panelu
		c2.setLayout(new GridLayout(3,2,1,1));
		
		JLabel showSource=new JLabel("Source:"+cortex.clksrcSTR,JLabel.CENTER);
		JLabel showEnable=new JLabel("Enable:"+cortex.isEnableFlag(),JLabel.CENTER);
		JLabel showInterruptStatus=new JLabel("Interrupts status:"+cortex.interruptStatus,JLabel.CENTER);
		
		interrupt.setForeground(Color.gray); ///jesli przerwanie nastepuje, wtedy red
		c2.add(showCVR);
		c2.add(showRVR);
		c2.add(showSource);
		c2.add(showEnable);
		c2.add(showInterruptStatus);
		c2.add(interrupt);
		
		///organizacja 3 panelu
		c3.setLayout(new GridLayout(3,3,1,1));
		//RVR
		Container conRVR=new Container();
		conRVR.setLayout(new GridLayout(2,1,1,1));
		JLabel setRVRlabel=new JLabel("Set RVR:",JLabel.CENTER);
		JTextField writeRVR=new JTextField();
		
		writeRVR.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        		
	        	try{
	        		cortex.RVR=Integer.parseInt(writeRVR.getText()); 
	        		showRVR.setText("RVR:"+cortex.RVR);
	        	
	        	}
			    catch (NumberFormatException event) {
			         writeRVR.setText("");
	         }}
	      });
		
		conRVR.add(setRVRlabel);
		conRVR.add(writeRVR);
		///CVR
		Container conCVR=new Container();
		conCVR.setLayout(new GridLayout(2,1,1,1));
		JLabel setCVRlabel=new JLabel("Set CVR:",JLabel.CENTER);
		JTextField writeCVR=new JTextField();
		
		writeCVR.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        		
	        	try{
	        		cortex.RVR=Integer.parseInt(writeCVR.getText());
	        		cortex.CVR=0;
	        		showRVR.setText("RVR:"+cortex.CVR);
	        		writeCVR.setText("0");
	        	
	        	}
			    catch (NumberFormatException event) {
			         writeCVR.setText("");
	         }}
	      });
		
		conCVR.add(setCVRlabel);
		conCVR.add(writeCVR);
		///CSR
		Container conCSR=new Container();
		conCSR.setLayout(new GridLayout(2,1,1,1));
		JLabel setCSRlabel=new JLabel("Set CSR:",JLabel.CENTER);
		JTextField writeCSR=new JTextField();
		
		writeCSR.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        		
	        	try{
	        		cortex.CSR=Integer.parseInt(writeCSR.getText()); 
	        	
	        	}
			    catch (NumberFormatException event) {
			         writeCSR.setText("");
	         }}
	      });
		
		conCSR.add(setCSRlabel);
		conCSR.add(writeCSR);
		
		///ustawienie przerwan
		Container interruptSettings=new Container();
		interruptSettings.setLayout(new GridLayout(2,1,1,1));
		JLabel setInterruptLabel=new JLabel("Set interrupts:",JLabel.CENTER);
		String interruptStatuses[]= {"ENABLE","DISABLE"};
		JComboBox chooseInterruptStatus=new JComboBox(interruptStatuses);
		chooseInterruptStatus.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        	 if(chooseInterruptStatus.getSelectedItem()=="ENABLE") {showInterruptStatus.setText("Interrupts status:ON ");cortex.setInterruptEnable();}
	        	 else{showInterruptStatus.setText("Interrupts status:OFF");cortex.setInterruptDisable();}
	        	}
	      });
		interruptSettings.add(setInterruptLabel);
		interruptSettings.add(chooseInterruptStatus);
		///ustawienie enable
		Container enableSettings=new Container();
		enableSettings.setLayout(new GridLayout(2,1,1,1));
		JLabel setEnableLabel=new JLabel("Set enabling:",JLabel.CENTER);
		String enableStatuses[]= {"DISABLE","ENABLE"};
		JComboBox chooseEnableStatus=new JComboBox(enableStatuses);
		chooseEnableStatus.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        	 if(chooseEnableStatus.getSelectedItem()=="ENABLE") {cortex.setEnable();showEnable.setText("Enable:"+cortex.isEnableFlag());}
	        	 else{cortex.setDisable();showEnable.setText("Enable:"+cortex.isEnableFlag());showCVR.setText("CVR:"+cortex.CVR);}
	        	}
	      });
		enableSettings.add(setEnableLabel);
		enableSettings.add(chooseEnableStatus);
		///ustawienie burstmode
		Container modeSettings=new Container();
		modeSettings.setLayout(new GridLayout(2,1,1,1));
		JLabel setModeLabel=new JLabel("Set Mode:",JLabel.CENTER);
		String modeStatuses[]= {"BURST","CONTINOUS"}; ///BURST 0 continuous 1
		JComboBox chooseModeStatus=new JComboBox(modeStatuses);
		chooseModeStatus.addActionListener(new ActionListener() { ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	         public void actionPerformed(ActionEvent e) {
	        	 if(chooseModeStatus.getSelectedItem()=="BURST") {burst_mode=false;}
	        	 else{burst_mode=true;}
	        	}
	      });
		modeSettings.add(setModeLabel);
		modeSettings.add(chooseModeStatus);
		c3.add(conRVR);
		c3.add(interruptSettings);
		c3.add(conCVR);
		c3.add(enableSettings);
		c3.add(conCSR);
		c3.add(modeSettings);
		
		c4.setLayout(new GridLayout(3,1));
		JLabel externalInfo=new JLabel("Press to count with external clock(if avaliable):",JLabel.CENTER);
		JButton externalButton=new JButton("TICK EXTERNAL");
		externalButton.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {///!!!!!!!!!!! TICK TICK TICK TICK  TICK TICK TICK TICK  TICK TICK TICK TICK  TICK TICK TICK TICK  TICK TICK TICK TICK  TICK TICK TICK TICK 
	            cortex.tickExternal();
	            showCVR.setText("CVR:"+cortex.CVR);
	            showRVR.setText("RVR:"+cortex.RVR);
	            if(cortex.interrupt) {
	            	interrupt.setForeground(Color.red);
	            }
	            else {
	            	interrupt.setForeground(Color.gray);
	            }
	            
	         }
	      });
		c4.add(externalInfo);
		c4.add(externalButton);
		Container last=new Container();
		last.setLayout(new GridLayout(1,2,1,1));
		Container sourceSettings=new Container();
		sourceSettings.setLayout(new GridLayout(2,1,1,1));
		JLabel setSourceLabel=new JLabel("Set source:",JLabel.CENTER);
		String sourceStatuses[]= {"INTERNAL","EXTERNAL"};
		JComboBox chooseSourceStatus=new JComboBox(sourceStatuses);
		chooseSourceStatus.addActionListener(new ActionListener() { ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	         public void actionPerformed(ActionEvent e) {
	        	 if(chooseSourceStatus.getSelectedItem()=="INTERNAL") {cortex.setSource(true);showSource.setText("Source:"+cortex.clksrcSTR);}
	        	 else{cortex.setSource(false);showSource.setText("Source:"+cortex.clksrcSTR);}
	        	}
	      });
		sourceSettings.add(setSourceLabel);
		sourceSettings.add(chooseSourceStatus);
		Container conBURST=new Container();
		conBURST.setLayout(new GridLayout(2,1,1,1));
		JLabel setBURSTlabel=new JLabel("Set burst count:",JLabel.CENTER);
		JTextField writeBURST=new JTextField("1");
		
		writeBURST.addActionListener(new ActionListener() { /// RVR ACTION DODAC TEZ DO CSR I CVR
	         public void actionPerformed(ActionEvent e) {
	        		
	        	try{
	        		burst_count=Integer.parseInt(writeBURST.getText()); 
	        		
	        		writeBURST.setText(""+burst_count);
	        		
	        		if(burst_count<1) {
	        			writeBURST.setText("1");
				         burst_count=1;
	        		}
	        	}
			    catch (NumberFormatException event) {
			         writeBURST.setText("1");
			         burst_count=1;
	         }}
	      });
		conBURST.add(setBURSTlabel);
		conBURST.add(writeBURST);
		last.add(conBURST);
		last.add(sourceSettings);
		
		c4.add(last);
		
		color1.addActionListener(new ActionListener() { 
	         public void actionPerformed(ActionEvent e) {
	        	 button_start.setBackground(Color.ORANGE);
	        	 button_stop.setBackground(Color.ORANGE);
	        	 externalButton.setBackground(Color.ORANGE);
	        		}
	      });
		color2.addActionListener(new ActionListener() { 
	         public void actionPerformed(ActionEvent e) {
	        	 button_start.setBackground(Color.PINK);
	        	 button_stop.setBackground(Color.PINK);
	        	 externalButton.setBackground(Color.PINK);
	        		}
	      });
		color3.addActionListener(new ActionListener() { 
	         public void actionPerformed(ActionEvent e) {
	        	 button_start.setBackground(null);
	        	 button_stop.setBackground(null);
	        	 externalButton.setBackground(null);
	        		}
	      });
		button_start.addActionListener(new ActionListener() { 
	         public void actionPerformed(ActionEvent e) {
	        	 		if(thread_started==false) {
	        	 			thread_started=true;
	        	 			generator.im_alive=true;
	        	 			generator.start();
	        	 		}
	        	 		else if(thread_started==true) {
	        	 			///generator.activate_again();
	        	 			generator.im_alive=true;
	        	 		}
	        	}
	      });
		button_stop.addActionListener(new ActionListener() { 
	         public void actionPerformed(ActionEvent e) {
	        	
	        	generator.kill();
	        	}
	      });
		
		}
		
		
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
