package com.keng.agent;

import java.lang.instrument.Instrumentation;

/**
 *
 */
public class KengAgent {

    public static void premain(String args, Instrumentation inst) {
        System.out.println("参数 -> " + args + ", Instrumentation -> " + inst.getClass().getName());
        inst.addTransformer(new LoggingClassFileTransformer());
    }

}
