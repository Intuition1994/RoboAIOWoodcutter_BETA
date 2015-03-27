import com.epicbot.api.concurrent.Task;
import com.epicbot.api.concurrent.node.Node;
import com.epicbot.api.rs3.methods.Walking;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.node.SceneEntities;
import com.epicbot.api.rs3.methods.tab.inventory.Inventory;
import com.epicbot.api.rs3.methods.widget.Bank;
import com.epicbot.api.rs3.methods.widget.Camera;
import com.epicbot.api.rs3.wrappers.Area;
import com.epicbot.api.rs3.wrappers.Tile;
import com.epicbot.api.rs3.wrappers.map.TilePath;
import com.epicbot.api.rs3.wrappers.node.Item;
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

    private boolean dropping = false;
    private SceneObject bank = null;

    // NOTE: In this particular Case, Ivy does not use any of this data except the Tree Area. Since it does not bank, the bot aims
    // to maintain cutting Ivy in the same area.
    public Area bankAreaToUse = null;
    public Area treeAreaToUse = null;
    public Tile[] pathToBankToUse = null;
    public Tile[] pathToTreeToUse = null;

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

        // We want to cut ivy.
        // Is the player in the Tree Area
        if (treeAreaToUse.contains(Players.getLocal().getLocation()))
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
                treeAreaToUse = a;
            }
        }
    }

    public void MovePlayerBackToArea()
    {
        Tile[] verts = treeAreaToUse.getAreaVertices();
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
        if( !treeAreaToUse.contains(Players.getLocal().getLocation()) )
        {
            MovePlayerBackToArea();
            return;
        }

        BotUtil.BOTSTATE = BotUtil.BotState.CHOPPING_TREE;
        SceneObject ivyToCut = SceneEntities.getNearest(ivyName);
        String actionToUse = "";

        for( String action : ivyToCut.getDefinition().getActions() )
        {
            if (action.toLowerCase().contains("chop"))
            {
                actionToUse = action;
                break;
            }
        }

        // Now that we've found a valid Ivy to cut.
        if( ivyToCut != null )
        {
            if( ivyToCut.validate() )
            {
                // Tree is valid, so check if its on screen and player is Idle.
                // Added Player not in combat because in some places lower level players get attacked by highwaymen.
                if (ivyToCut.isOnScreen() && Players.getLocal().isIdle() && !Players.getLocal().isInCombat())
                {
                    ivyToCut.interact(actionToUse, ivyToCut.getDefinition().getName());
                    Time.sleep(2500, 5000);
                }
                else
                {
                    // if conditions for cutting not met, then turn the camera to face the tree so we can see it.
                    // and hover over it until player is Idle and not in combat.
                    Camera.turnTo(ivyToCut, 20);
                    ivyToCut.hover();
                }
            }
            else
            {
                Walking.walk( ivyToCut );
                Time.sleep( 500, 500 );
            }
        }
        else
        {
            BotUtil.WriteMessage("HUGE ERROR HERE!: " + this.getClass().getName() + " Cannot Find the Bank, Contact DamnYouRobo ASAP. CODE 0" );
        }
    }
}
