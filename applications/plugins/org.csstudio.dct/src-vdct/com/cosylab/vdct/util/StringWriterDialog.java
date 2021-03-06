/*
 * StringWriterDialog.java
 *
 * Created on August 5, 2004, 11:39 AM
 */

package com.cosylab.vdct.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;

/**
 *
 * @author  ilist
 */
public class StringWriterDialog extends javax.swing.JDialog {

	private String returnValue = null;
    
    /** Creates new form StringWriterDialog */
    public StringWriterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
        pack();
       	setLocationRelativeTo(parent);
    }
    
    /**
	 * 
	 */
	private void init() {
		jTextField.getDocument().addDocumentListener(new DocumentListener() {
			private DBDEntry entry = new DBDEntry("");

			public void changedUpdate(DocumentEvent e) {
				// we won't ever get this with a PlainDocument
				
			}

			public void insertUpdate(DocumentEvent e) {
				update(e);
			}

			public void removeUpdate(DocumentEvent e) {
				update(e);
			}
			
			private void update(DocumentEvent e) {
				entry.setValue(jTextField.getText());				
				jLabelValue.setText(DBDEntry.matchAndReplace(jTextField.getText()));
				boolean ok = entry.getFile().canRead() && entry.getFile().isFile();
				jLabelWarning.setVisible(!ok);
				jButtonDefault.setEnabled(ok);
			}
		});

		jComboBoxMacros.setModel(new ComboBoxModel() {
			Vector elements = null;
			Object selectedItem;
		
			public Object getSelectedItem() {
				if (elements==null) loadElements();
				return selectedItem;
			}

			public void setSelectedItem(Object anItem) {
				selectedItem = anItem;
			}

			public int getSize() {
				if (elements==null) loadElements();
				return elements.size();
			}

			public Object getElementAt(int index) {
			   	if (elements==null) loadElements();
				return elements.get(index);
			}

			private void loadElements() {
				Enumeration e = DBDEntry.getProperties().propertyNames();
				elements = new Vector();
				while (e.hasMoreElements()) {
					elements.add(e.nextElement());
				}
				Collections.sort(elements);
				selectedItem = elements.get(0);
			}

			public void addListDataListener(ListDataListener l) {
				
			}

			public void removeListDataListener(ListDataListener l) {
				
			}
			
		});	
		
		jButtonDefault.setEnabled(false);	
	}

	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabelValue = new javax.swing.JLabel();
        jLabelWarning = new javax.swing.JLabel();
        jComboBoxMacros = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jButtonAppend = new javax.swing.JButton();
        jPanelButtons = new javax.swing.JPanel();
        jButtonDefault = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanelSeparator2 = new javax.swing.JPanel();
        jPanelSeparator1 = new javax.swing.JPanel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Input");
        jLabel1.setText("Enter string:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jTextField, gridBagConstraints);

        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Evaluates to: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jLabel2, gridBagConstraints);

        jLabelValue.setForeground(new java.awt.Color(102, 102, 102));
        jLabelValue.setText("value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jLabelValue, gridBagConstraints);

        jLabelWarning.setForeground(new java.awt.Color(255, 0, 0));
        jLabelWarning.setText("Warning: file doesn't exist");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jLabelWarning, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jComboBoxMacros, gridBagConstraints);

        jLabel5.setText("List of macros: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jLabel5, gridBagConstraints);

        jButtonAppend.setMnemonic('A');
        jButtonAppend.setText("Append");
        jButtonAppend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAppendActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jButtonAppend, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonDefault.setMnemonic('O');
        jButtonDefault.setText("Ok");
        jButtonDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDefaultActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelButtons.add(jButtonDefault, gridBagConstraints);

        jButtonCancel.setMnemonic('C');
        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelButtons.add(jButtonCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelSeparator2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelSeparator1, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDefaultActionPerformed
        returnValue = jTextField.getText();
        setVisible(false);
    }//GEN-LAST:event_jButtonDefaultActionPerformed

    private void jButtonAppendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAppendActionPerformed
        jTextField.setText(jTextField.getText()+"$("+jComboBoxMacros.getSelectedItem()+")");
    }//GEN-LAST:event_jButtonAppendActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new StringWriterDialog(new javax.swing.JFrame(), true).setVisible(true);
    }
    
    public String showDialog() {
		returnValue = null;    	
    	setModal(true);
    	setVisible(true);
    	
    	return returnValue;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAppend;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDefault;
    private javax.swing.JComboBox jComboBoxMacros;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelValue;
    private javax.swing.JLabel jLabelWarning;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelSeparator1;
    private javax.swing.JPanel jPanelSeparator2;
    private javax.swing.JTextField jTextField;
    // End of variables declaration//GEN-END:variables
    
}
