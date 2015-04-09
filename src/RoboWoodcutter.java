import com.epicbot.api.ActiveScript;
import com.epicbot.api.GameType;
import com.epicbot.api.Manifest;
import com.epicbot.event.events.MessageEvent;
import com.epicbot.event.listeners.MessageListener;
import com.epicbot.event.listeners.PaintListener;

import javax.swing.*;
import java.awt.*;

/**
 * Created by jt13602 on 24/03/2015.
 */
@Manifest(
        author = "DamnYouRobo",
        game = GameType.RS3,
        name = "[RS3] Robo AIO Woodcutter",
        description = "Chops Wood Anywhere, and banks in listed locations.")
public class RoboWoodcutter extends ActiveScript implements PaintListener, MessageListener
{
    private PaintHandler paintHandler;
    private AntibanThread antiban;

    @Override
    public boolean onStart()
    {
        paintHandler = new PaintHandler();
        antiban      = new AntibanThread();

        BotUtil.main = this;
        BotUtil.paintHandler = paintHandler;
        BotUtil.antiban = antiban;

        // Create GUI and Wait for user input.
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        OptionsGUI gui = new OptionsGUI();
                        JFrame frame = new JFrame("GUI");
                        frame.setContentPane(gui);
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.pack();
                        frame.setVisible(true);
                    }
                });

        return true;
    }

    public void StartBot()
    {
        provide( new CutNormalTrees() );
        provide( new CutOakTrees() );
        provide( new CutWillowTrees() );
        provide( new CutMapleTrees() );
        provide( new CutMapleTreesDaemonheim() );
        provide( new CutYewTrees() );
        // provide( new CutYewTreesDaemonheim() );
        provide( new CutMagicTrees() );
        provide( new CutElderTrees() );
        provide( new CutIvy() );
        provide( new CutCurlyRoots() );
    }

    public void onStop()
    {
        BotUtil.BOT_IS_RUNNING = false;
    }

    @Override
    public void messageReceived(MessageEvent messageEvent)
    {
        String msg = messageEvent.getMessage();
        String CutLogsMessage = "you get some ";

        if( msg.toLowerCase().startsWith(CutLogsMessage) && msg.toLowerCase().contains("logs") )
        {
            paintHandler.logsCut += 1;
        }
    }

    @Override
    public void onRepaint(Graphics2D graphics2D)
    {
        paintHandler.DrawPaint(graphics2D);
    }
}
