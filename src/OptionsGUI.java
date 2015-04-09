/**
 * Created by Admin on 23/01/2015.
 */

import com.epicbot.api.rs3.methods.tab.Skills;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsGUI extends JFrame
{
    public static Container             pane;
    public static Insets                insets;
    public static JButton               btnStart;
    public static JComboBox<String>     cboLogs, cboBank;
    public static JLabel                lblWhichLogs, lblWhichBank;
    public static JCheckBox             cboxPickupOrts, cboxBankLogs;
    public static JTextField            tfPattern;

    public RoboWoodcutter bot;

    public OptionsGUI()
    {
        super("Robo's AIO Woodcutter");

        bot = BotUtil.main;

        CreateTheFrame();
        CreateControls();
        PreparePanel();

        setVisible(true);
    }

    public void CreateTheFrame()
    {
        //Set its size to 250x300 pixels
        setSize(250, 500);
        setMaximumSize( new Dimension(250, 500) );
        setMinimumSize(new Dimension( 250, 500));
        setResizable(false);

        pane = getContentPane();
        insets = pane.getInsets();

        // Apply the null layout
        pane.setLayout(null);
    }

    public void CreateControls()
    {
        int itterator = 0;
        lblWhichLogs    = new JLabel("Which logs should I chop?");
        lblWhichLogs.setLocation(0, 0);
        lblWhichLogs.setSize( new Dimension(insets.left + 5, getWidth() - 10) );
        lblWhichLogs.setMinimumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        lblWhichLogs.setMaximumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        lblWhichLogs.setBounds(insets.left + 5, itterator*30, getWidth() - 20, 25);

        itterator += 1;
        cboLogs         = new JComboBox<String>();
        cboLogs.setLocation(0, itterator*30);
        cboLogs.setSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboLogs.setMinimumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboLogs.setMaximumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboLogs.setBounds(insets.left + 5, itterator*30, getWidth() - 20, 25);
        cboLogs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SetBankModel();
            }
        });

        itterator += 1;
        cboxPickupOrts  = new JCheckBox("Pickup Orts?");
        cboxPickupOrts.setLocation(0, itterator*30);
        cboxPickupOrts.setSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboxPickupOrts.setMinimumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboxPickupOrts.setMaximumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboxPickupOrts.setBounds(insets.left + 5, itterator*30, getWidth() - 20, 25);

        itterator += 1;
        cboxBankLogs    = new JCheckBox("Do you want me to bank logs?");
        cboxBankLogs.setLocation(0, itterator*30);
        cboxBankLogs.setSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboxBankLogs.setMinimumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboxBankLogs.setMaximumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboxBankLogs.setBounds(insets.left + 5, itterator*30, getWidth() - 20, 25);

        itterator += 1;
        lblWhichBank    = new JLabel("Which Bank should I use?");
        lblWhichBank.setLocation(0, itterator*30);
        lblWhichBank.setSize( new Dimension(insets.left + 5, getWidth() - 10) );
        lblWhichBank.setMinimumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        lblWhichBank.setMaximumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        lblWhichBank.setEnabled(false);
        lblWhichBank.setBounds(insets.left + 5, itterator*30, getWidth() - 20, 25);

        itterator += 1;
        cboBank         = new JComboBox<String>();
        cboBank.setLocation(0, itterator*30);
        cboBank.setSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboBank.setMinimumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboBank.setMaximumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        cboBank.setEnabled(false);
        cboBank.setBounds(insets.left + 5, itterator*30, getWidth() - 20, 25);

        itterator += 1;
        tfPattern = new JTextField();
        tfPattern.setLocation(0, itterator*30);
        tfPattern.setSize( new Dimension(insets.left + 5, getWidth() - 10) );
        tfPattern.setMinimumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        tfPattern.setMaximumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        tfPattern.setEnabled(false);
        tfPattern.setBounds(insets.left + 5, itterator*30, getWidth() - 20, 25);

        itterator += 3;
        btnStart        = new JButton("Start");
        btnStart.setLocation(0, itterator*30);
        btnStart.setSize( new Dimension(insets.left + 5, getWidth() - 10) );
        btnStart.setMinimumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        btnStart.setMaximumSize( new Dimension(insets.left + 5, getWidth() - 10) );
        btnStart.setBounds(insets.left + 5, itterator*30, getWidth() - 20, 25);

        btnStart.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                OnStart();
            }
        });

        cboxBankLogs.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                lblWhichBank.setEnabled(cboxBankLogs.isSelected());
                cboBank.setEnabled(cboxBankLogs.isSelected());
            }
        });

        cboLogs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( cboLogs.getSelectedItem().toString().equals("Elder") )
                {
                    tfPattern.setEnabled(true);
                }
                else
                {
                    tfPattern.setEnabled(false);
                }
            }
        });

        SetLogsModel();
        SetBankModel();
    }

    public void PreparePanel()
    {
        pane.add(lblWhichLogs);
        pane.add(cboLogs);

        pane.add(cboxPickupOrts);

        pane.add(cboxBankLogs);
        pane.add(lblWhichBank);
        pane.add(cboBank);
        pane.add(tfPattern);

        pane.add(btnStart);
    }

    public void SetLogsModel()
    {
        DefaultComboBoxModel<String> LogsModel = new DefaultComboBoxModel<String>();

        for(BotUtil.PossibleLogs b : BotUtil.PossibleLogs.values())
        {
            String s = b.toString();
            String[] words = s.split("_");
            String value = "";

            for( String word : words )
            {
                value += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            }

            LogsModel.addElement( value );
        }

        try
        {
            cboLogs.setModel(LogsModel);
        }
        catch( Exception e)
        {

        }
    }

    public void SetBankModel()
    {
        DefaultComboBoxModel<String> BankModel = new DefaultComboBoxModel<String>();

        for(BotUtil.Banks b : BotUtil.Banks.values())
        {
            if (BotUtil.CanBankLogsHere(GetSelectedLogs(), b))
            {
                String s = b.toString();
                String[] words = s.split("_");
                String value = "";

                for (String word : words)
                {
                    value += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
                }

                BankModel.addElement(value);
            }
        }

        try
        {
            cboBank.setModel( BankModel );
        }
        catch( Exception e)
        {

        }
    }

    public void OnStart()
    {
        try
        {
            BotUtil.BANK_LOGS       = cboxBankLogs.isSelected();
            BotUtil.PICKUP_ORTS     = cboxPickupOrts.isSelected();
            BotUtil.CHOSENLOGS      = GetSelectedLogs();
            BotUtil.CHOSENBANK      = GetSelectedBank();

            BotUtil.WriteMessage("Bank Logs:   " + BotUtil.BANK_LOGS);
            BotUtil.WriteMessage("Pickup Orts: " + BotUtil.PICKUP_ORTS);
            BotUtil.WriteMessage("Chosen Logs: " + BotUtil.CHOSENLOGS.toString());
            BotUtil.WriteMessage("Chosen Bank: " + BotUtil.CHOSENBANK.toString());

            BotUtil.BOT_IS_RUNNING = true;
            BotUtil.paintHandler.timeStarted = System.currentTimeMillis();
            BotUtil.paintHandler.expAtStart = Skills.Skill.WOODCUTTING.getExperience();
            BotUtil.paintHandler.levelAtStart = Skills.Skill.WOODCUTTING.getCurrentLevel();

            if( BotUtil.CHOSENLOGS == BotUtil.PossibleLogs.CRYSTAL || BotUtil.CHOSENLOGS == BotUtil.PossibleLogs.ELDER )
            {
                // Pattern Reading:
                String[] patternString = tfPattern.getText().replace(" ", "").split(",");
                BotUtil.patternArray = new int[patternString.length];

                for (int i = 0; i < patternString.length; i++)
                {
                    BotUtil.patternArray[i] = Integer.parseInt(patternString[i]);
                }
            }
            BotUtil.antiban.start();
            BotUtil.main.StartBot();
            BotUtil.WriteMessage("Bot Started.");
            setVisible(false);
        }
        catch (Exception e )
        {
            e.printStackTrace();
            BotUtil.WriteMessage("Startup Failed, Please check parametres are correct and try again, or contact DamnYouRobo on EB Forums. ");
        }
    }

    public BotUtil.PossibleLogs GetSelectedLogs()
    {
        BotUtil.PossibleLogs retVal = BotUtil.PossibleLogs.NORMAL;

        for(BotUtil.PossibleLogs logs : BotUtil.PossibleLogs.values() )
        {
            String[] words = logs.toString().split("_");
            String value = "";

            for( String word : words )
            {
                value += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            }

            if( value.equals(cboLogs.getSelectedItem()) )
            {
                retVal = logs;
            }
        }

        return retVal;
    }

    public BotUtil.Banks GetSelectedBank()
    {
        BotUtil.Banks retVal = BotUtil.Banks.None;

        for (BotUtil.Banks bank : BotUtil.Banks.values())
        {
            String[] words = bank.toString().split("_");
            String value = "";

            for( String word : words )
            {
                value += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
            }

            if( value.equals(cboBank.getSelectedItem()) )
            {
                retVal = bank;
            }
        }

        return retVal;
    }

}
