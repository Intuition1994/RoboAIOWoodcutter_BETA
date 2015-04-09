import com.epicbot.api.concurrent.Task;
import com.epicbot.api.concurrent.node.Node;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.tab.inventory.Inventory;
import com.epicbot.api.rs3.methods.web.web.data.teleports.Teleports;
import com.epicbot.api.rs3.methods.web.web.data.teleports.types.Lodestone;
import com.epicbot.api.rs3.methods.widget.Lodestones;
import com.epicbot.api.rs3.wrappers.Area;
import com.epicbot.api.rs3.wrappers.Tile;
import com.epicbot.api.rs3.wrappers.node.SceneObject;
import com.epicbot.api.util.Random;
import com.epicbot.api.util.Time;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jt13602 on 25/03/2015.
 */
public class CutElderTrees extends Node implements Task
{
    private String FILEPATH = "C:\\Temp\\RoboAIOWoodcutterElderPattern.txt";
    private Charset ENCODING = StandardCharsets.UTF_8;


    public BotUtil.PossibleLogs     myLogs              = BotUtil.PossibleLogs.ELDER;
    public final String             treeName            = "elder";
    public final int[]              logsIDs             = new int[] { 1513 };
    private String                  actionToUse         = "chop";

    public Area                     lodeAreaToUse       = null;
    public Area                     bankAreaToUse       = null;
    public Area                     treeAreaToUse       = null;
    public Tile[]                   pathToBankToUse     = null;
    public Tile[]                   pathToTreeToUse     = null;
    public Tile[]                   lodeToBankToUse     = null;
    public Tile[]                   lodeToTreeToUse     = null;

    public boolean                  running             = false;
    public boolean                  _running            = false;

    public enum Locations
    {
        SEERS_VILLAGE,
        YANILlE,
        DRAYNOR,
        EDGEVILLE
    }

    public Locations currentLocation = null;
    public int curSelection = -1;                   // CURRENT Locations enum selection.
    public int patternIndex = 0;
    public int _patternIndex = 0;
    public boolean curLoactionFinished = false;
    public boolean teleporting = false;
    public boolean walkingFromLode = false;

    //region [ Seer's Village Data ]...

    private final Area TreeArea_SeersVillage = new Area(
            new Tile(2712, 3415, 0),
            new Tile(2706, 3413, 0),
            new Tile(2706, 3388, 0),
            new Tile(2720, 3390, 0),
            new Tile(2731, 3410, 0)
    );

    private final Area BankArea_SeersVillage = new Area(
            new Tile(2719, 3498, 0),
            new Tile(2718, 3485, 0),
            new Tile(2732, 3485, 0),
            new Tile(2734, 3499, 0)
    );

    private final Tile[] PathToTree_SeersVillage = new Tile[] {
            new Tile(2726, 3490, 0),
            new Tile(2727, 3479, 0),
            new Tile(2727, 3468, 0),
            new Tile(2727, 3457, 0),
            new Tile(2727, 3445, 0),
            new Tile(2726, 3434, 0),
            new Tile(2725, 3423, 0),
            new Tile(2723, 3413, 0),
            new Tile(2716, 3405, 0)
    };

    private final Tile[] PathToBank_SeersVillage = new Tile[] {
            new Tile(2714, 3403, 0),
            new Tile(2716, 3414, 0),
            new Tile(2718, 3424, 0),
            new Tile(2720, 3435, 0),
            new Tile(2724, 3445, 0),
            new Tile(2727, 3455, 0),
            new Tile(2728, 3465, 0),
            new Tile(2728, 3475, 0),
            new Tile(2727, 3485, 0),
            new Tile(2726, 3491, 0)
    };

    private final Tile[] LodeToBank_SeersVillage = new Tile[]
    {
            new Tile(2690, 3485, 0),
            new Tile(2700, 3485, 0),
            new Tile(2711, 3485, 0),
            new Tile(2721, 3485, 0),
            new Tile(2725, 3491, 0)
    };

    private final Tile[] LodeToTree_SeersVillage = new Tile[] {
            new Tile(2688, 3482, 0),
            new Tile(2686, 3472, 0),
            new Tile(2685, 3462, 0),
            new Tile(2685, 3452, 0),
            new Tile(2690, 3442, 0),
            new Tile(2698, 3434, 0),
            new Tile(2705, 3426, 0),
            new Tile(2712, 3418, 0),
            new Tile(2718, 3409, 0),
            new Tile(2723, 3400, 0),
            new Tile(2731, 3394, 0)
    };

