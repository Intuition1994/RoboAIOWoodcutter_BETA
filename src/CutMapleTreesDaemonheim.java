import com.epicbot.api.concurrent.Task;
import com.epicbot.api.concurrent.node.Node;
import com.epicbot.api.rs3.methods.Walking;
import com.epicbot.api.rs3.methods.interactive.NPCs;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.node.SceneEntities;
import com.epicbot.api.rs3.methods.tab.Skills;
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

/**
 * Created by jt13602 on 25/03/2015.
 */
public class CutMapleTreesDaemonheim extends Node implements Task
{

    public BotUtil.PossibleLogs myLogs = BotUtil.PossibleLogs.MAPLE;
    public final int[] treeIDs             = new int[] { 51843 };
    public final int[] logsIDs             = new int[] { 1517, 1518 };

    private boolean dropping = false;
    private NPC npcbank = null;
    private SceneObject sceneObjectBank = null;
    private boolean useNPCBank = false;
    private SceneObject treeToCut = null;

    private String actionToUse = "";

    private boolean walkingToBanker = false;

    // region [ Paths and Areas ]...

    private final Tile[] pathToDungeonEntrance = new Tile[]
    {
            new Tile(3449, 3707, 0),
            new Tile(3452, 3697, 0),
            new Tile(3462, 3695, 0),
            new Tile(3472, 3691, 0),
            new Tile(3482, 3687, 0),
            new Tile(3494, 3687, 0),
            new Tile(3502, 3694, 0),
            new Tile(3500, 3684, 0),
            new Tile(3494, 3676, 0),
            new Tile(3492, 3666, 0),
            new Tile(3502, 3662, 0),
            new Tile(3512, 3665, 0)
    };

    private final Tile[] pathToBanker = new Tile[]
    {
            new Tile(3446, 3700, 0),
            new Tile(3449, 3702, 0),
            new Tile(3450, 3707, 0),
            new Tile(3450, 3716, 0)
    };

    private final Area teleportationArea = new Area(
            new Tile(3447, 3704, 0),
            new Tile(3432, 3704, 0),
            new Tile(3432, 3691, 0),
            new Tile(3449, 3691, 0),
            new Tile(3460, 3703, 0)
    );

    private final int ringID = 15707;

    public Area bankAreaToUse = null;
    public Area treeAreaToUse = null;

    private final Area doorEntranceArea = null;

    // endregion

    // --------------------------
    //  Task Execution condition
    // --------------------------
    @Override
    public boolean shouldExecute()
    {
        boolean retVal = false;

        if( !BotUtil.BOT_IS_RUNNING ) return false;

        if( BotUtil.CHOSENLOGS == myLogs && BotUtil.BANK_LOGS )
        {
            if( Players.getLocal() != null )
            {
                if( BotUtil.CHOSENBANK == BotUtil.Banks.Daemonheim )
                {
                    if( Skills.Skill.DUNGEONEERING.getCurrentLevel() < 30 )
                    {
                        BotUtil.WriteMessage("You do not have a high enough level to enter this resource dungeon.");
                        BotUtil.BOT_IS_RUNNING = false;
                        return false;
                    }

                    retVal = true;
                }
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

        // If the player inventory is not full.
        if( Inventory.getCount() < 28)
        {
            walkingToBanker = false;
            if (treeAreaToUse.contains(Players.getLocal().getLocation()))
            {
                // if they are, start chopping tree's
                CutLogs();
            }
            else
            {
                if( doorEntranceArea.contains(Players.getLocal().getLocation()) )
                {
                    // At the dungeon entrance.
                    SceneObject door = SceneEntities.getNearest("Mysterious entrance");
                    door.interact("Enter");
                }
                else
                {
                    Walking.walkTilePath(Walking.newTilePath(pathToDungeonEntrance));
                }
            }
        }
        else
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
                if(!walkingToBanker)
                {
                    if(Players.getLocal().isIdle())
                    {
                        Inventory.getItem(ringID).interact("Teleport to Daemonheim");
                        Time.sleep(2000);
                    }

                    if(teleportationArea.contains(Players.getLocal().getLocation()))
                    {
                        walkingToBanker = true;
                    }
                }
                else
                {
                    if( bankAreaToUse.contains(Players.getLocal().getLocation()))
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
                        Walking.walkTilePath(Walking.newTilePath(pathToBanker));
                        Time.sleep(1000, 2500);
                    }
                }
            }
        }
    }


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
}
