import com.epicbot.api.input.Mouse;
import com.epicbot.api.rs3.methods.Widgets;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.tab.Skills;
import com.epicbot.api.rs3.methods.widget.Camera;
import com.epicbot.api.rs3.wrappers.node.item.WidgetChildItem;
import com.epicbot.api.rs3.wrappers.widget.Widget;
import com.epicbot.api.rs3.wrappers.widget.WidgetChild;
import com.epicbot.api.util.Random;

/**
 * Created by Admin on 30/03/2015.
 */
public class AntibanThread implements Runnable
{
    public Thread t;
    public String threadName = "AntiBan";
    int itterator = 0;

    public void start ()
    {
        BotUtil.WriteMessage(threadName + " Starting.");

        if (t == null)
        {
            t = new Thread (this, threadName);
            t.start ();
            BotUtil.WriteMessage(threadName + " Started.");
        }
    }

    @Override
    public void run()
    {
        while( BotUtil.BOT_IS_RUNNING )
        {
            try
            {
                CheckIfAntiBanEnabled();
                RunAntiBan();
            }
            catch (InterruptedException e)
            {
                BotUtil.WriteMessage( threadName + " Error: " + e.getMessage() );
            }
        }
    }

    public void CheckIfAntiBanEnabled() throws InterruptedException
    {
        if( BotUtil.BOTSTATE == BotUtil.BotState.CHOPPING_TREE )
        {
            BotUtil.ANTIBAN_ENABLED = true;
        }
        else
        {
            BotUtil.ANTIBAN_ENABLED = false;
        }
    }

    public void RunAntiBan() throws InterruptedException
    {
        if(!BotUtil.ANTIBAN_ENABLED) return;

        if( itterator > 4 )
        {
            itterator = 0;
            int choice = Random.nextInt(0, 100);

            if( choice < 20 )
            {
                CameraRandom();
            }

            if( choice < 30 )
            {
                MouseRandom();
            }
        }

        itterator += 1;
        t.sleep(Random.nextInt(500, 750));
    }

    public void CameraRandom() throws InterruptedException
    {
        int newYaw = Camera.getYaw() + Random.nextInt(-180, 180);
        int newPitch = Camera.getPitch() + Random.nextInt(-45, 45);

        if( newPitch < 0 )
        {
            newPitch = 0;
        }
        else if( newPitch > 90)
        {
            newPitch = 90;
        }

        Camera.setAngle(newYaw);
        Camera.setPitch(newPitch);
        t.sleep(Random.nextInt(3000, 5000));
    }

    public void MouseRandom() throws InterruptedException
    {
        int choice = Random.nextInt(0, 100);
        // Move mouse to random Position.
        // Move mouse to chat window.
        // Move mouse to skills tab
        // Move offscreen.

        if( choice < 10 )
        {
            Mouse.moveRandomly(100, 400);
        }

        if( choice > 9 && choice < 80 )
        {
            Mouse.moveOffScreen();
        }

        if( choice > 79 && choice < 90 )
        {
            Skills.Skill.WOODCUTTING.hover(2000);
            Mouse.moveRandomly(100, 400);
        }

        t.sleep(Random.nextInt(1000, 3000));
    }
}
