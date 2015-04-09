import com.epicbot.api.concurrent.Task;
import com.epicbot.api.concurrent.node.Node;
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

/**
 * Created by jt13602 on 25/03/2015.
 */
public class CutWillowTrees extends Node implements Task
{

    public BotUtil.PossibleLogs myLogs = BotUtil.PossibleLogs.WILLOW;
    public final int[] treeIDs             = new int[] { 38616, 38627, 58006 };
    public final int[] logsIDs             = new int[] { 1519, 1520 };

    private boolean dropping = false;
    private NPC npcbank = null;
    private SceneObject sceneObjectBank = null;
    private boolean useNPCBank = false;
    private SceneObject treeToCut = null;

    private String actionToUse = "";

    public Area bankAreaToUse = null;
    public Area treeAreaToUse = null;
    public Tile[] pathToBankToUse = null;
    public Tile[] pathToTreeToUse = null;

    //region [ Draynor Data ]...

    private final Area BankArea_Draynor = new Area(
            new Tile(3097, 3249, 0),
            new Tile(3087, 3249, 0),
            new Tile(3086, 3249, 0),
            new Tile(3086, 3238, 0),
            new Tile(3099, 3238, 0)
    );
    private final Area TreeArea_Draynor = new Area(
            new Tile(3077, 3244, 0),
            new Tile(3087, 3244, 0),
            new Tile(3092, 3235, 0),
            new Tile(3093, 3225, 0),
            new Tile(3093, 3215, 0),
            new Tile(3086, 3223, 0),
            new Tile(3081, 3232, 0),
            new Tile(3077, 3242, 0)
    );

    private final Tile[] PathToBank_Draynor = new Tile[] {
            new Tile(3088, 3232, 0),
            new Tile(3084, 3241, 0),
            new Tile(3085, 3247, 0),
            new Tile(3092, 3245, 0),
            new Tile(3093, 3243, 0)
    };

    private final Tile[] PathToTree_Draynor = new Tile[] {
            new Tile(3091, 3246, 0),
            new Tile(3086, 3249, 0),
            new Tile(3085, 3241, 0),
            new Tile(3087, 3233, 0)
    };

    //endregion

    // --------------------------
    //  Task Execution condition
    // --------------------------
    @Override
    public boolean shouldExecute()
    {
        boolean retVal = false;

        if( !BotUtil.BOT_IS_RUNNING ) return false;

        if( BotUtil.CHOSENLOGS == myLogs )
        {
            if( Players.getLocal() != null )
            {
                retVal = true;
            }
        }

        return retVal;
    }

    @Override
    public void run()
    {
        treeToCut = FindClosestTree();
        npcbank = FindNearestNPCBanker();
        sceneObjectBank = FindNearestSceneObjectBanker();

        if(npcbank != null) { useNPCBank = true; }
        else { useNPCBank = false; }

        if( BotUtil.BANK_LOGS )
        {
            DetermineTreeAreaAndPathData();
        }

        // If the player inventory is not full.
        if( Inventory.getCount() < 28 && !dropping )
        {
            // We want to cut logs.
            // Is the player in the Tree Area
            if( treeAreaToUse != null )
            {
                if (treeAreaToUse.contains(Players.getLocal().getLocation()))
                {
                    // if they are, start chopping tree's
                    CutLogs();
                }
                else
                {
                    // If they aren't, start walking there.
                    WalkToTrees();
                }
            }
            else
            {
                CutLogs();
            }
        }
        else
        {
            if( BotUtil.BANK_LOGS )
            {
                // First Check they are at the bank?
                if (bankAreaToUse.contains(Players.getLocal().getLocation()))
                {
                    if (useNPCBank)
                    {
                        if (npcbank != null)
                        {
                            NPCBankItems();
                        }
                        else
                        {
                            BotUtil.WriteMessage("HUGE ERROR HERE!: " + this.getClass().getName() + " Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 1 NPC");
                        }
                    }
                    else
                    {
                        if (sceneObjectBank != null)
                        {
                            SceneObjectBankItems();
                        }
                        else
                        {
                            BotUtil.WriteMessage("HUGE ERROR HERE!: " + this.getClass().getName() + " Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 1 SceneObject");
                        }
                    }
                }
                else
                {
                    // if not, time to walk there.
                    WalkToBank();
                }
            }
            else
            {
                DropLogs();
            }
        }
    }

    public void DetermineTreeAreaAndPathData()
    {
        switch(BotUtil.CHOSENBANK)
        {
            case Draynor:
                bankAreaToUse       = BankArea_Draynor;
                treeAreaToUse       = TreeArea_Draynor;
                pathToBankToUse     = PathToBank_Draynor;
                pathToTreeToUse     = PathToTree_Draynor;
                break;
            default:
                bankAreaToUse       = null;
                treeAreaToUse       = null;
                pathToBankToUse     = null;
                pathToTreeToUse     = null;
                break;
        }
    }

    //region [ Walking Methods ] ...

    public void WalkToTrees()
    {
        try
        {
            BotUtil.BOTSTATE = BotUtil.BotState.TRAVELING_TO_TREE;
            TilePath path = Walking.newTilePath(pathToTreeToUse);
            Walking.walkTilePath(path);
            Time.sleep(Random.nextInt(2500, 5000));
        }
        catch (Exception e)
        {
            BotUtil.WriteMessage("HUGE ERROR HERE!: " + this.getClass().getName() + " Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 2" );
            BotUtil.WriteMessage("HUGE ERROR HERE!: Error is: " + e.getMessage() );
        }
    }

