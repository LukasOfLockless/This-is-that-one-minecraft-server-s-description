package lockless.killquest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

//todo
import org.bukkit.event.world.WorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.raid.RaidEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

//test
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin implements Listener{

	private File pluginFolderPath;
	private String foldername="killquest";
	private String SaveFileName="killquestWorld";
	private double checkTotalQuestRadius=15000;
	private double onePartAffectArea=1000;

	private int lookfilteredMobs=0;
	private int lookWrongWorldfilteredout=0;
	
	
	private int currentlyClear=0;
	private int currentlyRunning=0;
	private int chaosOfNature=0;
	private int assumedSize;
	private boolean[] killquestClearBool;//i have the brain capacity to code this, not that
	private int[][] killquestQuests;//i have the brain capacity to code this, not that
	private ArrayList <Player> killquestQuesters;//i have the brain capacity to code this, not that
	private ArrayList <QuesterAndScore> ScrollOfQuestingKnights;
	
	private class QuesterAndScore
	{
		public Player player;
		public int score;
		public int currentIndex;
		public String name ="";
		
		public QuesterAndScore(Player p ,int areaIndex) 
		{
			score = 0;
			player = p;
			name = p.getName();
			currentIndex = areaIndex;
		}
		public void AddScore(int index , int addScore) 
		{
			if(currentIndex == index) 
			{
				score += addScore;
			}
			else 
			{
				currentIndex= index;
				score = addScore;
			}
		}
		public boolean isThis(String someName) 
		{
			return (name==someName);
		}
		
		
	}
	
	
	//private Map<Integer,Boolean> areaClear;
	//private Map<Integer, ArrayList<Integer> > killquests;//can i use it like this instead of that i mean im not retarded but ">>"!??!? 
	//private Map<Integer, Player> killquesters;
	
	/*
	private List<Integer> areaSpooders;
	private List<Integer> areaZomboz;
	private List<Integer> areaDingos;
	private List<Integer> areaNeets;
	*/
	private boolean canSpawnRAID = true;
	

	public void onEnable() {
		int sideSize = 2*(int)(checkTotalQuestRadius/onePartAffectArea);
		assumedSize = sideSize*sideSize;
		System.out.println("assumed size of killquest arrays " + assumedSize);
		killquestClearBool = new boolean[assumedSize];
		killquestQuests = new int[assumedSize][4];
		killquestQuesters = new  ArrayList<Player>();
		ScrollOfQuestingKnights = new ArrayList<QuesterAndScore>();
		//areaClear =Collections.emptyMap();
		//killquests = Collections.emptyMap();
		//killquesters = Collections.emptyMap();
		
		doSomeLoad();
		
		
		getServer().getPluginManager().registerEvents(this, this);
		System.out.println("killquest enabled");
	}
	
	public void onDisable() 
	{
		doSomeSave();
		System.out.println("killquest saving data and disabling");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		
		if(label.equalsIgnoreCase("killquest")) 
		{
			if(sender instanceof Player) 
			{
				PlayerCommandLogic((Player) sender);
				return true;
			}
			else 
			{
				ConsoleCommandLogic(sender);
			}
			
			
			
			
			return true;
		}
		
		else if(label.equalsIgnoreCase("killquestraidpls")) 
		{
			if(sender instanceof Player) 
			{
				doRaid((Player) sender);
				return true;
			}
		}
		else if (label.equalsIgnoreCase("turret")) 
		{
			
			CommandSpawns(sender, command, label, args);
	        
	                
	            
            
	        return false;
        }
	            
		return false;
	}
	
	
    
	LivingEntity testentity;
	Player testplayer;
    private void CommandSpawns(CommandSender sender, Command command, String label, String[] args) 
    {
    	if(sender instanceof Player) 
        {
            final Player testplayer = (Player) sender;
            
            Location loc = testplayer.getLocation();
            World w = Bukkit.getServer().getWorld("world");
            double y = loc.getY();
            double x = loc.getX();
            double z = loc.getZ();
            Location sentry = new Location(w,x,y,z);
            final Creature testentity = (Creature) Bukkit.getWorld("world").spawnEntity(sentry, EntityType.SNOWMAN);
            testentity.setMaxHealth(250.0);
            testentity.setHealth(250.0);
            testentity.setTicksLived(20 * 20);
            testentity.setRemoveWhenFarAway(false);
            testentity.setCanPickupItems(true);
            testentity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 10));
            testentity.getEquipment().setHelmet(new ItemStack(Material.AIR, 1));
            testentity.getEquipment().setHelmetDropChance(0.0F);
            testentity.getEntityId();
               
       
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() 
            {
                @SuppressWarnings("deprecation")
                public void run() {
                    Player anyplayer = (Player) testplayer;
                    testentity.getTarget();
                    testentity.setTarget(anyplayer);
                    testentity.launchProjectile(Snowball.class);
                }
                   
                           
               
            },  60);
        }
    	else 
    	{
    		if(args==null)
    		{
    			return;
			}
    		else
    		{
    			String playerName = args[0];
    			boolean found=false;
    			for(Player p: getServer().getOnlinePlayers()) 
    			{
    				if(p.getName() ==playerName) 
    				{
    					found =true;
    					final Player testplayer = p;
    					break;
    				}
    			}
    			if(found==false) 
    			{
    				return;
    			}
    			
                
                Location loc = testplayer.getLocation();
                World w = Bukkit.getServer().getWorld("world");
                double y = loc.getY();
                double x = loc.getX();
                double z = loc.getZ();
                Location sentry = new Location(w,x,y,z);
                final Creature testentity = (Creature) Bukkit.getWorld("world").spawnEntity(sentry, EntityType.SNOWMAN);
                testentity.setMaxHealth(250.0);
                testentity.setHealth(250.0);
                testentity.setTicksLived(20 * 20);
                testentity.setRemoveWhenFarAway(false);
                testentity.setCanPickupItems(true);
                testentity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 10));
                testentity.getEquipment().setHelmet(new ItemStack(Material.AIR, 1));
                testentity.getEquipment().setHelmetDropChance(0.0F);
                testentity.getEntityId();
                   
           
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() 
                {
                    @SuppressWarnings("deprecation")
                    public void run() {
                        Player anyplayer = (Player) testplayer;
                        testentity.getTarget();
                        testentity.setTarget(anyplayer);
                        testentity.launchProjectile(Snowball.class);
                    }
                       
                               
                   
                },  60);
    			
    		}
    		
    	}
    }
	
	private int[] spawnMobAmounts;
	private ArrayList<Location> raidSpawnLocations;
	private ArrayList<Mob> raiders;
	private raidCoordinate lastRaidSpot;
	private ArrayList<Player> raidInitialTargets;
	private BukkitRunnable raidSpawningTask;
	private BukkitRunnable raidTargetingTask;
	private BukkitRunnable raidEndCheckTask;
	private BukkitRunnable raidNightmare;
	private World mainWorld;
	//.spawnEntity(l, EntityType.ARMOR_STAND);
	private class raidCoordinate
	{
		double x;
		double z;


		public raidCoordinate(double setX,double setZ)
		{
			x = setX;
			z = setZ;
		}
		public String getString() 
		{
			return (""+(int)x+" "+(int)z+"");
		}
	}
	//logic
	private void SpawnARaider(EntityType type,double x, double z) 
	{
		mainWorld.spawnEntity(null, type);
	}
	
	private void SpawnARidingRaider(EntityType type,double x, double z) 
	{
		
	}
	
	private Location wishWooshXZisLocation(double x, double z) 
	{
		World w;
		if(mainWorld !=null) 
		{
			w= mainWorld;
		}
		else 
		{
			w = getServer().getWorld("world");
			if(w == null) 
			{
				w = getServer().getWorlds().get(0);
				System.out.println("not WORLD but "+w.getName());
			}
			if(w!=null) 
			{
				mainWorld=w;
			}
			
		}
		
		double y = w.getHighestBlockYAt((int)x,(int)z);
		return new Location(w,x,y,z);
	}
	
	private void spawnChaos(int tier , ArrayList<Player> targets) 
	{
		
		if(canSpawnRAID==false) 
		{
			System.out.println("cant spawn raid, there might be "+raiders.size()+" around "+lastRaidSpot.getString() );
			return;
		}
		raidCoordinate  middle = new raidCoordinate(0,0);
		for(Player p : targets) 
		{
			middle.x += p.getLocation().getX();
			middle.z += p.getLocation().getZ();
		}
		middle.z = middle.z/targets.size();
		middle.x = middle.x/targets.size();
		
		double checkDistance=1000;
		double gameDistance=60;
		
		for (int i =-1 ; i<=1;i++) 
		{
			
			for (int u =-1 ; u<=1;u++) 
			{
				if(killquestClearBool[getLocationsKillQuestIndex(middle.x+checkDistance*i, middle.z+checkDistance*u)]) raidSpawnLocations.add(wishWooshXZisLocation(middle.x+gameDistance*i,middle.z+gameDistance*u));
			} 
			
		}
		
			
		if(raidSpawnLocations.size()==0) 
		{
			raidSpawnLocations.add(wishWooshXZisLocation(middle.x, middle.z));
			System.out.println(" added to middle smt "+raidSpawnLocations.size());
				
		}	
		else 
		{
			
			System.out.println(" sides of Chaos Atm "+raidSpawnLocations.size());
		}
		
		spawnMobAmounts = getRaidMobAmounts(tier);
		raiders = new ArrayList<Mob>();
		canSpawnRAID =false;
		raidInitialTargets = targets;
		
		
	}
	
	
	
	
	private int rng(int min, int max) 
	{
		return (int)(Math.random()*(max-min)) +min;
	}
	private int[] getRaidMobAmounts(int tier) 
	{
		// i think 0 through 6 are pretty vanilla, but 7 -9 are something that i enjoy
		//pillager, vindicator, ravager,witch, evoker, and then mixes of ravriders pill,vindic, evoker.
		int[][] amounts = new int[10][];
		amounts[0]= new int[]{rng(4,6),	rng(0,2)	,0	,0			,0		,0	,0	,0};
		amounts[1]= new int[]{rng(3,5),	rng(2,4)	,0	,0			,0		,0	,0	,0};
		amounts[2]= new int[]{rng(3,5),	rng(0,2)	,1	,rng(0,1)	,0		,0	,0	,0};
		amounts[3]= new int[]{rng(4,6),	rng(1,3)	,0	,3			,0		,0	,0	,0};
		amounts[4]= new int[]{rng(4,6),	rng(4,6)	,0	,rng(0,1)	,1		,1	,0	,0};
		amounts[5]= new int[]{rng(4,6),	rng(2,4)	,0	,rng(0,1)	,1		,0	,0	,0};
		amounts[6]= new int[]{rng(2,4),	rng(5,7)	,0	,rng(2,3)	,2		,0	,1	,1};
		amounts[7]= new int[]{rng(2,6),	rng(4,5)	,1	,rng(1,3)	,4		,0	,1	,0};
		amounts[8]= new int[]{rng(2,6),	rng(3,7)	,1	,rng(1,4)	,0		,2	,1	,1};
		amounts[9]= new int[]{rng(2,9),	rng(0,7)	,2	,rng(1,5)	,2		,0	,2	,2};
		if(tier<=9) 
		{
			return amounts[tier];			
		}
		
		
		return amounts[7];
		
	}
	
	private void doRaid(Player p) 
	{
		if(canSpawnRAID==false) 
		{
			p.sendMessage("the forces of Ill  might be "+raiders.size()+" around "+lastRaidSpot.getString() );
			return;
		}
		Location loca = p.getLocation();
		loca.setX(loca.getX()+32);
		loca= p.getWorld().getHighestBlockAt((int)loca.getX(), (int)loca.getZ()).getLocation();
		System.out.println(p.getDisplayName() + " wants a raid on " + loca);
		ArrayList<Player> singlePlayerCampaign=new ArrayList<Player>();
		singlePlayerCampaign.add(p);
		
		double AcceptableChaos =Math.sqrt(chaosOfNature/16);
		p.sendMessage(ChatColor.DARK_RED + "are you ready to take on "+ChatColor.MAGIC +"CHAOS"+ChatColor.RESET+""+ChatColor.DARK_RED+" Lv "+AcceptableChaos);
		chaosOfNature = (int)(chaosOfNature-1)/2;
		spawnChaos((int)AcceptableChaos,singlePlayerCampaign);
	}
	
	private void PlayerCommandLogic(Player playerOne) 
	{
		int theloca = getLocationsKillQuestIndex(playerOne.getLocation().getX(), playerOne.getLocation().getZ());
		
		if(playerIsWithinTrackingRange(playerOne)) 
		{
			//PlayerCommandLogic(playerOne);
			playerOne.sendMessage("you are in killquest location {"+theloca+"}");
			if (Math.random()>0.5f) //just to fuck with the ux
			{
				currentlyClear = QuickMath();				
			}
			playerOne.sendMessage("areas safe " +currentlyClear);
			
		}
		else 
		{
			playerOne.sendMessage("you are out of range for killquest to track kills");
		}
		//maps
		//killquester
		if (killquestQuesters.contains(playerOne)==false)
		{
			playerOne.sendMessage("Started tracking your kills for ");
			killquestQuesters.add(playerOne);
			boolean doublecheckForBetterList=false;
			for(QuesterAndScore p:ScrollOfQuestingKnights) 
			{
				if(p.isThis(playerOne.getName())) 
				{
					doublecheckForBetterList = true;
					break;
				}
			}
			if(doublecheckForBetterList==false) 
			{
				ScrollOfQuestingKnights.add(new QuesterAndScore(playerOne, theloca));
				
			}
			//TODO: maybe let the player reset their Killquest location via index.
		}
		else 
		{
			QuesterAndScore me = new QuesterAndScore(playerOne, 0);
			for(QuesterAndScore qs:ScrollOfQuestingKnights) 
			{
				if(qs.isThis(playerOne.getName())) 
				{
					me = qs;
					System.out.println("Think found the score");
					break;
				}
			}
			playerOne.sendMessage("your score:"+me.score);
		}
		
		//list killquest 
		
		try 
		{
			playerOne.sendMessage(Progress(theloca));
			/*
			playerOne.sendMessage("killed total");
			playerOne.sendMessage("creepers  "+killquestQuests[theloca][0]);
			playerOne.sendMessage("zombiess  "+killquestQuests[theloca][1]);
			playerOne.sendMessage("spiders   "+killquestQuests[theloca][2]);
			playerOne.sendMessage("skeletons "+killquestQuests[theloca][3]);
			*/
		}
		catch(NullPointerException e) 
		{
			System.out.println("found null in kill list");
			killquestQuests[theloca] = new int[4];
		}
		
		//pdate scoreboard?
		
	}

	
	private void ConsoleCommandLogic(CommandSender cmd) 
	{
		
		int countMaps_areaClear= killquestClearBool.length;//arr
		int countMaps_killquests= killquestQuests.length;//arr
		int countMaps_killquesters= killquestQuesters.size();//arraylist
		System.out.println("total :"+countMaps_areaClear+"\t Ongoing :"+countMaps_killquests+" \t Questers:"+countMaps_killquesters);
		System.out.println("currentlyrunning var"+currentlyRunning + " clear var"+currentlyClear);
		System.out.println( " \tfilteredtypes "+lookfilteredMobs+" \tworld "+lookWrongWorldfilteredout);
		for(QuesterAndScore qk: ScrollOfQuestingKnights) 
		{
			System.out.println(qk.name + " " +qk.currentIndex+ "   \t score"+qk.score);
		}
	}
	
	
	
	
	private boolean playerIsWithinTrackingRange(Player p) 
	{
		double x = p.getLocation().getX();
		if(x > checkTotalQuestRadius) 
		{
			return false;
		}
		if(x < -checkTotalQuestRadius) 
		{
			return false;
		}
		double z = p.getLocation().getZ();
		if(z > checkTotalQuestRadius) 
		{
			return false;
		}
		if(z < -checkTotalQuestRadius) 
		{
			return false;
		}
		return true;
	}
	
	private boolean entityIsWithinTrackingRange(Location loc) 
	{
		double x =  loc.getX();
		if(x > checkTotalQuestRadius) 
		{
			return false;
		}
		if(x < -checkTotalQuestRadius) 
		{
			return false;
		}
		double z = loc.getZ();
		if(z > checkTotalQuestRadius) 
		{
			return false;
		}
		if(z < -checkTotalQuestRadius) 
		{
			return false;
		}
		return true;
	}
	
	
	private int getLocationsKillQuestIndex(double x , double z) 
	{
		//System.out.println("killquest loc? x int is "+((int)((x+checkTotalQuestRadius)/onePartAffectArea))+" total "+(int)(2*checkTotalQuestRadius/onePartAffectArea*2*checkTotalQuestRadius/onePartAffectArea)+"y"+(int)((z+checkTotalQuestRadius)/onePartAffectArea));
		int theIndexIs = (int)((x+checkTotalQuestRadius)/onePartAffectArea)+(int)(2*checkTotalQuestRadius/onePartAffectArea)*(int)((z+checkTotalQuestRadius)/onePartAffectArea);
		return theIndexIs;
	}
	
	private boolean checkQuestComplete(int index) 
	{
		int[] checkthis =killquestQuests[index];
		Integer sum =0;
		for(int i =0;i<checkthis.length ; i++) 
		{
			sum+=checkthis[i];
		}
		
		if (sum < 256) //64*4
		{
			return false;
		}
		if(sum >=1024) 
		{
			return true;
		}
		for(int i =0;i<checkthis.length ; i++) 
		{
			if(checkthis[i]<64) 
			{
				return false;
			}
		}
		return true;
	}
	
	//TODO rename
	
	private int QuickMath() 
	{
		int countSome=0;
		for(int i = 0 ; i<killquestClearBool.length;i++)
		{
			if(killquestClearBool[i]) 
			{
				countSome++;
			}
		}
		
		return countSome;
	}
	
	private void Win(int index) 
	{
		for(Player p : getServer().getOnlinePlayers()) 
		{
			if (getLocationsKillQuestIndex(p.getLocation().getX(), p.getLocation().getZ())==index) 
			{
				p.sendMessage("you win");
			}
		}
		killquestClearBool[index] = true;
		currentlyClear++;
		System.out.println("Quest Complete!!"+killquestQuests[index][0]+" "+killquestQuests[index][1]+" "+killquestQuests[index][2]+" "+killquestQuests[index][3]+" ");
		//killquests.remove(key);
		killquestQuests[index] = new int[1];
		int localCounter=0;
		for(int i = 0; i<killquestQuests.length ; i++) 
		{
			if(killquestQuests[i].length >1) 
			{
				localCounter ++;
			}
		}
		currentlyRunning =  localCounter;
		ArrayList<Player> possiblyTargeted= new ArrayList<Player>();
		int sum = 0;
		for(QuesterAndScore qs:ScrollOfQuestingKnights) 
		{
			if(qs.currentIndex == index) 
			{
				possiblyTargeted.add(qs.player);
				sum += qs.score;
				qs = new QuesterAndScore(qs.player, index);
			}
		}
		
		
		if(chaosOfNature > sum) 
		{
			//findwhich areas around this one are infested with mobs.
			//todoSpawnRaidImmidiatelly.
			//target playerswho are still questing here.
			int tier = possiblyTargeted.size()/2;
			spawnChaos(tier , possiblyTargeted);
			
		}
		chaosOfNature-=sum;
	}
	private String Progress(int index) 
	{
		int[] checkthis =killquestQuests[index];
		int sumNormal =0;
		int sumTotal=0;
		for(int i =0;i<checkthis.length ; i++) 
		{
			sumTotal+=checkthis[i];
		}
		float SmarterProgress=(float)sumNormal/256f;
		float DumbProgress = (float)sumTotal/1024f;
		
		if(DumbProgress > SmarterProgress) 
		{
			return progressCompiler((int)DumbProgress*9);
		}
		else 
		{
			return progressCompiler((int)SmarterProgress*9);
		}		
	}
	
	private String progressCompiler(int progress) 
	{
		String[] progressbars = new String[9];
		progressbars[0] = ChatColor.GOLD + "<=__ ___ ___>";
		progressbars[1] = ChatColor.GOLD + "<==_ ___ ___>";
		progressbars[2] = ChatColor.GOLD + "<=== ___ ___>";
		
		progressbars[3] = ChatColor.GOLD + "<=== =__ ___>";
		progressbars[4] = ChatColor.GOLD + "<=== ==_ ___>";
		progressbars[5] = ChatColor.GOLD + "<=== === ___>";
		
		progressbars[6] = ChatColor.GOLD + "<=== === =__>";
		progressbars[7] = ChatColor.GOLD + "<=== === ==_>";
		progressbars[8] = ChatColor.GOLD + "<=== === ==::>";
		
		return progressbars[progress];
	}
	
	
	
	private void addToScore(Integer key, EntityType type) 
	{
		
		if(killquestClearBool[key]==true) 
		{
			//already done with the quest here
			return;
		}
		if(killquestQuests[key]==null) 
		{
			killquestQuests[key] = new int[4];
		}
		
		if(type == EntityType.ZOMBIE || type ==  EntityType.ZOMBIE_VILLAGER|| type == EntityType.HUSK|| type ==EntityType.DROWNED ) 
		{
			killquestQuests[key][1]++;
		}
		if(type == EntityType.CREEPER ) 
		{
			killquestQuests[key][0]++;
		}
		if(type == EntityType.SPIDER ) 
		{
			killquestQuests[key][2]++;
		}
		if(type == EntityType.SKELETON  ||type == EntityType.STRAY) 
		{
			killquestQuests[key][3]++;
		}
		if(checkQuestComplete(key)) 
		{
			Win(key);
		}
	}
	
	
	
	
	//event listeners
	
	//typefilter/quest completes
	
	@EventHandler
	private void onmobSpawn(EntitySpawnEvent event) 
	{
		if (event == null) 
		{
			System.out.println("event missing. WHAT THE FUCK SPIGOT BUKKIT");
			return;
		}
		/*
		SpawnReason reason = event.getSpawnReason();
		if(reason != SpawnReason.NATURAL) 
		{
			lookAtNotNaturalSpawns++;
			System.out.println("not naturals");
			return;
		}
		*/
		
		
		if(event.getLocation().getWorld() != getServer().getWorlds().get(0)) 
		{
			lookWrongWorldfilteredout++;
			//System.out.println("dont filter this dimension,cuz it not '"+getServer().getWorlds().get(0)+"'");
			return;
		}
		EntityType type= event.getEntityType();
		EntityType[] filterThis = {EntityType.ZOMBIE,EntityType.SPIDER , EntityType.SKELETON, EntityType.STRAY  ,EntityType.ZOMBIE_VILLAGER ,EntityType.HUSK ,EntityType.DROWNED, EntityType.CREEPER };
		
		//filter what to cancel
		boolean  goodType=false;
		for (EntityType filter : filterThis) 
		{
			if(filter== type) 
			{
				goodType =true;
				break;
			}
		}
		
		
		if(goodType==false) 
		{
			lookfilteredMobs++;
			//System.out.println("[killquest] basic mobs filtered");
			return;
		}
		
		Location loc = event.getEntity().getLocation();
		double x = loc.getX();
		double z = loc.getZ();
			
		int index = getLocationsKillQuestIndex(x,z);
		
		
		
		
		//location scores
		
		
		if(entityIsWithinTrackingRange(loc)) 
		{
			
			try 
			{
					
					// TODO: handle exception
				if(killquestClearBool[index]==true) 
				{
					event.setCancelled(true);
					
					System.out.println("cockblockworked");
				}
				else 
				{
					//assume area not clear
					//String outputintotrashcan = ""+event.getEntityType();
					//outputintotrashcan +=" is on index "+index;
					//System.out.println("[killquest] "+outputintotrashcan);
				}
			} 
			catch (NullPointerException e)
			{
				 if(killquestClearBool == null) 
				 {
					 System.out.println("really bad");
				 }
				 else 
				 {
					 System.out.println("whats null in onspawnmob? \n"+ e.getStackTrace().toString());
				 }
				 
			}			
		}
		
		
		
		
	}
	
	@EventHandler
	private void onmobDeath(EntityDeathEvent event) 
	{
		
		if(!(event.getEntity() instanceof Mob) && !(event.getEntity() instanceof Creature)) 
		{
			System.out.println("not mob n not creature");
			return;
			
		}
		
		EntityType type= event.getEntityType();
		EntityType[] filterThis = {EntityType.CREEPER , EntityType.ZOMBIE , EntityType.SKELETON , EntityType.ZOMBIE_VILLAGER , EntityType.HUSK , EntityType.DROWNED ,EntityType.SPIDER};
		
		//filter what to cancel
		boolean  goodType=false;
		for (EntityType filter : filterThis) 
		{
			if(filter== type) 
			{
				goodType =true;
				break;
			}
		}
		
		if(goodType==false) 
		{
			//System.out.println("[killquest] hostiles filtered");//this gets called. nice
			return;
		}
		//location scores
		Location loc = event.getEntity().getLocation();
		
		double x = loc.getX();
		double z = loc.getZ();
		
		int index = getLocationsKillQuestIndex(x,z);
		
		if(entityIsWithinTrackingRange(loc)) 
		{
			
			addToScore( index, type);
		}
		else 
		{
			return;
		}
		
		if (event.getEntity() instanceof Creature) 
		{
			Creature creature = (Creature)event.getEntity();
			String deathnotes="creature+dmgCause.eventname \t" +creature.getType() +"+"+creature.getLastDamageCause().getEventName();
			
			if(creature.getKiller() != null) 
			{
				if(creature.getKiller() instanceof Player) 
				{
					
					for(QuesterAndScore p:ScrollOfQuestingKnights) 
					{
						if(p.isThis(((Player)creature.getKiller()).getName())) 
						{
							deathnotes+=" by"+p.name +"'s power";
							p.AddScore(index, 1);
							break;
						}
					}
					
					//ScrollOfQuestingKnights.contains().where
				}
				else 
				{
					deathnotes += creature.getKiller().getType();
					chaosOfNature++;//asume creature killing creature or some shit
				}
			}
			System.out.println("dead "+deathnotes);
		}
		
		
		
	}
	
	
	
	
	
	
	//makes file management
	private void initSaving() 
	{
		
		//makes a file filled with 0
		String printthis="";
		int howWide = (int)(2*checkTotalQuestRadius/onePartAffectArea);
		int howLong = (int)(2*checkTotalQuestRadius/onePartAffectArea);
		for(int iWide = 0 ;iWide<howWide;iWide++ ) 
		{
			for(int iLong = 0 ;iLong<howLong;iLong++ ) 
			{
				printthis+="0";
			}
			printthis+="\n";
		}
		try{
			FileWriter writer = new FileWriter(SaveFileName);
		    writer.write(printthis);
		    writer.close();
		} catch (IOException e) {
		   System.out.println("well that print writer didnt work "+e.toString());
		}
		System.out.println(" initload worked ");
	}
	
	private void doSomeLoad() 
	{
		//folder checks
		FolderCreation();
		System.out.println("killquest loading");
		File testExist = new File(SaveFileName);
		try 
		{
			
		
			if(testExist.exists() == false) 
			{
				System.out.println("initialLoad cuz not exist on the second check in LOAD");
				initSaving();
			}
		} 
		catch(NullPointerException e)
		{

			System.out.println("initialLoad cuz null exception");
			initSaving();
			
		}
		

				
		//why map anything,array it
		
		try{
			FileReader reader = new FileReader(testExist);
			BufferedReader buffer=new BufferedReader(reader); 
			String line="";
			int MapIndex = 0;
			
			
			while( (line=buffer.readLine()) != null) 
			{
				for(int i = 0; i <line.length();i++) 
				{
					if(MapIndex>=killquestClearBool.length) 
					{
						System.out.println("text file contained a tad bit more letters than expected. something scuffed");
						return;
					}
					if(line.charAt(i)=='0') 
					{
						killquestClearBool[MapIndex]=false;
						killquestQuests[MapIndex]=new int[4];
						MapIndex++;
					}
					else if(line.charAt(i)=='1') 
					{
						killquestClearBool[MapIndex]=true;
						killquestQuests[MapIndex]=new int[1];
						currentlyClear++;
						MapIndex++;
					}
				}
			}
			
			buffer.close();
		    reader.close();
		}
		catch (IOException e) 
		{
		   System.out.println("well that print writer didnt work "+e.toString());
		}
		
		
	}
	
	
	
	private void doSomeSave() 
	{
		
		System.out.println("killquest saving");
		String printthis="";
		int perLine=(int)(checkTotalQuestRadius/onePartAffectArea *2);
		for(int i =0;i<killquestClearBool.length;i++) 
		{
			if(i%perLine==0) 
			{
				printthis+="\n";
			}
			if(killquestClearBool[i]) 
			{
				printthis+="1";	
			}
			else 	
			{
				printthis+="0";
			}
		}
		try{
			//savedData.delete();//deleted
			//replaces it lol
			File savedData = new File(SaveFileName);
			
			FileWriter writer = new FileWriter(savedData);
		    writer.write(printthis);
		    writer.close();
		} 
		catch (IOException e) 
		{
		   System.out.println("well that print writer didnt work "+e.toString());
		}
	}

	private void FolderCreation() 
    {
    	//System.out.println("what's wrong?" +this.getDataFolder().toString() + "vs."+"plugins");
		File authorDir=new File("plugins"+File.separator+"Lockless");
		if(authorDir.exists()==false) 
		{
			try
			{
				authorDir.mkdirs();
		    }
		    catch(Exception e){
		    	System.out.println("try make authordir fail ");
		    	e.printStackTrace();
		    } 
			if(authorDir.exists()) 
			{
				System.out.println("\nHope you enjoy\n-Lockless\n\n");
			}
		}
		
		pluginFolderPath = new File(authorDir+File.separator+foldername);
		if(pluginFolderPath.exists()==false) 
		{
			try
			{
				System.out.println("plugin "+foldername+ "folder doesnt exist. creating");
				pluginFolderPath.mkdirs();
		    }
		    catch(Exception e)
			{
		    	System.out.println("try make dir fail ");
		    	e.printStackTrace();
		    }
		}
		
		SaveFileName = "plugins"+File.separator+"Lockless"+File.separator+foldername+File.separator+"killquestWorld.txt";
		
		if(new File(SaveFileName).exists() == false) 
		{
			try 
			{
				
				FileWriter writer = new FileWriter(new File(SaveFileName));
			    writer.write(" ");
			    writer.close();
			}catch (IOException e) {
				// TODO: handle exception
				System.out.println("clean up on aisle 7");
			}
			initSaving();
		}
		
		
    }
	
}