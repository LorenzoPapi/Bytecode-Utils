package com.tfc.bytecode.compilers;

public class DynamicClassLoader extends ClassLoader {

	public Class<?> defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}

}