    public void WalkToBank()
    {
        try
        {
            BotUtil.BOTSTATE = BotUtil.BotState.TRAVELING_TO_BANK;
            TilePath path = Walking.newTilePath(pathToBankToUse);
            Walking.walkTilePath(path);
            Time.sleep(Random.nextInt(2500, 5000));
        }
        catch (Exception e)
        {
            BotUtil.WriteMessage("HUGE ERROR HERE!: " + this.getClass().getName() + " Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 3" );
            BotUtil.WriteMessage("HUGE ERROR HERE!: Error is: " + e.getMessage() );
        }
    }

    //endregion

    public void CutLogs()
    {
        BotUtil.BOTSTATE = BotUtil.BotState.CHOPPING_TREE;

        // Now that we've found a valid Tree to cut.
        if( treeToCut != null )
        {
            // Tree is valid, so check if its on screen and player is Idle.
            // Added Player not in combat because in some places lower level players get attacked by highwaymen.
            if( treeToCut.isOnScreen() && Players.getLocal().isIdle() && !Players.getLocal().isInCombat() )
            {
                treeToCut.interact(actionToUse, treeToCut.getDefinition().getName());
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
            BotUtil.WriteMessage("HUGE ERROR HERE!: " + this.getClass().getName() + " Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 0" );
        }
    }

    public SceneObject FindClosestTree()
    {
        SceneObject[] trees = SceneEntities.getLoaded(treeIDs);
        SceneObject retVal = null;
        double curDist = 99999999d;

        for ( SceneObject obj : trees )
        {
            if (obj != null)
            {
                if (obj.canReach())
                {
                    if (treeAreaToUse != null)
                    {
                        if (!treeAreaToUse.contains(obj.getLocation()))
                        {
                            continue;
                        }
                    }

                    if (Players.getLocal().getLocation().distance(obj.getLocation()) < curDist)
                    {
                        for (String action : obj.getDefinition().getActions())
                        {
                            if (action.toLowerCase().contains("chop"))
                            {
                                // Tree is valid. Therefore accept this tree.
                                curDist = (float) Players.getLocal().getLocation().distance(obj.getLocation());
                                retVal = obj;
                                actionToUse = action;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return retVal;
    }

    //region [ Banking Methods ] ...

    public void NPCBankItems()
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

            npcbank.interact(actionToPerform, npcbank.getName());           // perform the action.
            Time.sleep(250, 1000);                                          // make the bot wait for a second or two to let the process occur.
        }
    }

    public void SceneObjectBankItems()
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
            if( sceneObjectBank != null )
            {
                if( !sceneObjectBank.validate() )
                {
                    Walking.walk(sceneObjectBank);
                }
            }

            // if it isn't open, interact with the bank object.
            String[] bankActions = sceneObjectBank.getDefinition().getActions();       // get list of bank actions.
            String actionToPerform = "";

            for( String action : bankActions )
            {
                if( action.toLowerCase().contains("bank") )                 // check each action to make sure it includes bank.
                {
                    actionToPerform = action;                               // this is the action we want to perform.
                    break;                                                  // break the loop since no need to look further.
                }
            }

            sceneObjectBank.interact(actionToPerform, sceneObjectBank.getDefinition().getName()); // perform the action.
            Time.sleep(Random.nextInt(1000, 2000));                         // make the bot wait for a second or two to let the process occur.
        }
    }

    public NPC FindNearestNPCBanker()
    {
        NPC retValue = null;
        NPC[] bankersByID = NPCs.getLoaded(BotUtil.bankIDs);
        double curDist = 99999999999d;

        NPC bankerByID = null;

        for( NPC obj : bankersByID )
        {
            if( obj == null ) continue;
            double dist = obj.getLocation().distance(Players.getLocal().getLocation());

            // if the object is valid AND we are able to reach it AND its closer to us than the one we have already checked.
            if ( dist < curDist)
            {
                // store it ready to return
                curDist = dist;
                bankerByID = obj;
            }
        }
        // Now that the loop is over we have found the closest, reachable, valid NPC bank.

        NPC bankerByName = NPCs.getNearest(BotUtil.bankNames);

        if( bankerByName != null)
        {
            retValue = bankerByName;
        }

        if( bankerByID != null )
        {
            retValue = bankerByID;
        }

        // return it.
        return retValue;
    }

    public SceneObject FindNearestSceneObjectBanker()
    {
        SceneObject retValue = null;
        SceneObject[] bankersByID = SceneEntities.getLoaded(BotUtil.bankIDs);
        double curDist = 99999999999d;

        SceneObject bankerByID = null;

        for( SceneObject obj : bankersByID )
        {
            if( obj == null ) continue;
            double dist = obj.getLocation().distance(Players.getLocal().getLocation());

            // if the object is valid AND we are able to reach it AND its closer to us than the one we have already checked.
            if ( dist < curDist)
            {
                // store it ready to return
                curDist = dist;
                bankerByID = obj;
            }
        }
        // Now that the loop is over we have found the closest, reachable, valid NPC bank.

        SceneObject bankerByName = SceneEntities.getNearest(BotUtil.bankNames);

        if( bankerByName != null)
        {
            retValue = bankerByName;
        }

        if( bankerByID != null )
        {
            retValue = bankerByID;
        }

        // return it.
        return retValue;
    }
    //endregion

    public void DropLogs()
    {
        BotUtil.BOTSTATE = BotUtil.BotState.DROPPING_LOGS;
        dropping = true;

        if( Inventory.getCount() > 0 )
        {
            Item i = Inventory.getItem(logsIDs);
            i.interact("Drop");
            Time.sleep(Random.nextInt(500, 1000));
        }
        else
        {
            dropping = false;
        }
    }
}
