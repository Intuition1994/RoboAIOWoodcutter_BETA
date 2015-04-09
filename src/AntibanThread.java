import com.epicbot.api.input.Mouse;
import com.epicbot.api.rs3.methods.tab.Skills;
import com.epicbot.api.rs3.methods.widget.Camera;
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
                BotUtil.WriteMessage(threadName + " Error: " + e.getMessage());
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

        int choice = Random.nextInt(0, 100);

        if( choice < 10 )
        {
            CameraRandom();
        }

        if( choice > 9 && choice < 50 )
        {
            MouseRandom();
        }

        t.sleep(Random.nextInt(2000, 3500));
    }

    public void CameraRandom() throws InterruptedException
    {
        int newYaw = Camera.getYaw() + Random.nextInt(-180, 180);
        int newPitch = Camera.getPitch() + Random.nextInt(-45, 45);

        if( newPitch < 30 )
        {
            newPitch = Random.nextInt(25, 30);
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

        if( choice > 9 && choice < 90 )
        {
            Mouse.moveOffScreen();
        }

        if( choice > 89 && choice < 90 )
        {
            Skills.Skill.WOODCUTTING.hover(2000);
            Mouse.moveRandomly(100, 400);
        }

        t.sleep(Random.nextInt(1000, 3000));
    }
}
