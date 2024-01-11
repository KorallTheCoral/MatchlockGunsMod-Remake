package korallkarlsson.matchlockguns;

public class Config {

    public static final String BASE_CONTENT_PACK =
"""
flintlock_musket
	damage: 16
	accuracy: 50
	gunpowderAmount: 2
	range: 8
	type: flintlock
	chargeTime: 60
	reliability: 0.95
	durability: 300

flintlock_pistol
	damage: 12
	accuracy: 18
	gunpowderAmount: 1
	range: 6
	type: flintlock
	canDualWield: true
	chargeTime: 35
	reliability: 0.95
	durability: 300

flintlock_revolver
    damage: 10
    accuracy: 15
    maxShots: 6
    gunpowderAmount: 1
    range: 5
    type: flintlock
    canDualWield: true
    useRamRod: false
    chargeTime: 35
    reliability: 0.94
    durability: 250

flintlock_rifle
    damage: 16
    accuracy: 80
    range: 10
    type: flintlock
    chargeTime: 100
    gunpowderAmount: 2
    reliability: 0.90
    durability: 280

matchlock_arquebus
	damage: 16
	accuracy: 50
	gunpowderAmount: 2
	range: 8
	type: matchlock
	chargeTime: 60

matchlock_pistol
	damage: 12
	accuracy: 18
	gunpowderAmount: 1
	range: 6
	type: matchlock
	canDualWield: true
	chargeTime: 35

wheellock_pistol
	damage: 12
	accuracy: 18
	gunpowderAmount: 1
	range: 6
	type: wheellock
	canDualWield: true
	chargeTime: 35
	reliability: 0.98
	durability: 280

wheellock_musket
	damage: 16
	accuracy: 50
	gunpowderAmount: 2
	range: 8
	type: wheellock
	chargeTime: 60
	reliabilty: 0.98
	durability: 280
""";

}
