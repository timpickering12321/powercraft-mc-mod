package net.minecraft.src;

class EntityAITaskEntry
{
	/** The EntityAIBase object. */
	public EntityAIBase action;

	/** Priority of the EntityAIBase */
	public int priority;

	/** The EntityAITasks object of which this is an entry. */
	final EntityAITasks tasks;

	public EntityAITaskEntry(EntityAITasks par1EntityAITasks, int par2, EntityAIBase par3EntityAIBase)
	{
		tasks = par1EntityAITasks;
		priority = par2;
		action = par3EntityAIBase;
	}
}
