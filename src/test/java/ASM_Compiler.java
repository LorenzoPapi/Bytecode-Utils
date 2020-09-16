import com.github.lorenzopapi.bytecode.asm.ASMParser;
import com.tfc.bytecode.compilers.DynamicClassLoader;
import com.tfc.bytecode.utils.asm.NodeBasedMethodVisitor;
import com.tfc.bytecode.utils.class_structure.FieldNode;
import com.tfc.bytecode.utils.class_structure.MethodNode;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ASM_Compiler implements Opcodes {
	public ASM_Compiler() {
	}

	private static void createMethod (ClassWriter cw, Object access, Object name, Object descriptor, int maxStacks, int maxLocals) {
		String fullDescriptor = (String) descriptor;
		if (!fullDescriptor.contains("("))
			fullDescriptor = "()" + descriptor;
		MethodVisitor con = cw.visitMethod((int) access, (String) name, fullDescriptor, null, null);
		con.visitCode();
		con.visitInsn(RETURN);
		con.visitMaxs(1 + maxStacks, 1 + maxLocals);
	}

	private static ClassWriter writeClass(int javaVersion, int classAccess, String className, String signature, String superClassName, String[] interfaces) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superClassName2 = superClassName;
		if (superClassName2 == null)
			superClassName2 = "java/lang/Object";
		cw.visit(javaVersion, classAccess, className, signature, superClassName2, interfaces);

		//Object[] method1Specs = ASMParser.parseMethod("public static void hello() {}");
		Object[] method2Specs = ASMParser.parseMethod("protected void seeYa() {}");
		Object[] methodSpecs = ASMParser.parseMethod("private void E() {}");

		createMethod(cw, methodSpecs[0], methodSpecs[1], methodSpecs[2], 0, 0);
		createMethod(cw, method2Specs[0], method2Specs[1], method2Specs[2], 0, 0);
		//createMethod(cw, method1Specs[0], method1Specs[1], method1Specs[2], 0, 0);

		MethodVisitor con = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		con.visitCode();
		con.visitVarInsn(ALOAD, 0);
		con.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		con.visitInsn(RETURN);
		con.visitMaxs(1, 1);

		MethodVisitor con2 = cw.visitMethod(ACC_PUBLIC, "hello", "(II)I", null, null);
		con2.visitCode();
		con2.visitVarInsn(ILOAD, 1);
		con2.visitVarInsn(ILOAD, 2);
		con2.visitInsn(IADD);
		con2.visitInsn(IRETURN);
		con2.visitMaxs(2, 3);

		cw.visitEnd();
		return cw;
	}

	/*
	* public int hello(int var1, int var2) {
    *     return var1 + var2;
    * }
    * if contains public -> access += ACC_PUBLIC
    * if contains private -> access += ACC_PRIVATE
	* */

	public static void main(String[] args) throws IllegalAccessException, InstantiationException, IOException {
		DynamicClassLoader loader = new DynamicClassLoader();
		ClassWriter cw = writeClass(V1_8, ACC_PUBLIC, "test/Hello", null, null, null);
		Class<?> clazz = loader.defineClass("test.Hello", cw.toByteArray());
		System.out.println(clazz.getName());
		System.out.println(clazz.newInstance());

		writeTo(cw.toByteArray());
	}

	//https://www.beyondjava.net/quick-guide-writing-byte-code-asm
	public byte[] generate(String name, int access, String superName, String[] interfaces, ArrayList<FieldNode> nodesF, ArrayList<MethodNode> nodesM) {
		ClassWriter writer = new ClassWriter(Opcodes.ASM8);
		if (superName.equals("")) superName = "java/lang/Object";
		writer.visit(8, access, name, null, superName, interfaces);
		writer.visitSource(name.replace(".", "/") + ".java", null);
		for (FieldNode node : nodesF)
			writer.visitField(node.access, node.name, node.desc, node.signature, node.value);
		for (MethodNode node : nodesM) {
			MethodVisitor visitor = writer.visitMethod(node.access, node.name, node.desc, node.signature, node.exceptions);
			new NodeBasedMethodVisitor(Opcodes.ASM8, visitor, node).visitCode();
		}
		writer.visitEnd();
		return writer.toByteArray();
	}

	private static void writeTo(byte[] b) throws IOException {
		File f = new File(System.getProperty("user.dir") + "\\asm-fabricated\\test\\Hello.class");
		if (!f.exists()) {
			f.getParentFile().getParentFile().mkdirs();
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		FileOutputStream writer = new FileOutputStream(f);
		writer.write(b);
		writer.close();
	}
}
