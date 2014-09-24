//plugin version 0.9.6
//date: 4.9.2014 by mag

package mrui.preprocessing.l2gfilter;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mrui.Mrui;
//import mrui.mode1d.Mode1DWinImpl;
import mrui.plugin.PluginInfo;
import mrui.plugin.preprocessing.PreprocessingPlugin;
import mrui.utils.BatchTokenizer;

public class L2GfilterPlugin extends PreprocessingPlugin {

	public L2GfilterPlugin(Mrui mrui, PluginInfo pluginInfo) {
		super(mrui, pluginInfo);
		filterGUI();
	}

	//Load data
	int fidIndex0 = 0;
	int NumberOfSignals = mrui.getData().getSignalsNb();
	int fidLength = mrui.getData().getLength();
		
	// Load the MRS data
	double[][] fid_original = mrui.getData().getSignal(fidIndex0);
	double[][][] FID_3Darray = new double[NumberOfSignals][2][fidLength];
	double[][][] FIDtemp_3Darray = new double[NumberOfSignals][2][fidLength];

    // CancelButton status
	boolean CancelClicked;	
	
	//swing GUI
	
	//Labels to identify the fields
    JLabel TLLabel = new JLabel("T_L:");
    JLabel TGLabel = new JLabel("T_G:");
	
    //Fields for data entry
    JFormattedTextField TLField = new JFormattedTextField("70");
    JFormattedTextField TGField = new JFormattedTextField("150");
    
    //Sliders
	public JSlider slider1 = new JSlider(JSlider.HORIZONTAL, 0, 1000, 70);
	public JSlider slider2 = new JSlider(JSlider.HORIZONTAL, 0, 1000, 150);
	// Intensity
	//public JSlider slider3 = new JSlider(JSlider.HORIZONTAL, 0, 1, 1);
	
	
	@Override
	protected void histo() {
		mrui.addHistoryState(getShortName());
	}

	@Override
	public boolean batchPerform(BatchTokenizer commandTokenizer) {
		return defaultBatch();
	}

	//@Override	
	protected boolean beginPreprocessing() {
		return true;
	}

	@Override
	public void launch() {
		background();
	}

	@Override
	protected boolean endPreprocessing() {
		return true;
	}

	@Override
	protected boolean preprocessingFid(int fidIndex) {
		//double[][] fid_original = mrui.getData().getSignal(fidIndex);
		//double[][] fid_temp = new double[fid_original.length][];
		
		//System.out.println("preprocessingFid fidIndex: "+ fidIndex);

		
		double T_L = slider1.getValue();
		//System.out.println("Slider1: " + slider1.getValue());
		double T_G = slider2.getValue();
		//System.out.println("Slider2: " + slider2.getValue());
	
		for (int i = 0; i < NumberOfSignals; i++){
			for (int j = 0; j < 2; j++){
				for (int k = 0; k < fidLength; k++) {
					FIDtemp_3Darray[i][j][k] = FID_3Darray[i][j][k]*Math.exp(k/T_L)*Math.exp(-Math.pow(k,2)/Math.pow(T_G,2));
				}
			}
			
		}
        
		if (CancelClicked == true){
			for (int i = 0; i < NumberOfSignals; i++){
        		mrui.getData().setDataOfSignal(i, FID_3Darray[i]);
        	}
		} else {
			for (int i = 0; i < NumberOfSignals; i++){
           		mrui.getData().setDataOfSignal(i, FIDtemp_3Darray[i]);
        	}
		}
		
		//mrui.getData().setDataOfSignal(fidIndex, fid_temp);
		//System.out.println("Data: "+ fid_original[REAL][10]);
		
		return true;		
	}
	