    private final Area Lodestone_SeersVillage = new Area(
            new Tile(2688, 3491, 0),
            new Tile(2681, 3483, 0),
            new Tile(2687, 3475, 0),
            new Tile(2697, 3478, 0),
            new Tile(2698, 3488, 0)
    );
    //endregion

    // region [ Yanille Data ]...

    private final Area Lodestone_Yanille = new Area(
            new Tile(2529, 3100, 0),
            new Tile(2519, 3095, 0),
            new Tile(2525, 3086, 0),
            new Tile(2535, 3088, 0),
            new Tile(2540, 3095, 0)
    );

    private final Area BankArea_Yanille = new Area(
            new Tile(2606, 3099, 0),
            new Tile(2608, 3085, 0),
            new Tile(2621, 3086, 0),
            new Tile(2621, 3102, 0)
    );

    private final Area TreeArea_Yanille = new Area(
            new Tile(2569, 3074, 0),
            new Tile(2584, 3074, 0),
            new Tile(2589, 3061, 0),
            new Tile(2577, 3055, 0),
            new Tile(2560, 3063, 0)
    );

    private final Tile[] LodeToTree_Yanille = new Tile[] {
            new Tile(2527, 3091, 0),
            new Tile(2530, 3081, 0),
            new Tile(2534, 3071, 0),
            new Tile(2542, 3065, 0),
            new Tile(2552, 3062, 0),
            new Tile(2562, 3062, 0),
            new Tile(2572, 3063, 0),
            new Tile(2578, 3066, 0)
    };

    private final Tile[] LodeToBank_Yanille = new Tile[] {
            new Tile(2529, 3093, 0),
            new Tile(2539, 3093, 0),
            new Tile(2550, 3093, 0),
            new Tile(2562, 3093, 0),
            new Tile(2572, 3093, 0),
            new Tile(2582, 3093, 0),
            new Tile(2592, 3093, 0),
            new Tile(2602, 3095, 0),
            new Tile(2611, 3093, 0)
    };

    private final Tile[] PathToTree_Yanille = new Tile[] {
            new Tile(2612, 3094, 0),
            new Tile(2616, 3104, 0),
            new Tile(2626, 3102, 0),
            new Tile(2628, 3092, 0),
            new Tile(2627, 3082, 0),
            new Tile(2622, 3073, 0),
            new Tile(2612, 3069, 0),
            new Tile(2602, 3068, 0),
            new Tile(2592, 3066, 0),
            new Tile(2584, 3067, 0)
    };

    private final Tile[] PathToBank_Yanille = new Tile[] {
            new Tile(2581, 3069, 0),
            new Tile(2591, 3069, 0),
            new Tile(2601, 3069, 0),
            new Tile(2612, 3069, 0),
            new Tile(2622, 3072, 0),
            new Tile(2627, 3081, 0),
            new Tile(2628, 3091, 0),
            new Tile(2627, 3101, 0),
            new Tile(2617, 3105, 0),
            new Tile(2611, 3097, 0),
            new Tile(2611, 3092, 0)
    };
    // endregion

    // region [ Draynor Data ]...

    private final Tile[] LodeToBank_Draynor = new Tile[] {
            new Tile(3107, 3296, 0),
            new Tile(3110, 3286, 0),
            new Tile(3106, 3276, 0),
            new Tile(3103, 3266, 0),
            new Tile(3105, 3256, 0),
            new Tile(3097, 3249, 0),
            new Tile(3092, 3243, 0)
    };

    private final Tile[] LodeToTree_Draynor = new Tile[] {
            new Tile(3107, 3297, 0),
            new Tile(3110, 3287, 0),
            new Tile(3105, 3277, 0),
            new Tile(3105, 3267, 0),
            new Tile(3105, 3257, 0),
            new Tile(3105, 3247, 0),
            new Tile(3105, 3237, 0),
            new Tile(3102, 3227, 0),
            new Tile(3098, 3217, 0)
    };

    private final Area TreeArea_Draynor = new Area(
            new Tile(3099, 3224, 0),
            new Tile(3087, 3223, 0),
            new Tile(3089, 3208, 0),
            new Tile(3100, 3206, 0),
            new Tile(3105, 3218, 0)
    );

    private final Area Lodestone_Draynor = new Area(
            new Tile(3109, 3302, 0),
            new Tile(3099, 3303, 0),
            new Tile(3099, 3292, 0),
            new Tile(3113, 3291, 0)
    );

    private final Area BankArea_Draynor = new Area(
            new Tile(3098, 3249, 0),
            new Tile(3085, 3249, 0),
            new Tile(3085, 3237, 0),
            new Tile(3100, 3237, 0)
    );

