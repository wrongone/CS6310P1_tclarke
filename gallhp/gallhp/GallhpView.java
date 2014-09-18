package gallhp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import lib.ISimulation;
import lib.IView;
import lib.UpdatePacket;
import util.GallhpHandler;
import util.SimulationFactory;
import util.SimulationSelection;

public class GallhpView extends JFrame implements Observer, IView {

	private static final long serialVersionUID = 1L;
	
	private JButton runBtn, resetBtn;
	private MenuView menuView;
	private ResultsView resultsView;
	private GallhpHandler resultHandler;
	private ISimulation simulation;
	
	public GallhpView() {
		
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
		} catch (InstantiationException e1) {
		} catch (IllegalAccessException e1) {
		} catch (UnsupportedLookAndFeelException e1) {
		}
		
		// add our JPanel components
		menuView = new MenuView(this);
		resultsView = new ResultsView();
		
		this.setTitle("Heat Diffusion Simulation");
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 2, 2);
		
		this.add(menuView, gbc);
		gbc.gridx++;
		this.add(resultsView, gbc);
		gbc.gridx++;
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		runBtn = new JButton("Run");
		runBtn.setPreferredSize(new Dimension(40, 25));
		runBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Run Button pressed");
				runSim();
			}
		});
		
		this.add(runBtn, gbc);
		gbc.gridx++;
		
		resetBtn = new JButton("Reset");
		resetBtn.setPreferredSize(new Dimension(40, 25));
		resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Reset Button pressed");
				menuView.reset();
			}
		});
		
		this.add(resetBtn, gbc);
		
		this.pack();
		this.setVisible(true);
	}
	
	public void runSim() {
		
		System.out.println("Running simulation....");
		
		double top = Double.parseDouble(menuView.getTopEdgeText());
		double bottom = Double.parseDouble(menuView.getBottomEdgeText());
		double left = Double.parseDouble(menuView.getLeftEdgeText());
		double right = Double.parseDouble(menuView.getRightEdgeText());
		
		int width = Integer.parseInt(menuView.getPlateWidth());
		int height = Integer.parseInt(menuView.getPlateHeight());
		
		resultHandler = new GallhpHandler();
		resultHandler.addObserver(this);
		
		SimulationSelection selection = menuView.getSelectedSimulation();
		simulation = SimulationFactory.getInstance().produceSimulation(selection);
		simulation.injectHandler(resultHandler);
		simulation.setup(width, height, top, bottom, left, right);
		simulation.run();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		if (!(arg1 instanceof UpdatePacket)) 
			return;
		
		UpdatePacket packet = (UpdatePacket) arg1;
		resultsView.updateResults(packet.temp, packet.x, packet.y);
	}
}
