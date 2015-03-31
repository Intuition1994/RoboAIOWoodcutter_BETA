import com.epicbot.api.concurrent.Task;
import com.epicbot.api.concurrent.node.Node;
import com.epicbot.api.rs3.methods.Walking;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.node.SceneEntities;
import com.epicbot.api.rs3.methods.widget.Camera;
import com.epicbot.api.rs3.wrappers.Area;
import com.epicbot.api.rs3.wrappers.Tile;
import com.epicbot.api.rs3.wrappers.node.SceneObject;
import com.epicbot.api.util.Random;
import com.epicbot.api.util.Time;

/**
 * Created by jt13602 on 27/03/2015.
 */
public class CutIvy extends Node implements Task
{

    public BotUtil.PossibleLogs myLogs = BotUtil.PossibleLogs.IVY;
    public String ivyName = "Ivy";      // When cutting Ivy we will use the name only instead of ID's
    private SceneObject ivyToCut = null;
    private SceneObject _ivyToCut = null;   // last Ivy to cut.
    private String actionToUse = "Chop";

    int idleCounter = 20;    // Using this to prevent the bot clicking on Ivy too much.
    // idleCounter starts high to meet the interaction for chopping conditions. Before being set to zero.

    // NOTE: In this particular Case, Ivy does not use any of this data except the Tree Area. Since it does not bank, the bot aims
    // to maintain cutting Ivy in the same area.
    public Area ivyAreaToUse = null;

    private boolean _started = false;
    private boolean started = false;

    //region [ Ivy Chopping Area's ]...

    public final Area VarrockCastleArea = new Area(
            new Tile(3201, 3507, 0),
            new Tile(3208, 3497, 0),
            new Tile(3221, 3497, 0),
            new Tile(3227, 3506, 0)
    );

    private final Area FaladorSouthArea = new Area(
            new Tile(3025, 3329, 0),
            new Tile(3025, 3318, 0),
            new Tile(3062, 3318, 0),
            new Tile(3062, 3331, 0)
    );

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
                started = true;
            }
        }

        return retVal;
    }

    @Override
    public void run()
    {
        if( _started != started )
        {
            _started = started;

            if(_started)
            {
                InitialSetup();
            }
        }

        ivyToCut = FindClosestTree();

        if( ivyToCut != null )
        {
            // We want to cut ivy.
            // Is the player in the Tree Area
            if (ivyAreaToUse.contains(Players.getLocal().getLocation()))
            {
                // if they are, start chopping ivy
                CutLogs();
            }
            else
            {
                // if the player moves out of the area for any reason. bring them back!
                MovePlayerBackToArea();
            }
        }
    }

    public SceneObject FindClosestTree()
    {
        SceneObject[] possibletrees = SceneEntities.getLoaded();
        SceneObject retVal = null;
        double curDist = 99999999d;

        for ( SceneObject obj : possibletrees ) // for each Object in the trees array.
        {
            String name = obj.getDefinition().getName();
            String[] actions = obj.getDefinition().getActions();

            if( ivyAreaToUse != null )
            {
                if( !ivyAreaToUse.contains(obj.getLocation()))
                {
                    // We are using the tree Area to find trees,
                    // if the code reaches this point, then the obj being checked is not in the
                    // tree area being used.
                    // therefore continue to the next object.
                    continue;
                }
                // if not using an area, this section of code is skipped.
            }

            if( name.toLowerCase().contains("ivy") )
            {
                if( actions != null ) // make sure the actions array exists for this object.
                {
                    if( actions.length > 0) // Check it has at least one element. (i.e there is something to check.)
                    {
                        // filter out all items that do not have "oak" in their name.
                        for (String s : actions)
                        {
                            if( s != null ) // make sure the string exists?????
                            {
                                if (s.toLowerCase().contains("chop"))
                                {
                                    // Filter out any objects that do not allow the player to chop at them.
                                    double dist = obj.getLocation().distance(Players.getLocal().getLocation());

                                    if( dist < curDist )    // check its closer than the current closest.
                                    {
                                        curDist = dist;     // set new distance for closest.
                                        retVal = obj;       // for now just accept the last one found.
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        return retVal;
    }

    public void InitialSetup()
    {
        DetermineTreeAreaAndPathData();
    }

    public void DetermineTreeAreaAndPathData()
    {
        // Create an array of all potential Area's for chopping Ivy
        Area[] potentialAreas = new Area[]
        {
            VarrockCastleArea,
            FaladorSouthArea
        };

        // find the one the player is in.
        for( Area a : potentialAreas )
        {
            if( a.contains(Players.getLocal().getLocation()))
            {
                ivyAreaToUse = a;
            }
        }
    }

    public void MovePlayerBackToArea()
    {
        Tile[] verts = ivyAreaToUse.getAreaVertices();
        Tile closest = null;
        double dist = 99999999999d;

        for( Tile t : verts )
        {
            if(t.getLocation().distance(Players.getLocal().getLocation()) < dist)
            {
                dist = t.getLocation().distance(Players.getLocal().getLocation());
                closest = t;
            }
        }

        if( Walking.walk( closest ) )
        {
            // WOOOP I think this means the player is there.
        }
    }

    public void CutLogs()
    {
        BotUtil.BOTSTATE = BotUtil.BotState.CHOPPING_TREE;

        if( _ivyToCut != ivyToCut )
        {
            if (_ivyToCut == null)
            {
                idleCounter = 0;
            }

            _ivyToCut = ivyToCut;
        }

        // Now that we've found a valid Tree to cut.
        if( ivyToCut != null )
        {
            // Tree is valid, so check if its on screen and player is Idle.
            // Added Player not in combat because in some places lower level players get attacked by highwaymen.
            if( ivyToCut.isOnScreen() && !Players.getLocal().isInCombat() )
            {
                if( Players.getLocal().isIdle() && idleCounter > Random.nextInt(10, 15))
                {
                    idleCounter = 0;
                    ivyToCut.interact(actionToUse, ivyToCut.getDefinition().getName());
                    Time.sleep(Random.nextInt(500, 1000));
                }
                else
                {
                    if (Players.getLocal().isIdle())
                    {
                        idleCounter += 1;
                        Time.sleep(250, 500);

                    }
                }
            }
            else
            {
                // if conditions for cutting not met, then turn the camera to face the tree so we can see it.
                // and hover over it until player is Idle and not in combat.

                if(ivyToCut.getLocation().distance(Players.getLocal().getLocation()) > 2d)
                {
                    Camera.turnTo(ivyToCut);
                    Walking.findPath(ivyToCut);
                }
            }
        }
        else
        {
            BotUtil.WriteMessage("HUGE ERROR HERE!: " + this.getClass().getName() + " Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 0" );
        }
    }
}
