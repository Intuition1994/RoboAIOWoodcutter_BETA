import com.epicbot.api.input.Mouse;
import com.epicbot.api.rs3.methods.Walking;
import com.epicbot.api.rs3.methods.interactive.NPCs;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.node.SceneEntities;
import com.epicbot.api.rs3.methods.tab.inventory.Inventory;
import com.epicbot.api.rs3.methods.widget.Bank;
import com.epicbot.api.rs3.methods.widget.Camera;
import com.epicbot.api.rs3.wrappers.Area;
import com.epicbot.api.rs3.wrappers.Tile;
import com.epicbot.api.rs3.wrappers.interactive.NPC;
import com.epicbot.api.rs3.wrappers.map.TilePath;
import com.epicbot.api.rs3.wrappers.node.Item;
import com.epicbot.api.rs3.wrappers.node.SceneObject;
import com.epicbot.api.util.Random;
import com.epicbot.api.util.Time;
import javafx.scene.Scene;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.List;
import java.io.IOException;
import java.net.URL;

/**
 * Created by jt13602 on 24/03/2015.
 */
public class BotUtil
{
    public static String version = "0.43";

    public static RoboWoodcutter main;
    public static PaintHandler paintHandler;
    public static AntibanThread antiban;

    public static boolean ANTIBAN_ENABLED       = false;
    public static boolean BOT_IS_RUNNING        = false;
    public static boolean BANK_LOGS             = false;
    public static boolean PICKUP_ORTS           = false;
    public static boolean InBankArea            = false;
    public static boolean DroppingIventory      = false;
    public static PossibleLogs CHOSENLOGS       = PossibleLogs.NORMAL;
    public static Banks CHOSENBANK              = Banks.None;
    public static BotState BOTSTATE             = BotState.CHECKING;

    public static int[] bankIDs             = new int[] { 2012, 2015, 2019, 4456, 4458, 4457, 4459, 9710 };
    public static String[] bankNames           = new String[] { "Banker", "Counter", "Bank", "Bank chest", "Fremennik Banker" };

