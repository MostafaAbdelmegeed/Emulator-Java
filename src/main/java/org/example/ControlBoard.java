package org.example;

import javax.swing.*;
import java.util.ArrayList;

public class ControlBoard extends JTabbedPane {  // Extend JTabbedPane directly
    ArrayList<ControlUnit> controlUnits = new ArrayList<>();

    public ControlBoard() {
        // You can initialize any additional settings here if needed
        super();
    }


    // Method to add a tab with a new ControlUnit
    public void addUnitControl(String title) {
        // Add a new ControlUnit as a tab with the given title
        ControlUnit cu = new ControlUnit();
        controlUnits.add(cu);
        this.addTab(title, cu);
    }

    public ArrayList<ArrayList<Double>> getControlData()
    {
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
        for (ControlUnit controlUnit : controlUnits) {
            data.add(controlUnit.getControlData());
        }
        return data;
    }
}
