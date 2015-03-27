import com.epicbot.api.rs3.methods.tab.Skills;

import java.awt.*;

/**
 * Created by Admin on 22/01/2015.
 */
public class PaintHandler
{
    public Thread t;
    public String threadName = "Graphics Handler";

    private int paintXPos = 8;
    private int paintYPos = 8;

    private final Color color1 = new Color(0, 0, 67, 200);
    private final Color color2 = new Color(255, 255, 255);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 1, 15);
    private final Font font2 = new Font("Arial", 0, 9);
    private final Font font3 = new Font("Arial", 0, 12);

    private final Image img1 = BotUtil.getImage("http://img4.wikia.nocookie.net/__cb20140830152718/runescape/images/e/e2/Woodcutting_master_cape_detail.png");

    public long timeStarted = 0;
    public long timeRunning = 0;
    public long timeToLevel = 0;
    public int logsCut = 0;
    public int expAtStart = 0;
    public int expGained = 0;
    public int levelAtStart = 0;
    public int levelsGained = 0;
    public int logsPerHour = 0;
    public int expPerHour = 0;

    public PaintHandler()
    {
        BotUtil.WriteMessage(threadName + " thread started.");
    }

    public void DrawPaint(Graphics2D g)
    {
        if( g == null ) return;

        CalculateData();

        g.setColor(color1);
        g.fillRoundRect(paintXPos, paintYPos, 352, 135, 16, 16);

        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(paintXPos, paintYPos, 352, 135, 16, 16);

        g.setFont(font1);
        g.drawString("Robo's AIO Woodcutter", paintXPos + 124, paintYPos + 23);

        g.setFont(font2);
        g.drawString("By DamnYouRobo", paintXPos + 254, paintYPos + 130);
        g.setFont(font2);
        g.drawString("Version: "+BotUtil.version, paintXPos + 124, paintYPos + 130);

        g.setFont(font3);
        g.drawString("Time Running: ", paintXPos + 124, paintYPos + 38);
        g.drawString("Logs Cut: " , paintXPos + 124, paintYPos + 53);
        g.drawString("Exp Gained: " , paintXPos + 124, paintYPos + 68);
        g.drawString("Time To Level: " , paintXPos + 124, paintYPos + 83);
        g.drawString("Level: " , paintXPos + 124, paintYPos + 98);
        g.drawString("Bot State: " , paintXPos + 124, paintYPos + 113);

        g.drawString("" + BotUtil.milliSecondsToString(timeRunning), paintXPos + 224, paintYPos + 38 );
        g.drawString("" + logsCut + "( " + logsPerHour + " )", paintXPos + 224, paintYPos + 53 );
        g.drawString("" + expGained + "( " + expPerHour + " )", paintXPos + 224, paintYPos + 68 );
        g.drawString("" + BotUtil.milliSecondsToString(timeToLevel * 1000), paintXPos + 224, paintYPos + 83 );
        g.drawString("" + Skills.Skill.WOODCUTTING.getCurrentLevel() + "( " + levelsGained + " )", paintXPos + 224, paintYPos + 98 );
        g.drawString(BotUtil.GetBotStateString(), paintXPos + 224, paintYPos + 113 );

        Image scaledImage = BotUtil.ScaledImage(img1, 20);

        g.drawImage(scaledImage, (62 - scaledImage.getWidth(null)/2), paintYPos-1, null);
    }

    public void CalculateData()
    {
        timeRunning = System.currentTimeMillis() - timeStarted;
        expGained = Skills.Skill.WOODCUTTING.getExperience() - expAtStart;
        levelsGained = Skills.Skill.WOODCUTTING.getCurrentLevel() - levelAtStart;

        expPerHour = (int) (expGained / (timeRunning / (1000f * 60f * 60f)));
        logsPerHour = (int) (logsCut / (timeRunning / (1000f * 60f * 60f)));


        if (expPerHour != 0)
        {
            timeToLevel = (long)(3600 * (double)Skills.Skill.WOODCUTTING.getExperienceToLevel(Skills.Skill.WOODCUTTING.getCurrentLevel() + 1) / (double)expPerHour);
        }
    }
}