    public static int[] patternArray = new int[] { 0 };

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
        IVY,
        CURLY_ROOTS
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
            case MAGIC:
                //region MAGIC Logs Banks
                switch( bank )
                {
                    case Seers_Village:
                        return true;
                    default:
                        return false;
                }
                //endregion
            case YEW:
                //region YEW Logs Banks
                switch( bank )
                {
                    case Grand_Exchange:
                        return true;
                    default:
                        return false;
                }
                //endregion
            case ELDER:
                //region ELDER Logs Banks
                switch( bank )
                {
                    case Seers_Village:
                        return true;
                    default:
                        return false;
                }
                //endregion
            default:
                return false;
        }
    }
    //
    // endregion

    // region [ Bot Methods ]...

    // region Entity Searching methods...
     /**
     * This method does a search for an object with name/partial name, name, and in the area, area.
     * @param name name to look for.
     * @param area area to search in
     * @return closest object that meets the given parameters.
     */
     public static SceneObject FindNearestSceneObject( String name, Area area )
     {
         if( area == null ) return null;

         SceneObject retValue = null;
         SceneObject[] objects = SceneEntities.getLoaded();
         double curDist = 9999999d;

         for( SceneObject obj : objects )
         {
             // Check the Area for the objects
             if( area.contains(obj.getLocation() ))
             {
                 // Check the name of the objects
                 if( obj.getDefinition().getName().toLowerCase().contains(name.toLowerCase()))
                 {
                     // Now make sure the objects are valid and reachable.
                     if ( obj.validate() )
                     {
                         // Now check the actions available.
                         for( String a : obj.getDefinition().getActions())
                         {
                             if( a != null )
                             {
                                 if (a.toLowerCase().contains("chop"))
                                 {
                                     // if we get to here the object is valid, so do a final range check.
                                     double distance = obj.getLocation().distance(Players.getLocal().getLocation());

                                     if( distance < curDist )
                                     {
                                         curDist = distance;
                                         retValue = obj;
                                     }
                                 }
                             }
                         }
                     }
                 }
             }
         }

         return retValue;
     }

     public static SceneObject FindNearestSceneObject( String name, Area area, String action )
     {
         if( area == null ) return null;

         SceneObject retValue = null;
         SceneObject[] objects = SceneEntities.getLoaded();
         double curDist = 9999999d;

         for( SceneObject obj : objects )
         {
             // Check the Area for the objects
             if( area.contains(obj.getLocation() ))
             {
                 // Check the name of the objects
                 if( obj.getDefinition().getName().toLowerCase().contains(name.toLowerCase()))
                 {
                     // Now make sure the objects are valid and reachable.
                     if ( obj.validate() )
                     {
                         // Now check the actions available.
                         for( String a : obj.getDefinition().getActions())
                         {
                             if( a != null )
                             {
                                 if (a.toLowerCase().contains(action.toLowerCase()))
                                 {
                                     // if we get to here the object is valid, so do a final range check.
                                     double distance = obj.getLocation().distance(Players.getLocal().getLocation());

                                     if( distance < curDist )
                                     {
                                         curDist = distance;
                                         retValue = obj;
                                     }
                                 }
                             }
                         }
                     }
                 }
             }
         }

         return retValue;
     }

    /**
     * This method does a search for an object with name/partial name, name.
     * @param name name to look for.
     * @return closest object that meets the given parameters.
     */
    public static SceneObject FindNearestSceneObject( String name )
    {
        SceneObject retValue = null;
        SceneObject[] objects = SceneEntities.getLoaded();
        double curDist = 9999999d;

        for( SceneObject obj : objects )
        {
            // Check the name of the objects
            if( obj.getDefinition().getName().toLowerCase().contains(name.toLowerCase()))
            {
                // Now make sure the objects are valid and reachable.
                if ( obj.validate() )
                {
                    // Now check the actions available.
                    for( String a : obj.getDefinition().getActions())
                    {
                        if( a != null )
                        {
                            if (a.toLowerCase().contains("chop"))
                            {
                                // if we get to here the object is valid, so do a final range check.
                                double distance = obj.getLocation().distance(Players.getLocal().getLocation());

                                if( distance < curDist )
                                {
                                    curDist = distance;
                                    retValue = obj;
                                }
                            }
                        }
                    }
                }
            }
        }

        return retValue;
    }

    public static SceneObject FindNearestSceneObject( String name, String action )
    {
        SceneObject retValue = null;
        SceneObject[] objects = SceneEntities.getLoaded();
        double curDist = 9999999d;

        for( SceneObject obj : objects )
        {
            // Check the name of the objects
            if( obj.getDefinition().getName().toLowerCase().contains(name.toLowerCase()))
            {
                // Now make sure the objects are valid and reachable.
                if ( obj.validate() )
                {
                    // Now check the actions available.
                    for( String a : obj.getDefinition().getActions())
                    {
                        if( a != null )
                        {
                            if (a.toLowerCase().contains(action.toLowerCase()))
                            {
                                // if we get to here the object is valid, so do a final range check.
                                double distance = obj.getLocation().distance(Players.getLocal().getLocation());

                                if( distance < curDist )
                                {
                                    curDist = distance;
                                    retValue = obj;
                                }
                            }
                        }
                    }
                }
            }
        }

        return retValue;
    }

    //endregion

    /**
     * Walks along the given path.
     * @param path path to travel along.
     * @return true when path is complete.
     */
    public static boolean WalkAlongPath(Tile[] path)
    {
        boolean retValue = false;

        try
        {
            if( Walking.walkTilePath( Walking.newTilePath(path) ) )
            {
                retValue = true;
            }
            Time.sleep(Random.nextInt(2500, 5000));
        }
        catch (Exception e)
        {
            BotUtil.WriteMessage("HUGE ERROR HERE!: Error is: " + e.getMessage());
        }

        return retValue;
    }

    /**
     * Cuts down trees with the given tree name, in the specified area, using the action provided.
     * @param treeName
     * @param area
     * @param actionToUse
     */
    public static void CutLogs( String treeName, Area area, String actionToUse )
    {
        SceneObject treeToCut = null;
        BotUtil.BOTSTATE = BotUtil.BotState.CHOPPING_TREE;
        String action = "";     // Actual Action to use.

        if( area != null )
        {
            treeToCut = FindNearestSceneObject(treeName, area, actionToUse);
        }
        else
        {
            treeToCut = FindNearestSceneObject(treeName, actionToUse);
        }

        // Now that we've found a valid Tree to cut.
        if( treeToCut != null )
        {
            for( String a : treeToCut.getDefinition().getActions())
            {
                if( a != null)
                {
                    if (a.toLowerCase().contains(actionToUse.toLowerCase()))
                    {
                        action = a;
                    }
                }
            }

            // Tree is valid, so check if its on screen and player is Idle.
            // Added Player not in combat because in some places lower level players get attacked by highwaymen.
            if( treeToCut.isOnScreen() && Players.getLocal().isIdle() && !Players.getLocal().isInCombat() )
            {
                treeToCut.interact(action, treeToCut.getDefinition().getName());
                Mouse.moveRandomly(100, 400);
                Time.sleep(Random.nextInt(500, 1000));
            }
            else
            {
                // if conditions for cutting not met, then turn the camera to face the tree so we can see it.
                // and hover over it until player is Idle and not in combat.
                double dist = treeToCut.getLocation().distance(Players.getLocal().getLocation());

                if(dist > 5d)
                {
                    Camera.turnTo(treeToCut, 5);
                    Time.sleep(100, 200);
                }

                if(dist > 2d)
                {
                    Walking.findPath(treeToCut);
                }

                Time.sleep( 50 );
            }
        }
        else
        {
            BotUtil.WriteMessage("HUGE ERROR HERE!: Cannot Find the " + treeName + " Tree, Contact DamnYouRobo ASAP. CODE 0");
        }
    }

    public static void BankItems()
    {
        NPC npcbank = null;
        SceneObject sceneObjectBank = null;
        boolean useNPCBank = false;

        npcbank = FindNearestNPCBank();
        sceneObjectBank = FindNearestSceneObjectBank();

        if(npcbank != null) { useNPCBank = true; }
        else { useNPCBank = false; }

        if (useNPCBank)
        {
            if (npcbank != null)
            {
                NPCBankItems(npcbank);
            }
            else
            {
                BotUtil.WriteMessage("HUGE ERROR HERE!: Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 1 NPC");
            }
        }
        else
        {
            if (sceneObjectBank != null)
            {
                SceneObjectBankItems(sceneObjectBank);
            }
            else
            {
                BotUtil.WriteMessage("HUGE ERROR HERE!: Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 1 SceneObject");
            }
        }
    }

    // region Banking Specific Methods...

    public static void SceneObjectBankItems(SceneObject bank)
    {
        BotUtil.BOTSTATE = BotUtil.BotState.BANKING_LOGS;
        // if the bank window is open
        if (Bank.isOpen())
        {
            // deposit player inventory.
            Bank.depositInventory();
            Time.sleep(Random.nextInt(1000, 2000));
        }
        else
        {
            if( bank != null )
            {
                if( !bank.validate() )
                {
                    Walking.walk(bank);
                }
            }

            // if it isn't open, interact with the bank object.
            String[] bankActions = bank.getDefinition().getActions();       // get list of bank actions.
            String actionToPerform = "";

            for( String action : bankActions )
            {
                if( action.toLowerCase().contains("bank") )                 // check each action to make sure it includes bank.
                {
                    actionToPerform = action;                               // this is the action we want to perform.
                    break;                                                  // break the loop since no need to look further.
                }
            }

            if( bank.isOnScreen() )
            {
                bank.interact(actionToPerform, bank.getDefinition().getName()); // perform the action.
                Time.sleep(Random.nextInt(1000, 2000));                         // make the bot wait for a second or two to let the process occur.
            }
            else
            {
                Walking.walk(bank);
                Camera.turnTo(bank, 5);
                Time.sleep(150, 250);
            }
        }
    }

    public static SceneObject FindNearestSceneObjectBank()
    {
        SceneObject retValue = null;
        SceneObject[] objects = new SceneObject[bankNames.length + 1];

        for(int i = 0; i< bankNames.length; i++ )
        {
            SceneObject obj = FindNearestSceneObject(bankNames[i], "bank");
            objects[i] = obj;
        }

        double curDist = 9999999d;

        for( SceneObject obj : objects )
        {
            if( obj != null )
            {
                double distance = obj.getLocation().distance(Players.getLocal().getLocation());
                if (distance < curDist)
                {
                    curDist = distance;
                    retValue = obj;
                }
            }
        }

        return retValue;
    }

    public static void NPCBankItems( NPC npcbank )
    {
        BotUtil.BOTSTATE = BotUtil.BotState.BANKING_LOGS;
        // if the bank window is open
        if (Bank.isOpen())
        {
            // deposit player inventory.
            Bank.depositInventory();
            Time.sleep(Random.nextInt(250, 500));
            Bank.close();
        }
        else
        {
            if( npcbank != null )
            {
                if( !npcbank.validate() )
                {
                    Walking.walk(npcbank);
                }
            }

            // if it isn't open, interact with the bank object.
            String[] bankActions = npcbank.getActions();                    // get list of bank actions.
            String actionToPerform = "";

            for( String action : bankActions )
            {
                if( action.toLowerCase().contains("bank") )                 // check each action to make sure it includes bank.
                {
                    actionToPerform = action;                               // this is the action we want to perform.
                    break;                                                  // break the loop since no need to look further.
                }
            }

            if( npcbank.isOnScreen() )
            {
                npcbank.interact(actionToPerform, npcbank.getName());           // perform the action.
                Time.sleep(250, 1000);                                          // make the bot wait for a second or two to let the process occur.
            }
            else
            {
                Walking.walk(npcbank);
                Camera.turnTo(npcbank, 5);
                Time.sleep(150, 250);
            }
        }
    }

    public static NPC FindNearestNPCBank()
    {
        NPC     retValue = null;
        NPC[]   objects = new NPC[bankNames.length + 1];

        for(int i = 0; i< bankNames.length; i++ )
        {
            // region [ Find closest NPC banker, with given bankName[i] ]...

            NPC obj = null;

            NPC[] npcs = NPCs.getLoaded();
            double curDist = 9999999d;

            for( NPC n : npcs )
            {
                // Check the name of the objects
                if( n.getName().toLowerCase().contains(bankNames[i].toLowerCase()))
                {
                    // Now make sure the objects are valid and reachable.
                    if ( n.validate() && n.canReach() )
                    {
                        // Now check the actions available.
                        for( String a : n.getActions())
                        {
                            if( a != null )
                            {
                                if (a.toLowerCase().contains("bank"))
                                {
                                    // if we get to here the object is valid, so do a final range check.
                                    double distance = n.getLocation().distance(Players.getLocal().getLocation());

                                    if( distance < curDist )
                                    {
                                        curDist = distance;
                                        obj = n;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // endregion
            objects[i] = obj;
        }

        double curDist = 9999999d;

        for( NPC obj : objects )
        {
            if( obj != null )
            {
                double distance = obj.getLocation().distance(Players.getLocal().getLocation());
                if (distance < curDist)
                {
                    curDist = distance;
                    retValue = obj;
                }
            }
        }

        return retValue;
    }

    // endregion

    public static void DropLogs(int[] logsIDs)
    {
        BotUtil.BOTSTATE = BotUtil.BotState.DROPPING_LOGS;
        DroppingIventory = true;

        if( Inventory.getCount(logsIDs) > 0 )
        {
            Item i = Inventory.getItem(logsIDs);
            i.interact("Drop");
            Time.sleep(Random.nextInt(500, 1000));
        }
        else
        {
            DroppingIventory = false;
        }
    }

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
