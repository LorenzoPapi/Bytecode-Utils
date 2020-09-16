package com.github.lorenzopapi.bytecode.asm;

import org.objectweb.asm.Opcodes;

public class ASMParser implements Opcodes {

	public static Object[] parseMethod(String method) {
		Object[] infoArray = new Object[100];
		int access = 0;
		String returnValue = "";
		String name = "";
		String s = method.replace("\r\t\t", "").split("\n")[0];
		for (String s1 : s.split(" ")) {
			System.out.print(s1 + " ");
			switch (s1) {
				case "public":
					access += ACC_PUBLIC;
					break;
				case "private":
					access += ACC_PRIVATE;
					break;
				case "protected":
					access += ACC_PROTECTED;
					break;
				case "static":
					access += ACC_STATIC;
					break;
				case "strictfp":
					access += ACC_STRICT;
					break;
				case "abstract":
					access += ACC_ABSTRACT;
					break;
				case "final":
					access += ACC_FINAL;
					break;
				case "transient":
					access += ACC_TRANSIENT;
					break;
				case "native":
					access += ACC_NATIVE;
					break;
				case "interface":
					access += ACC_INTERFACE;
					break;
				case "void":
					returnValue = "V";
					break;
				case "int":
					returnValue = "I";
					break;
				case "long":
					returnValue = "J";
					break;
			}

			if (s1.contains("("))
				name = s1.split("\\(")[0].replace("(", "");

		}
		infoArray[0] = access;
		infoArray[1] = name;
		infoArray[2] = returnValue;
		return infoArray;
	}
}