	public void filterGUI(){

		final JDialog LGfilterDialog = new JDialog(mrui.getMainWindow(), "L2G filter plugin", true);
		LGfilterDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		LGfilterDialog.setResizable(true);
		LGfilterDialog.setSize(600,212);
		
		//This will center the JFrame in the middle of the screen
		LGfilterDialog.setLocationRelativeTo(null);
		
		//LGfilterDialog.setBounds(100, 100, 290, 400);
		GridBagLayout layout = new GridBagLayout();
		LGfilterDialog.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		// ArrayList approach from Lubos Bistak
		// Create and fill multidimensional ArrayList
	  	ArrayList<ArrayList<ArrayList<String>>> indexFID = new ArrayList<ArrayList<ArrayList<String>>>();
	  	for (int i = 0; i < NumberOfSignals; i++){
	  		ArrayList<ArrayList<String>> complexFID = new ArrayList<ArrayList<String>>();
	  		for (int j = 0; j < 2; j++){
	  			ArrayList<String> vectorFID = new ArrayList<String>();
	  			for (int k = 0; k <  fidLength; k++){
	  				vectorFID.add(String.valueOf(mrui.getData().getSignal(i)[j][k]));
	  			}
	  		complexFID.add(vectorFID);
	  		}
	  	indexFID.add(complexFID);
	  	}
	  	
	  	System.out.println(NumberOfSignals);
	  	System.out.println(fidLength);
	  	
	  	/*
		for (int i = 0; i < NumberOfSignals; i++){
			System.out.println(Double.valueOf(indexFID.get(i).get(REAL).get(0)) + " " + Double.valueOf(indexFID.get(i).get(IMAG).get(900)));
		}
		*/
	  	
		// Fill all the FID datapoints from all datasets into the 3D array
	  	for (int i = 0; i < NumberOfSignals; i++){
	  		for (int j = 0; j < 2; j++){
	  			for (int k = 0; k <  fidLength; k++){
	  				FID_3Darray[i][j][k] = Double.valueOf(indexFID.get(i).get(j).get(k));
	  			}
	  		}
	  	}
		
	  	/*
	  	double testNum = Double.valueOf(indexFID.get(0).get(IMAG).get(0));
	    System.out.println("Test number from String to Int: " + testNum);
		*/
	    	    
    
		// T_L
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		gbc.gridx = 0;
	    gbc.gridy = 0;
		LGfilterDialog.add(TLLabel,gbc);
		
		gbc.gridx = 1;
	    gbc.gridy = 0;
	    LGfilterDialog.add(TLField,gbc);
	
	    
		slider1.setMajorTickSpacing(100);
		slider1.setMinorTickSpacing(10);
		slider1.setPaintLabels(true);
		slider1.addChangeListener(new ChangeListener() {
			 
			public void stateChanged(ChangeEvent e) {		        
		        TLField.setText(String.valueOf(slider1.getValue()));	
		          
		        
		        double T_L = slider1.getValue();
				double T_G = slider2.getValue();
				
				for (int i = 0; i < NumberOfSignals; i++){
					for (int j = 0; j < 2; j++){
						for (int k = 0; k < fidLength; k++) {
							FIDtemp_3Darray[i][j][k] = FID_3Darray[i][j][k]*Math.exp(k/T_L)*Math.exp(-Math.pow(k,2)/Math.pow(T_G,2));
						}
					}
					
				}
		        
				for (int i = 0; i < NumberOfSignals; i++){
					mrui.getData().setDataOfSignal(i, FIDtemp_3Darray[i]);
				}
				
				// Refresh the window with spectra
				//((Mode1DWinImpl) (mrui.getMainWindow().get1DWindow())).resetContent();
				mrui.getMainWindow().get1DWindow().getGraph().calculatePreprocessingButKeepView(true);
		        
			}
		});
		

		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.gridwidth = 2;
	    LGfilterDialog.add(slider1,gbc);
			    
		// T_G
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.weightx = 0.5;
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    LGfilterDialog.add(TGLabel,gbc);
	    
	    gbc.gridx = 1;
	    gbc.gridy = 2;
		LGfilterDialog.add(TGField,gbc);
		
		slider2.setMajorTickSpacing(100);
		slider2.setMinorTickSpacing(10);
		slider2.setPaintLabels(true);
		slider2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ee) {
		        TGField.setText(String.valueOf(slider2.getValue()));
		        
		        double T_L = slider1.getValue();
				double T_G = slider2.getValue();
		        
		        	        
				for (int i = 0; i < NumberOfSignals; i++){
					for (int j = 0; j < 2; j++){
						for (int k = 0; k < fidLength; k++) {
							FIDtemp_3Darray[i][j][k] = FID_3Darray[i][j][k]*Math.exp(k/T_L)*Math.exp(-Math.pow(k,2)/Math.pow(T_G,2));
						}
					}
					
				}
		        
				for (int i = 0; i < NumberOfSignals; i++){
					mrui.getData().setDataOfSignal(i, FIDtemp_3Darray[i]);
				}
				
				
				// Refresh the window with spectra
				//((Mode1DWinImpl) (mrui.getMainWindow().get1DWindow())).resetContent();
				mrui.getMainWindow().get1DWindow().getGraph().calculatePreprocessingButKeepView(true);
		        
		    }
		});
		
		
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    LGfilterDialog.add(slider2,gbc);

		    
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipady = 20; 
		gbc.gridx = 0;
		gbc.gridy = 4;
		JCheckBox ShowOriginalSignal = new JCheckBox();
		ShowOriginalSignal.setText("Show original signal");  
		LGfilterDialog.add(ShowOriginalSignal,gbc);
		ShowOriginalSignal.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {

				AbstractButton abstractButton = (AbstractButton) e.getSource();
		        boolean selected = abstractButton.getModel().isSelected();
		        //System.out.println(selected);
				
		        if (selected == true){
		        	for (int i = 0; i < NumberOfSignals; i++){
		        		mrui.getData().setDataOfSignal(i, FID_3Darray[i]);
		        	}	        	
		        	
		        }
		        
		        if (selected == false){
		        	for (int i = 0; i < NumberOfSignals; i++){
		        		mrui.getData().setDataOfSignal(i, FIDtemp_3Darray[i]);
		        	}
		        }
		        		        
				// Refresh the window with spectra
				//((Mode1DWinImpl) (mrui.getMainWindow().get1DWindow())).resetContent();
				mrui.getMainWindow().get1DWindow().getGraph().calculatePreprocessingButKeepView(true);
				
		    }
		});
		
		// Add buttons Apply and Cancel
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipady = 0; 
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 5;
		JButton ApplyButton = new JButton("Apply");
		LGfilterDialog.add(ApplyButton,gbc);
		ApplyButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LGfilterDialog.dispose();
			}
		});
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 5;
		JButton CancelButton = new JButton("Cancel");
		LGfilterDialog.add(CancelButton,gbc);
		CancelButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				CancelClicked = true;
				LGfilterDialog.dispose();
			}
		});
		
		//LGfilterDialog.pack();
		LGfilterDialog.setVisible(true);
		
	}
	
}

