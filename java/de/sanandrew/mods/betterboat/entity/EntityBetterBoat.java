/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.betterboat.entity;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.betterboat.BetterBoat;
import de.sanandrew.mods.betterboat.network.PacketSendBoatPos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityBetterBoat
        extends Entity
{
    private static final int DW_TIME_SINCE_HIT = 17;
    private static final int DW_FORWARD_DIRECTION = 18;
    private static final int DW_DMG_TAKEN = 19;

    private boolean isBoatEmpty;
    private double speedMultiplier;
    private int boatPosRotationIncrements;
    private double boatX;
    private double boatY;
    private double boatZ;
    private double boatYaw;
    private double boatPitch;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    public EntityBetterBoat(World world) {
        super(world);
        this.isBoatEmpty = true;
        this.speedMultiplier = 0.07D;
        this.preventEntitySpawning = true;
        this.setSize(1.5F, 0.6F);
        this.yOffset = this.height / 2.0F;
    }

    public EntityBetterBoat(World world, double posX, double posY, double posZ) {
        this(world);
        this.setPosition(posX, posY + (double)this.yOffset, posZ);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(DW_TIME_SINCE_HIT, 0);
        this.dataWatcher.addObject(DW_FORWARD_DIRECTION, 1);
        this.dataWatcher.addObject(DW_DMG_TAKEN, 0.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return -0.3D;
    }

    @Override
    public boolean attackEntityFrom(DamageSource dmgSource, float damage) {
        if(this.isEntityInvulnerable() ) {
            return false;
        } else if( !this.worldObj.isRemote && !this.isDead ) {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + damage * 10.0F);
            this.setBeenAttacked();
            boolean isRiddenPlayerCreative = dmgSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)dmgSource.getEntity()).capabilities.isCreativeMode;

            if( isRiddenPlayerCreative || this.getDamageTaken() > 40.0F ) {
                if (this.riddenByEntity != null) {
                    this.riddenByEntity.mountEntity(this);
                }

                if (!isRiddenPlayerCreative) {
                    this.func_145778_a(Items.boat, 1, 0.0F);
                }

                this.setDead();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void performHurtAnimation() {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double posX, double posY, double posZ, float yaw, float pitch, int posRotIncr) {
        if( this.isBoatEmpty ) {
            this.boatPosRotationIncrements = posRotIncr + 5;
        } else {
            double deltaX = posX - this.posX;
            double deltaY = posY - this.posY;
            double deltaZ = posZ - this.posZ;
            double deltaVec = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

            if( deltaVec <= 1.0D ) {
                return;
            }

            this.boatPosRotationIncrements = 3;
        }

        this.boatX = posX;
        this.boatY = posY;
        this.boatZ = posZ;
        this.boatYaw = yaw;
        this.boatPitch = pitch;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double motionX, double motionY, double motionZ) {
        this.velocityX = this.motionX = motionX;
        this.velocityY = this.motionY = motionY;
        this.velocityZ = this.motionZ = motionZ;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if( this.getTimeSinceHit() > 0 ) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if( this.getDamageTaken() > 0.0F ) {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        double motionAmount = 0.0D;

        for( int i = 0; i < 5; i++ ) {
            double minY = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i) / 5.0D - 0.125D;
            double maxY = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 1) / 5.0D - 0.125D;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(this.boundingBox.minX, minY, this.boundingBox.minZ, this.boundingBox.maxX, maxY, this.boundingBox.maxZ);

            if( this.worldObj.isAABBInMaterial(axisalignedbb, Material.water) ) {
                motionAmount += 1.0D / 5.0D;
            }
        }

        double horizMotion = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if( horizMotion > 0.2625D ) {
            double motionDirCos = Math.cos((double)this.rotationYaw * Math.PI / 180.0D);
            double motionDirSin = Math.sin((double) this.rotationYaw * Math.PI / 180.0D);

            for( double d = 0; d < 1.0D + horizMotion * 60.0D; d++ ) {
                double particlePosRnd1 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
                double particlePosRnd2 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;
                double particleX;
                double particleZ;

                if( this.rand.nextBoolean() ) {
                    particleX = this.posX - motionDirCos * particlePosRnd1 * 0.8D + motionDirSin * particlePosRnd2;
                    particleZ = this.posZ - motionDirSin * particlePosRnd1 * 0.8D - motionDirCos * particlePosRnd2;
                } else {
                    particleX = this.posX + motionDirCos + motionDirSin * particlePosRnd1 * 0.7D;
                    particleZ = this.posZ + motionDirSin - motionDirCos * particlePosRnd1 * 0.7D;
                }

                this.worldObj.spawnParticle("splash", particleX, this.posY - 0.125D, particleZ, this.motionX, this.motionY, this.motionZ);
            }
        }

        if( this.worldObj.isRemote && this.isBoatEmpty ) {
            double newPosX;
            double newPosY;
            double newPosZ;

            if( this.boatPosRotationIncrements > 0 ) {
                float yawAngle = MathHelper.wrapAngleTo180_float((float) (this.boatYaw - this.rotationYaw));

                newPosX = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
                newPosY = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
                newPosZ = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;
                this.rotationYaw = (this.rotationYaw + yawAngle / (float)this.boatPosRotationIncrements);
                this.rotationPitch = (float)(this.rotationPitch + (this.boatPitch - this.rotationPitch) / (double)this.boatPosRotationIncrements);
                this.boatPosRotationIncrements--;

                this.setPosition(newPosX, newPosY, newPosZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            } else {
                newPosX = this.posX + this.motionX;
                newPosY = this.posY + this.motionY;
                newPosZ = this.posZ + this.motionZ;

                this.setPosition(newPosX, newPosY, newPosZ);

                if( this.onGround ) {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                this.motionX *= 0.99D;
                this.motionY *= 0.95D;
                this.motionZ *= 0.99D;
            }
        } else {
            double motionVal;
            if( motionAmount < 1.0D ) {
                motionVal = motionAmount * 2.0D - 1.0D;
                this.motionY += 0.04D * motionVal;
            } else {
                if( this.motionY < 0.0D ) {
                    this.motionY /= 2.0D;
                }

                this.motionY += 0.007000000216066837D;
            }

            if( this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase ) {
                EntityLivingBase riddenLiving = (EntityLivingBase)this.riddenByEntity;
                float sideMotion = this.riddenByEntity.rotationYaw + -riddenLiving.moveStrafing * 90.0F;
                this.motionX += -Math.sin((double)(sideMotion * (float)Math.PI / 180.0F)) * this.speedMultiplier * (double)riddenLiving.moveForward * 0.05D;
                this.motionZ += Math.cos((double)(sideMotion * (float)Math.PI / 180.0F)) * this.speedMultiplier * (double)riddenLiving.moveForward * 0.05D;
            }

            motionVal = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if( motionVal > 0.35D ) {
                double motionValRecur = 0.35D / motionVal;
                this.motionX *= motionValRecur;
                this.motionZ *= motionValRecur;
                motionVal = 0.35D;
            }

            if( motionVal > horizMotion && this.speedMultiplier < 0.35D ) {
                this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;

                if( this.speedMultiplier > 0.35D ) {
                    this.speedMultiplier = 0.35D;
                }
            } else {
                this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;

                if( this.speedMultiplier < 0.07D ) {
                    this.speedMultiplier = 0.07D;
                }
            }

            for( int i = 0; i < 4; i++ ) {
                int blockX = MathHelper.floor_double(this.posX + ((double)(i % 2) - 0.5D) * 0.8D);
                int blockZ = MathHelper.floor_double(this.posZ + ((double)(i / 2) - 0.5D) * 0.8D);

                for( int j = 0; j < 2; j++ ) {
                    int blockY = MathHelper.floor_double(this.posY) + j;
                    Block block = this.worldObj.getBlock(blockX, blockY, blockZ);

                    if( block == Blocks.snow_layer ) {
                        this.worldObj.setBlockToAir(blockX, blockY, blockZ);
                        this.isCollidedHorizontally = false;
                    } else if( block == Blocks.waterlily ) {
                        this.worldObj.func_147480_a(blockX, blockY, blockZ, true);
                        this.isCollidedHorizontally = false;
                    }
                }
            }

            if( this.onGround ) {
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            this.rotationPitch = 0.0F;
            double yaw = (double)this.rotationYaw;
            double deltaPosX = this.prevPosX - this.posX;
            double deltaPosZ = this.prevPosZ - this.posZ;

            if( deltaPosX * deltaPosX + deltaPosZ * deltaPosZ > 0.001D ) {
                yaw = (double)((float)(Math.atan2(deltaPosZ, deltaPosX) * 180.0D / Math.PI));
            }

            double yawAngle = MathHelper.wrapAngleTo180_double(yaw - (double)this.rotationYaw);

            if( yawAngle > 20.0D ) {
                yawAngle = 20.0D;
            }

            if( yawAngle < -20.0D ) {
                yawAngle = -20.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + yawAngle);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            if( !this.worldObj.isRemote) {
                List collidedEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2D, 0.0D, 0.2D));

                if( collidedEntities != null && !collidedEntities.isEmpty() ) {
                    for( Object entityObj : collidedEntities ) {
                        Entity entity = (Entity) entityObj;

                        if( entity != this.riddenByEntity ) {
                            if( entity.canBePushed() && entity instanceof EntityBoat ) {
                                entity.applyEntityCollision(this);
                            } else if( entity instanceof EntityLivingBase ) {
                                entity.applyEntityCollision(this);
                                this.isCollidedHorizontally = false;
                                if( horizMotion > 0.2D ) {
                                    entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.riddenByEntity != null ? this.riddenByEntity : this), (float) (horizMotion * 5.0D));
                                }
                            }
                        }
                    }
                }

                if( this.riddenByEntity != null && this.riddenByEntity.isDead ) {
                    this.riddenByEntity = null;
                }

                if( this.ticksExisted % 20 == 0 ) {
                    BetterBoat.network.sendToAllAround(new PacketSendBoatPos(this), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 128.0D));
                }
            }

            if( this.isCollidedHorizontally && horizMotion > 0.2D ) {
                if( !this.worldObj.isRemote && !this.isDead ) {
                    this.setDead();

                    if( !(this.riddenByEntity instanceof EntityPlayer && ((EntityPlayer)this.riddenByEntity).capabilities.isCreativeMode) ) {
                        this.func_145778_a(Items.boat, 1, 0.0F);
                    }
                }
            } else {
                this.motionX *= 0.99D;
                this.motionY *= 0.95D;
                this.motionZ *= 0.99D;
            }
        }
    }

    @Override
    public void updateRiderPosition() {
        if( this.riddenByEntity != null ) {
            double xOffset = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            double zOffset = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            this.riddenByEntity.setPosition(this.posX + xOffset, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + zOffset);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {}

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if( this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player ) {
            return true;
        } else {
            if( !this.worldObj.isRemote ) {
                player.mountEntity(this);
            }

            return true;
        }
    }

    @Override
    protected void updateFallState(double tickDistance, boolean onGround) {
        int blockX = MathHelper.floor_double(this.posX);
        int blockY = MathHelper.floor_double(this.posY);
        int blockZ = MathHelper.floor_double(this.posZ);

        if( onGround ) {
            if( this.fallDistance > 3.0F ) {
                this.fall(this.fallDistance);

                if( !this.worldObj.isRemote && !this.isDead ) {
                    this.setDead();

                    if( !(this.riddenByEntity instanceof EntityPlayer && ((EntityPlayer)this.riddenByEntity).capabilities.isCreativeMode) ) {
                        this.func_145778_a(Items.boat, 1, 0.0F);
                    }
                }

                this.fallDistance = 0.0F;
            }
        } else if( this.worldObj.getBlock(blockX, blockY - 1, blockZ).getMaterial() != Material.water && tickDistance < 0.0D ) {
            this.fallDistance = (float)((double)this.fallDistance - tickDistance);
        }
    }

    public void setDamageTaken(float damage) {
        this.dataWatcher.updateObject(DW_DMG_TAKEN, damage);
    }

    public float getDamageTaken() {
        return this.dataWatcher.getWatchableObjectFloat(DW_DMG_TAKEN);
    }

    public void setTimeSinceHit(int time) {
        this.dataWatcher.updateObject(DW_TIME_SINCE_HIT, time);
    }

    public int getTimeSinceHit() {
        return this.dataWatcher.getWatchableObjectInt(DW_TIME_SINCE_HIT);
    }

    public void setForwardDirection(int direction) {
        this.dataWatcher.updateObject(DW_FORWARD_DIRECTION, direction);
    }

    public int getForwardDirection() {
        return this.dataWatcher.getWatchableObjectInt(DW_FORWARD_DIRECTION);
    }
}