    private final Tile[] PathToTree_Draynor = new Tile[] {
            new Tile(3091, 3244, 0),
            new Tile(3091, 3234, 0),
            new Tile(3093, 3224, 0),
            new Tile(3095, 3216, 0)
    };

    private final Tile[] PathToBank_Draynor = new Tile[] {
            new Tile(3096, 3215, 0),
            new Tile(3094, 3225, 0),
            new Tile(3092, 3235, 0),
            new Tile(3092, 3245, 0)
    };
    // endregion

    // region [ Edgeville ]...
    private final Tile[] LodeToBank_Edgeville = new Tile[] {
            new Tile(3067, 3505, 0),
            new Tile(3076, 3500, 0),
            new Tile(3087, 3498, 0),
            new Tile(3095, 3492, 0),
            new Tile(3096, 3492, 0)
    };

    private final Tile[] LodeToTree_Edgeville = new Tile[] {
            new Tile(3067, 3501, 0),
            new Tile(3069, 3491, 0),
            new Tile(3073, 3481, 0),
            new Tile(3075, 3471, 0),
            new Tile(3080, 3462, 0),
            new Tile(3094, 3459, 0)
    };

    private final Area TreeArea_Edgeville = new Area(
            new Tile(3100, 3462, 0),
            new Tile(3085, 3463, 0),
            new Tile(3086, 3449, 0),
            new Tile(3102, 3449, 0)
    );

    private final Area Lodestone_Edgeville = new Area(
            new Tile(3071, 3512, 0),
            new Tile(3058, 3513, 0),
            new Tile(3061, 3498, 0),
            new Tile(3077, 3498, 0)
    );

    private final Area BankArea_Edgeville = new Area(
            new Tile(3088, 3501, 0),
            new Tile(3088, 3485, 0),
            new Tile(3103, 3485, 0),
            new Tile(3103, 3502, 0)
    );

    private final Tile[] PathToTree_Edgeville = new Tile[] {
            new Tile(3093, 3495, 0),
            new Tile(3086, 3487, 0),
            new Tile(3085, 3477, 0),
            new Tile(3085, 3467, 0),
            new Tile(3091, 3458, 0),
            new Tile(3092, 3456, 0)
    };

    private final Tile[] PathToBank_Edgeville = new Tile[] {
            new Tile(3092, 3457, 0),
            new Tile(3086, 3465, 0),
            new Tile(3086, 3475, 0),
            new Tile(3086, 3485, 0),
            new Tile(3095, 3490, 0),
            new Tile(3096, 3494, 0)
    };
    // endregion

    public CutElderTrees()
    {
        patternIndex = 0;
        curSelection = BotUtil.patternArray[patternIndex];
        currentLocation = Locations.valueOf(Locations.values()[curSelection].toString());
        DetermineNewAreaPathData();         // Collect data on the new location
        teleporting = true;
    }

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

    public void SetNextLocation()
    {
        Locations[] locArray = Locations.values();

        patternIndex += 1;
        if( patternIndex >= BotUtil.patternArray.length)
        {
            patternIndex = 0;
        }

        curSelection = BotUtil.patternArray[patternIndex];

        currentLocation = Locations.valueOf(locArray[curSelection].toString());
    }

    @Override
    public void run()
    {
        if( curLoactionFinished && !teleporting ) // Tree at current spot is dead.
        {
            SetNextLocation();                  // Set next location
            DetermineNewAreaPathData();         // Collect data on the new location
            teleporting = true;
        }

        if( teleporting )
        {
            TeleportToLocation();
        }

        if( !teleporting )
        {
            if (Inventory.getCount() < 28)
            {
                InventoryNotFull();
            }
            else
            {
                InventoryFull();
            }
        }
    }

