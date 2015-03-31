import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created by jt13602 on 24/03/2015.
 */
public class BotUtil
{
    public static String version = "0.42";

    public static RoboWoodcutter main;
    public static PaintHandler paintHandler;
    public static AntibanThread antiban;

    public static boolean ANTIBAN_ENABLED       = false;
    public static boolean BOT_IS_RUNNING        = false;
    public static boolean BANK_LOGS             = false;
    public static boolean PICKUP_ORTS           = false;
    public static boolean InBankArea            = false;
    public static PossibleLogs CHOSENLOGS       = PossibleLogs.NORMAL;
    public static Banks CHOSENBANK              = Banks.None;
    public static BotState BOTSTATE             = BotState.CHECKING;

    public static int[] bankIDs             = new int[] { 2012, 2015, 2019, 4456, 4458, 4457, 4459, 9710 };
    public static String[] bankNames           = new String[] { "Banker", "Counter", "Bank", "Bank chest", "Fremennik Banker" };

    // region [ Enumerators ]...
    //
    public static enum BotState
    {
        CHECKING,
        CHOPPING_TREE,
        BANKING_LOGS,
        TRAVELING_TO_TREE,
        TRAVELING_TO_BANK,
        DROPPING_LOGS
    }

    public static enum PossibleLogs
    {
        NORMAL,
        OAK,
        WILLOW,
        MAPLE,
        YEW,
        MAGIC,
        ELDER,
        CRYSTAL,
        IVY
    }

    public static enum Banks
    {
        None,
        Grand_Exchange,
        Seers_Village,
        Draynor,
        Clan_Camp,
        Daemonheim
    }
    //
    //endregion

    // region [ GUI Specific Methods ]...
    //
    public static boolean CanBankLogsHere(PossibleLogs log, Banks bank)
    {
        if( bank == Banks.None ) return true;

        switch(log)
        {
            case NORMAL:
                //region Normal Logs Banks
                switch( bank )
                {
                    case Grand_Exchange:
                        return true;
                    default:
                        return false;
                }
                //endregion
            case OAK:
                //region OAK Logs Banks
                switch( bank )
                {
                     case Clan_Camp:
                         return true;
                    case Draynor:
                        return true;
                    default:
                        return false;
                }
                //endregion
            case WILLOW:
                //region WILLOW Logs Banks
                switch( bank )
                {
                    case Draynor:
                        return true;
                    default:
                        return false;
                }
                //endregion

            case MAPLE:
                //region MAPLE Logs Banks
                switch( bank )
                {
                    case Seers_Village:
                        return true;
                    case Daemonheim:
                        return true;
                    default:
                        return false;
                }
                //endregion

                //region NOT YET ENABLED...
//
//            case YEW:
                //region YEW Logs Banks
//                switch( bank )
//                {
//                    case Grand_Exchange:
//                        return true;
//                    default:
//                        return false;
//                }
                //endregion
//            case MAGIC:
                //region MAGIC Logs Banks
//                switch( bank )
//                {
//                    case Seers_Village:
//                        return true;
//                    default:
//                        return false;
//                }
                //endregion
//            case ELDER:
                //region ELDER Logs Banks
//                switch( bank )
//                {
//                    default:
//                        return false;
//                }
                //endregion
//            case CRYSTAL:
                //region CRYSTAL Logs Banks
//                switch( bank )
//                {
//                    default:
//                        return false;
//                }
                //endregion
//            case IVY:
                //region IVY Banks
//                switch( bank )
//                {
//                    case Grand_Exchange:
//                        return true;
//                    default:
//                        return false;
//                }
                //endregion
            //endregion

            default:
                return false;
        }
    }
    //
    // endregion

    // region [ Utility Methods ]...
    //
    public static void WriteMessage(String message)
    {
        System.out.println("[RoboAIOWoodcutter]: " + message);
    }

    public static String milliSecondsToString(long millis)
    {
        int secs = (int)(millis/1000);
        int mins = secs/60;
        int hours = mins/60;

        String hrsString = "";
        String mnsString = "";
        String scsString = "";

        if( hours < 10 )
        {
            hrsString = "0"+hours;
        }
        else
        {
            hrsString = ""+hours;
        }

        if( (mins%60) < 10 )
        {
            mnsString = "0"+(mins%60);
        }
        else
        {
            mnsString = ""+(mins%60);
        }

        if( (secs%60) < 10 )
        {
            scsString = "0"+(secs%60);
        }
        else
        {
            scsString = ""+(secs%60);
        }

        return hrsString+":"+mnsString+":"+scsString;
    }

    public static String GetBotStateString()
    {
        String state = BotUtil.BOTSTATE.toString();
        String[] parts = state.split("_");
        String output = "";

        for(String s : parts)
        {
            output += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }

        return output;
    }

    public static Image getImage(String url)
    {
        try
        {
            return ImageIO.read(new URL(url));
        }
        catch(IOException e)
        {
            return null;
        }
    }

    public static Image ScaledImage(Image img, int scaleFactor)
    {
        Image scaledImage = img.getScaledInstance((int)(img.getWidth(null) * ((float)scaleFactor / 100f)), (int)(img.getHeight(null) * ((float)scaleFactor / 100f)), Image.SCALE_SMOOTH);
        return scaledImage;
    }
    //
    // endregion
}
