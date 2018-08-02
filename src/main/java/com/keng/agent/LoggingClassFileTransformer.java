package com.keng.agent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class LoggingClassFileTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        System.out.println("load class : " + className);

        if (className.startsWith("com/keng")) {

            try {
                CtClass ctClass = new ClassPool(true).get(className.replace("/", "."));
                // CtClass ctClass = new ClassPool(true).makeClass(new ByteArrayInputStream(classfileBuffer));
                CtMethod[] methods = ctClass.getMethods();

                for (CtMethod method : methods) {
                    if ("getClass".equals(method.getName()) || "hashCode".equals(method.getName())
                            || "equals".equals(method.getName())
                            || "clone".equals(method.getName())
                            || "toString".equals(method.getName())
                            || "notify".equals(method.getName())
                            || "notifyAll".equals(method.getName())
                            || "wait".equals(method.getName())
                            || "finalize".equals(method.getName())
                            || "main".equals(method.getName())) {
                        continue;
                    }

                    System.out.println("rebuild method -> " + method.getLongName());

                    /*
                    // 使用这种方式老是报错，不知道为什么
                    method.addLocalVariable("start", CtClass.longType);
                    method.insertBefore("start = System.currentTimeMillis();\n");
                    method.addLocalVariable("end", CtClass.longType);
                    method.insertAfter("\nend = System.currentTimeMillis();\nSystem.out.println(String.format(\"method[%d] 执行 [%d]\", " + method.getLongName() + ", (end - start)));");

                    */


                    // 需要修改的方法名
                    String sourceMethodname = method.getName();
                    // 重新命名需要修改的方法名
                    String newMethodname = sourceMethodname + "$$impl";
                    // 把原来方法的方法名设置为新的方法名
                    method.setName(newMethodname);
                    // 方法返回值
                    CtClass returnType = method.getReturnType();
                    // 判断是否有返回值
                    boolean returnVoid = CtClass.voidType.equals(returnType);
                    // 在方法体内执行新方法返回值变量
                    String returnVar = newMethodname + "$$var";

                    // 这个方法其实是原来的方法
                    CtMethod newMethod = CtNewMethod.copy(method, sourceMethodname, ctClass, null);

                    System.out.println(method.getName() + "\t" + newMethod.getName());

                    StringBuffer body = new StringBuffer();
                    body.append("{\n\tlong start$$0 = System.currentTimeMillis();\n");

                    if (!returnVoid) {
                        body.append("\t").append(returnType.getName()).append(" ")
                                .append(returnVar)
                                .append(" = ");
                    }
                    body.append(newMethodname + "($$);\n");
                    body.append("\tSystem.out.println(\"Call to method[" + sourceMethodname + "] 执行 [\" + (System.currentTimeMillis() - start$$0) + \"] ms\");");
                    if (!returnVoid) {
                        body.append("\n\treturn ").append(returnVar).append(";\n");
                    }
                    body.append("\n}");
                    System.out.println(body.toString());
                    /**
                     * 重新设置方法体
                     *
                     * 上面的一堆操作其实是把原来的方法名[method1()]修改为 method01$$impl
                     * 然后在新建一个 method1() 的方法，在 method01 体内执行 method01$$impl
                     *
                     */
                    newMethod.setBody(body.toString());
                    ctClass.addMethod(newMethod);
                }
                return ctClass.toBytecode();
            }catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}