    public void DetermineNewAreaPathData()
    {
        switch (currentLocation)
        {
            case EDGEVILLE:
                lodeAreaToUse       = Lodestone_Edgeville;
                bankAreaToUse       = BankArea_Edgeville;
                treeAreaToUse       = TreeArea_Edgeville;
                pathToBankToUse     = PathToBank_Edgeville;
                pathToTreeToUse     = PathToTree_Edgeville;
                lodeToBankToUse     = LodeToBank_Edgeville;
                lodeToTreeToUse     = LodeToTree_Edgeville;
                break;
            case DRAYNOR:
                lodeAreaToUse       = Lodestone_Draynor;
                bankAreaToUse       = BankArea_Draynor;
                treeAreaToUse       = TreeArea_Draynor;
                pathToBankToUse     = PathToBank_Draynor;
                pathToTreeToUse     = PathToTree_Draynor;
                lodeToBankToUse     = LodeToBank_Draynor;
                lodeToTreeToUse     = LodeToTree_Draynor;
                break;
            case SEERS_VILLAGE:
                bankAreaToUse       = BankArea_SeersVillage;
                treeAreaToUse       = TreeArea_SeersVillage;
                lodeAreaToUse       = Lodestone_SeersVillage;
                pathToBankToUse     = PathToBank_SeersVillage;
                pathToTreeToUse     = PathToTree_SeersVillage;
                lodeToBankToUse     = LodeToBank_SeersVillage;
                lodeToTreeToUse     = LodeToTree_SeersVillage;
                break;
            case YANILlE:
                bankAreaToUse       = BankArea_Yanille;
                treeAreaToUse       = TreeArea_Yanille;
                lodeAreaToUse       = Lodestone_Yanille;
                pathToBankToUse     = PathToBank_Yanille;
                pathToTreeToUse     = PathToTree_Yanille;
                lodeToBankToUse     = LodeToBank_Yanille;
                lodeToTreeToUse     = LodeToTree_Yanille;
                break;
            default:
                lodeAreaToUse       = Lodestone_Edgeville;
                bankAreaToUse       = BankArea_Edgeville;
                treeAreaToUse       = TreeArea_Edgeville;
                pathToBankToUse     = PathToBank_Edgeville;
                pathToTreeToUse     = PathToTree_Edgeville;
                lodeToBankToUse     = LodeToBank_Edgeville;
                lodeToTreeToUse     = LodeToTree_Edgeville;
                break;

        }
    }

    public void InventoryNotFull()
    {
        // If the player is at the lodestone.
        if( lodeAreaToUse.contains(Players.getLocal().getLocation()) || walkingFromLode )
        {
            walkingFromLode = true; // set the lode walking flag to true.
            BotUtil.WalkAlongPath(lodeToTreeToUse); // walk to the tree area.

            // when you get there
            if( treeAreaToUse.contains(Players.getLocal().getLocation()) )
            {
                // turn the flag off.
                walkingFromLode = false;
            }
        }
        else
        {
            // if we aren't at the lode or walking from it, then we're either at the tree area, or walking to it.
            if( treeAreaToUse.contains(Players.getLocal().getLocation()) )
            {
                // lets see if that tree is still there.
                SceneObject elderTree = BotUtil.FindNearestSceneObject(treeName, treeAreaToUse, "chop");

                // if we're at the tree area, start chopping.
                BotUtil.CutLogs(treeName, treeAreaToUse, "chop");

                // if it isn't then it must have died.
                if (elderTree == null)
                {
                    // this location is finished, so set this flag to true, so that the bot knows to go to the next lode.
                    curLoactionFinished = true;
                }
            }
            else
            {
                // if not, walk to the tree from the bank.
                BotUtil.WalkAlongPath(pathToTreeToUse);
            }
        }

    }

    public void InventoryFull()
    {
        // if the player is in the tree area.
        if( treeAreaToUse.contains(Players.getLocal().getLocation()) )
        {
            // lets see if that tree is still there.
            SceneObject elderTree = BotUtil.FindNearestSceneObject(treeName, treeAreaToUse, "chop");

            // if it isn't then it must have died when we got our last log.
            if (elderTree == null)
            {
                // this location is finished, so set this flag to true, so that the bot knows to go to the next lode.
                curLoactionFinished = true;
            }
        }

        // If the player is at the lodestone.
        if( lodeAreaToUse.contains(Players.getLocal().getLocation()) || walkingFromLode )
        {
            walkingFromLode = true; // set the lode walking flag to true.
            BotUtil.WalkAlongPath(lodeToBankToUse); // walk to the tree area.

            // when you get there
            if( bankAreaToUse.contains(Players.getLocal().getLocation()) )
            {
                // turn the flag off.
                walkingFromLode = false;
            }
        }
        else
        {
            // if we aren't at the lode or walking from it, then we're either at the tree area, or walking to it.
            if( bankAreaToUse.contains(Players.getLocal().getLocation()) )
            {
                BotUtil.BankItems();
            }
            else
            {
                // if not, walk to the tree from the bank.
                BotUtil.WalkAlongPath(pathToBankToUse);
            }
        }
    }

    public void TeleportToLocation()
    {
        if( !lodeAreaToUse.contains(Players.getLocal().getLocation()) )
        {
            Lodestones.valueOf(currentLocation.toString()).teleport();
            Time.sleep(4000, 5000);
        }
        else
        {
            curLoactionFinished = false;
            teleporting = false;
        }
    }
}
