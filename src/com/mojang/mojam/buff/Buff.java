package com.mojang.mojam.buff;

abstract public class Buff {
	/**
	 * Type of buff
	 */
	public enum BuffType { 
		ALL, HEALTH_MODIF, REGEN_RATE, EXP_MODIF , EXP_RATE
	}
	
	public BuffType buffType;
	
	protected boolean dispell  = true;
	protected boolean infinite = false;
	protected int charge;
	protected float param;
	
	public Buff(int charge, float how) {
		this.charge = charge;
		this.param = how;
	}
	
	public float effect() {
		float r = this.value();
		this.tick();
		return r;
	}
	
	protected float value() {
		if (this.infinite || this.charge > 0) {
			return this.param;
		} else {
			return 0;
		}
	}
	protected void tick() { this.charge -- ; }
	
	public float combine( float b ) {
		return this.effect() + b ;
	}
	
	public boolean is(BuffType type) { return type == BuffType.ALL || this.buffType == type ; }
	public boolean over() { return !this.infinite || this.charge <= 0 ;}
	public boolean canDispell() { return this.dispell ; }
}