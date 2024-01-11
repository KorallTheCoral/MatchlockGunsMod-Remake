package korallkarlsson.matchlockguns.entities;

import korallkarlsson.matchlockguns.MatchlockGuns;
import net.minecraft.client.particle.FallingDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;


public class BulletEntity extends ThrowableProjectile {

    final int damage;

    public BulletEntity(EntityType<BulletEntity> entityType, Level world) {
        super(entityType, world);
        this.damage = 0;
    }
    public BulletEntity(Level world, Entity owner, double x, double y, double z, int damage, Vec3 velocity) {
        super(MatchlockGuns.BULLET_ENTITY_TYPE, world);
        this.setPos(x, y, z);
        this.damage = damage;
        this.setOwner(owner);
        this.setDeltaMovement(velocity);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();


        Level world = level();
        if(!world.isClientSide()) {
            ((ServerLevel) world).sendParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
            //world.addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if(result.getEntity() instanceof LivingEntity entity) {
            if(this.getOwner() == null)
                entity.hurt(this.damageSources().generic(), this.damage);
            else
                entity.hurt(this.damageSources().thrown(this.getOwner(), entity), this.damage);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if(!level().isClientSide())
            this.discard();
    }

    static boolean canDestroyBlock(Block block) {
        return block instanceof AbstractGlassBlock || block instanceof StainedGlassPaneBlock || block == Blocks.GLASS_PANE;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        Level world = level();

        if(!world.isClientSide()) {
            BlockPos blockPos = result.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);

            //world.addParticle(ParticleTypes.CLOUD, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
            Vec3 hitPos = result.getLocation().subtract(getDeltaMovement().normalize());
            ((ServerLevel) world).sendParticles(ParticleTypes.CLOUD, hitPos.x, hitPos.y, hitPos.z, 2, 0, 0, 0, 0.1);
            ((ServerLevel) world).sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, blockState),  hitPos.x, hitPos.y, hitPos.z, 3, 0.1, 0.1, 0.1, 0.1);


            if(canDestroyBlock(blockState.getBlock()))
                world.destroyBlock(blockPos, true, this);
        }
    }
}
