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
public class CutOakTrees extends Node implements Task
{
    public BotUtil.PossibleLogs myLogs  = BotUtil.PossibleLogs.OAK;
    public final String treeName        = "oak";
    public final int[] logsIDs          = new int[] { 1521 };
    private String actionToUse          = "chop";

    public Area bankAreaToUse           = null;
    public Area treeAreaToUse           = null;
    public Tile[] pathToBankToUse       = null;
    public Tile[] pathToTreeToUse       = null;

    //region [ Draynor Data ]...
    private final Area TreeArea_Draynor = new Area(
            new Tile(3126, 3260, 0),
            new Tile(3116, 3260, 0),
            new Tile(3106, 3258, 0),
            new Tile(3101, 3249, 0),
            new Tile(3098, 3247, 0),
            new Tile(3099, 3238, 0),
            new Tile(3109, 3234, 0),
            new Tile(3132, 3232, 0),
            new Tile(3134, 3239, 0),
            new Tile(3133, 3259, 0)
    );

    private final Area BankArea_Draynor = new Area(
            new Tile(3087, 3249, 0),
            new Tile(3086, 3239, 0),
            new Tile(3099, 3239, 0),
            new Tile(3099, 3248, 0)
    );

    private final Tile[] PathToTree_Draynor = new Tile[] {
            new Tile(3091, 3245, 0),
            new Tile(3101, 3249, 0),
            new Tile(3111, 3248, 0),
            new Tile(3118, 3245, 0)
    };

    private final Tile[] PathToBank_Draynor = new Tile[] {
            new Tile(3120, 3253, 0),
            new Tile(3110, 3251, 0),
            new Tile(3100, 3250, 0),
            new Tile(3094, 3248, 0),
            new Tile(3091, 3241, 0)
    };
    //endregion

    //region [ Clan Camp Data ]...

    private final Area TreeArea_ClanCamp = new Area(
            new Tile(2954, 3298, 0),
            new Tile(2952, 3308, 0),
            new Tile(2938, 3310, 0),
            new Tile(2926, 3308, 0),
            new Tile(2921, 3296, 0),
            new Tile(2939, 3292, 0),
            new Tile(2944, 3291, 0),
            new Tile(2953, 3286, 0)
    );

    private final Area BankArea_ClanCamp = new Area(
            new Tile(2957, 3305, 0),
            new Tile(2947, 3305, 0),
            new Tile(2948, 3289, 0),
            new Tile(2958, 3289, 0)
    );

    private final Tile[] PathToTree_ClanCamp = new Tile[] {
        new Tile(2957, 3293, 0),
        new Tile(2947, 3295, 0),
        new Tile(2942, 3301, 0)
    };

    private final Tile[] PathToBank_ClanCamp = new Tile[] {
        new Tile(2943, 3300, 0),
        new Tile(2948, 3292, 0),
        new Tile(2955, 3296, 0)
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
        if( BotUtil.BANK_LOGS )
        {
            DetermineTreeAreaAndPathData();
        }

        // If the player inventory is not full.
        if( Inventory.getCount() < 28 && !BotUtil.DroppingIventory )
        {
            // We want to cut logs.
            // Is the player in the Tree Area
            if( treeAreaToUse != null )
            {
                if (treeAreaToUse.contains(Players.getLocal().getLocation()))
                {
                    // if they are, start chopping tree's
                    BotUtil.CutLogs(treeName, treeAreaToUse, actionToUse);
                }
                else
                {
                    // If they aren't, start walking there.
                    BotUtil.WalkAlongPath(pathToTreeToUse);
                }
            }
            else
            {
                BotUtil.CutLogs(treeName, null, actionToUse);       // When not using an area, pass null for that parameter.
            }
        }
        else
        {
            if( BotUtil.BANK_LOGS )
            {
                // First Check they are at the bank?
                if (bankAreaToUse.contains(Players.getLocal().getLocation()))
                {
                    BotUtil.BankItems();
                }
                else
                {
                    // if not, time to walk there.
                    BotUtil.WalkAlongPath(pathToBankToUse);
                }
            }
            else
            {
                BotUtil.DropLogs(logsIDs);
            }
        }
    }

    public void DetermineTreeAreaAndPathData()
    {
        switch(BotUtil.CHOSENBANK)
        {
            case Clan_Camp:
                bankAreaToUse       = BankArea_ClanCamp;
                treeAreaToUse       = TreeArea_ClanCamp;
                pathToBankToUse     = PathToBank_ClanCamp;
                pathToTreeToUse     = PathToTree_ClanCamp;
                break;
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

}
