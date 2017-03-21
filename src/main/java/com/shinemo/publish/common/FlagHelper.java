package com.shinemo.publish.common;

import java.io.Serializable;


public class FlagHelper implements Serializable{
	
	private static final long serialVersionUID = 6089514727875855801L;

	private long mask;

	private long value;
	
	public FlagHelper() {
	}
	
	public FlagHelper(long mask, long value) {
		super();
		this.mask = mask;
		this.value = value;
	}
	
	public static FlagHelper build() {
		return build(0, 0);
	}

	public static FlagHelper build(long mask, long value) {
		return new FlagHelper(mask, value);
	}

	public FlagHelper add(long mask) {
		this.mask |= mask;
		this.value |= mask;
		return this;
	}

	public FlagHelper add(long mask, long value) {
		this.mask |= mask;
		this.value |= value;
		return this;
	}

	public FlagHelper has(long mask) {
		this.mask |= mask;
		this.value |= mask;
		return this;
	}

	public FlagHelper noHas(long mask) {
		this.mask |= mask;
		this.value &= (~mask);
		return this;
	}

	public FlagHelper remove(long mask) {
		this.mask |= mask;
		this.value &= (~mask);
		return this;
	}

	public long getMask() {
		return mask;
	}

	public long getValue() {
		return value;
	}
	
    public static boolean hasFlag(long flag, long mask) {
        return (flag & mask) == mask;
    }

}
