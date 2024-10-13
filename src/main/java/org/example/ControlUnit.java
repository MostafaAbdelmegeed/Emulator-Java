package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ControlUnit extends JPanel {
    private JSpinner qx;
    private JSpinner tx;
    private JSpinner ty;
    private JSpinner tz;
    private JSpinner qy;
    private JSpinner qz;
    private JSpinner qo;
    Double defaultValue = 0.0;
    Double defaultStep = 1.0;
    Double defaultLowerBoundary = -1000.0;
    Double defaultUpperBoundary = 1000.0;

    public ControlUnit() {
        // Initialize components
        tx = new JSpinner();
        ty = new JSpinner();
        tz = new JSpinner();
        qx = new JSpinner();
        qy = new JSpinner();
        qz = new JSpinner();
        qo = new JSpinner();

        // Set spinner models
        tx.setModel(new SpinnerNumberModel(defaultValue, defaultLowerBoundary, defaultUpperBoundary, defaultStep));
        tx.setEditor(new JSpinner.NumberEditor(tx, "#.##"));
        ty.setModel(new SpinnerNumberModel(defaultValue, defaultLowerBoundary, defaultUpperBoundary, defaultStep));
        ty.setEditor(new JSpinner.NumberEditor(ty, "#.##"));
        tz.setModel(new SpinnerNumberModel(defaultValue, defaultLowerBoundary, defaultUpperBoundary, defaultStep));
        tz.setEditor(new JSpinner.NumberEditor(tz, "#.##"));
        qx.setModel(new SpinnerNumberModel(defaultValue, defaultLowerBoundary, defaultUpperBoundary, defaultStep));
        qx.setEditor(new JSpinner.NumberEditor(qx, "#.##"));
        qy.setModel(new SpinnerNumberModel(defaultValue, defaultLowerBoundary, defaultUpperBoundary, defaultStep));
        qy.setEditor(new JSpinner.NumberEditor(qy, "#.##"));
        qz.setModel(new SpinnerNumberModel(defaultValue, defaultLowerBoundary, defaultUpperBoundary, defaultStep));
        qz.setEditor(new JSpinner.NumberEditor(qz, "#.##"));
        qo.setModel(new SpinnerNumberModel(Double.valueOf(1.0), defaultLowerBoundary, defaultUpperBoundary, defaultStep));
        qo.setEditor(new JSpinner.NumberEditor(qo, "#.##"));

        // Set layout for this panel (for example, GridLayout with 2 columns)
        setLayout(new GridLayout(4, 2, 4, 4));  // 4 rows, 2 columns, 10px gaps

        // Add components to the panel (T components in one column, Q components in the other)
        add(new JLabel("QX:"));
        add(qx);
        add(new JLabel("TX:"));
        add(tx);
        add(new JLabel("QY:"));
        add(qy);
        add(new JLabel("TY:"));
        add(ty);
        add(new JLabel("QZ:"));
        add(qz);
        add(new JLabel("TZ:"));
        add(tz);
        add(new JLabel("QO:"));
        add(qo);
    }

    public ArrayList<Double> getControlData()
    {
        ArrayList<Double> data = new ArrayList<>();
        data.add((Double) tx.getValue());
        data.add((Double) ty.getValue());
        data.add((Double) tz.getValue());
        data.add((Double) qo.getValue());
        data.add((Double) qx.getValue());
        data.add((Double) qy.getValue());
        data.add((Double) qz.getValue());
        return data;
    }
}
