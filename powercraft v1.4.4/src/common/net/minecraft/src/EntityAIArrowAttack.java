package net.minecraft.src;

public class EntityAIArrowAttack extends EntityAIBase
{
    private final EntityLiving entityHost;

    private final IRangedAttackMob rangedAttackEntityHost;
    private EntityLiving attackTarget;

    private int rangedAttackTime = 0;
    private float entityMoveSpeed;
    private int field_75318_f = 0;

    private int maxRangedAttackTime;
    private float field_82642_h;

    public EntityAIArrowAttack(IRangedAttackMob par1IRangedAttackMob, float par2, int par3, float par4)
    {
        if (!(par1IRangedAttackMob instanceof EntityLiving))
        {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        else
        {
            this.rangedAttackEntityHost = par1IRangedAttackMob;
            this.entityHost = (EntityLiving)par1IRangedAttackMob;
            this.entityMoveSpeed = par2;
            this.maxRangedAttackTime = par3;
            this.field_82642_h = par4 * par4;
            this.rangedAttackTime = par3 / 2;
            this.setMutexBits(3);
        }
    }

    public boolean shouldExecute()
    {
        EntityLiving var1 = this.entityHost.getAttackTarget();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = var1;
            return true;
        }
    }

    public boolean continueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    public void resetTask()
    {
        this.attackTarget = null;
        this.field_75318_f = 0;
        this.rangedAttackTime = this.maxRangedAttackTime / 2;
    }

    public void updateTask()
    {
        double var1 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        boolean var3 = this.entityHost.getEntitySenses().canSee(this.attackTarget);

        if (var3)
        {
            ++this.field_75318_f;
        }
        else
        {
            this.field_75318_f = 0;
        }

        if (var1 <= (double)this.field_82642_h && this.field_75318_f >= 20)
        {
            this.entityHost.getNavigator().clearPathEntity();
        }
        else
        {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        this.rangedAttackTime = Math.max(this.rangedAttackTime - 1, 0);

        if (this.rangedAttackTime <= 0)
        {
            if (var1 <= (double)this.field_82642_h && var3)
            {
                this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget);
                this.rangedAttackTime = this.maxRangedAttackTime;
            }
        }
    }
}
